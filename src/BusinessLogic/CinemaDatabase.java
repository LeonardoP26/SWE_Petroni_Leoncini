package BusinessLogic;

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
                            "name VARCHAR UNIQUE NOT NULL ON CONFLICT IGNORE" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Movies(" +
                            "id INTEGER PRIMARY KEY, " +
                            "name TEXT, " +
                            "duration INTEGER, " +
                            "UNIQUE(name, duration)" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Halls(" +
                            "id INTEGER PRIMARY KEY, " +
                            "cinemaId INTEGER, " +
                            "type VARCHAR, " +
                            "FOREIGN KEY (cinemaId) REFERENCES Cinemas(id)" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Seats(" +
                            "id INTEGER PRIMARY KEY, " +
                            "row CHARACTER(1), " +
                            "number INTEGER, " +
                            "isBooked BOOLEAN, " +
                            "hallId INTEGER, " +
                            "UNIQUE(row, number, hallId), " +
                            "FOREIGN KEY(hallId) REFERENCES Halls(id)" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Users(" +
                            "id INTEGER PRIMARY KEY, " +
                            "username VARCHAR(15) UNIQUE NOT NULL ON CONFLICT IGNORE, " +
                            "balance BIGINT DEFAULT 0" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS ShowTimes(" +
                            "id INTEGER PRIMARY KEY, " +
                            "movieId INTEGER, " +
                            "hallId INTEGER, " +
                            "date VARCHAR(16), " +
                            "UNIQUE(movieId, hallId, date) ON CONFLICT IGNORE, " +
                            "FOREIGN KEY(movieId) REFERENCES Movies(id), " +
                            "FOREIGN KEY(hallId) REFERENCES Halls(id)" +
                            ")"
            );
            stmt.execute(
                    "CREATE TABLE IF NOT EXISTS Bookings(" +
                            "showTimeId INTEGER, " +
                            "seatId INTEGER, " +
                            "userId INTEGER, " +
                            "bookingNumber INTEGER, " +
                            "FOREIGN KEY(showTimeId) REFERENCES ShowTimes(id), " +
                            "FOREIGN KEY(seatId) REFERENCES Seats(id), " +
                            "FOREIGN KEY(userId) REFERENCES Users(id), " +
                            "PRIMARY KEY (showTimeId, seatId, userId)" +
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

}
