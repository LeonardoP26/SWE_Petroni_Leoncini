package daos;

import Domain.Schedule;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface ScheduleDaoInterface {

    void insert(@NotNull Schedule schedule) throws SQLException;

}
