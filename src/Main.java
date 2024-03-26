import BusinessLogic.CinemaDatabase;
import BusinessLogic.UnableToOpenDatabaseException;
import BusinessLogic.repositories.UserRepository;
import Domain.*;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        try {
            CinemaDatabase.getConnection();
        } catch (SQLException | UnableToOpenDatabaseException e) {
            throw new RuntimeException(e);
        }

    }
}