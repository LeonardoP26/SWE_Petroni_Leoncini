package BusinessLogic;

import java.sql.SQLException;

public interface Observer {

    void update(Subject subject) throws SQLException;

}
