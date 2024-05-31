package easv.dal.teamDao;

import easv.be.*;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.dal.regionDao.IRegionDAO;
import easv.exception.ErrorCode;
import easv.exception.RateException;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class TeamDao implements ITeamDao {
    private IConnection connectionManager;

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

    public TeamDao() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }

    @Override
    public boolean savePerformedDistribution(Map<Team, Map<RateType, BigDecimal>> receivedTeams, Team selectedTeamToDistributeFrom) throws RateException {

        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

            //set previous configurations to inactive
            setPreviousConfigurationsToInactive(conn, selectedTeamToDistributeFrom, receivedTeams.keySet());
            //insert new configuration
            Map<Integer, Integer> insertedTeamConfigurationId = insertTeamNewConfiguration(conn, selectedTeamToDistributeFrom, receivedTeams.keySet());
            // map team with configuration
            mapTeamWithConfiguration(conn, insertedTeamConfigurationId);
            //insert team employees in teamConfigurationHistory
            insertTeamConfigurationEmployees(conn, insertedTeamConfigurationId.get(selectedTeamToDistributeFrom.getId()), selectedTeamToDistributeFrom.getEmployees());
            //insert team  shared distribution
            BigDecimal sharedDayRate = receivedTeams.get(selectedTeamToDistributeFrom).get(RateType.DAY_RATE);
            BigDecimal sharedHourRate = receivedTeams.get(selectedTeamToDistributeFrom).get(RateType.HOUR_RATE);
            insertTeamSharedDistribution(conn, insertedTeamConfigurationId.get(selectedTeamToDistributeFrom.getId()), selectedTeamToDistributeFrom.getId(), sharedDayRate, sharedHourRate);
            //insert teams into received overhead
            insertReceivedOverhead(conn, insertedTeamConfigurationId.get(selectedTeamToDistributeFrom.getId()), selectedTeamToDistributeFrom.getId(), receivedTeams);

            conn.commit();
            return true;
        } catch (RateException | SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
                }
            }
        } finally {

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "error closing the database connection", e);
                }
            }
        }
        return true;
    }


    /**
     * save the  distribution resulted configuration , returns a map with the teamId and asociated resulted configuration id
     */
    private Map<Integer, Integer> insertTeamNewConfiguration(Connection conn, Team selectedTeam, Set<Team> receivedTeams) throws RateException {
        Map<Integer, Integer> teamConfigMap = new HashMap<>();
        String sql = "INSERT INTO TeamConfiguration (TeamDailyRate, TeamHourlyRate, GrossMargin, MarkupMultiplier, ConfigurationDate, Active) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement psmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Add the main team configuration
            psmt.setDouble(1, selectedTeam.getActiveConfiguration().getTeamDayRate().doubleValue());
            psmt.setDouble(2, selectedTeam.getActiveConfiguration().getTeamHourlyRate().doubleValue());
            psmt.setDouble(3, selectedTeam.getActiveConfiguration().getGrossMargin());
            psmt.setDouble(4, selectedTeam.getActiveConfiguration().getMarkupMultiplier());
            psmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            psmt.setString(6, "true");

            // Execute the main team configuration insert
            int affectedRows = psmt.executeUpdate();
            if (affectedRows == 0) {
                throw new RateException(ErrorCode.OPERATION_DB_FAILED);
            }

            // Retrieve the generated key for the main team configuration
            try (ResultSet res = psmt.getGeneratedKeys()) {
                if (res.next()) {
                    teamConfigMap.put(selectedTeam.getId(), res.getInt(1));
                    selectedTeam.getActiveConfiguration().setId(res.getInt(1));
                    selectedTeam.getTeamConfigurationsHistory().add(selectedTeam.getActiveConfiguration());
                } else {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }
            }

            // Insert configurations for each team in receivedTeams
            for (Team team : receivedTeams) {
                psmt.setDouble(1, team.getActiveConfiguration().getTeamDayRate().doubleValue());
                psmt.setDouble(2, team.getActiveConfiguration().getTeamHourlyRate().doubleValue());
                psmt.setDouble(3, team.getActiveConfiguration().getGrossMargin());
                psmt.setDouble(4, team.getActiveConfiguration().getMarkupMultiplier());
                psmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                psmt.setString(6, "true");
                affectedRows = psmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }

                // Retrieve the generated key for each team configuration
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        teamConfigMap.put(team.getId(), res.getInt(1));
                        team.getActiveConfiguration().setId(res.getInt(1));
                        team.getTeamConfigurationsHistory().add(team.getActiveConfiguration());
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
            }
        } catch (SQLException | RateException e) {
         throw new RateException(e.getMessage(),e,ErrorCode.OPERATION_DB_FAILED);
        }
        return teamConfigMap;
    }


    //set  teams old configuration  to inactive
    private boolean setPreviousConfigurationsToInactive(Connection conn, Team selectedTem, Set<Team> receivedTeams) throws RateException {
        String sql = "UPDATE TeamConfiguration set Active =? WHERE TeamConfigurationID=? ";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, "false");
            psmt.setInt(2, selectedTem.getActiveConfiguration().getId());
            psmt.addBatch();
            for (Team team : receivedTeams) {
                psmt.setString(1, "false");
                psmt.setInt(2, team.getActiveConfiguration().getId());
                psmt.addBatch();
            }
            int[] updatedRows = psmt.executeBatch();
            if (updatedRows.length == 0) {
                throw new RateException(ErrorCode.OPERATION_DB_FAILED);
            }
            return true;
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }


    }




    /**
     * save the resulted distribution configuration for the team
     */
    private boolean mapTeamWithConfiguration(Connection conn, Map<Integer, Integer> teamsConfigs) throws RateException {
        String sql = "INSERT INTO TeamConfigurationsHistory (TeamConfigurationID,TeamID) values(?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {

            for (Integer teamId : teamsConfigs.keySet()) {
                psmt.setInt(1, teamsConfigs.get(teamId));
                psmt.setInt(2, teamId);
                psmt.addBatch();
            }
            int[] updatedRows = psmt.executeBatch();
            if (updatedRows.length == 0) {
                throw new RateException(ErrorCode.OPERATION_DB_FAILED);
            }
            return true;
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }



    /**
     * insert team  configuration employees
     */
    private boolean insertTeamConfigurationEmployees(Connection conn, int configId, List<Employee> employees) throws RateException {
        String sql = "INSERT INTO TeamEmployeesHistory (EmployeeName,EmployeeDailyRate,EmployeeHourlyRate,TeamConfigurationId,Currency) values(?,?,?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Employee employee : employees) {
                psmt.setString(1, employee.getName());
                psmt.setDouble(2, employee.getActiveConfiguration().getDayRate().doubleValue());
                psmt.setDouble(3, employee.getActiveConfiguration().getHourlyRate().doubleValue());
                psmt.setInt(4, configId);
                psmt.setString(5, employee.getCurrency().name());
                psmt.addBatch();
            }
            psmt.executeBatch();
            return true;
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**
     * insert team shared distribution
     */

    private boolean insertTeamSharedDistribution(Connection conn, int configId, int selectedTeamId, BigDecimal sharedDay, BigDecimal sharedHour) throws RateException {
        String sql = "INSERT INTO TeamSharedDistribution (TeamConfigurationID,SharedTeamID,SharedDayOverhead,SharedHourOverhead)  values(?,?,?,?)";

        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, configId);
            psmt.setInt(2, selectedTeamId);
            psmt.setDouble(3, sharedDay.doubleValue());
            psmt.setDouble(4, sharedHour.doubleValue());
            psmt.addBatch();
            psmt.executeBatch();
            return true;
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }

    }


    /**
     * insert the received  overhead from the distribution operation
     */
    private boolean insertReceivedOverhead(Connection conn, int configId, int selectedTeamId, Map<Team, Map<RateType, BigDecimal>> receivedTeams) throws RateException {
        String sql = "INSERT INTO TeamReceivedDistribution(TeamConfigurationID,ReceivedTeamID,ReceivedDayOverhead,ReceivedHourOverhead) " +
                "VALUES (?,?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : receivedTeams.keySet()) {
                if (team.getId() != selectedTeamId) {
                    psmt.setInt(1, configId);
                    psmt.setInt(2, team.getId());
                    psmt.setDouble(3, receivedTeams.get(team).get(RateType.DAY_RATE).doubleValue());
                    psmt.setDouble(4, receivedTeams.get(team).get(RateType.HOUR_RATE).doubleValue());
                    psmt.addBatch();
                }

            }
            psmt.executeBatch();

            return true;

        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /** Saves the edited team information and performs necessary database operations such as updating team configurations and employee associations */
    @Override
    public Team saveEditOperationTeam(Team editedTeam, int idOriginalTeam, List<Employee> employeesToDelete, List<Employee> employees) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            int configurationID = addTeamConfigurationT(editedTeam, conn);
            editedTeam.getActiveConfiguration().setId(configurationID);
            editedTeam.getTeamConfigurationsHistory().add(editedTeam.getActiveConfiguration());
            addTeamToConfiguration(editedTeam, configurationID, conn);
            addEmployeeHistoryTeams(configurationID, employees, conn);
            deleteTeamEmployeeConnections(conn, employeesToDelete, editedTeam.getId());
            setOldConfigurationToInactiveTeams(idOriginalTeam, conn);
            addEmployeesToTeam(employees, editedTeam.getId(), conn);
            conn.commit();
            return editedTeam;
        } catch (RateException | SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "error in save edit team operation", e);
                    throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Failed to close the database connection", e);
                }
            }
        }
        return null;
    }
    /** Sets the old configuration of a team to inactive in the database */
    public void setOldConfigurationToInactiveTeams(int configurationId, Connection conn) throws RateException {
        String sql = "UPDATE  TeamConfiguration  Set Active =? where TeamConfigurationID=?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, "false");
            psmt.setInt(2, configurationId);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }
    /** Deletes the connections between the team and specified employees in the database */
    public void deleteTeamEmployeeConnections(Connection conn, List<Employee> employeesToDelete, int teamID) throws SQLException {
        String sql = "DELETE FROM TeamEmployee WHERE EmployeeID = ? AND TeamID = ?";
        System.out.println(employeesToDelete + "dao");
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Employee employee : employeesToDelete) {
                psmt.setInt(1, employee.getId());
                psmt.setInt(2,teamID);
                psmt.executeUpdate();
            }
        }
    }
    /** Adds employees to the team in the database */
    public void addEmployeesToTeam(List<Employee> employees, int teamId, Connection conn) throws RateException, SQLException {
        String checkSql = "SELECT COUNT(*) FROM TeamEmployee WHERE TeamID = ? AND EmployeeID = ?";
        String insertSql = "INSERT INTO TeamEmployee (TeamID, EmployeeID, UtilizationPercentage) VALUES (?, ?, ?)";

        try (PreparedStatement checkPsmt = conn.prepareStatement(checkSql);
             PreparedStatement insertPsmt = conn.prepareStatement(insertSql)) {

            for (Employee employee : employees) {
                /* Check if the connection already exists*/
                checkPsmt.setInt(1, teamId);
                checkPsmt.setInt(2, employee.getId());
                ResultSet rs = checkPsmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                rs.close();

                if (count == 0) {
                    /* Insert the new connection if it doesn't exist*/
                    insertPsmt.setInt(1, teamId);
                    insertPsmt.setInt(2, employee.getId());
                    insertPsmt.setBigDecimal(3, employee.getUtilPerTeams().get(teamId));
                    insertPsmt.addBatch();
                }
            }

            insertPsmt.executeBatch();
        }
    }
    /** Adds employees history in the team in the database */
    private void addEmployeeHistoryTeams(int teamConfigurationID, List<Employee> employees, Connection conn) throws RateException {
        String sql = "INSERT INTO TeamEmployeesHistory (EmployeeName, EmployeeDailyRate, EmployeeHourlyRate, TeamConfigurationId, Currency) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Employee employee : employees) {
                psmt.setString(1, employee.getName());
                psmt.setBigDecimal(2, employee.getTeamDailyRate());
                psmt.setBigDecimal(3, employee.getTeamHourlyRate());
                psmt.setInt(4, teamConfigurationID);
                psmt.setString(5, employee.getCurrency().name());
                psmt.addBatch();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            throw new RateException(ErrorCode.OPERATION_DB_FAILED);        }
    }
    /** Adds new team configuration in the database */
    public Integer addTeamConfigurationT(Team editedTeam, Connection conn) throws SQLException, RateException {
        String sql = "INSERT INTO TeamConfiguration (TeamDailyRate, TeamHourlyRate, GrossMargin, MarkupMultiplier, ConfigurationDate, Active) VALUES (?, ?, ?, ?, ?, ?)";

        Integer configurationID = null;
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            /* Retrieve the active configuration from the team*/
            TeamConfiguration teamConfiguration = editedTeam.getActiveConfiguration();

            psmt.setBigDecimal(1, teamConfiguration.getTeamDayRate());
            psmt.setBigDecimal(2, teamConfiguration.getTeamHourlyRate());
            psmt.setDouble(3, teamConfiguration.getGrossMargin());
            psmt.setDouble(4, teamConfiguration.getMarkupMultiplier());
            psmt.setTimestamp(5, Timestamp.valueOf(teamConfiguration.getSavedDate()));
            psmt.setString(6,"true");
            psmt.executeUpdate();

            try (ResultSet res = psmt.getGeneratedKeys()) {
                if (res.next()) {
                    configurationID = res.getInt(1);

                } else {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }

            }
        }
        return configurationID;
    }

    /** Connect team with configuration in the database */

    private void addTeamToConfiguration(Team team, int teamConfigurationID, Connection conn) {
        String sql = "INSERT INTO TeamConfigurationsHistory (TeamConfigurationID, TeamID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, teamConfigurationID);
            psmt.setInt(2, team.getId());
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
