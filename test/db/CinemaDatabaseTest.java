package db;

import business_logic.CinemaDatabase;
import business_logic.HallFactory;
import domain.*;
import utils.ThrowingFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CinemaDatabaseTest extends CinemaDatabase{

    public final static String DB_URL = "jdbc:sqlite:./test/db/test.sqlite";
    private static Cinema testCinema1;
    private static Cinema testCinema2;
    private static Booking testBooking1;
    private static Booking testBooking2;
    private static Hall testHall1;
    private static Hall testHall2;
    private static Movie testMovie1;
    private static Movie testMovie2;
    private static ArrayList<Seat> testSeats;
    private static ShowTime testShowTime1;
    private static ShowTime testShowTime2;
    private static User testUser1;
    private static User testUser2;

    public static <T> T runQuery(String sql, ThrowingFunction<ResultSet, T> function){
        try(Connection conn = CinemaDatabaseTest.getConnection(DB_URL);
            PreparedStatement s = conn.prepareStatement(sql);
            ResultSet res = s.executeQuery()
        ){
            return function.apply(res);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void setUp(){
        Connection connection = getConnection(DB_URL);
        try {
            try (
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT INTO Cinemas(cinema_id, cinema_name) VALUES (1, 'cinema1') RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next()) {
                    testCinema1 = new Cinema(res);
                    testCinema1.setName(res.getString("cinema_name"));
                }
            }
            try (
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT INTO Cinemas(cinema_id, cinema_name) VALUES (2, 'cinema2') RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next()) {
                    testCinema2 = new Cinema(res);
                    testCinema2.setName(res.getString("cinema_name"));
                }
            }
            try (
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT INTO Movies(movie_id, movie_name, duration) VALUES (1, 'movie1', 90) RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next()) {
                    testMovie1 = new Movie(res);
                    testMovie1.setName(res.getString("movie_name"));
                    testMovie1.setDuration(Duration.of(res.getLong("duration"), ChronoUnit.MINUTES));
                    testCinema1.getMovies().add(testMovie1);
                }
            }
            try (
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT INTO Movies(movie_id, movie_name, duration) VALUES (2, 'movie2', 80) RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next()) {
                    testMovie2 = new Movie(res);
                    testMovie2.setName(res.getString("movie_name"));
                    testMovie2.setDuration(Duration.of(res.getLong("duration"), ChronoUnit.MINUTES));
                    testCinema2.getMovies().add(testMovie2);
                }
            }
            try(
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT INTO Users(user_id, username, password, balance) VALUES (1, 'user1', 'user1', 100) RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next()) {
                    testUser1 = new User(res);
                    testUser1.setUsername(res.getString("username"));
                    testUser1.setPassword(res.getString("password"));
                    testUser1.setBalance(res.getLong("balance"));
                }
            }
            try(
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT INTO Users(user_id, username, password, balance) VALUES (2, 'user2', 'user2', 100) RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next()) {
                    testUser2 = new User(res);
                    testUser2.setUsername(res.getString("username"));
                    testUser2.setPassword(res.getString("password"));
                    testUser2.setBalance(res.getLong("balance"));
                }
            }
            try(
                    PreparedStatement s = connection.prepareStatement(
                    "INSERT OR IGNORE INTO Halls(hall_id, hall_number, cinema_id, type) VALUES (1, 1, 1, 'STANDARD') RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ){
                if(res.next()) {
                    testHall1 = HallFactory.createHall(res);
                    testHall1.setCinema(testCinema1);
                    testHall1.setHallNumber(res.getInt("hall_number"));
                }
            }
            try(
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT OR IGNORE INTO Halls(hall_id, hall_number, cinema_id, type) VALUES (2, 1, 2, 'IMAX') RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ){
                if(res.next()) {
                    testHall2 = HallFactory.createHall(res);
                    testHall2.setCinema(testCinema2);
                    testHall2.setHallNumber(res.getInt("hall_number"));
                }
            }
            StringBuilder sql = new StringBuilder("INSERT OR IGNORE INTO Seats(row, number, hall_id) VALUES ");
            for(char row = 'a'; row < 'c'; row++) {
                for (int number = 1; number < 4; number++) {
                    sql.append("('%s', %d, 1), ".formatted(row, number));
                }
            }
            sql.replace(sql.length() - 2, sql.length(), " RETURNING *");
            try (
                    PreparedStatement s = connection.prepareStatement(sql.toString());
                    ResultSet res = s.executeQuery()
            ) {
                testSeats = new ArrayList<>();
                while(res.next()){
                    Seat seat = new Seat(res);
                    seat.setRow(res.getString("row").charAt(0));
                    seat.setNumber(res.getInt("number"));
                    testSeats.add(seat);
                }
                testHall1.setSeats(testSeats);
            }
            try(PreparedStatement s = connection.prepareStatement("INSERT OR IGNORE INTO ShowTimes(showtime_id, movie_id, hall_id, date) VALUES (1, 1, 1, ?) RETURNING *")){
                LocalDateTime now =  LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
                s.setString(1, now.toString());
                try(ResultSet res = s.executeQuery()){
                    if(res.next()) {
                        testShowTime1 = new ShowTime(res);
                        testShowTime1.setHall(testHall1);
                        testShowTime1.setMovie(testMovie1);
                        testShowTime1.setDate(now);
                    }
                }
            }
            try(PreparedStatement s = connection.prepareStatement("INSERT OR IGNORE INTO ShowTimes(showtime_id, movie_id, hall_id, date) VALUES (2, 2, 2, ?) RETURNING *")){
                LocalDateTime now =  LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
                s.setString(1, now.toString());
                try(ResultSet res = s.executeQuery()){
                    if(res.next()) {
                        testShowTime2 = new ShowTime(res);
                        testShowTime2.setHall(testHall2);
                        testShowTime2.setMovie(testMovie2);
                        testShowTime2.setDate(now);
                    }
                }
            }
            int numSeats = 2;
            testBooking1 = new Booking(testShowTime1, new ArrayList<>(testSeats.subList(0, 2)));
            testBooking1.getSeats().forEach(s -> s.setBooked(true));
            for(int i = 1; i <= numSeats; i++) {
                try (PreparedStatement s = connection.prepareStatement("INSERT OR IGNORE INTO Bookings(showtime_id, seat_id, user_id, booking_number) VALUES (?, ?, ?, ?) RETURNING booking_number")) {
                    s.setInt(1, 1);
                    s.setInt(2, i);
                    s.setInt(3, 1);
                    s.setInt(4, 1);
                    try(ResultSet res = s.executeQuery()){
                        testBooking1.setBookingNumber(res);
                    }
                }
            }
            testUser1.setBookings(new ArrayList<>(List.of(testBooking1)));

            testBooking2 = new Booking(testShowTime2, new ArrayList<>(testSeats.subList(2, 4)));
            testBooking2.getSeats().forEach(s -> s.setBooked(true));
            for(int i = testSeats.size() - numSeats; i < testSeats.size(); i++) {
                try (PreparedStatement s = connection.prepareStatement("INSERT OR IGNORE INTO Bookings(showtime_id, seat_id, user_id, booking_number) VALUES (?, ?, ?, ?) RETURNING booking_number")) {
                    s.setInt(1, 2);
                    s.setInt(2, i);
                    s.setInt(3, 2);
                    s.setInt(4, 2);
                    try(ResultSet res = s.executeQuery()){
                        testBooking2.setBookingNumber(res);
                    }
                }
            }
            testUser2.setBookings(new ArrayList<>(List.of(testBooking2)));

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        return getConnection(DB_URL);
    }

    public static void tearDown(){
        try(Connection conn = getConnection(DB_URL)){
            try(PreparedStatement s = conn.prepareStatement("DROP TABLE IF EXISTS Bookings")){
                s.execute();
            }
            try(PreparedStatement s = conn.prepareStatement("DROP TABLE IF EXISTS ShowTimes")){
                s.execute();
            }
            try(PreparedStatement s = conn.prepareStatement("DROP TABLE IF EXISTS Seats")){
                s.execute();
            }
            try(PreparedStatement s = conn.prepareStatement("DROP TABLE IF EXISTS Halls")){
                s.execute();
            }
            try(PreparedStatement s = conn.prepareStatement("DROP TABLE IF EXISTS Cinemas")){
                s.execute();
            }
            try(PreparedStatement s = conn.prepareStatement("DROP TABLE IF EXISTS Movies")){
                s.execute();
            }
            try(PreparedStatement s = conn.prepareStatement("DROP TABLE IF EXISTS Users")){
                s.execute();
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Cinema getTestCinema1() {
        return testCinema1;
    }

    public static Booking getTestBooking1() {
        return testBooking1;
    }

    public static Hall getTestHall1() {
        return testHall1;
    }

    public static Movie getTestMovie1() {
        return testMovie1;
    }

    public static ArrayList<Seat> getTestSeats() {
        return testSeats;
    }

    public static ShowTime getTestShowTime1() {
        return testShowTime1;
    }

    public static User getTestUser1() {
        return testUser1;
    }

    public static Cinema getTestCinema2() {
        return testCinema2;
    }

    public static Booking getTestBooking2() {
        return testBooking2;
    }

    public static Hall getTestHall2() {
        return testHall2;
    }

    public static Movie getTestMovie2() {
        return testMovie2;
    }

    public static ShowTime getTestShowTime2() {
        return testShowTime2;
    }

    public static User getTestUser2() {
        return testUser2;
    }
}
