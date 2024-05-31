package easv.bll.EmployeesLogic;
import easv.be.*;
import easv.dal.employeeDao.EmployeesDAO;
import easv.dal.employeeDao.IEmployeeDAO;
import easv.exception.ErrorCode;
import easv.exception.RateException;
import javafx.collections.ObservableMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class EmployeeManager implements IEmployeeManager {
    private IEmployeeDAO employeeDAO;
    IRateCalculator rateCalculator;

    public EmployeeManager() throws RateException {
        this.employeeDAO = new EmployeesDAO();
        this.rateCalculator = new RateCalculator();
    }


    @Override
    public Employee addEmployee(Employee employee, Configuration configuration, List<Team> teams) throws RateException {
        Integer employeeID = employeeDAO.addEmployee(employee, configuration, teams);
        if (employeeID != null) {
            employee.setId(employeeID);
        }
        return employee;
    }


    @Override
    public Map<Integer, Employee> returnEmployees() throws RateException {
        Map<Integer, Employee> employees = employeeDAO.returnEmployees();
        return employees;
    }


    public List<Team> deleteEmployee(Employee employee, List<Team> employeeTeams) throws RateException {
        List<Team> teamsToModify = new ArrayList<>();

        // get employee utilization in teams , from the database
        try {
            Map<Integer, BigDecimal> employeeTeamUtilization = new HashMap<>(employeeDAO.getEmployeeUtilizationPerTeams(employee.getId()));
            employee.setUtilPerTeams(employeeTeamUtilization);
        } catch (RateException e) {
            throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
        }

        //create copies off the employee teams in order to remove the employee from their list ,
        // and  recalculate the overhead
        if (employee.getUtilPerTeams() != null || (!employee.getUtilPerTeams().isEmpty())) {
            for (Team team : employeeTeams) {
                Team teamToModify = new Team(team);
                //remove the employee from the team
                teamToModify.getEmployees().remove(employee);
                //remove the employee overhead from the team
                removeEmployeeOverheadFromTeam(employee, teamToModify);
                //remove employee from the active configuration
                removeEmployeeFromConfiguration(employee, teamToModify);
                teamToModify.getActiveConfiguration().setSavedDate(LocalDateTime.now());
                teamsToModify.add(teamToModify);
            }
        }

        boolean deleteOperationPerformed = employeeDAO.deleteEmployee(employee, teamsToModify);
        for (Team team : teamsToModify) {
            team.getTeamConfigurationsHistory().add(team.getActiveConfiguration());
        }

        if (deleteOperationPerformed) {
            return teamsToModify;
        } else {
            return Collections.emptyList();
        }
    }


    /**
     * remove the deleted employee from the current active configuration
     *
     * @param employee employee entity to be removed
     * @param team     team entity to remove employee from
     */

    private void removeEmployeeFromConfiguration(Employee employee, Team team) {
        TeamConfigurationEmployee teamConfigurationEmployeeToRemove = null;

        for (TeamConfigurationEmployee teamConfigurationEmployee : team.getActiveConfiguration().getTeamMembers()) {
            if (teamConfigurationEmployee.getEmployeeName().equals(employee.getName())) {
                teamConfigurationEmployeeToRemove = teamConfigurationEmployee;
                break;
            }
        }
        if (teamConfigurationEmployeeToRemove != null) {
            team.getActiveConfiguration().getTeamMembers().remove(teamConfigurationEmployeeToRemove);
        }
    }


    /**
     * filter the employees by the selected country
     */
    @Override
    public List<Employee> filterByCountry(Region region, List<Country> countries, Map<Integer, Employee> employees) {
        List<Team> teams = countries.stream().flatMap(e -> e.getTeams().stream()).toList();
        Map<Integer, Employee> employeesFiltered = new HashMap<>();
        for (Team team : teams) {
            if (team.getEmployees() != null) {
                for (Employee employee : team.getEmployees()) {
                    if (!employeesFiltered.containsKey(employee.getId())) {
                        setEmployeeRelatedInfo(employees, employee, employeesFiltered);
                    }
                }
            }
        }
        return employeesFiltered.values().stream().toList();
    }


    /**
     * filter teams by region
     */
    @Override
    public List<Team> filterTeamsByRegion(Region region, List<Country> countries) {
        return countries.stream().flatMap((e) -> e.getTeams().stream()).toList();
    }

    /**
     * set to the filtered employee the regions, countries,and teams value in order to be displayed
     */
    private static void setEmployeeRelatedInfo(Map<Integer, Employee> employees, Employee employee, Map<Integer, Employee> employeesFiltered) {
        employee.setRegions(employees.get(employee.getId()).getRegions());
        employee.setCountries(employees.get(employee.getId()).getCountries());
        employee.setTeams(employees.get(employee.getId()).getTeams());
        employeesFiltered.put(employee.getId(), employee);
    }

    @Override
    public BigDecimal calculateTeamDayRate(Team team) {
        return rateCalculator.calculateTeamDailyRate(team);
    }

    @Override
    public BigDecimal calculateTeamHourlyRate(Team team) {
        return rateCalculator.calculateTeamHourlyRate(team);
    }

    @Override
    public BigDecimal getEmployeeDayRateOnTeam(Employee employee, Team team) {
        return rateCalculator.calculateEmployeeDayRateOnTeam(employee, team);
    }

    @Override
    public BigDecimal getEmployeeHourlyRateOnTeam(Employee employee, Team team) {
        return rateCalculator.calculateEmployeeHourlyRateOnTeam(employee, team);
    }


    /**
     * update the country teams with the newest values
     */

    @Override
    public void updateCountryTeams(Collection<Country> values, List<Team> teamsWithoutEmployee) {

        Map<Integer, Team> teamsMap = new HashMap<>();
        for (Team team : teamsWithoutEmployee) {
            teamsMap.put(team.getId(), team);
        }
        for (Country country : values) {
            if (country.getTeams() != null) {
                for (Team team : country.getTeams()) {
                    Team teamModified = teamsMap.get(team.getId());
                    if (teamModified != null) {
                        team.setActiveConfiguration(teamModified.getActiveConfiguration());
                        team.setTeamMembers(teamModified.getTeamMembers());
                        team.setTeamConfigurationsHistory(teamModified.getTeamConfigurationsHistory());
                    }
                }
            }
        }
    }


    /**get employee utilization per teams from the database
     * @param employeeId id of the employee to retrieve for*/
    @Override
    public Map<Integer, BigDecimal> getEmployeeUtilizationInTeams(int employeeId) throws RateException {
        return employeeDAO.getEmployeeUtilizationPerTeams(employeeId);
    }


    /**
     * calculate the total overhead of the teams
     *
     * @param filteredTeams the  teams resulted from the filter operation
     */
    public BigDecimal calculateGroupTotalDayRate(List<Team> filteredTeams) {
        System.out.println(filteredTeams.size());
        BigDecimal teamsDayRateSum = BigDecimal.ZERO;
        for (Team teams : filteredTeams) {
            TeamConfiguration teamConfiguration = teams.getActiveConfiguration();
            if (teamConfiguration != null) {
                BigDecimal teamDayRate = teamConfiguration.getTeamDayRate();
                System.out.println(teamDayRate + "teamDayRate");
                if (teamDayRate != null) {
                    teamsDayRateSum = teamsDayRateSum.add(teamDayRate);
                }
            }
        }
        return teamsDayRateSum;
    }


    /**
     * calculate the teams total  hourly rate
     *
     * @param filteredTeams the  teams resulted from the filter operation
     */
    public BigDecimal calculateGroupTotalHourRate(List<Team> filteredTeams) {
        BigDecimal teamsHourRateSum = BigDecimal.ZERO;
        for (Team teams : filteredTeams) {
            TeamConfiguration teamConfiguration = teams.getActiveConfiguration();
            if (teamConfiguration != null) {
                BigDecimal teamHourlyRate = teamConfiguration.getTeamHourlyRate();
                if (teamHourlyRate != null) {
                    teamsHourRateSum = teamsHourRateSum.add(teamHourlyRate);
                }
            }
        }
        return teamsHourRateSum;
    }


    @Override
    public List<Employee> performSearchOperation(Collection<Employee> employees, String filter) {
        String filterToLowerCase = filter.toLowerCase();
        return employees.stream().filter((employee) -> {
            String name = employee.getName().toLowerCase();
            return name.contains(filterToLowerCase);
        }).toList();
    }

    /**
     * sort employees by name  alphabetically
     */
    public List<Employee> sortedEmployeesByName(Collection<Employee> values) {
        return values.stream().sorted(Comparator.comparing(Employee::getName)).collect(Collectors.toList());
    }

    /**
     * check if the editOperation was performed on the employee object
     *
     * @param originalEmployee the original employee object
     * @param editedEmployee   the edited employee object
     */

    public boolean isEmployeeEdited(Employee originalEmployee, Employee editedEmployee) {
        boolean isActiveConfigurationEdited = !originalEmployee.getActiveConfiguration().isEqualTo(editedEmployee.getActiveConfiguration());
        boolean areEmployeeValuesEdited = !originalEmployee.equals(editedEmployee);
        return isActiveConfigurationEdited || areEmployeeValuesEdited;
    }


    /**
     * save the employee edit operation
     *
     * @param editedEmployee   the edited employee
     * @param originalEmployee employee before the edit operation
     * @param employeeTeams    the employee teams that will be affected by the edit operation
     */
    public Employee saveEditOperation(Employee editedEmployee, Employee originalEmployee, List<Team> employeeTeams) throws RateException {
        List<Team> employeeTeamsNewConfigurations = new ArrayList<>();
        List<Team> validTeams = new ArrayList<>();
        //get the employee utilization per teams
        try {
            Map<Integer, BigDecimal> employeeTeamUtilization = new HashMap<>(employeeDAO.getEmployeeUtilizationPerTeams(editedEmployee.getId()));
            editedEmployee.setUtilPerTeams(employeeTeamUtilization);
        } catch (RateException e) {
            return null;
        }

        // Create copies of the original employee teams
        for (Team team : employeeTeams) {
            Team teamToEdit = new Team(team);
            teamToEdit.setActiveConfiguration(team.getActiveConfiguration());
            employeeTeamsNewConfigurations.add(teamToEdit);
        }

        //calculate employee new  day rate and hourly rate
        BigDecimal dayRateEmployee = rateCalculator.calculateEmployeeDayRateWithoutUtilization(editedEmployee);
        BigDecimal employeeHourRate = rateCalculator.calculateEmployeeHourlyRateWithoutUtilization(editedEmployee);


        // Replace the originalEmployee with the edited one in the copies to calculate the new team rates
        for (Team team : employeeTeamsNewConfigurations) {
            for (int i = 0; i < team.getTeamMembers().size(); i++) {
                if (team.getTeamMembers().get(i).getId() == editedEmployee.getId()) {
                    team.getTeamMembers().set(i, editedEmployee);
                    break;
                }
            }
        }

        // Calculate the new team day rates
        for (Team team : employeeTeamsNewConfigurations) {
            if (team.getActiveConfiguration() != null) {
                computeTeamNewDayRate(team, editedEmployee, originalEmployee, dayRateEmployee, employeeHourRate);
                validTeams.add(team);
            }
        }


        editedEmployee.getActiveConfiguration().setDayRate(dayRateEmployee);
        editedEmployee.getActiveConfiguration().setHourlyRate(employeeHourRate);
        editedEmployee.setTeams(validTeams);

        for (Team team : employeeTeamsNewConfigurations) {
            replaceEmployeeInTeamConfiguration(team, originalEmployee, editedEmployee);
            team.getTeamConfigurationsHistory().add(team.getActiveConfiguration());
        }

        return employeeDAO.saveEditOperation(editedEmployee, originalEmployee.getActiveConfiguration().getConfigurationId());
    }


    /**
     * calculate the team new  day rate and hourly rate based on the new edited employee values
     */
    private void computeTeamNewDayRate(Team team, Employee editedEmployee, Employee originalEmployee, BigDecimal newEmployeeDayRate, BigDecimal newEmployeeHourRate) {

        //extract the employee utilization per team
        BigDecimal employeeUtilizationOnTeam = editedEmployee.getUtilPerTeams().get(team.getId());

        BigDecimal removedOldEmployeeDayRate;
        BigDecimal removedOldEmployeeHourRate;
        // if employee is utilized 100% remove his whole overhead from the team
        if (employeeUtilizationOnTeam.compareTo(new BigDecimal(100)) == 0) {
            removedOldEmployeeDayRate = team.getActiveConfiguration().getTeamDayRate().subtract(originalEmployee.getActiveConfiguration().getDayRate());
            removedOldEmployeeHourRate = team.getActiveConfiguration().getTeamHourlyRate().subtract(originalEmployee.getActiveConfiguration().getHourlyRate());
        } else {
            //extract the employee old rates from the team
            removedOldEmployeeDayRate = team.getActiveConfiguration().getTeamDayRate().multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
            removedOldEmployeeHourRate = team.getActiveConfiguration().getTeamHourlyRate().multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
        }

        //calculate  employee new overhead on the team
        BigDecimal teamEmployeeNewDayRate;
        BigDecimal teamEmployeeNewHourRate;
        if (employeeUtilizationOnTeam.compareTo(new BigDecimal(100)) == 0) {
            teamEmployeeNewDayRate = removedOldEmployeeDayRate.add(newEmployeeDayRate);
            teamEmployeeNewHourRate = removedOldEmployeeHourRate.add(newEmployeeHourRate);
        } else {
            teamEmployeeNewDayRate = removedOldEmployeeDayRate.add(newEmployeeDayRate.multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP))));
            teamEmployeeNewHourRate = removedOldEmployeeHourRate.add(newEmployeeHourRate.multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP))));
        }

        //add employee new rates on the team
        team.getActiveConfiguration().setTeamDayRate(teamEmployeeNewDayRate);
        team.getActiveConfiguration().setTeamHourlyRate(teamEmployeeNewHourRate);
    }

    /**
     * calculate the team new dayRate and hourly rate, by removing the deleted employee overhead
     */
    private void removeEmployeeOverheadFromTeam(Employee employee, Team team) {
        // get the employee utilization in the team
        BigDecimal employeeUtilizationOnTeam = employee.getUtilPerTeams().get(team.getId());
        // remove the employee overhead from the team;
        BigDecimal removeOldEmployeeDayRate = team.getActiveConfiguration().getTeamDayRate().multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
        BigDecimal removeEmployeeHourRate = team.getActiveConfiguration().getTeamHourlyRate().multiply(BigDecimal.ONE.subtract(employeeUtilizationOnTeam.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)));
        //calculate new rates
        BigDecimal teamNewDayRate = team.getActiveConfiguration().getTeamDayRate().subtract(removeOldEmployeeDayRate);
        BigDecimal teamNewHourRate = team.getActiveConfiguration().getTeamHourlyRate().subtract(removeEmployeeHourRate);

        //set new rates on the team
        team.getActiveConfiguration().setTeamDayRate(teamNewDayRate);
        team.getActiveConfiguration().setTeamHourlyRate(teamNewHourRate);
    }


    /**
     * add the edited employee values into the  team configuration history
     */
    private void replaceEmployeeInTeamConfiguration(Team team, Employee oldEmployee, Employee newEmployee) throws RateException {
        List<TeamConfigurationEmployee> teamMembers = team.getActiveConfiguration().getTeamMembers();
        for (int i = 0; i < teamMembers.size(); i++) {
            try {
                if (teamMembers.get(i).getEmployeeName().equals(oldEmployee.getName())) {
                    TeamConfigurationEmployee newTeamMember = new TeamConfigurationEmployee(newEmployee.getName(), newEmployee.getActiveConfiguration().getDayRate().doubleValue(), newEmployee.getActiveConfiguration().getHourlyRate().doubleValue(), newEmployee.getCurrency());
                    teamMembers.set(i, newTeamMember);
                    break;
                }
            } catch (Exception e) {
                throw new RateException(e.getMessage(), e, ErrorCode.OPERATION_DB_FAILED);
            }
        }
    }


    /**
     * calculate the day rate for an employee
     */
    public BigDecimal getDayRate(Employee employee) {
        return rateCalculator.calculateEmployeeTotalDayRate(employee);
    }

    /**
     * calculate the hourly rate for an employee
     */
    public BigDecimal getHourlyRate(Employee employee, double configurableHours) {
        return rateCalculator.calculateEmployeeTotalHourlyRate(employee, configurableHours);
    }

    /**
     * retrieve all the teams with associated employees from the database
     */
    @Override
    public Map<Integer, Team> getTeamWithEmployees() throws RateException {
        return employeeDAO.getTeamsWithEmployees();
    }

    @Override
    public Map<Integer, Country> getCountriesWithTeams(Map<Integer, Team> teams) throws RateException {
        return employeeDAO.getCountriesWithTeams(teams);
    }

    @Override
    public Map<Integer, Region> getRegionsWithCountries(ObservableMap<Integer, Country> countriesWithTeams) throws RateException {
        return employeeDAO.getRegionsWithCountries(countriesWithTeams);
    }

    @Override
    public Integer addTeamConfiguration(TeamConfiguration teamConfiguration, Team team, Map<Integer, BigDecimal> employeeDayRate, Map<Integer, BigDecimal> employeeHourlyRate, int oldTeamConfigurationID) throws   RateException {
        return employeeDAO.addNewTeamConfiguration(teamConfiguration, team, employeeDayRate, employeeHourlyRate, oldTeamConfigurationID);
    }


    /**
     * filter the employees by the selected country from the filter
     */
    @Override
    public List<Employee> filterTeamsByCountry(List<Team> countryTeams, ObservableMap<Integer, Employee> employees) {
        List<Employee> employeesFromCountryTeams = new ArrayList<>();
        if (countryTeams == null) {
            return employeesFromCountryTeams; // Return an empty list if countryTeams is null
        }

        countryTeams.stream()
                .flatMap(team -> Optional.ofNullable(team.getEmployees()).stream().flatMap(Collection::stream))
                .forEach(employee -> {
                    if (employee != null) {
                        employeesFromCountryTeams.add(employees.get(employee.getId()));
                    }
                });

        return employeesFromCountryTeams;
    }


    /**
     * filter the employees for the selected team from the teams filter
     */
    @Override
    public List<Employee> filterEmployeesByTeam(Team selectedTeam, ObservableMap<Integer, Employee> employees) {
        List<Employee> teamEmployees = new ArrayList<>();
        if (selectedTeam != null && selectedTeam.getEmployees() != null) {
            selectedTeam.getEmployees().forEach(e -> {
                Employee emp = employees.get(e.getId());
                if (emp != null) {
                    teamEmployees.add(emp);
                }
            });
        }
        return teamEmployees;
    }

}
