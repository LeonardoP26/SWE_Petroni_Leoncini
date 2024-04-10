package BusinessLogic;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;

import java.sql.*;

public class CinemaDatabase {

    private CinemaDatabase() { }

    private static Connection connection = null;

    private final static String dbUrl = "jdbc:sqlite:./db/cinema.sqlite";

    private static Connection connect() throws SQLException {
        connection = DriverManager.getConnection(dbUrl);
        if (connection != null) {
            Statement stmt = connection.createStatement();
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Cinemas(" +
                            "id INTEGER PRIMARY KEY, " +
                            "name VARCHAR UNIQUE NOT NULL ON CONFLICT ROLLBACK" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Movies(" +
                            "id INTEGER PRIMARY KEY, " +
                            "name TEXT UNIQUE NOT NULL ON CONFLICT ROLLBACK, " +
                            "duration INTEGER " +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Halls(" +
                            "id INTEGER PRIMARY KEY , " +
                            "hallNumber INTEGER NOT NULL , " +
                            "cinemaId INTEGER NOT NULL, " +
                            "type VARCHAR NOT NULL, " +
                            "UNIQUE (hallNumber, cinemaId) ON CONFLICT ROLLBACK, " +
                            "FOREIGN KEY (cinemaId) REFERENCES Cinemas(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Seats(" +
                            "id INTEGER PRIMARY KEY, " +
                            "row CHARACTER(1) NOT NULL , " +
                            "number INTEGER NOT NULL, " +
                            "hallId INTEGER NOT NULL, " +
                            "UNIQUE(row, number, hallId) ON CONFLICT ROLLBACK, " +
                            "FOREIGN KEY(hallId) REFERENCES Halls(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Users(" +
                            "id INTEGER PRIMARY KEY, " +
                            "username VARCHAR(15) UNIQUE NOT NULL ON CONFLICT ROLLBACK, " +
                            "password TEXT NOT NULL, " +
                            "balance BIGINT NOT NULL DEFAULT 0" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS ShowTimes(" +
                            "id INTEGER PRIMARY KEY, " +
                            "movieId INTEGER NOT NULL, " +
                            "hallId INTEGER NOT NULL, " +
                            "date VARCHAR(16) NOT NULL, " +
                            "UNIQUE(movieId, hallId, date) ON CONFLICT ROLLBACK, " +
                            "FOREIGN KEY(movieId) REFERENCES Movies(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                            "FOREIGN KEY(hallId) REFERENCES Halls(id) ON DELETE CASCADE ON UPDATE CASCADE" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Bookings(" +
                            "showTimeId INTEGER, " +
                            "seatId INTEGER, " +
                            "userId INTEGER, " +
                            "bookingNumber INTEGER, " +
                            "FOREIGN KEY(showTimeId) REFERENCES ShowTimes(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                            "FOREIGN KEY(seatId) REFERENCES Seats(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                            "FOREIGN KEY(userId) REFERENCES Users(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                            "PRIMARY KEY (showTimeId, seatId, userId)" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS ShowTimeSeats(" +
                            "showTimeId INTEGER, " +
                            "seatId INTEGER, " +
                            "bookingNumber INTEGER DEFAULT 0, " +
                            "PRIMARY KEY (showTimeId, seatId), " +
                            "FOREIGN KEY (showTimeId) REFERENCES ShowTimes(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                            "FOREIGN KEY (seatId) REFERENCES Seats(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
                            "FOREIGN KEY (bookingNumber) REFERENCES Bookings(bookingNumber) ON DELETE SET DEFAULT ON UPDATE CASCADE" +
                            ")"
            );
        }
        return connection;
    }

    public static Connection getConnection() throws SQLException, UnableToOpenDatabaseException {
        if(connection == null || connection.isClosed()) {
            connection = connect();
            if (connection == null)
                throw new UnableToOpenDatabaseException("Unable to open the database.");
        }
        return connection;
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
