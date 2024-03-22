package daos;

import Domain.Movie;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface MovieDaoInterface {

    void insert(@NotNull Movie movie) throws SQLException;

}
