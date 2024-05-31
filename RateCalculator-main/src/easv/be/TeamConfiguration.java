package easv.be;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TeamConfiguration {
    private int id;
    private BigDecimal teamDayRate;
    private BigDecimal teamHourlyRate;
    private double grossMargin;
    private double markupMultiplier;
    private LocalDateTime savedDate;
    private List<TeamConfigurationEmployee> teamMembers;

    private boolean active;



    /**  TeamConfiguration copy constructor */
    public TeamConfiguration (TeamConfiguration teamConfiguration){
        if(teamConfiguration!=null){
            this.id= teamConfiguration.getId();
            this.teamDayRate = teamConfiguration.getTeamDayRate();
            this.teamHourlyRate = teamConfiguration.getTeamHourlyRate();
            this.grossMargin = teamConfiguration.getGrossMargin();
            this.markupMultiplier = teamConfiguration.getMarkupMultiplier();
            this.savedDate = teamConfiguration.savedDate;
            this.teamMembers =  new ArrayList<>();
            if(teamConfiguration.getTeamMembers()!=null){
                teamMembers.addAll(teamConfiguration.getTeamMembers());
            }
            this.active = teamConfiguration.isActive();
        }else{
            this.teamDayRate=BigDecimal.ZERO;
            this.teamHourlyRate=BigDecimal.ZERO;
        }
    }


    public TeamConfiguration(BigDecimal teamDayRate, BigDecimal teamHourlyRate, double grossMargin, double markupMultiplier, LocalDateTime savedDate, List<TeamConfigurationEmployee> teamMembers, boolean active) {
        this.teamDayRate = teamDayRate;
        this.teamHourlyRate = teamHourlyRate;
        this.grossMargin = grossMargin;
        this.markupMultiplier = markupMultiplier;
        this.savedDate = savedDate;
        this.teamMembers = teamMembers;
        this.active = active;
    }

    public TeamConfiguration(BigDecimal teamDayRate, BigDecimal teamHourlyRate, double grossMargin, double markupMultiplier, LocalDateTime savedDate, boolean active) {
        this.teamDayRate = teamDayRate;
        this.teamHourlyRate = teamHourlyRate;
        this.grossMargin = grossMargin;
        this.markupMultiplier = markupMultiplier;
        this.savedDate = savedDate;
        this.active = active;
        this.teamMembers = new ArrayList<>();
    }


    /**
     * add  team member to team history
     *
     * @param employee the new employee to be added to the team history
     */
    public void addEmployeeToTeamHistory(TeamConfigurationEmployee employee) {
        this.teamMembers.add(employee);
    }


    /**
     * remove  team member from team history
     *
     * @param employee the employee to be removed from the team history
     */

    public void removeEmployeeFromTeamHistory(TeamConfigurationEmployee employee) {
        this.teamMembers.remove(employee);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getTeamDayRate() {
        return teamDayRate;
    }

    public void setTeamDayRate(BigDecimal teamDayRate) {
        this.teamDayRate = teamDayRate;
    }

    public BigDecimal getTeamHourlyRate() {
        return teamHourlyRate;
    }

    public void setTeamHourlyRate(BigDecimal teamHourlyRate) {
        this.teamHourlyRate = teamHourlyRate;
    }

    public double getGrossMargin() {
        return grossMargin;
    }

    public void setGrossMargin(double grossMargin) {
        this.grossMargin = grossMargin;
    }

    public double getMarkupMultiplier() {
        return markupMultiplier;
    }

    public void setMarkupMultiplier(double markupMultiplier) {
        this.markupMultiplier = markupMultiplier;
    }

    public LocalDateTime getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(LocalDateTime savedDate) {
        this.savedDate = savedDate;
    }

    public List<TeamConfigurationEmployee> getTeamMembers() {
        return teamMembers;
    }

    public void setTeamMembers(List<TeamConfigurationEmployee> teamMembers) {
        this.teamMembers = teamMembers;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    public LocalDate getSavedDateWithoutTime() {
        return savedDate.toLocalDate();
    }
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return savedDate.format(formatter) + " " ;
    }


}
