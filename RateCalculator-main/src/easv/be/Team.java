package easv.be;

import java.math.BigDecimal;
import java.util.*;

public class Team {
    private String teamName;
    private int id;
    private List<Employee> teamMembers;
    private List<TeamConfiguration> teamConfigurationsHistory;
    private TeamConfiguration activeConfiguration;
    private List<Country> countries;
    private List<Region> regions;
    private BigDecimal utilizationPercentage;
    private Currency currency;
    private double grossMarginTemporary;
    private double markupMultiplierTemporary;


    public double getGrossMarginTemporary() {
        return grossMarginTemporary;
    }

    public void setGrossMarginTemporary(double grossMarginTemporary) {
        this.grossMarginTemporary = grossMarginTemporary;
    }

    public double getMarkupMultiplierTemporary() {
        return markupMultiplierTemporary;
    }

    public void setMarkupMultiplierTemporary(double markupMultiplierTemporary) {
        this.markupMultiplierTemporary = markupMultiplierTemporary;
    }

    public Team(String teamName) {
        this.teamName = teamName;
    }

    public Team(String teamName, int id) {
        this.teamName = teamName;
        this.id = id;
    }

    public Team(String teamName, Currency currency) {
        this.teamName = teamName;
        this.currency = currency;
    }

    public Team(String teamName, int id, Currency currency) {
        this.teamName = teamName;
        this.id = id;
        this.currency = currency;
    }

    public Team(String teamName, int id, BigDecimal utilizationPercentage) {
        this.teamName = teamName;
        this.id = id;
        this.utilizationPercentage = utilizationPercentage;
    }

    public Team(String teamName, Currency currency, int id, List<Employee> teamMembers, List<TeamConfiguration> teamConfigurationsHistory ) {
        this(teamName, id);
        this.teamMembers = teamMembers;
        this.teamConfigurationsHistory = teamConfigurationsHistory;
        this.currency = currency;

    }

    /**
     * copy constructor, this constructor will not do a deep copy off the employees list,
     * if operations are done on the team employees, they will be reflected in the employee
     * so do not perform employee edit operations while this copy constructor is used
     */
    public Team(Team team) {
        this(team.getTeamName(),team.getCurrency(), team.getId(), team.teamMembers, team.teamConfigurationsHistory);
        this.teamName = team.getTeamName();
        this.currency = team.getCurrency();
        this.id= team.getId();
        this.teamMembers = new ArrayList<>();
        teamMembers.addAll(team.getTeamMembers());
        this.teamConfigurationsHistory = new ArrayList<>();
        for (TeamConfiguration config : team.getTeamConfigurationsHistory()) {
            this.teamConfigurationsHistory.add(new TeamConfiguration(config));
        }
        this.utilizationPercentage = team.getUtilizationPercentage();
        this.countries = team.getCountries();
        this.regions= team.getRegions();
        this.activeConfiguration=  new TeamConfiguration(team.getActiveConfiguration());
        this.currency= team.getCurrency();
    }


    /**
     * to not be used in equal comparison , it is used for the view only
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team1 = (Team) o;
        return Objects.equals(teamName, team1.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamName,id);
    }

    public List<TeamConfiguration> getTeamConfigurationsHistory() {
        return teamConfigurationsHistory;
    }

    public void setTeamConfigurationsHistory(List<TeamConfiguration> teamConfigurationsHistory) {
        this.teamConfigurationsHistory = teamConfigurationsHistory;
    }

    public TeamConfiguration getActiveConfiguration() {
        return activeConfiguration;
    }

    public void setActiveConfiguration(TeamConfiguration activeConfiguration) {
        this.activeConfiguration = activeConfiguration;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String team) {
        this.teamName = team;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return teamName;
    }

    public List<Employee> getEmployees() {
        return teamMembers;
    }

    public void setEmployees(List<Employee> employees) {
        this.teamMembers = employees;
    }

    public boolean addNewTeamMember(Employee employee) {
        return this.teamMembers.add(employee);
    }

    public boolean removeTeamMember(Employee employee) {
        return this.teamMembers.remove(employee);
    }

    public boolean addNewTeamConfiguration(TeamConfiguration teamConfiguration) {
        return this.teamConfigurationsHistory.add(teamConfiguration);
    }

    public boolean removeTeamConfiguration(TeamConfiguration teamConfiguration) {
        return this.teamConfigurationsHistory.remove(teamConfiguration);
    }

    public List<Employee> getTeamMembers() {
        return teamMembers;
    }

    public Employee getTeamMember(int Id) {
        for(Employee employee: teamMembers){
            if(employee.getId() == Id){
                return employee;
            }
        }
        return null;
    }
    public void replaceTeaMember(Employee employee) {
        Iterator<Employee> iterator = teamMembers.iterator();
        while (iterator.hasNext()) {
            Employee employeeReplace = iterator.next();
            if (employeeReplace.getId() == employee.getId()) {
                iterator.remove();
                teamMembers.add(employee);
                break;
            }
        }
    }

    public void setTeamMembers(List<Employee> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public BigDecimal getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(BigDecimal utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
