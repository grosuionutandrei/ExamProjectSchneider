package easv.dal.employeeDao;

import easv.be.*;
import easv.be.Country;
import easv.be.Currency;
import easv.be.Employee;
import easv.be.EmployeeType;
import easv.be.Team;
import easv.dal.connectionManagement.DatabaseConnectionFactory;
import easv.dal.connectionManagement.IConnection;
import easv.dal.teamDao.TeamDao;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;


public class EmployeesDAO implements IEmployeeDAO {
    private final IConnection connectionManager;
    private TeamDao teamDao;
    private static final Logger LOGGER = Logger.getLogger(EmployeesDAO.class.getName());

    private static final String CLOSE_CONN = "Failed to close the database connection";

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


    public EmployeesDAO() throws RateException {
        this.connectionManager = DatabaseConnectionFactory.getConnection(DatabaseConnectionFactory.DatabaseType.SCHOOL_MSSQL);
        this.teamDao= new TeamDao();
    }


    /**
     * Retrieves all employees and puts them in a map
     * Retrieves all the employee info like employee teams, configurations and countries and sets them in the lists in employee
     * Finds active configuration and set it in employee
     */
    @Override
    public Map<Integer, Employee> returnEmployees() throws RateException {
        Map<Integer, Employee> employees = new HashMap<>();
        String sql = "SELECT * FROM Employees";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false); // Start transaction
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet res = psmt.executeQuery();
                while (res.next()) {
                    int employeeID = res.getInt("EmployeeID");
                    String name = res.getString("Name");
                    String employeeType = res.getString("EmployeeType");
                    String currency1 = res.getString("Currency");
                    /* Retrieve employee type as string*/
                    EmployeeType type = EmployeeType.valueOf(employeeType);
                    /* Retrieve employee type as string*/
                    Currency currency = Currency.valueOf(currency1);
                    Employee employee = new Employee(name, type, currency);
                    employee.setId(employeeID);
                    /* Add Employee to HashMap*/
                    employees.put(employeeID, employee);
                }
            }
            /* Retrieve teams for employees*/
            for (Employee employee : employees.values()) {
                List<Team> teams = retrieveTeamsForEmployee(employee.getId(), conn);
                employee.setTeams(teams);
            }
            /* Retrieve countries for employees from teams*/
            for (Employee employee : employees.values()) {
                List<Country> countries = new ArrayList<>();
                for (Team team : employee.getTeams()) {
                    countries.addAll(retrieveCountriesForEmployee(team.getId(), conn));
                }
                employee.setCountries(countries);
            }
            /* Retrieve regions for employees from countries*/
            for (Employee employee : employees.values()) {
                List<Region> regions = new ArrayList<>();
                for (Country country : employee.getCountries()) {
                    regions.addAll(retrieveRegionsForEmployee(country.getId(), conn));
                }
                employee.setRegions(regions);
            }
            /* Retrieve configurations for employees*/
            for (Employee employee : employees.values()) {
                List<Configuration> configurations = retrieveConfigurationsForEmployee(employee, conn);
                employee.setConfigurations(configurations);
            }
            conn.commit();

            } catch (SQLException e) {
                throw new RateException(e.getMessage(), e.getCause(), ErrorCode.OPERATION_DB_FAILED);

            }
        return employees;
    }

    /**
     * Retrieves the teams for employee
     */
    private List<Team> retrieveTeamsForEmployee(int employeeId, Connection conn) throws SQLException {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.TeamID, t.TeamName " +
                "FROM TeamEmployee te " +
                "JOIN Teams t ON te.TeamID = t.TeamID " +
                "WHERE te.EmployeeID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, employeeId);
            ResultSet res = psmt.executeQuery();
            while (res.next()) {
                int teamID = res.getInt("TeamID");
                String teamName = res.getString("TeamName");
                Team team = new Team(teamName, teamID);
                teams.add(team);
            }
        }
        return teams;
    }

    /**
     * Retrieves the countries for employee by using team id
     */
    private List<Country> retrieveCountriesForEmployee(int teamId, Connection conn) throws SQLException {
        List<Country> countries = new ArrayList<>();
        String sql = "SELECT c.CountryID, c.CountryName " +
                "FROM CountryTeam ct " +
                "JOIN Countries c ON ct.CountryID = c.CountryID " +
                "WHERE ct.TeamID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, teamId);
            ResultSet res = psmt.executeQuery();
            while (res.next()) {
                int countryID = res.getInt("CountryID");
                String countryName = res.getString("CountryName");
                Country country = new Country(countryName, countryID);
                countries.add(country);
            }
        }
        return countries;

    }


    /**
     * Retrieves the regions for employee by using country id
     */
    private List<Region> retrieveRegionsForEmployee(int countryId, Connection conn) throws SQLException {
        List<Region> regions = new ArrayList<>();
        String sql = "SELECT r.RegionID, r.RegionName " +
                "FROM RegionCountry rc " +
                "JOIN Region r ON rc.RegionID = r.RegionID " +
                "WHERE rc.CountryID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, countryId);
            ResultSet res = psmt.executeQuery();
            while (res.next()) {
                int regionID = res.getInt("RegionID");
                String regionName = res.getString("RegionName");

                Region region = new Region(regionName, regionID);
                regions.add(region);
            }
        }
        return regions;

    }

    /**
     * Retrieves the configurations for employee and sets active configuration
     */
    private List<Configuration> retrieveConfigurationsForEmployee(Employee employee, Connection conn) throws SQLException {
        List<Configuration> configurations = new ArrayList<>();
        String sql = "SELECT " +
                "conf.ConfigurationID, conf.AnnualSalary, conf.FixedAnnualAmount, " +
                "conf.OverheadMultiplier, conf.UtilizationPercentage, conf.WorkingHours, " +
                "conf.Date AS ConfigurationDate, conf.Active, conf.DayRate,conf.HourlyRate,conf.DayWorkingHours " +
                "FROM " +
                "EmployeeConfigurations ec " +
                "INNER JOIN Configurations conf ON ec.ConfigurationID = conf.ConfigurationID " +
                "WHERE " +
                "ec.EmployeeID = ?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, employee.getId());
            ResultSet res = psmt.executeQuery();
            while (res.next()) {
                int configurationId = res.getInt("ConfigurationID");
                BigDecimal annualSalary = res.getBigDecimal("AnnualSalary");
                BigDecimal fixedAnnualAmount = res.getBigDecimal("FixedAnnualAmount");
                BigDecimal overheadMultiplier = res.getBigDecimal("OverheadMultiplier");
                BigDecimal workingHours = res.getBigDecimal("WorkingHours");
                LocalDateTime configurationDate = res.getTimestamp("ConfigurationDate").toLocalDateTime();
                boolean active = Boolean.parseBoolean(res.getString("Active"));
                BigDecimal dayRate = res.getBigDecimal("DayRate");
                BigDecimal hourlyRate = res.getBigDecimal("HourlyRate");
                int dayWorkingHours = res.getInt("DayWorkingHours");
                BigDecimal utilizationPercentage = res.getBigDecimal("UtilizationPercentage");
                Configuration configuration = new Configuration(configurationId, annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, configurationDate, active, dayRate, hourlyRate, dayWorkingHours);
                if (configuration.isActive()) {
                    employee.setActiveConfiguration(configuration);
                }

                configurations.add(configuration);
            }
        }
        return configurations;
    }


    @Override
    public Integer addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException {
        Integer employeeID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            // Insert employee data
            String sql = "INSERT INTO dbo.Employees (Name, EmployeeType, Currency) VALUES (?, ?, ?)";
            try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psmt.setString(1, employee.getName());
                psmt.setString(2, employee.getEmployeeType().toString());
                psmt.setString(3, employee.getCurrency().name());
                psmt.executeUpdate();
                // Retrieve generated employee ID
                try (ResultSet res = psmt.getGeneratedKeys()) {
                    if (res.next()) {
                        employeeID = res.getInt(1);
                                        } else {
                        throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                    }
                }
                // Add configuration if provided
                if (configuration != null) {
                    Integer configurationID = addConfiguration(configuration, conn);
                    if (configurationID != null) {
                        addEmployeeConfiguration(employeeID, configurationID, conn);
                    }
                }
                // Add employee to teams if provided
                if (!teams.isEmpty()) {
                    addEmployeeToTeam(employeeID, teams, conn);
                }
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
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
         return employeeID;
    }



    /**
     * set the newest configuration to be the active one
     */
    @Override
    public Integer addConfiguration(Configuration configuration, Connection conn) throws RateException, SQLException {
        Integer configurationID = null;
        String sql = "INSERT INTO Configurations (AnnualSalary, FixedAnnualAmount, OverheadMultiplier, WorkingHours, Date, Active, DayRate, HourlyRate, DayWorkingHours, UtilizationPercentage) VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psmt.setBigDecimal(1, configuration.getAnnualSalary());
            psmt.setBigDecimal(2, configuration.getFixedAnnualAmount());
            psmt.setBigDecimal(3, configuration.getOverheadMultiplier());
            psmt.setBigDecimal(4, configuration.getWorkingHours());
            psmt.setTimestamp(5, Timestamp.valueOf(configuration.getSavedDate()));
            psmt.setString(6, String.valueOf(configuration.isActive()));
            psmt.setBigDecimal(7, configuration.getDayRate());
            psmt.setBigDecimal(8, configuration.getHourlyRate());
            psmt.setDouble(9, configuration.getDayWorkingHours());
            psmt.setBigDecimal(10, configuration.getUtilizationPercentage());
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

    /**
     * Links an employee to a configuration in the database
     */
    @Override
    public void addEmployeeConfiguration(int employeeID, int configurationID, Connection conn) throws RateException, SQLException {
        String sql = "INSERT INTO EmployeeConfigurations (EmployeeID, ConfigurationID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, employeeID);
            psmt.setInt(2, configurationID);
            psmt.executeUpdate();
        }
    }

    /**
     * Adds an employee to multiple teams in the database
     */
    @Override
    public void addEmployeeToTeam(int employeeID, List<Team> teams, Connection conn) throws RateException, SQLException {
        String sql = "INSERT INTO TeamEmployee (TeamID, EmployeeID, UtilizationPercentage) VALUES (?, ?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Team team : teams) {
                psmt.setInt(1, team.getId());
                psmt.setInt(2, employeeID);
                psmt.setBigDecimal(3, team.getUtilizationPercentage());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        }
    }

    /**
     * Adds a history entry for team employees in the database
     */
    private void addEmployeeHistory(Team team, int teamConfigurationID, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate, Connection conn) {
         String sql = "INSERT INTO TeamEmployeesHistory (EmployeeName, EmployeeDailyRate, EmployeeHourlyRate, TeamConfigurationId, Currency) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Employee employee : team.getEmployees()) {
                psmt.setString(1, employee.getName());
                psmt.setBigDecimal(2, employeeDayRate.get(employee.getId()));
                psmt.setBigDecimal(3, employeeHourlyRate.get(employee.getId()));
                psmt.setInt(4, teamConfigurationID);
                psmt.setString(5, employee.getCurrency().name());
                psmt.executeUpdate();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a team to a configuration history in the database
     */
    private void addTeamToConfiguration(Team team, int teamConfigurationID, Connection conn) {
        String sql = "INSERT INTO TeamConfigurationsHistory (TeamConfigurationID, TeamID) VALUES (?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, teamConfigurationID);
            psmt.setInt(2, team.getId());
            psmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * delete the employee from the employees table
     *
     * @param employee employee to delete
     */
    @Override
    public Boolean deleteEmployee(Employee employee,List<Team> employeeTeams) throws RateException {
        boolean succeeded = false;
        String sql = "DELETE FROM Employees WHERE EmployeeID=?";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

             if(!employeeTeams.isEmpty()){
                 for (Team team : employeeTeams) {
                     //set ole team configuration to false
                     teamDao.setOldConfigurationToInactiveTeams(team.getActiveConfiguration().getId(), conn);
                    //add new team configuration
                     Integer teamConfigId = teamDao.addTeamConfigurationT(team, conn);

                     //set the new team configuration id
                     team.getActiveConfiguration().setId(teamConfigId);

                     //add configuration to the associated team
                     addTeamToConfiguration(team, teamConfigId, conn);
                     //add new team employees configuration to history;
                     addEmployeesToTeamHistory(teamConfigId, team.getEmployees(), conn);
                 }
                 //delete employee configurations from the database
                 deleteEmployeeConfigurations(employee, conn);

                 // delete the employee from the employees table and all the associated relations with configurations
                 try(PreparedStatement psmt=conn.prepareStatement(sql)){
                     psmt.setInt(1, employee.getId());
                     psmt.executeUpdate();
                 }
             }else{
                 //delete employee configurations from the database
                 deleteEmployeeConfigurations(employee, conn);

                 // delete the employee from the employees table and all the associated relations with configurations
                 try(PreparedStatement psmt=conn.prepareStatement(sql)){
                     psmt.setInt(1, employee.getId());
                     psmt.executeUpdate();
                 }
             }
             conn.commit();
            succeeded = true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RateException(e.getMessage(),e,ErrorCode.OPERATION_DB_FAILED);
            }
        }finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, CLOSE_CONN, e);
                }
            }
        }
        return succeeded;
    }

    /**
     * delete employee configurations
     *
     * @param employee employee to delete configurations
     */
    private boolean deleteEmployeeConfigurations(Employee employee, Connection conn) {
        String sql = "DELETE FROM Configurations WHERE ConfigurationID=?";
        boolean executed = false;
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Configuration employeeConfiguration : employee.getConfigurations()) {
                psmt.setInt(1, employeeConfiguration.getConfigurationId());
                psmt.addBatch();
            }
            psmt.executeBatch();
            executed = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return executed;
    }



    /**
     * save the edit operation , change the active configuration of the user
     *
     * @param editedEmployee     the employee object that was edited
     * @param oldConfigurationId the old configuration that needs to be set to inactive
     */
    @Override
    public Employee saveEditOperation(Employee editedEmployee, int oldConfigurationId) throws RateException {
        String sql = "UPDATE Employees SET Name=? , EmployeeType=? , Currency=? WHERE EmployeeID=?";
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                psmt.setString(1, editedEmployee.getName());
                psmt.setString(2, editedEmployee.getType().toString());
                psmt.setString(3, editedEmployee.getCurrency().name());
                psmt.setInt(4, editedEmployee.getId());
                psmt.executeUpdate();
            }
            editedEmployee.getActiveConfiguration().setConfigurationId(addConfigurationEditedEmployee(conn, editedEmployee.getActiveConfiguration()));
            setOldConfigurationToInactive(oldConfigurationId, conn);
            addEmployeeConfiguration(editedEmployee.getId(), editedEmployee.getActiveConfiguration().getConfigurationId(), conn);
            for(Team team: editedEmployee.getTeams()){

                teamDao.setOldConfigurationToInactiveTeams(team.getActiveConfiguration().getId(),conn);
                Integer teamConfigId =  teamDao.addTeamConfigurationT(team,conn);
                addTeamToConfiguration(team,teamConfigId,conn);
                addEmployeesToTeamHistory(teamConfigId,team.getEmployees(),conn);
            }
            conn.commit();
                       return editedEmployee;
        } catch (RateException | SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "error in save edit employee opeartion", e);
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


    /**
     * add the team employees to the team  history , from the active configuration
     */
    private void addEmployeesToTeamHistory(int teamConfigurationID, List<Employee> employees, Connection conn) throws RateException {

        String sql = "INSERT INTO TeamEmployeesHistory (EmployeeName, EmployeeDailyRate, EmployeeHourlyRate, TeamConfigurationId, Currency) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            for (Employee employee : employees) {
                psmt.setString(1, employee.getName());
                psmt.setBigDecimal(2, employee.getActiveConfiguration().getDayRate());
                psmt.setBigDecimal(3, employee.getActiveConfiguration().getHourlyRate());
                psmt.setInt(4, teamConfigurationID);
                psmt.setString(5, employee.getCurrency().name());
                psmt.addBatch();
            }
            psmt.executeBatch();
        } catch (SQLException e) {
            throw new RateException(ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**
     * get all the teams with employees in the system
     */
    @Override
    public Map<Integer, Team> getTeamsWithEmployees() throws RateException {
        String sql = "SELECT t.TeamID,t.TeamName,t.TeamCurrency,e.EmployeeID,e.Name,e.EmployeeType,e.Currency,te.UtilizationPercentage FROM TeamEmployee  te " +
                "RIGHT JOIN Employees e ON e.EmployeeID=te.EmployeeID " +
                "RIGHT JOIN Teams t ON t.TeamId = te.TeamId " +
                "Order By TeamID; ";
        Map<Integer, Team> retrievedTeams = new HashMap<>();
        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int teamId = rs.getInt("TeamID");
                                 Team currentTeam = retrievedTeams.get(teamId);
                    if (currentTeam == null) {
                        String currencyStr = rs.getString("TeamCurrency");
                        Currency currency = Currency.valueOf(currencyStr);
                        currentTeam = new Team(rs.getString("TeamName"), currency, teamId, new ArrayList<>(), new ArrayList<>());
                        currentTeam.setTeamConfigurationsHistory(retrieveTeamConfigurations(currentTeam, conn));
                        currentTeam.setCountries(retrieveCountriesForEmployee(currentTeam.getId(), conn));
                        retrievedTeams.put(currentTeam.getId(), currentTeam);
                    }
                    int employeeId = rs.getInt("EmployeeID");
                    // Check if employee ID is null for teams that have no employees
                    if (!rs.wasNull()) {
                        String employeeName = rs.getString("Name");
                        EmployeeType employeeType = EmployeeType.valueOf(rs.getString("EmployeeType"));
                        Currency currency = Currency.valueOf(rs.getString("Currency"));
                        Employee employee = new Employee(employeeName, employeeType, currency);
                        employee.setId(employeeId);
                        List<Configuration> employeeConfigurations = retrieveConfigurationsForEmployee(employee, conn);
                        employee.setConfigurations(employeeConfigurations);
                        currentTeam.addNewTeamMember(employee);

                        /* retrieves utilization percentage and stores it in the employee's map*/
                        BigDecimal utilization = rs.getBigDecimal("UtilizationPercentage");
                        if (employee.getUtilPerTeams() == null) {
                            employee.setUtilPerTeams(new HashMap<>());
                        }
                        employee.getUtilPerTeams().put(teamId, utilization);
                    }
                    for (Team team : retrievedTeams.values()) {
                        List<Region> regions = new ArrayList<>();
                        for (Country country : team.getCountries()) {
                            regions.addAll(retrieveRegionsForEmployee(country.getId(), conn));
                        }
                        team.setRegions(regions);
                    }
                }
            }
        } catch (SQLException | RateException e) {

            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
        return retrievedTeams;

    }


    /**
     * get all the  operational countries with teams
     */
    @Override
    public Map<Integer, Country> getCountriesWithTeams(Map<Integer, Team> teams) throws RateException {
        String sql = "SELECT c.* ,ct.TeamID from CountryTeam ct " +
                "RIGHT JOIN Countries c ON c.CountryID =ct.CountryID ORDER BY c.CountryID;";

        Map<Integer, Country> retrievedCountries = new HashMap<>();

        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int countryId = rs.getInt("CountryID");
                    Country currentCountry = retrievedCountries.get(countryId);
                    if (currentCountry == null) {
                        currentCountry = new Country(rs.getString("CountryName"), countryId, new ArrayList<>());
                        retrievedCountries.put(currentCountry.getId(), currentCountry);
                    }
                    currentCountry.addNewTeam(teams.get(rs.getInt("TeamID")));

                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }

        return retrievedCountries;
    }


    /**
     * get all regions in the system with associated countries
     */
    @Override
    public Map<Integer, Region> getRegionsWithCountries(ObservableMap<Integer, Country> countriesWithTeams) throws RateException {
        String sql = "SELECT r.*,rc.CountryID from RegionCountry rc " +
                "RIGHT JOIN Region r ON r.RegionID = rc.RegionID " +
                "ORDER BY r.RegionID; ";
        Map<Integer, Region> retrievedRegions = new HashMap<>();

        try (Connection conn = connectionManager.getConnection()) {
            try (PreparedStatement psmt = conn.prepareStatement(sql)) {
                ResultSet rs = psmt.executeQuery();
                while (rs.next()) {
                    int regionId = rs.getInt("RegionID");
                    Region currentRegion = retrievedRegions.get(regionId);
                    if (currentRegion == null) {
                        currentRegion = new Region(rs.getString("RegionName"), regionId, new ArrayList<>());
                        retrievedRegions.put(currentRegion.getId(), currentRegion);
                    }
                    currentRegion.addCountryToRegion(countriesWithTeams.get(rs.getInt("CountryID")));
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }

        return retrievedRegions;
    }

    /**
     * Adds a team to a configuration history in the database
     */
    @Override
    public Integer addNewTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate, int oldTeamConfigurationID) throws  RateException {
        Integer configurationID = null;
        Connection conn = null;
        try {
            conn = connectionManager.getConnection();
            conn.setAutoCommit(false);
        String sql = "INSERT INTO TeamConfiguration (TeamDailyRate, TeamHourlyRate, GrossMargin, MarkupMultiplier, ConfigurationDate, Active) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psmt.setBigDecimal(1, teamConfiguration.getTeamDayRate());
            psmt.setBigDecimal(2, teamConfiguration.getTeamHourlyRate());
            psmt.setDouble(3, teamConfiguration.getGrossMargin());
            psmt.setDouble(4, teamConfiguration.getMarkupMultiplier());
            psmt.setTimestamp(5, Timestamp.valueOf(teamConfiguration.getSavedDate()));
            psmt.setString(6, String.valueOf(teamConfiguration.isActive()));
            psmt.executeUpdate();
            try (ResultSet res = psmt.getGeneratedKeys()) {
                if (res.next()) {
                    configurationID = res.getInt(1);
                } else {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }
            }
        }
            teamDao.setOldConfigurationToInactiveTeams(oldTeamConfigurationID, conn);
            addTeamToConfiguration(team, configurationID, conn);
            addEmployeeHistory(team, configurationID, employeeDayRate, employeeHourlyRate, conn);
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new RateException(ErrorCode.OPERATION_DB_FAILED);
            }
        }
         catch (RateException e) {
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
        return configurationID;
    }


    /**
     * set the old configuration active status to false
     */
    private void setOldConfigurationToInactive(int configurationId, Connection conn) throws RateException {
        String sql = "UPDATE  Configurations  Set Active =? where Configurations.ConfigurationId=?";
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setString(1, "false");
            psmt.setInt(2, configurationId);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }
    }

    /**
     * add configuration with grossMargin and markup
     */
    private Integer addConfigurationEditedEmployee(Connection conn, Configuration configuration) throws RateException {
        Integer configurationID = null;
        String sql = "INSERT INTO Configurations (AnnualSalary, FixedAnnualAmount, OverheadMultiplier, UtilizationPercentage, WorkingHours, Date,Active,DayRate,HourlyRate,DayWorkingHours) VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?)";
        try (PreparedStatement psmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            psmt.setBigDecimal(1, configuration.getAnnualSalary());
            psmt.setBigDecimal(2, configuration.getFixedAnnualAmount());
            psmt.setBigDecimal(3, configuration.getOverheadMultiplier());
            psmt.setBigDecimal(4, configuration.getUtilizationPercentage());
            psmt.setBigDecimal(5, configuration.getWorkingHours());
            psmt.setTimestamp(6, Timestamp.valueOf(configuration.getSavedDate()));
            psmt.setString(7, String.valueOf(configuration.isActive()));
            psmt.setDouble(8,configuration.getDayRate().doubleValue());
            psmt.setDouble(9,configuration.getHourlyRate().doubleValue());
            psmt.setDouble(10,configuration.getDayWorkingHours());
            psmt.executeUpdate();
            try (ResultSet res = psmt.getGeneratedKeys()) {
                if (res.next()) {
                    configurationID = res.getInt(1);
                } else {
                    throw new RateException(ErrorCode.OPERATION_DB_FAILED);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return configurationID;
    }


    /**
     * retrieve the team configurations and set the active configuration
     */
    private List<TeamConfiguration> retrieveTeamConfigurations(Team team, Connection conn) {
        String sql = "SELECT tc.* FROM TeamConfigurationsHistory tch " +
                "JOIN Teams t ON t.TeamID=tch.TeamID " +
                "JOIN TeamConfiguration tc ON tc.TeamConfigurationID = tch.TeamConfigurationID " +
                "WHERE t.TeamId=?";
        List<TeamConfiguration> teamConfigurations = new ArrayList<>();
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, team.getId());
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                int configId = rs.getInt("TeamConfigurationID");
                BigDecimal teamDailyRate = BigDecimal.valueOf(rs.getDouble("TeamDailyRate"));
                BigDecimal teamHourlyRate = BigDecimal.valueOf(rs.getDouble("TeamHourlyRate"));
                double grossMargin = rs.getDouble("GrossMargin");
                double markupMultiplier = rs.getDouble("MarkupMultiplier");
                LocalDateTime savedDate = rs.getTimestamp("ConfigurationDate").toLocalDateTime();
                boolean active = Boolean.parseBoolean(rs.getString("Active"));
                List<TeamConfigurationEmployee> teamConfigurationEmployees = getEmployeesForTeamConfiguration(configId, conn);
                TeamConfiguration teamConfiguration = new TeamConfiguration(teamDailyRate, teamHourlyRate, grossMargin, markupMultiplier, savedDate, teamConfigurationEmployees, active);
                teamConfiguration.setId(configId);
                if (active) {
                    team.setActiveConfiguration(teamConfiguration);
                }
                teamConfigurations.add(teamConfiguration);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return teamConfigurations;
    }

    private List<TeamConfigurationEmployee> getEmployeesForTeamConfiguration(int teamConfigurationId, Connection conn) throws SQLException {
        String sql = "SELECT teh.EmployeeName,teh.EmployeeDailyRate,teh.EmployeeHourlyRate,teh.Currency from TeamEmployeesHistory teh WHERE  teh.TeamConfigurationId=?";
        List<TeamConfigurationEmployee> configurationTeamMembers = new ArrayList<>();
        try (PreparedStatement psmt = conn.prepareStatement(sql)) {
            psmt.setInt(1, teamConfigurationId);
            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                String employeeName = rs.getString("EmployeeName");
                double employeeDailyRate = rs.getDouble("EmployeeDailyRate");
                double employeeHourlyRate = rs.getDouble("EmployeeHourlyRate");
                String currency = rs.getString("Currency");
                Currency currencyH = Currency.valueOf(currency);
                TeamConfigurationEmployee employee = new TeamConfigurationEmployee(employeeName, employeeDailyRate, employeeHourlyRate, currencyH);
                configurationTeamMembers.add(employee);
            }
        }
        return configurationTeamMembers;
    }



//EDIT EMPLOYEE LOGIC

    /**
     * retrieve the employee utilization per teams in order to calculate the  new team overhead
     */
    public Map<Integer, BigDecimal> getEmployeeUtilizationPerTeams(int employeeId) throws RateException {
        String sql = "SELECT TeamID, UtilizationPercentage FROM TeamEmployee te WHERE te.EmployeeID = ?";
        Map<Integer, BigDecimal> employeeTeamsUtilization = new HashMap<>();
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement psmt = conn.prepareStatement(sql)) {

            psmt.setInt(1, employeeId);
            try (ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    int teamId = rs.getInt("TeamID");
                    BigDecimal utilPercentage = rs.getBigDecimal("UtilizationPercentage");
                    employeeTeamsUtilization.put(teamId, utilPercentage);
                }
            }
        } catch (SQLException | RateException e) {
            throw new RateException(e.getMessage(),e,ErrorCode.OPERATION_DB_FAILED);
        }
        return employeeTeamsUtilization;
    }



}








