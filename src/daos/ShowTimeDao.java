package daos;

import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import Domain.ShowTime;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowTimeDao implements ShowTimeDaoInterface {

    private static ShowTimeDaoInterface instance = null;

    public static ShowTimeDaoInterface getInstance(){
        if(instance == null)
            instance = new ShowTimeDao();
        return instance;
    }

    private ShowTimeDao() { }

    @Override
    public void insert(@NotNull ShowTime showTime) throws SQLException, UnableToOpenDatabaseException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO ShowTimes(id, movieId, hallId, date) VALUES (?, ?, ?, ?)"
        );
        s.setInt(1, showTime.getId());
        s.setInt(2, showTime.getMovieId());
        s.setInt(3, showTime.getHallId());
        s.setString(4, showTime.getDate().toString());
        s.executeUpdate();
    }

}
