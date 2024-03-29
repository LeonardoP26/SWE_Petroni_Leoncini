package BusinessLogic.repositories;

import Domain.DatabaseEntity;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Repository {

    protected boolean isQueryResultEmpty(ResultSet res) throws SQLException {
        return !res.isBeforeFirst();
    }

    protected <T extends DatabaseEntity> List<T> getList(ResultSet res, Class<T> klass) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(isQueryResultEmpty(res))
            return null;
        List<T> list = new ArrayList<>();
        while(res.next()){
            T e = klass.getConstructor(ResultSet.class).newInstance(res);
            list.add(e);
        }
        return list;
    }


}
