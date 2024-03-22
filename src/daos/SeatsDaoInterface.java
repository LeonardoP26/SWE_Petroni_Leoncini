package daos;

import Domain.Hall;
import Domain.Seat;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface SeatsDaoInterface {

    void insert(@NotNull Seat seat) throws SQLException;

}
