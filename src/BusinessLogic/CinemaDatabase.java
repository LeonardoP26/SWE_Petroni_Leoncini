package BusinessLogic;

import utils.ThrowingSupplier;

import java.sql.*;

public class CinemaDatabase {

    private CinemaDatabase() { }

    private static Connection connection = null;

    private final static String dbUrl = "jdbc:sqlite:./db/cinema.sqlite";

    private static Connection connect() throws SQLException {
        connection = DriverManager.getConnection(dbUrl);
        if (connection != null) {
            Statement stmt = connection.createStatement();
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
        return connection;
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = connect();
                if (connection == null)
                    throw new RuntimeException("Unable to open the database.");
            }
            return connection;
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public static <E extends Exception> boolean withTransaction(ThrowingSupplier<Boolean, E> lambda) throws E{
        Connection conn = getConnection();
        boolean oldAutoCommit = true;
        try{
            oldAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            if(lambda.get()) {
                conn.commit();
                return true;
            }
            else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e){
            try{
                conn.rollback();
                return false;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        } finally{
            try{
                conn.setAutoCommit(oldAutoCommit);
            } catch (SQLException e){
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isDatabaseEmpty() throws SQLException {
        try (
                Connection conn = DriverManager.getConnection(dbUrl);
                Statement s = conn.createStatement()
        ) {
            ResultSet res = s.executeQuery("SELECT COUNT(*) FROM sqlite_master");
            if (res.next())
                return res.getInt(1) == 0;
            return true;
        }
    }

}
