package daos;

import Domain.Hall;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface HallDaoInterface {

    void insert(@NotNull Hall hall) throws SQLException;

}
