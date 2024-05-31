package easv.dal.regionDao;

import easv.be.Country;
import easv.be.Region;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RegionDAO implements IRegionDAO{

    private final IConnection connectionManager;
    private static final Logger LOGGER = Logger.getLogger(IRegionDAO.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("application.log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            System.out.println("Logger could not be created");
        }
    }


    public RegionDAO() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }

    /**
     * Adds a new region to the database and associates it with the specified countries.
     *
     * @param region the Region object to add.
     * @param countries a list of Country objects to associate with the region.
     * @return the ID of the newly added region.
     */
    @Override
    public Integer addRegion(Region region, List<Country> countries) throws RateException {
        Integer regionID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Region (RegionName) VALUES (?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, region.getRegionName());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        regionID = res.getInt(1);
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
                if(!countries.isEmpty()){
                    addCountryToRegion(regionID, countries, conn);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "error closing the database connection", e);
            }
        }
        return regionID;
    }

    /**
     * Adds countries to a specified region in the RegionCountry table.
     *
     * @param regionID the ID of the region to associate the countries with.
     * @param countries a list of Country objects to add to the region.
     * @param conn the database connection to use.
     */
    @Override
    public void addCountryToRegion(Integer regionID, List<Country> countries, Connection conn) throws SQLException {
        String sql = "INSERT INTO RegionCountry (RegionID, CountryID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Country country : countries) {
                psmt.setInt(1, regionID);
                psmt.setInt(2, country.getId());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        }
    }

    /**
     * Updates the details of a region and modifies its associated countries.
     *
     * @param region the Region object with updated details.
     * @param addedCountries a list of Country objects to associate with the region.
     * @param removedCountries a list of Country objects to disassociate from the region.
     */
    @Override
    public void updateRegion(Region region, List<Country> addedCountries, List<Country> removedCountries) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "UPDATE Region SET RegionName = ? WHERE RegionID = ?";
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setString(1, region.getRegionName());
                psmt.setInt(2, region.getId());
                psmt.executeUpdate();

                if(!addedCountries.isEmpty()){
                    addCountryToRegion(region.getId(), addedCountries, conn);
                }
                if(!removedCountries.isEmpty()){
                    removeCountryFromRegion(region.getId(), removedCountries, conn);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {

                LOGGER.log(Level.SEVERE, "error closing the database onnection", e);
            }
        }
    }

    /**
     * Removes countries from a specified region in the RegionCountry table.
     *
     * @param regionID the ID of the region to disassociate the countries from.
     * @param removedCountries a list of Country objects to remove from the region.
     * @param conn the database connection to use.
     */
    private void removeCountryFromRegion(int regionID, List<Country> removedCountries, Connection conn) throws RateException {
        String sql = "DELETE FROM RegionCountry WHERE RegionID = ? AND CountryID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Country country : removedCountries) {
                psmt.setInt(1, regionID);
                psmt.setInt(2, country.getId());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
          throw  new RateException(e.getMessage(),e,ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**
     * Deletes a region from the database.
     *
     * @param region the Region object to delete.
     * @return true if the deletion was successful, false otherwise.
     */
    @Override
    public boolean deleteRegion(Region region) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            String sql = "DELETE FROM Region WHERE RegionID = ?";
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setInt(1, region.getId());
                psmt.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "error closing the database connection", e);
            }
        }
        return true;
    }

}
