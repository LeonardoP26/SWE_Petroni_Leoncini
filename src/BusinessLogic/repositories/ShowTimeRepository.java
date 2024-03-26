package BusinessLogic.repositories;

import BusinessLogic.UnableToOpenDatabaseException;
import Domain.ShowTime;
import daos.ShowTimeDao;
import daos.ShowTimeDaoInterface;
import org.jetbrains.annotations.NotNull;
import java.sql.SQLException;

public class ShowTimeRepository implements ShowTimeRepositoryInterface {

    private final static ShowTimeDaoInterface dao = ShowTimeDao.getInstance();
    private static ShowTimeRepositoryInterface instance = null;

    public static ShowTimeRepositoryInterface getInstance(){
        if(instance == null)
            instance = new ShowTimeRepository();
        return instance;
    }

    private ShowTimeRepository() { }


    @Override
    public void insert(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        dao.insert(showTime);
    }

}
