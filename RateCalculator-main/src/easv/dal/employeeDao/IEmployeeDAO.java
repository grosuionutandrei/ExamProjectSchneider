package easv.dal.employeeDao;

import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableMap;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;
import java.util.Map;

public interface IEmployeeDAO {
    Map<Integer, Employee> returnEmployees() throws RateException;

    Integer addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException;

    /**delete the employee ,and  modify the teams overhead , where he is present
     * @param employee employee to delete
     * @param employeeTeams  the teams that employee is in */
    Boolean deleteEmployee(Employee employee,List<Team> employeeTeams) throws RateException;

    void addEmployeeToTeam(int employeeID, List<Team> teams, Connection conn) throws RateException, SQLException;
    Integer addConfiguration(Configuration configuration, Connection conn) throws RateException, SQLException;
    void addEmployeeConfiguration(int employeeID, int configurationID, Connection conn) throws RateException, SQLException;



    Employee saveEditOperation(Employee editedEmployee,int oldConfigurationId) throws RateException;


    /**retrieve the teams with associated  employees  */
    Map<Integer, Team> getTeamsWithEmployees() throws RateException;

    /**retrieve all the countries with the associated teams */
    Map<Integer, Country> getCountriesWithTeams(Map<Integer,Team> teams) throws RateException;

    /**retrieve regions with the associated countries*/
    Map<Integer, Region> getRegionsWithCountries(ObservableMap<Integer, Country> countriesWithTeams) throws RateException;

    Integer addNewTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate, int oldTeamConfigurationID) throws  RateException;

    /** retrieve the employee utilization per teams in order to calculate the  new team overhead*/
   Map<Integer, BigDecimal> getEmployeeUtilizationPerTeams(int employeeId) throws RateException;
}
