package easv.bll.EmployeesLogic;

import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableMap;


import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IEmployeeManager {
    Employee addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException;
    Map<Integer, Employee> returnEmployees() throws RateException;

    /**delete the employee ,and  modify the teams overhead , where he is present,return a list with the modified teams
     * @param employee employee to delete
     * @param employeeTeams  the teams that employee is in
     */
    List<Team> deleteEmployee(Employee employee,List<Team> employeeTeams) throws RateException;

    BigDecimal calculateTeamDayRate(Team team);
    BigDecimal calculateTeamHourlyRate(Team team);



    /**calculate the total overhead of the teams */
    public BigDecimal calculateGroupTotalDayRate(List<Team> filteredTeams);

    /**calculate the teams total  hourly rate */
    public BigDecimal calculateGroupTotalHourRate(List<Team> filteredTeams);

    BigDecimal getEmployeeDayRateOnTeam(Employee employee, Team team);

    BigDecimal getEmployeeHourlyRateOnTeam(Employee employee, Team team);


    public List<Employee> performSearchOperation (Collection<Employee> employees, String filter);

    /**check if an edit operation was performed on the epmloyee object*/
    boolean isEmployeeEdited(Employee originalEmployee,Employee editedEmployee);
    /**save the edit operation*/
    Employee saveEditOperation(Employee editedEmployee,Employee originalEmployee,List<Team> originalEmployeeTeams) throws RateException;
    List<Employee> sortedEmployeesByName(Collection<Employee> values);

    /**calculate the day rate for an employee*/
    BigDecimal getDayRate(Employee employee);

    /**calculate the hourly rate for an employee, with the configurable hours*/
    BigDecimal getHourlyRate(Employee employee,double configurableHours);

    List<Employee> filterByCountry(Region region,List<Country> countries,Map<Integer,Employee> employees);

    /**filter teams by region */
    List<Team> filterTeamsByRegion(Region region , List<Country> countries);



    /**retrieve the teams in the system*/
    Map<Integer,Team> getTeamWithEmployees() throws RateException;

    /**retrieve countries with the associated teams */
    Map<Integer,Country> getCountriesWithTeams(Map<Integer,Team> teams) throws RateException;

    /**retrieve all regions with countries*/
    Map<Integer, Region> getRegionsWithCountries(ObservableMap<Integer, Country> countriesWithTeams) throws RateException;

    Integer addTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate, int oldTeamConfigurationID) throws  RateException;

    List<Employee> filterTeamsByCountry(List<Team> countryTeams, ObservableMap<Integer, Employee> employees);

    List<Employee> filterEmployeesByTeam(Team selectedTeam,ObservableMap<Integer,Employee> employees);


    void updateCountryTeams(Collection<Country> values, List<Team> teamsWithoutEmployee);


    /**get employee utilization per teams from the database
     * @param employeeId id of the employee to retrieve for*/
    Map<Integer,BigDecimal> getEmployeeUtilizationInTeams(int employeeId) throws RateException;

}
