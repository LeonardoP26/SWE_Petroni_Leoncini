package daos;

import domain.DatabaseEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import utils.ThrowingFunction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Dao {

    default boolean isQueryResultEmpty(@NotNull ResultSet res) {
        try{
            return !res.isBeforeFirst();
        } catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }

    default <T extends DatabaseEntity> @Nullable List<T> getList(@NotNull ResultSet res, @NotNull ThrowingFunction<List<T>, T> lambda) throws Exception {
        if (isQueryResultEmpty(res))
            return null;
        List<T> t = new ArrayList<>();
        try {
            while (res.next()) {
                t.add(lambda.apply(t));
            }
            return t;
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


}
