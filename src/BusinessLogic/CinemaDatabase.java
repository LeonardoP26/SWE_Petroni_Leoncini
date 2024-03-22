package BusinessLogic;

import java.sql.*;

public class CinemaDatabase {

    private CinemaDatabase() { }

    private static Connection connection = null;

    private static String dbUrl = "jdbc:sqlite:./db/cinema.sqlite";

    private static Connection connect(){
        try{
            connection = DriverManager.getConnection(dbUrl);
            if(connection != null){
                Statement stmt = connection.createStatement();
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Cinemas(" +
                                "id INTEGER PRIMARY KEY" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Movies(" +
                                "id INTEGER PRIMARY KEY," +
                                "name TEXT," +
                                "duration INTEGER" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Halls(" +
                                "id INTEGER PRIMARY KEY," +
                                "cinemaId INTEGER," +
                                "FOREIGN KEY (cinemaId) REFERENCES Cinemas(id)" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Seats(" +
                                "id INTEGER PRIMARY KEY ," +
                                "row CHARACTER(1)," +
                                "number INTEGER," +
                                "isBooked BOOLEAN," +
                                "hallId INTEGER," +
                                "UNIQUE(row, number, hallId)," +
                                "FOREIGN KEY(hallId) REFERENCES Halls(id)" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Users(" +
                                "id INTEGER PRIMARY KEY," +
                                "username VARCHAR(15) UNIQUE NOT NULL ON CONFLICT IGNORE," +
                                "balance BIGINT DEFAULT 0" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Schedules(" +
                                "id INTEGER PRIMARY KEY," +
                                "movieId INTEGER," +
                                "hallId INTEGER," +
                                "date VARCHAR(16)," +
                                "UNIQUE(movieId, hallId, date) ON CONFLICT IGNORE," +
                                "FOREIGN KEY(movieId) REFERENCES Movies(id)," +
                                "FOREIGN KEY(hallId) REFERENCES Halls(id)" +
                                ")"
                );
                stmt.execute(
                        "CREATE TABLE IF NOT EXISTS Bookings(" +
                                "scheduleId INTEGER," +
                                "seatId INTEGER," +
                                "userId INTEGER," +
                                "bookingNumber INTEGER," +
                                "FOREIGN KEY(scheduleId) REFERENCES Schedules(id)," +
                                "FOREIGN KEY(seatId) REFERENCES Seats(id)," +
                                "FOREIGN KEY(userId) REFERENCES Users(id)," +
                                "PRIMARY KEY (scheduleId, seatId, userId)" +
                                ")"
                );
            }
        } catch(SQLException ex){
            System.out.println("Unable to open database.");
        }
        return connection;
    }

    public static Connection getConnection() throws SQLException{
        if(connection == null || connection.isClosed())
            connection = connect();
        return connection;
    }

}
