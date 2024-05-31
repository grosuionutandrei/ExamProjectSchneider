package easv.dal.teamDao;
import easv.be.Employee;
import easv.be.RateType;
import easv.be.Team;
import easv.exception.RateException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ITeamDao {

    boolean savePerformedDistribution(Map<Team, Map<RateType, BigDecimal>> receivedTeams, Team selectedTeamToDistributeFrom) throws RateException;
    Team saveEditOperationTeam(Team editedTeam, int idOriginalTeam, List<Employee> employeesToDelete, List<Employee> employees) throws RateException;
}
