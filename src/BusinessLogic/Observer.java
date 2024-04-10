package BusinessLogic;

import BusinessLogic.exceptions.UnableToOpenDatabaseException;

import java.sql.SQLException;

public interface Observer {

    void update(Subject subject) throws SQLException, UnableToOpenDatabaseException;

}
