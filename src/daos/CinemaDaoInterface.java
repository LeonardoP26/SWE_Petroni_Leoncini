package daos;

import Domain.Cinema;

import java.sql.SQLException;

public interface CinemaDaoInterface {

    void insert(Cinema cinema) throws SQLException;

}
