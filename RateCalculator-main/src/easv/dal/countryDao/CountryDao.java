package easv.dal.countryDao;

import easv.be.Country;
import easv.be.Team;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.exception.ErrorCode;
import easv.exception.RateException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountryDao implements ICountryDao {
    private IConnection connectionManager;

    public CountryDao() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
    }


    /**
     * Adds a new country and associates it with the specified teams.
     *
     * @param country      The country to add.
     * @param teams        The list of existing teams to associate with the country.
     * @param newTeams     The list of new teams to add and associate with the country.
     * @param teamsToUpdate The list of teams to update.
     * @return The ID of the added country.
     */
    @Override
    public Integer addCountry(Country country, List<Team> teams, List<Team> newTeams, List<Team> teamsToUpdate) throws RateException {
        Integer countryID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Countries (CountryName) VALUES (?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, country.getCountryName());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        countryID = res.getInt(1);
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
                if(!teamsToUpdate.isEmpty()){
                    updateTeams(teamsToUpdate, conn);
                }
                if(!newTeams.isEmpty()){
                    List<Integer> teamsIds = addTeams(newTeams, conn);
                    addNewTeamsToCountry(teamsIds, countryID, conn);
                }

                if(!teams.isEmpty()){
                    addTeamToCountry(countryID, teams, conn);
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
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
        return countryID;
    }

    /**
     * Updates the specified list of teams in the database.
     *
     * @param teamsToUpdate The list of teams to update.
     * @param conn          The database connection to use.
     */
    private void updateTeams(List<Team> teamsToUpdate, Connection conn) throws RateException {
        String sql = "UPDATE Teams SET TeamName = ?, TeamCurrency = ? WHERE TeamID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : teamsToUpdate) {
                psmt.setString(1, team.getTeamName());
                psmt.setString(2, team.getCurrency().name());
                psmt.setInt(3, team.getId());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**
     * Associates a list of new team IDs with a specified country in the database.
     *
     * @param newTeamsIds The list of new team IDs to associate.
     * @param countryID   The ID of the country to associate the teams with.
     * @param conn        The database connection to use.
     */
    @Override
    public void addNewTeamsToCountry(List<Integer> newTeamsIds, Integer countryID, Connection conn) throws RateException {
        if (conn == null) {
            conn = connectionManager.getConnection();
        }
        String sql = "INSERT INTO CountryTeam (CountryID, TeamID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Integer teamID : newTeamsIds) {
                psmt.setInt(1, countryID);
                psmt.setInt(2, teamID);
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**
     * Associates a list of existing teams with a specified country in the database.
     *
     * @param countryID The ID of the country to associate the teams with.
     * @param teams     The list of teams to associate.
     * @param conn      The database connection to use.
     */
    @Override
    public void addTeamToCountry(Integer countryID, List<Team> teams, Connection conn) throws RateException {
        String sql = "INSERT INTO CountryTeam (CountryID, TeamID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : teams) {
                psmt.setInt(1, countryID);
                psmt.setInt(2, team.getId());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(),e,ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**
     * Adds a list of new teams to the database and returns their generated IDs.
     *
     * @param teams The list of new teams to add.
     * @param conn  The database connection to use.
     * @return A list of generated team IDs.
     */
    @Override
    public List<Integer> addTeams(List<Team> teams, Connection conn) throws RateException {
        if (conn == null) {
            conn = connectionManager.getConnection();
        }
        List<Integer> teamIds = new ArrayList<>();
        String sql = "INSERT INTO Teams (TeamName, TeamCurrency) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (Team team : teams) {
                psmt.setString(1, team.getTeamName());
                psmt.setString(2, team.getCurrency().name());
                psmt.executeUpdate();
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        int id = res.getInt(1);
                        teamIds.add(id);
                        team.setId(id);
                    } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(),e,ErrorCode.OPERATION_DB_FAILED);
        }
        return teamIds;
    }

    /**
     * Updates an existing country and its associated teams in the database.
     *
     * @param country       The country to update.
     * @param teamsToAdd    The list of teams to add.
     * @param teamsToRemove The list of teams to remove.
     * @param newTeams      The list of new teams to add.
     * @param teamsToUpdate The list of teams to update.
     */
    @Override
    public void updateCountry(Country country, List<Team> teamsToAdd, List<Team> teamsToRemove, List<Team> newTeams, List<Team> teamsToUpdate) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "UPDATE Countries SET CountryName = ? WHERE CountryID = ?";
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setString(1, country.getCountryName());
                psmt.setInt(2, country.getId());
                psmt.executeUpdate();

                if(!teamsToUpdate.isEmpty()){
                    updateTeams(teamsToUpdate, conn);
                }

                if(!newTeams.isEmpty()){
                    List<Integer> teamsIds = addTeams(newTeams, conn);
                    addNewTeamsToCountry(teamsIds, country.getId(), conn);
                }

                if(!teamsToAdd.isEmpty()){
                    addTeamToCountry(country.getId(), teamsToAdd, conn);
                }

                if(!teamsToRemove.isEmpty()){
                    removeTeamFromCountry(country.getId(), teamsToRemove, conn);
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
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
    }

    private void removeTeamFromCountry(int countryID, List<Team> removedTeams, Connection conn) {
        String sql = "DELETE FROM CountryTeam WHERE CountryID = ? AND TeamID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : removedTeams) {
                psmt.setInt(1, countryID);
                psmt.setInt(2, team.getId());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean deleteCountry(Country country) throws RateException {
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            String sql = "DELETE FROM Countries WHERE CountryID = ?";
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setInt(1, country.getId());
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
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
            }
        }
        return true;
    }

    @Override
    public boolean deleteTeam(Team team) throws RateException {
        String sql = "DELETE FROM Teams WHERE TeamID = ?";
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, team.getId());
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);
        }
        return true;
    }

}

