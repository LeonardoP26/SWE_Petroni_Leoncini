package daos;

import business_logic.exceptions.DatabaseFailedException;
import domain.Movie;
import domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ShowTimeDao extends Dao {

    void insert(@NotNull ShowTime showTime) throws DatabaseFailedException;

    void update(@NotNull ShowTime showTime) throws DatabaseFailedException;

    void delete(@NotNull ShowTime showTime) throws DatabaseFailedException;

    ShowTime get(int showTimeId);

    List<ShowTime> get(@NotNull Movie movie);

//    boolean insertShowTimeSeat(int showTimeId, int seatId) throws SQLException;
//
//    boolean updateShowTimeSeat(int showTimeId, int seatId, int bookingNumber) throws SQLException;
}
