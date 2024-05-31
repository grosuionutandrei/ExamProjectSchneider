package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.be.*;
import easv.exception.RateException;
import javafx.collections.ObservableList;

import java.math.BigDecimal;


import java.util.List;
import java.util.Map;

public interface IModel {
    void returnEmployees() throws RateException;

    void deleteEmployee(Employee employee) throws RateException;

    void setDisplayer(DisplayEmployees displayEmployees);

    ObservableList<Employee> getUsersToDisplay();


    /**
     * get the operational countries  observable list
     */
    ObservableList<Country> getOperationalCountries();

    /**
     * get the operational regions observable list
     */

    ObservableList<Region> getOperationalRegions();

    /**
     * get the  operational teams
     */
    ObservableList<Team> getOperationalTeams();


    Map<String, Country> getCountries();

    void addNewEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException;

    void addTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate) throws  RateException;

    /**
     * retrieve the teams with the overhead computed
     */
    List<Team> getCountryTeams();

//    ObservableList<Team> getTeams();

    /**
     * used for country input validation
     */
    void populateValidCountries(List<String> validCountries);

    /**
     * set the country that user has selected from the map
     */
    void setSelectedCountry(String selectedCountry);


    /**EDIT EMPLOYEE  RELATED LOGIC*/
    /**get employee utilization per teams from the database
     * @param employeeId id of the employee to retrieve for*/
     Map<Integer,BigDecimal> getEmployeeUtilizationInTeams(int employeeId) throws RateException;

    /**
     * check if edit operation was performed
     */
    boolean isEditOperationPerformed(Employee originalEmployee, Employee editedEmployee);

    /**
     * save the updated employee to the database
     */
    Employee updateEditedEmployee(Employee employee, Employee editedEmployee) throws RateException;

    List<String> getValidCountries();

    ObservableList<Employee> getSearchResult(String filter);


    void performSelectUserSearchOperation(Employee employee) throws RateException;

    void filterByRegion(Region region, List<Country> countries);


    void performEmployeeSearchUndoOperation();


    /**
     * calculate the hourly rate for an employee
     */
    BigDecimal getComputedHourlyRate(Employee employee, double configurableHours);

    /**
     * calculate the day rate for an employee
     */

    BigDecimal getComputedDayRate(Employee employee);


    //FILTERS RELATED LOGIC


    /**
     * perform the region search operation
     */
    ObservableList<Region> getRegionFilterResults(String filter);

    /**
     * return region by id
     */
    Region getRegionById(int regionId);

    /**
     * return country by id
     */
    Country getCountryById(int countryId);

    /**
     * perform the region search operation
     */
    ObservableList<Country> getCountryFilterResults(String filter);

    /**
     * get the countries for the selected region from the regions filter
     *
     * @param region the selected region from the filter
     */
    ObservableList<Country> getRegionCountries(Region region);


    /**get the teams for the selected region from the filter
     * @param region selected region from the filter */
    List<Team> getRegionTeams(Region region);




    void returnEmployeesByRegion(Region region);

    Employee getEmployeeById(int id);


    /**
     * filter employees by the selected country
     */
    void filterByCountryTeams(Country newValue);

    /**
     * filter employees by the selected team
     */
    void filterEmployeesByTeam(Team selectedTeam);

    /**
     * undo the team filter operation to display the country active filter
     */
    void returnEmployeesByCountry(Country country);

    /**
     * calculate selected teams from filter day rate
     */
    BigDecimal calculateGroupDayRate();


    /**
     * calculate selected teams from filter hour rate
     */
    BigDecimal calculateGroupHourRate();


    /**OVERHEAD DISTRIBUTION RELATED LOGIC*/


    /**
     * add the team and the percentage that user chose to distribute
     *
     * @param team               the   team that will receive overhead
     * @param overheadPercentage the overhead percentage received by the team
     */
    void addDistributionPercentageTeam(Team team, String overheadPercentage);

    /**
     * remove the team and the inserted overhead percentage from the map
     */
    void removeDistributionPercentageTeam(Team team);


    /**
     * set the selected team that user chose to distribute from and the associated  value
     *
     * @param selectedTeamToDistributeFrom selected team and associated percentage
     */

    void setDistributeFromTeam(Team selectedTeamToDistributeFrom);

    Team getSelectedTeamToDistributeFrom();


    Team getDistributeFromTeam();


    /**
     * validate distribution operation inputs
     */
    DistributionValidation validateInputs();

    Map<Team, String> getInsertedDistributionPercentageFromTeams();

    /**
     * set the percentage of the selected team to distribute
     */
    void setDistributionPercentageTeam(Team selectedTeam, String newValue);

    /**
     * calculate total overhead inserted in order to update the displayed value
     */
    double calculateTeTotalOverheadInserted();


    /**
     * get the team name based on the  team id , is used in order to display the error message in distribution page
     */
    String getTeamName(int teamId);

    Map<OverheadHistory, List<Team>> performSimulation();

    void addNewRegion(Region region, List<Country> countries) throws RateException;

    void updateRegion(Region region, List<Country> countries) throws RateException;

    /**
     * check if the team is already selected to distribute
     */
    boolean isTeamSelectedToDistribute(Integer teamId);


    /**
     * save the  distribution operation
     */
    Map<OverheadHistory, List<Team>> saveDistribution() throws RateException;

    /**
     * initialize the distribution entities , when user enter on the page
     */
    void initializeDistributionEntities();

    /**
     * set  that the simulation was performed
     */
    void setSimulationPerformed(boolean simulationPerformed);


    /**
     * TEAM MANAGEMENT LOGIC
     */
    List<Employee> getAllEmployees();

    void performEditTeam(List<Employee> employees, List<Employee> employeesToDelete, Team editedTeam, Team originalTeam) throws RateException;


    /**
     * get unsuported countries
     */
    ObservableList<Country> getUnsoportedCountries();


    /**
     * perform the search operation for teams and return the results
     */
    ObservableList<Team> getTeamsFilterResults(String filter);

    /**
     * return the selected team from the search operation results
     */
    Team getTeamById(int entityId);

    void deleteRegion(Region region) throws RateException;

    void updateCountry(Country country, List<Team> teams) throws RateException;

    void addNewCountry(Country country, List<Team> teams) throws RateException;

    void deleteCountry(Country country) throws RateException;

    void deleteTeam(Team team) throws RateException;
}
