package easv.bll.EmployeesLogic;
import easv.be.Employee;
import easv.be.Team;
import java.math.BigDecimal;


public interface IRateCalculator {




    /**
     * calculate the day rate for the employee
     */
    BigDecimal calculateEmployeeTotalDayRate(Employee employee);

    /**
     * calculate the hourly rate for an employee
     */
    BigDecimal calculateEmployeeTotalHourlyRate(Employee employee, double configurableHours);

    BigDecimal calculateTeamDailyRate(Team team);

    BigDecimal calculateTeamHourlyRate(Team team);

    BigDecimal calculateEmployeeDayRateOnTeam(Employee employee, Team team);

    BigDecimal calculateEmployeeHourlyRateOnTeam(Employee employee, Team team);

    BigDecimal calculateEmployeeHourlyRateOnTeamE(Employee employee, Team team);

    BigDecimal calculateEmployeeDayRateOnTeamE(Employee employee, Team team);

    BigDecimal calculateTeamDailyRateE(Team team);


    BigDecimal calculateTeamHourlyRateE(Team team);


    /**
     * calculate employee day rate without the utilization percentage
     */
    BigDecimal calculateEmployeeDayRateWithoutUtilization(Employee employee);

    /**
     * calculate employee hour  rate without the utilization percentage
     */
    BigDecimal calculateEmployeeHourlyRateWithoutUtilization(Employee employee);

}
