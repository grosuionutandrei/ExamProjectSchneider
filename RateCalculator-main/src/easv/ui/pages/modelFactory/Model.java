
package easv.ui.pages.modelFactory;

import easv.Utility.DisplayEmployees;
import easv.be.*;
import easv.bll.EmployeesLogic.EmployeeManager;
import easv.bll.EmployeesLogic.IEmployeeManager;
import easv.bll.RegionLogic.IRegionManager;
import easv.bll.RegionLogic.RegionManager;
import easv.bll.TeamLogic.ITeamLogic;
import easv.bll.TeamLogic.TeamLogic;
import easv.bll.countryLogic.CountryLogic;
import easv.bll.countryLogic.ICountryLogic;
import easv.exception.RateException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Model implements IModel {

    /** holds all employees in the system */
    private ObservableMap<Integer, Employee> employees;

    /** logic responsible for employees */
    private IEmployeeManager employeeManager;


    // the bussines logic object responsible of team logic
    private ITeamLogic teamManager;
    /**
     * the logic layer responsible for region management
     */
    private IRegionManager regionManager;
    /**
     * the logic layer responsible  of countries management
     */
    private ICountryLogic countryLogic;
    /**
     * holds the countries that are currently operational for the company
     */
    private final ObservableMap<String, Country> countries;

    /**
     * displayer of employees
     */
    private DisplayEmployees displayEmployees;


    /**
     * the value off the selected country from the view map
     */
    private String selectedCountry;


    /**
     * used to check if the inserted country is valid
     */

    private List<String> validMapViewCountryNameValues;
    private final ObservableMap<Integer, Team> teams;

    /**
     * holds all the data related to the teams, like history, employees, countries,util, regions and active configuration
     */
    private final ObservableMap<Integer, Team> teamsWithEmployees;


    /**
     * holds all the data related to the operational  countries
     */
    private final ObservableMap<Integer, Country> countriesWithTeams;
    /**
     * holds all the date related to the  operational regions
     */
    private final ObservableMap<Integer, Region> regionsWithCountries;
    /**
     * holds all employees with rates and util % for each team
     */
    private ObservableList<Employee> employeesForTeamsPage;

    /** holds all employees for displaying */
    private ObservableList<Employee> displayedEmployees;
    /** holds all sorted employees */
    private ObservableList<Employee> sortedEmployeesByName;
    private ObservableList<Employee> filteredEmployeesListByRegion;
    private ObservableList<Employee> listEmployeeByCountryTemp;

    /**
     * holds the selected region choosed by the employee from  the filter
     */
    private List<Team> resultedTeamsFromFilterAction;


    /**
     * holds the temporary values for the teams that user inserted in the distribution page
     */
    private Map<Team, String> insertedDistributionPercentageFromTeams;

    /**
     * the selected team that user chose to distribute from and the associated value
     */
    private Team selectedTeamToDistributeFrom;

    /**
     * store if the distribution simulation was executed before the user pressed save button
     */

    private boolean simulationPerformed;


    public Model() throws RateException {
        this.employees = FXCollections.observableHashMap();
        this.filteredEmployeesListByRegion = FXCollections.observableArrayList();
        this.listEmployeeByCountryTemp = FXCollections.observableArrayList();
        this.countries = FXCollections.observableHashMap();
        this.employeeManager = new EmployeeManager();
        this.countryLogic = new CountryLogic();
        this.regionManager = new RegionManager();
        this.teamManager = new TeamLogic();
        this.validMapViewCountryNameValues = new ArrayList<>();
        this.teams = FXCollections.observableHashMap();
        this.displayedEmployees = FXCollections.observableArrayList();
        this.employeesForTeamsPage = FXCollections.observableArrayList();
        this.sortedEmployeesByName = FXCollections.observableArrayList();
        teamsWithEmployees = FXCollections.observableHashMap();
        countriesWithTeams = FXCollections.observableHashMap();
        regionsWithCountries = FXCollections.observableHashMap();

        populateTeamsWithEmployees();
        populateCountriesWithTeams();
        populateRegionsWithCountries();
    }


    public void setDisplayer(DisplayEmployees displayEmployees) {
        this.displayEmployees = displayEmployees;
    }



    /**
     * populate the teamWithEmployees map with data
     */
    private void populateTeamsWithEmployees() throws RateException {
        this.teamsWithEmployees.putAll(employeeManager.getTeamWithEmployees());

    }

    /**
     * populate the countriesWithTeams  with data
     */
    private void populateCountriesWithTeams() throws RateException {
        this.countriesWithTeams.putAll(employeeManager.getCountriesWithTeams(teamsWithEmployees));
    }

    /**
     * populate regionsWithCountries with data
     */
    private void populateRegionsWithCountries() throws RateException {
        this.regionsWithCountries.putAll(employeeManager.getRegionsWithCountries(countriesWithTeams));
    }


    /**
     * get the operational countries  observable list
     */
    public ObservableList<Country> getOperationalCountries() {
        ObservableList<Country> observableCountryList = FXCollections.observableArrayList();
        observableCountryList.setAll(countriesWithTeams.values());
        return observableCountryList.sorted();
    }


    /**
     * get the operational regions observable list
     */

    public ObservableList<Region> getOperationalRegions() {
        ObservableList<Region> observableRegionList = FXCollections.observableArrayList();
        observableRegionList.setAll(regionsWithCountries.values());
        return observableRegionList.sorted();
    }


    /**
     * get the  operational teams
     */
    public ObservableList<Team> getOperationalTeams() {
        ObservableList<Team> observableTeamList = FXCollections.observableArrayList();
        observableTeamList.setAll(teamsWithEmployees.values());
        return observableTeamList.sorted();
    }

    /** Returns all employees from the database, sorts them and then puts them into displayedEmployees for displaying */
    @Override
    public void returnEmployees() throws RateException {
        employees.putAll(employeeManager.returnEmployees());
        sortDisplayedEmployee();
        displayedEmployees = sortedEmployeesByName;

    }

    /** Returns employee by id for the search operation */
    public Employee getEmployeeById(int id) {
        return employees.get(id);
    }

    /** Sorts employees by name in alphabetical order*/
    private void sortDisplayedEmployee() {
        sortedEmployeesByName.setAll(employeeManager.sortedEmployeesByName(employees.values()));
    }

    @Override
    public void deleteEmployee(Employee employee) throws RateException {
        List<Team> employeeTeams = employee.getTeams().stream().map((e)->teamsWithEmployees.get(e.getId())).toList();
        List<Team> teamsWithoutEmployee = employeeManager.deleteEmployee(employee,employeeTeams);
        if (!teamsWithoutEmployee.isEmpty()) {
            // If the deletion was successful, remove the employee from the observable map
            employees.remove(employee.getId());
            //add the new teams configurations to the teamWithEmployees map
            for(Team team : teamsWithoutEmployee){
                teamsWithEmployees.put(team.getId(),team);
            }

            //update the countries with the new teams values
            employeeManager.updateCountryTeams(countriesWithTeams.values(),teamsWithoutEmployee );
            sortDisplayedEmployee();
            displayedEmployees = sortedEmployeesByName;
            Platform.runLater(() -> {
                displayEmployees.displayEmployees();
            });
        }
    }

    @Override
    public void addNewEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException {
        employee = employeeManager.addEmployee(employee, configuration, teams);
        if (employee != null) {
            employees.put(employee.getId(), employee);
            for (Team team : teams) {
                if(team.getTeamMembers() == null){
                    team.setTeamMembers(new ArrayList<>());
                }
                team.addNewTeamMember(employee);
                TeamConfiguration teamConfiguration = getNewEmployeeTeamConfiguration(team);
                Map<Integer, BigDecimal> employeesDayRates = new HashMap<>();
                Map<Integer, BigDecimal> employeesHourlyRates = new HashMap<>();
                for (Employee employeeToCheck : team.getEmployees()) {
                    BigDecimal employeeHourlyRate = employeeManager.getEmployeeHourlyRateOnTeam(employeeToCheck, team);
                    employeesHourlyRates.put(employeeToCheck.getId(), employeeHourlyRate);
                    BigDecimal employeeDayRate = employeeManager.getEmployeeDayRateOnTeam(employeeToCheck, team);
                    employeesDayRates.put(employeeToCheck.getId(), employeeDayRate);
                }
                addTeamConfiguration(teamConfiguration, team, employeesDayRates, employeesHourlyRates);
                teamsWithEmployees.get(team.getId()).addNewTeamMember(employee);
            }
        }
    }

    @Override
    public void addTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate) throws  RateException {
        int oldTeamConfigurationID = 0;
        if(team != null)
            if(team.getActiveConfiguration() != null)
                oldTeamConfigurationID = team.getActiveConfiguration().getId();

        int teamConfigurationID = employeeManager.addTeamConfiguration(teamConfiguration, team, employeeDayRate, employeeHourlyRate, oldTeamConfigurationID);
        if (teamConfiguration != null) {
            teamConfiguration.setId(teamConfigurationID);
            teamsWithEmployees.get(team.getId()).setActiveConfiguration(teamConfiguration);
        }
    }

    private TeamConfiguration getNewEmployeeTeamConfiguration(Team team) {
        BigDecimal teamHourlyRate = employeeManager.calculateTeamHourlyRate(team);
        BigDecimal teamDayRate = employeeManager.calculateTeamDayRate(team);
        double grossMargin = 0;
        double markupMultiplier = 0;
        if (team.getActiveConfiguration() != null) {
            grossMargin = checkNullValues(team.getActiveConfiguration().getGrossMargin());
            markupMultiplier = checkNullValues(team.getActiveConfiguration().getMarkupMultiplier());
        }
        LocalDateTime savedDate = LocalDateTime.now();
        return new TeamConfiguration(teamDayRate, teamHourlyRate, grossMargin, markupMultiplier, savedDate, true);
    }

    private double checkNullValues(double numberToCheck) {
        if (numberToCheck > 0) {
            return numberToCheck;
        } else {
            return 0;
        }
    }




//MAP RELATED LOGIC


    /**used in the map to display teams info*/
    @Override
    public List<Team> getCountryTeams() {
        return this.countries.get(selectedCountry).getTeams();
    }


    /**
     * return the operational countries
     */
    public Map<String, Country> getCountries() {
        this.countries.putAll(countryLogic.getCountriesForMap(countriesWithTeams));
   return countries;
    }


    /**get the unsuported countries by the map in order to be shown in the unsuported countries view */
    @Override
    public ObservableList<Country> getUnsoportedCountries() {
        ObservableList <Country> unsuportedCountries = FXCollections.observableArrayList();
        for(Country country:countriesWithTeams.values()){
            if(!validMapViewCountryNameValues.contains(country.getCountryName())){
                unsuportedCountries.add(country);
            }
        }

        return unsuportedCountries;
    }




    //EMPLOYEE EDITING RELATED LOGIC


    /**get employee utilization per teams from the database
     * @param employeeId id of the employee to retrieve for*/
     public Map<Integer,BigDecimal> getEmployeeUtilizationInTeams(int employeeId) throws RateException {
         return employeeManager.getEmployeeUtilizationInTeams(employeeId);
     }




    /**
     * save the  edited employee to the database , and if the operation is performed
     * add it to the all employees map and update the filtered employees list
     *
     * @param originalEmployee the employee before editing
     * @param editedEmployee   the employee after editing
     */
    @Override
    public Employee updateEditedEmployee(Employee originalEmployee, Employee editedEmployee) throws RateException {
        List<Team> originalEmployeeTeams =  new ArrayList<>();
        for(Team team : employees.get(originalEmployee.getId()).getTeams()){
            System.out.println(teamsWithEmployees.get(team.getId()).getActiveConfiguration().getTeamDayRate() + "old day rate before edit");
             originalEmployeeTeams.add(teamsWithEmployees.get(team.getId()));
        }
        Employee editedSavedEmployee = employeeManager.saveEditOperation(editedEmployee, originalEmployee,originalEmployeeTeams );
        editedSavedEmployee.setCountries(originalEmployee.getCountries());
        editedSavedEmployee.setRegions(originalEmployee.getRegions());
        editedSavedEmployee.setTeams(originalEmployee.getTeams());

        if (editedSavedEmployee != null) {
            this.employees.put(editedEmployee.getId(), editedSavedEmployee);
            // update the filter list with the new updated values
            for (int i = 0; i <sortedEmployeesByName.size(); i++) {
                if (displayedEmployees.get(i).getId()==editedSavedEmployee.getId()) {
                    displayedEmployees.set(i, editedSavedEmployee);
                    break;
                }
            }
        }
        return editedSavedEmployee;

    }



    /**
     * check if edit operation was performed
     */
    public boolean isEditOperationPerformed(Employee originalEmployee, Employee editedEmployee) {
        return employeeManager.isEmployeeEdited(originalEmployee, editedEmployee);
    }


    public void populateValidCountries(List<String> validCountries) {
        this.validMapViewCountryNameValues.addAll(validCountries);
    }

    public List<String> getValidCountries() {
        return validMapViewCountryNameValues;
    }


    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }



    public ObservableMap<Integer, Team> getTeams() {
        return teams;
    }




    /** FILTERS RELATED LOGIC IN EMPLOYEES */

    /** Returns results from the search operation in logic*/
    public ObservableList<Employee> getSearchResult(String filter) {
        ObservableList<Employee> searchResults = FXCollections.observableArrayList();
        searchResults.setAll(employeeManager.performSearchOperation(employees.values(), filter));
        return searchResults;
    }
    /** Show selected employee for the user */
    public void performSelectUserSearchOperation(Employee employee)  {
        filteredEmployeesListByRegion.setAll(displayedEmployees);
        displayedEmployees.setAll(employee);
        displayEmployees.displayEmployees();
    }


    /** Undoes all the filters to display all the employees  in the system again */
    public void performEmployeeSearchUndoOperation() {
        sortDisplayedEmployee();
        displayedEmployees = sortedEmployeesByName;
        displayEmployees.displayEmployees();
    }

    //TEAMS FIlTER SEARCH LOGIC


    @Override
    public ObservableList<Team> getTeamsFilterResults(String filter) {
        ObservableList<Team> filterResult =  FXCollections.observableArrayList();
        filterResult.setAll(teamManager.performSearchTeamFilter(filter,teamsWithEmployees.values()));
        return filterResult  ;
    }

    //REGION FILTER LOGIC

    /**get the regions resulted from the search operation
     * @param filter the input from the search component*/
    public ObservableList<Region> getRegionFilterResults(String filter){
        ObservableList<Region> regionFilterResult= FXCollections.observableArrayList();
        regionFilterResult.setAll(regionManager.performSearchRegionFilter(filter,regionsWithCountries.values()));
        return regionFilterResult;
    }

    /**get the countries for the selected region from the regions filter
     * @param region the selected region from the filter */
    public ObservableList<Country> getRegionCountries(Region region){
        ObservableList<Country> regionCountries =  FXCollections.observableArrayList();
        regionCountries.setAll(regionsWithCountries.get(region.getId()).getCountries());
        return regionCountries;
    }

    /**get the teams for the selected region from the filter
     * @param region selected region from the filter */
    public List<Team> getRegionTeams(Region region){
        return regionManager.filterTeamsByRegion(region);
    }

    /**return the selected team from the search operation results*/
    @Override
    public Team getTeamById(int entityId) {
        return  teamsWithEmployees.get(entityId);
    }
    /**return region by id */
    public Region getRegionById(int regionId){
        return regionsWithCountries.get(regionId);
    }

    public Country getCountryById(int countryId){
        return countriesWithTeams.get(countryId);
    };

    /**perform the region search operation*/
   public   ObservableList<Country> getCountryFilterResults(String filter){
       ObservableList<Country> countryFilterResult= FXCollections.observableArrayList();
       countryFilterResult.setAll(countryLogic.performSearchCountryFilter(filter,countriesWithTeams.values()));
       return countryFilterResult;
    };


    /**
     * calculate selected teams from filter day rate
     */
    public BigDecimal calculateGroupDayRate() {
        BigDecimal groupDayRate = BigDecimal.ZERO;
        groupDayRate = employeeManager.calculateGroupTotalDayRate(resultedTeamsFromFilterAction);
        return groupDayRate;

    }


    /**
     * calculate selected teams from filter hour rate
     */
    public BigDecimal calculateGroupHourRate() {
        BigDecimal groupHourRate = BigDecimal.ZERO;
         groupHourRate = employeeManager.calculateGroupTotalHourRate(resultedTeamsFromFilterAction);
        return groupHourRate;
    }

    /**
     * filter the employees that are present in the countries from the selected region
     */
    @Override
    public void filterByRegion(Region region, List<Country> countries) {
        filteredEmployeesListByRegion.setAll(displayedEmployees);
        displayedEmployees.setAll(employeeManager.filterByCountry(region, countries, employees));
        displayEmployees.displayEmployees();
        filteredEmployeesListByRegion.setAll(displayedEmployees);
        listEmployeeByCountryTemp.setAll(displayedEmployees);
        resultedTeamsFromFilterAction = new ArrayList<>();
        resultedTeamsFromFilterAction.addAll(employeeManager.filterTeamsByRegion(region,countries));
    }


    /**
     * filter the employees that are present in the teams from the selected country
     */
    @Override
    public void filterByCountryTeams(Country selectedCountry) {
        displayedEmployees.setAll(employeeManager.filterTeamsByCountry(countriesWithTeams.get(selectedCountry.getId()).getTeams(), employees));
        displayEmployees.displayEmployees();
        listEmployeeByCountryTemp.setAll(displayedEmployees);
        resultedTeamsFromFilterAction = new ArrayList<>();
        resultedTeamsFromFilterAction.addAll(countriesWithTeams.get(selectedCountry.getId()).getTeams());
    }

    /**
     * filter employees by selected team
     */
    public void filterEmployeesByTeam(Team selectedTeam) {
        ObservableList<Employee> teamEmployees = FXCollections.observableArrayList();
        teamEmployees.setAll(employeeManager.filterEmployeesByTeam(selectedTeam, employees));
        displayedEmployees.setAll(employeeManager.filterEmployeesByTeam(selectedTeam, employees));
        displayEmployees.displayEmployees();
        resultedTeamsFromFilterAction = new ArrayList<>();
        resultedTeamsFromFilterAction.add(selectedTeam);
    }




    /**
     * undo the country filter selection to show all the employees in the selected region , or all the employees in the system
     */
    public void returnEmployeesByRegion(Region region) {
        displayedEmployees.setAll(filteredEmployeesListByRegion);
        displayEmployees.displayEmployees();
        if (areObservableListsEqual(filteredEmployeesListByRegion, displayedEmployees)) {
            filteredEmployeesListByRegion.setAll(displayedEmployees);
        }
        resultedTeamsFromFilterAction = new ArrayList<>();
        resultedTeamsFromFilterAction.addAll(employeeManager.filterTeamsByRegion(region, region. getCountries()));

    }


    /**
     * if the country filter is active undo the teams filter to show the employees
     * from all the teams for the active country filter
     */
    @Override
    public void returnEmployeesByCountry(Country country) {
        displayedEmployees.setAll(listEmployeeByCountryTemp);
        displayEmployees.displayEmployees();
        resultedTeamsFromFilterAction = new ArrayList<>();
        resultedTeamsFromFilterAction.addAll(countriesWithTeams.get(country.getId()).getTeams());
    }


    private boolean areObservableListsEqual(ObservableList<Employee> list1, ObservableList<Employee> list2) {
        for (Employee employee : list1) {
            if (!list2.contains(employee)) {
                return false;
            }
        }
        return true;
    }


    public ObservableList<Employee> getUsersToDisplay() {
        return displayedEmployees;
    }

    /**
     * calculate the hourly rate for an employee
     */
    public BigDecimal getComputedHourlyRate(Employee employee, double configurableHours) {
        return employeeManager.getHourlyRate(employee, configurableHours);
    }

    /**
     * calculate the day rate for an employee
     */

    public BigDecimal getComputedDayRate(Employee employee) {
        return employeeManager.getDayRate(employee);
    }





/**OVERHEAD DISTRIBUTION RELATED LOGIC*/





    /**
     * add the team and the percentage that user chose to distribute
     *
     * @param team               the team that will receive overhead
     * @param overheadPercentage the overhead percentage received by the team
     */
    public void addDistributionPercentageTeam(Team team, String overheadPercentage) {
        this.insertedDistributionPercentageFromTeams.put(team, overheadPercentage);
    }

    public Map<Team, String> getInsertedDistributionPercentageFromTeams() {
        return insertedDistributionPercentageFromTeams;
    }


    /**
     * add the  overhead value to distribute,inserted by the user
     */
    @Override
    public void setDistributionPercentageTeam(Team selectedTeam, String newValue) {
        this.insertedDistributionPercentageFromTeams.put(selectedTeam, newValue);
    }

    @Override
    public double calculateTeTotalOverheadInserted() {
        return teamManager.calculateTotalOverheadInsertedForValidInputs(insertedDistributionPercentageFromTeams);
    }

    public Team getSelectedTeamToDistributeFrom() {
        return selectedTeamToDistributeFrom;
    }

    @Override
    public void addNewRegion(Region region, List<Country> countries) throws RateException {
        region = regionManager.addRegion(region, countries);
        regionsWithCountries.put(region.getId(), region);
    }

    @Override
    public void updateRegion(Region region, List<Country> countries) throws RateException {
        region = regionManager.updateRegion(region, countries);
        regionsWithCountries.get(region.getId()).setCountries(countries);
    }

    /**
     * remove the team and the inserted overhead percentage from the map
     */
    public void removeDistributionPercentageTeam(Team team) {
        this.insertedDistributionPercentageFromTeams.remove(team);
    }


    /**
     * set the selected team that user chose to distribute from and the associated  value
     *
     * @param selectedTeamToDistributeFrom selected team and associated percentage
     */

    public void setDistributeFromTeam(Team selectedTeamToDistributeFrom) {
        this.selectedTeamToDistributeFrom = selectedTeamToDistributeFrom;
    }

    public Team getDistributeFromTeam() {
        return this.selectedTeamToDistributeFrom;
    }

    @Override
    public DistributionValidation validateInputs() {
        return teamManager.validateDistributionInputs(insertedDistributionPercentageFromTeams, selectedTeamToDistributeFrom);

    }

    /**
     * return the name of the team , by team id
     */
    public String getTeamName(int teamId) {
        return this.teamsWithEmployees.get(teamId).getTeamName();
    }


    /**
     * perform simulation computation
     */
    @Override
    public Map<OverheadHistory, List<Team>> performSimulation() {
        return teamManager.performSimulationComputation(selectedTeamToDistributeFrom, insertedDistributionPercentageFromTeams, teamsWithEmployees);
    }


    public boolean isTeamSelectedToDistribute(Integer teamId) {
        Team team = insertedDistributionPercentageFromTeams.keySet().stream().filter(e -> e.getId() == teamId).findFirst().orElse(null);
        return team != null;
    }

    @Override
    public Map<OverheadHistory, List<Team>> saveDistribution() throws RateException {

        Map<OverheadHistory, List<Team>> performedValues = teamManager.saveDistributionOperation(insertedDistributionPercentageFromTeams, selectedTeamToDistributeFrom, simulationPerformed, teamsWithEmployees);
        //   update the  local teams with the new values;
        if (!performedValues.isEmpty()) {
            // add the previous overhead values to the map to be displayed in the barchart
            List<Team> previousValues = new ArrayList<>();
            for (Team team : performedValues.get(OverheadHistory.CURRENT_OVERHEAD)) {
                previousValues.add(teamsWithEmployees.get(team.getId()));
            }
            performedValues.put(OverheadHistory.PREVIOUS_OVERHEAD, previousValues);
            // add the selected team distribution performed,

            // update the teams map in order to have the updated overhead rates values
            for (Team team : performedValues.get(OverheadHistory.CURRENT_OVERHEAD)) {
                teamsWithEmployees.put(team.getId(), team);
            }

            //update the countries teams with the new updated overhead
            employeeManager.updateCountryTeams(countriesWithTeams.values(),performedValues.get(OverheadHistory.CURRENT_OVERHEAD));
        }

        return performedValues;
    }



    public void setSimulationPerformed(boolean simulationPerformed) {
        this.simulationPerformed = simulationPerformed;
    }

    /**
     * initialize distribution entities to empty when the user enters on the distribution page
     */
    public void initializeDistributionEntities() {
        this.insertedDistributionPercentageFromTeams = new HashMap<>();
        this.selectedTeamToDistributeFrom = null;
        this.simulationPerformed = false;
    }


    /**TEAM MANAGEMENT MODEL LOGIC*/


    /** Return all employees for team manage from team map */
    public List<Employee> getAllEmployees() {
        /* Create a set to store unique employees*/
        HashSet<Employee> uniqueEmployees = new HashSet<>();

        for (Map.Entry<Integer, Team> entry : teamsWithEmployees.entrySet()) {
            Team team = entry.getValue();
            if (team != null && team.getTeamMembers() != null) {
                /*Add employees to the set to remove duplicates*/
                uniqueEmployees.addAll(team.getTeamMembers());
            }
        }
        employeesForTeamsPage.clear();
        /* Add unique employees back to the observable list*/
        employeesForTeamsPage.addAll(uniqueEmployees);


        return employeesForTeamsPage;
    }
    /**
     * Performs the edit operation on the specified team,
     * updates the team by removing the specified employees, adding new or replacing existing with new values,
     * Setts new rates
     * Creates new history for employees
     * Creates new team configuration based on recalculated rates and sets active configuration
     * If the team is successfully saved, it updates the map that stores the teams.
     */
    public void performEditTeam (List<Employee> employees, List<Employee> employeesToDelete, Team editedTeam, Team originalTeam) throws RateException{
        /* Removes employees to delete from editedTeam */
        for (Employee employeesDelete : employeesToDelete) {
            editedTeam.removeTeamMember(employeesDelete);
        }
        List<TeamConfigurationEmployee> teamConfigurationEmployees = new ArrayList<>();
        /* Replace or add new employees */
        for (Employee employee : employees) {
            TeamConfigurationEmployee teamConfigurationEmployee = null;
            /* Calculate and set the new hourly and daily rates for the employee*/
            BigDecimal employeeHourlyRate = teamManager.getEmployeeHourlyRateOnTeamE(employee, editedTeam);
            employee.setTeamHourlyRate(employeeHourlyRate);
            BigDecimal employeeDayRate = teamManager.getEmployeeDayRateOnTeamE(employee, editedTeam);
            employee.setTeamDailyRate(employeeDayRate);
            if (editedTeam.getTeamMember(employee.getId()) != null) {
                editedTeam.replaceTeaMember(employee);
                /* Creates new history */
                teamConfigurationEmployee = new TeamConfigurationEmployee(employee.getName(), employee.getTeamDailyRate().doubleValue(), employee.getTeamHourlyRate().doubleValue(), employee.getCurrency());
            } else {
                teamConfigurationEmployee = new TeamConfigurationEmployee(employee.getName(), employee.getTeamDailyRate().doubleValue(), employee.getTeamHourlyRate().doubleValue(), employee.getCurrency());
                editedTeam.addNewTeamMember(employee);
            }

            teamConfigurationEmployees.add(teamConfigurationEmployee);
            TeamConfiguration newTeamConfiguration = getNewEmployeeTeamConfiguration1(editedTeam);
            newTeamConfiguration.setTeamMembers(teamConfigurationEmployees);
            editedTeam.setActiveConfiguration(newTeamConfiguration);

        }
        /* id editedTeam is empty, puts zeros in values*/
        if(editedTeam.getTeamMembers().isEmpty()){
            TeamConfiguration tm = new TeamConfiguration(BigDecimal.ZERO, BigDecimal.ZERO, 0, 0,LocalDateTime.now() , true);
            editedTeam.setActiveConfiguration(tm);
        }

        Team editedTeamSaved = null;
        if(originalTeam.getActiveConfiguration()!=null){
            editedTeamSaved  = teamManager.saveTeamEditOperation(editedTeam, originalTeam.getActiveConfiguration().getId(), employeesToDelete, employees);
        }else{
            editedTeamSaved=teamManager.saveTeamEditOperation(editedTeam,0, employeesToDelete, employees);
        }

        if (editedTeamSaved != null) {
            teamsWithEmployees.remove(originalTeam.getId());
            teamsWithEmployees.put(editedTeamSaved.getId(), editedTeamSaved);
        }
    }

    /** Creates a new configuration for the team with recalculated rates */
    private TeamConfiguration getNewEmployeeTeamConfiguration1(Team team) {
        BigDecimal teamHourlyRate = teamManager.calculateTeamHourlyRateE(team);
        BigDecimal teamDayRate = teamManager.calculateTeamDayRateE(team);
        double grossMargin = 0;
        double markupMultiplier = 0;

        if (team.getActiveConfiguration() != null) {
            grossMargin = checkNullValues(team.getGrossMarginTemporary());
            markupMultiplier = checkNullValues(team.getMarkupMultiplierTemporary());

        }
        LocalDateTime savedDate = LocalDateTime.now();
        return new TeamConfiguration(teamDayRate, teamHourlyRate, grossMargin, markupMultiplier, savedDate, true);
    }




    @Override
    public void deleteRegion(Region region) throws RateException {
        boolean succeeded = regionManager.deleteRegion(region);
        if (succeeded) {
            regionsWithCountries.remove(region.getId());
        }
    }

    @Override
    public void addNewCountry(Country country, List<Team> teamsToAdd) throws RateException {
        List<Team> newTeams = countryLogic.checkNewTeams(teamsToAdd, teams);
        List<Team> existingTeams = countryLogic.checkExistingTeams(teamsToAdd, teams);
        List<Team> teamsToUpdate = countryLogic.checkTeamsToUpdate(teamsToAdd, teams);
        country = countryLogic.addCountry(country, existingTeams, newTeams, teamsToUpdate);
        countriesWithTeams.put(country.getId(), country);
        countries.put(country.getCountryName(), country);
        newTeams.forEach(team -> {
            teamsWithEmployees.put(team.getId(), team);
            teams.put(team.getId(), team);
        });
        teamsToUpdate.forEach(team -> {
            teamsWithEmployees.put(team.getId(), team);
            teams.put(team.getId(), team);
        });
    }

    @Override
    public void updateCountry(Country country, List<Team> teamsToAdd) throws RateException {
        List<Team> newTeams = countryLogic.checkNewTeams(teamsToAdd, teams);
        List<Team> existingTeams = countryLogic.checkExistingTeams(teamsToAdd, teams);
        List<Team> teamsToUpdate = countryLogic.checkTeamsToUpdate(teamsToAdd, teams);
        country = countryLogic.updateCountry(country, existingTeams, newTeams, teamsToUpdate);
        countriesWithTeams.get(country.getId()).setTeams(teamsToAdd);
        countries.put(country.getCountryName(), country);
        newTeams.forEach(team -> {
            teamsWithEmployees.put(team.getId(), team);
            teams.put(team.getId(), team);
        });
        teamsToUpdate.forEach(team -> {
            teamsWithEmployees.put(team.getId(), team);
            teams.put(team.getId(), team);
        });
    }

    @Override
    public void deleteCountry(Country country) throws RateException {
        boolean succeeded = countryLogic.deleteCountry(country);
        if (succeeded) {
            countriesWithTeams.remove(country.getId());
            countries.remove(country.getCountryName());
        }
    }



    @Override
    public void deleteTeam(Team team) throws RateException {
        boolean isSucceed = countryLogic.deleteTeam(team);
        if(isSucceed){
            teamsWithEmployees.remove(team.getId());
            teams.remove(team.getId());
        }
    }
 Executor executor = Executors.

}
