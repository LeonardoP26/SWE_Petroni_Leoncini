package daos;

import Domain.Booking;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface BookingDaoInterface {

    void insert(@NotNull Booking booking) throws SQLException;

}
