package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDateTime;

public class ShowTimeDao implements ShowTimeDaoInterface {

    private static ShowTimeDaoInterface instance = null;

    public static ShowTimeDaoInterface getInstance(){
        if(instance == null)
            instance = new ShowTimeDao();
        return instance;
    }

    private ShowTimeDao() { }

    @Override
    public ResultSet insert(int movieId, int hallId, LocalDateTime date) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO ShowTimes(movie_id, hall_id, date) VALUES (?, ?, ?)"
        );
        s.setInt(1, movieId);
        s.setInt(2, hallId);
        s.setString(3, date.toString());
        s.executeUpdate();
        PreparedStatement getId = conn.prepareStatement("SELECT last_insert_rowid()");
        return getId.executeQuery();
    }

    @Override
    public boolean update(int showTimeId, int movieId, int hallId) throws SQLException, UnableToOpenDatabaseException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "UPDATE ShowTimes SET movie_id = ?, hall_id = ? WHERE showtime_id = ?"
        )) {
            s.setInt(1, movieId);
            s.setInt(2, hallId);
            s.setInt(3, showTimeId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int showTimeId) throws SQLException, UnableToOpenDatabaseException {
        try (PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "DELETE FROM ShowTimes WHERE showtime_id = ?"
        )) {
            s.setInt(1, showTimeId);
            return s.executeUpdate() > 0;
        }
    }

    @Override
    public ResultSet get(int showTimeId) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM ShowTimes WHERE showtime_id = ?"
        );
        s.setInt(1, showTimeId);
        return s.executeQuery();
    }

    @Override
    public ResultSet get(@NotNull Movie movie) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "SELECT * FROM ShowTimes JOIN Halls on ShowTimes.hall_id = Halls.hall_id WHERE movie_id = ?"
        );
        s.setInt(1, movie.getId());
        return s.executeQuery();
    }

    @Override
    public boolean insertShowTimeSeat(int showTimeId, int seatId) throws SQLException, UnableToOpenDatabaseException {
        try(PreparedStatement s = CinemaDatabase.getConnection().prepareStatement(
                "INSERT OR ROLLBACK INTO ShowTimeSeats(showtime_id, seat_id, booking_number) VALUES (?, ?, 0)"
        )) {
            s.setInt(1, showTimeId);
            s.setInt(2, seatId);
            return s.executeUpdate() > 0;
        }
    }


    @Override
    public boolean updateShowTimeSeat(int showTimeId, int seatId, int bookingNumber) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "UPDATE ShowTimeSeats SET booking_number = ? WHERE showtime_id = ? AND seat_id = ?"
        );
        s.setInt(1, bookingNumber);
        s.setInt(2, showTimeId);
        s.setInt(3, seatId);
        return s.executeUpdate() > 0;

    }

}
