package easv.bll.TeamLogic;
import easv.be.*;
import easv.exception.RateException;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ITeamLogic  {



  OverheadComputationPair <String ,BigDecimal> computeRegionOverhead(Region region);

    DistributionValidation  validateDistributionInputs(Map<Team, String> insertedDistributionPercentageFromTeams,Team selectedTeamToDistributeFrom);

    /**calculate the total overhead inserted for the valid inputs*/
    double calculateTotalOverheadInsertedForValidInputs(Map<Team, String> insertedDistributionPercentageFromTeams);


    /**perform the simulation computation*/
    Map<OverheadHistory, List<Team>> performSimulationComputation(Team selectedTeamToDistributeFrom, Map<Team, String> insertedDistributionPercentageFromTeams,Map<Integer , Team> originalTeams);

    /**save the distribution operation performed*/
    Map<OverheadHistory, List<Team>> saveDistributionOperation(Map<Team, String> insertedDistributionPercentageFromTeams, Team selectedTeamToDistributeFrom, boolean simulationPerformed, Map<Integer, Team> initialTeamsValues) throws RateException;

    /**perform the search operation for the teams*/
    List<Team> performSearchTeamFilter(String filter, Collection<Team> teams);
    Team saveTeamEditOperation(Team editedTeam, int idOriginalTeam, List<Employee> employeesToDelete, List<Employee> employees) throws RateException;

    BigDecimal getEmployeeHourlyRateOnTeamE(Employee employee, Team team);
    BigDecimal getEmployeeDayRateOnTeamE(Employee employee, Team team);
    BigDecimal calculateTeamHourlyRateE(Team team);
    BigDecimal calculateTeamDayRateE(Team team);



}
