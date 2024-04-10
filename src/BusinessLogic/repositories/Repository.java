package BusinessLogic.repositories;

import Domain.DatabaseEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import utils.ThrowingSupplier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Repository {

    final protected boolean isQueryResultEmpty(@NotNull ResultSet res) throws SQLException {
        return !res.isBeforeFirst();
    }

    final protected <T extends DatabaseEntity, E extends Exception> @Nullable List<T> getList(ResultSet res, ThrowingSupplier<T, E> lambda) throws E, SQLException {
        if (isQueryResultEmpty(res))
            return null;
        List<T> t = new ArrayList<>();
        while (res.next()) {
            t.add(lambda.get());
        }
        return t;
    }



}
