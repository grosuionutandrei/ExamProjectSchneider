package easv.dal.regionDao;

import easv.be.*;
import easv.exception.RateException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface IRegionDAO {

    Integer addRegion(Region region, List<Country> countries) throws RateException;

    void addCountryToRegion(Integer regionID, List<Country> countries, Connection conn) throws SQLException;

    void updateRegion(Region region, List<Country> addedCountries, List<Country> removedCountries) throws RateException;

    boolean deleteRegion(Region region) throws RateException;
}
