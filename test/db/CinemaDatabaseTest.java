package db;

import business_logic.CinemaDatabase;
import business_logic.HallFactory;
import domain.*;
import utils.ThrowingFunction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CinemaDatabaseTest extends CinemaDatabase{

    public final static String DB_URL = "jdbc:sqlite:./test/db/test.sqlite";
    private static Cinema testCinema;
    private static Booking testBooking;
    private static Hall testHall;
    private static Movie testMovie;
    private static ArrayList<Seat> testSeats;
    private static ShowTime testShowTime;
    private static User testUser;

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
                            "INSERT OR IGNORE INTO Movies(movie_id, movie_name, duration) VALUES (1, 'movie1', 90) RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next())
                    testMovie = new Movie(res);
            }
            try (
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT OR IGNORE INTO Cinemas(cinema_id, cinema_name) VALUES (1, 'cinema1') RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next())
                    testCinema = new Cinema(res);

            }
            try(
                    PreparedStatement s = connection.prepareStatement(
                            "INSERT INTO Users(user_id, username, password, balance) VALUES (1, 'user1', 'user1', 100) RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ) {
                if(res.next())
                    testUser = new User(res);
            }
            try(
                    PreparedStatement s = connection.prepareStatement(
                    "INSERT OR IGNORE INTO Halls(hall_id, hall_number, cinema_id, type) VALUES (1, 1, 1, 'STANDARD') RETURNING *"
                    );
                    ResultSet res = s.executeQuery()
            ){
                if(res.next()) {
                    testHall = HallFactory.createHall(res);
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
                    testSeats.add(new Seat(res));
                }
                testHall.setSeats(testSeats);
            }
            try(PreparedStatement s = connection.prepareStatement("INSERT OR IGNORE INTO ShowTimes(showtime_id, movie_id, hall_id, date) VALUES (1, 1, 1, ?) RETURNING *")){
                LocalDateTime now =  LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
                s.setString(1, now.toString());
                try(ResultSet res = s.executeQuery()){
                    if(res.next()) {
                        testShowTime = new ShowTime(res);
                        testShowTime.setCinema(testCinema);
                        testShowTime.setHall(testHall);
                        testShowTime.setMovie(testMovie);
                        testShowTime.setDate(now);
                    }
                }
            }
            int numSeats = 3;
            testBooking = new Booking(testShowTime, new ArrayList<>(testSeats.stream().filter(s -> s.getId() <= numSeats).toList()));
            testBooking.getSeats().forEach(s -> s.setBooked(true));
            for(int i = 1; i <= numSeats; i++) {
                try (PreparedStatement s = connection.prepareStatement("INSERT OR IGNORE INTO Bookings(showtime_id, seat_id, user_id, booking_number) VALUES (?, ?, ?, ?) RETURNING booking_number")) {
                    s.setInt(1, 1);
                    s.setInt(2, i);
                    s.setInt(3, 1);
                    s.setInt(4, 1);
                    try(ResultSet res = s.executeQuery()){
                        testBooking.setBookingNumber(res);
                    }
                }
            }
            testUser.setBookings(new ArrayList<>(List.of(testBooking)));

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

    public static Cinema getTestCinema() {
        return testCinema;
    }

    public static Booking getTestBooking() {
        return testBooking;
    }

    public static Hall getTestHall() {
        return testHall;
    }

    public static Movie getTestMovie() {
        return testMovie;
    }

    public static ArrayList<Seat> getTestSeats() {
        return testSeats;
    }

    public static ShowTime getTestShowTime() {
        return testShowTime;
    }

    public static User getTestUser() {
        return testUser;
    }
}
