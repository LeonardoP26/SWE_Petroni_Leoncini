package daos;

import BusinessLogic.CinemaDatabase;
import Domain.Schedule;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ScheduleDao implements ScheduleDaoInterface {

    @Override
    public void insert(@NotNull Schedule schedule) throws SQLException {
        Connection conn = CinemaDatabase.getConnection();
        PreparedStatement s = conn.prepareStatement(
                "INSERT OR IGNORE INTO Schedules(id, movieId, hallId, date) VALUES (?, ?, ?, ?)"
        );
        s.setInt(1, schedule.getId());
        s.setInt(2, schedule.getMovie().getId());
        s.setInt(3, schedule.getHall().getId());
        s.setString(4, schedule.getDate().toString());
        s.executeUpdate();
    }

}
