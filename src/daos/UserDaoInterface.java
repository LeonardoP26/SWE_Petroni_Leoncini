package daos;

import Domain.User;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface UserDaoInterface {

    void insert(@NotNull User user) throws SQLException;

}
