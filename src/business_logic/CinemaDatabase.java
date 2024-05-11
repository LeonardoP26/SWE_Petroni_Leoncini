package business_logic;

import org.jetbrains.annotations.NotNull;
import utils.ThrowingRunnable;

import java.sql.*;

public class CinemaDatabase {

    protected CinemaDatabase() { }

    protected static Connection connection = null;

    public final static String DB_URL = "jdbc:sqlite:./db/cinema.sqlite";
    private static boolean inTransaction = false;

    private static Connection connect(String dbUrl) throws SQLException {
        connection = DriverManager.getConnection(dbUrl);
        if (connection != null) {
            try(Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Cinemas(" +
                                "cinema_id INTEGER PRIMARY KEY, " +
                                "cinema_name VARCHAR UNIQUE NOT NULL ON CONFLICT ROLLBACK" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Movies(" +
                                "movie_id INTEGER PRIMARY KEY, " +
                                "movie_name TEXT UNIQUE NOT NULL ON CONFLICT ROLLBACK, " +
                                "duration INTEGER " +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Halls(" +
                                "hall_id INTEGER PRIMARY KEY , " +
                                "hall_number INTEGER NOT NULL , " +
                                "cinema_id INTEGER NOT NULL, " +
                                "type VARCHAR NOT NULL, " +
                                "UNIQUE (hall_number, cinema_id) ON CONFLICT ROLLBACK, " +
                                "FOREIGN KEY (cinema_id) REFERENCES Cinemas(cinema_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Seats(" +
                                "seat_id INTEGER PRIMARY KEY, " +
                                "row CHARACTER(1) NOT NULL , " +
                                "number INTEGER NOT NULL, " +
                                "hall_id INTEGER NOT NULL, " +
                                "UNIQUE(row, number, hall_id) ON CONFLICT ROLLBACK, " +
                                "FOREIGN KEY(hall_id) REFERENCES Halls(hall_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Users(" +
                                "user_id INTEGER PRIMARY KEY, " +
                                "username VARCHAR(15) UNIQUE NOT NULL ON CONFLICT ROLLBACK, " +
                                "password TEXT NOT NULL, " +
                                "balance BIGINT NOT NULL DEFAULT 0" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS ShowTimes(" +
                                "showtime_id INTEGER PRIMARY KEY, " +
                                "movie_id INTEGER NOT NULL, " +
                                "hall_id INTEGER NOT NULL, " +
                                "date VARCHAR(16) NOT NULL, " +
                                "UNIQUE(movie_id, hall_id, date) ON CONFLICT ROLLBACK, " +
                                "FOREIGN KEY(movie_id) REFERENCES Movies(movie_id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                                "FOREIGN KEY(hall_id) REFERENCES Halls(hall_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Bookings(" +
                                "showtime_id INTEGER, " +
                                "seat_id INTEGER, " +
                                "user_id INTEGER, " +
                                "booking_number INTEGER NOT NULL ON CONFLICT ROLLBACK, " +
                                "FOREIGN KEY(showtime_id) REFERENCES ShowTimes(showtime_id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                                "FOREIGN KEY(seat_id) REFERENCES Seats(seat_id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                                "FOREIGN KEY(user_id) REFERENCES Users(user_id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                                "PRIMARY KEY(showtime_id, seat_id, user_id)" +
                                ")"
                );
            }
        }
        return connection;
    }

    public static Connection getConnection(String dbUrl) {
        try {
            if (connection == null || connection.isClosed()) {
                connection = connect(dbUrl);
                if (connection == null)
                    throw new RuntimeException("Unable to open the database.");
            }
            return connection;
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static void withTransaction(@NotNull ThrowingRunnable lambda) throws Exception {
        inTransaction = true;
        try(Connection conn = getConnection(DB_URL)) {
            boolean oldAutoCommit = true;
            try {
                oldAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);
                lambda.run();
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            conn.setAutoCommit(oldAutoCommit);
        } finally {
            inTransaction = false;
        }
    }

    public static boolean isDatabaseEmpty() throws SQLException {
        try (
                Connection conn = DriverManager.getConnection(DB_URL);
                Statement s = conn.createStatement()
        ) {
            ResultSet res = s.executeQuery("SELECT COUNT(*) FROM sqlite_master");
            if (res.next())
                return res.getInt(1) == 0;
            return true;
        }
    }

    public static boolean isInTransaction() {
        return inTransaction;
    }
}
