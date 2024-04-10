package BusinessLogic.repositories;

import BusinessLogic.exceptions.DatabaseInsertionFailedException;
import BusinessLogic.exceptions.UnableToOpenDatabaseException;
import Domain.Cinema;
import Domain.Movie;
import daos.CinemaDao;
import daos.CinemaDaoInterface;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CinemaRepository extends Repository implements CinemaRepositoryInterface {

    private final CinemaDaoInterface dao = CinemaDao.getInstance();

    private static CinemaRepositoryInterface instance = null;
    public static CinemaRepositoryInterface getInstance(){
        if(instance == null)
            instance = new CinemaRepository();
        return instance;
    }

    private CinemaRepository() { }

    @Override
    public int insert(Cinema cinema) throws SQLException, UnableToOpenDatabaseException, DatabaseInsertionFailedException {
        try (ResultSet res = dao.insert(cinema.getName())) {
            if(res.next())
                return res.getInt(1);
            throw new DatabaseInsertionFailedException("Database insertion failed: Record already present");
        }
    }

    @Override
    public boolean update(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        return dao.update(cinema.getId(), cinema.getName());
    }

    @Override
    public boolean delete(@NotNull Cinema cinema) throws SQLException, UnableToOpenDatabaseException {
        return dao.delete(cinema.getId());
    }

    @Override
    public Cinema get(int cinemaId) throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get(cinemaId)){
            if(res.next())
                return new Cinema(res);
            return null;
        }
    }

    @Override
    public List<Cinema> get() throws SQLException, UnableToOpenDatabaseException {
        try(ResultSet res = dao.get()){
            return getList(res, () -> new Cinema(res));
        }
    }


}
