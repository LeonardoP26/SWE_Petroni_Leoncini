import BusinessLogic.CinemaDatabase;
import Domain.*;
import daos.*;

import java.awt.print.Book;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {

        CinemaDao cd = new CinemaDao();
        HallDao hd = new HallDao();
        MovieDao md = new MovieDao();
        ScheduleDao scd = new ScheduleDao();
        SeatsDao sed = new SeatsDao();
        BookingDao bd = new BookingDao();
        UserDao ud = new UserDao();

        try{
            Cinema c = new Cinema(1);
            cd.insert(c);
            Hall h = new Hall(1, c);
            hd.insert(h);
            ArrayList<Character> characters = new ArrayList<>();
            ArrayList<Seat> seats = new ArrayList<>();
            for(char r = 'a'; r < 'g'; r++){
                characters.add(r);
            }
            int id = 0;
            for (Character character : characters) {
                for (int n = 0; n <= 9; n++) {
                    Seat s = new Seat(id, character, n + 1, h);
                    seats.add(s);
                    sed.insert(s);
                    id++;
                }
            }
            Movie m = new Movie(1, "Kung Fu Panda 4", Duration.of(90, ChronoUnit.MINUTES));
            md.insert(m);
            Schedule s = new Schedule(1, m, h, LocalDateTime.of(2024, 4, 10, 21, 30));
            scd.insert(s);
            User u1 = new User(1, "nicco", 30);
            User u2 = new User(2, "leo", 40);
            ud.insert(u1);
            ud.insert(u2);
            Booking b = new Booking(1, s, seats.stream().filter(seat ->
                    seat.getRow() == 'e' && seat.getNumber() == 5
                            || seat.getRow() == 'e' && seat.getNumber() == 6
                            || seat.getRow() == 'e' && seat.getNumber() == 7
            ).toList(), List.of(u1, u2));
            bd.insert(b);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}