package easv.bll.EmployeesLogic;

import easv.be.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;


public class RateCalculator implements IRateCalculator {
    private long HoursInDay;


    /**
     * this method is calculating the day rate for an employee
     *
     * @param employee the employee to calculate for
     *                 if no values are present for the employee returns BigDecimal.ZERO
     */
    public BigDecimal calculateEmployeeTotalDayRate(Employee employee) {
        BigDecimal annualSalary = employee.getActiveConfiguration().getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getActiveConfiguration().getOverheadMultiplier().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32).add(BigDecimal.ONE);
        BigDecimal fixedAnnualAmount = employee.getActiveConfiguration().getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getActiveConfiguration().getWorkingHours();
        BigDecimal utilizationPercentage = employee.getActiveConfiguration().getUtilizationPercentage().divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        BigDecimal dayRate = BigDecimal.ZERO;

        if(employee.getActiveConfiguration() != null){
            HoursInDay = (long) employee.getActiveConfiguration().getDayWorkingHours();
        } else {
            HoursInDay = 8;
        }

        if (employee.getEmployeeType() == EmployeeType.Overhead) {
          dayRate = (((annualSalary.multiply(overheadMultiplier)).add(fixedAnnualAmount)).multiply(utilizationPercentage))
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(HoursInDay));
        } else {
            dayRate = ((annualSalary.multiply(overheadMultiplier)).add(fixedAnnualAmount)
                    .multiply(BigDecimal.ONE.subtract(utilizationPercentage)))
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(HoursInDay));
        }
        return dayRate.setScale(2, RoundingMode.HALF_UP);
    }




    /** calculate employee day rate without the utilization percentage */
    public BigDecimal calculateEmployeeDayRateWithoutUtilization(Employee employee) {
        if(employee.getActiveConfiguration() != null){
            HoursInDay = (long) employee.getActiveConfiguration().getDayWorkingHours();
        } else {
            HoursInDay = 8;
        }

        BigDecimal hourlyRate = calculateEmployeeHourlyRateWithoutUtilization(employee);
        BigDecimal dayRate = hourlyRate.multiply(BigDecimal.valueOf(HoursInDay));

        return dayRate.setScale(2, RoundingMode.HALF_UP);
    }

    /** calculate employee hour  rate without the utilization percentage  */
    public BigDecimal calculateEmployeeHourlyRateWithoutUtilization(Employee employee) {
        BigDecimal annualSalary = employee.getActiveConfiguration().getAnnualSalary();
        BigDecimal overheadMultiplier = employee.getActiveConfiguration().getOverheadMultiplier()
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL32).add(BigDecimal.ONE);
        BigDecimal fixedAnnualAmount = employee.getActiveConfiguration().getFixedAnnualAmount();
        BigDecimal annualEffectiveWorkingHours = employee.getActiveConfiguration().getWorkingHours();

        BigDecimal hourlyRate = BigDecimal.ZERO;

        if (employee.getEmployeeType() == EmployeeType.Overhead) {
            hourlyRate = (annualSalary.multiply(overheadMultiplier).add(fixedAnnualAmount))
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP);
    }
        else {
            hourlyRate = annualSalary
                    .divide(annualEffectiveWorkingHours, 2, RoundingMode.HALF_UP);
        }
        return hourlyRate.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * calculate hourly rate for an employee based on the hours of a configurable day
     *
     * @param employee          the employee to calculate for
     * @param configurableHours the configurable hours  of an working day
     *                          if no configuration is present for the employee it returns BigDecimal.ZERO
     */
    public BigDecimal calculateEmployeeTotalHourlyRate(Employee employee, double configurableHours) {
        BigDecimal hourlyRate = BigDecimal.ZERO;
        if(employee.getActiveConfiguration() != null){
            HoursInDay = (long) employee.getActiveConfiguration().getDayWorkingHours();
        } else {
            HoursInDay = 8;
        }
        if (configurableHours == 0) {
            hourlyRate = calculateEmployeeTotalDayRate(employee)
                    .divide(BigDecimal.valueOf(HoursInDay), RoundingMode.HALF_UP);
        } else {
            hourlyRate = calculateEmployeeTotalDayRate(employee)
                    .divide(BigDecimal.valueOf(configurableHours), RoundingMode.HALF_UP);
        }
        return hourlyRate.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmployeeDayRateOnTeam(Employee employee, Team team){
        if(employee.getActiveConfiguration() != null){
            HoursInDay = (long) employee.getActiveConfiguration().getDayWorkingHours();
        } else {
            HoursInDay = 8;
        }
        BigDecimal hourlyRate = calculateEmployeeHourlyRateOnTeam(employee, team);
        return hourlyRate.multiply(BigDecimal.valueOf(HoursInDay)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmployeeHourlyRateOnTeam(Employee employee, Team team){
        BigDecimal hourlyRate = employee.getActiveConfiguration().getHourlyRate();
        BigDecimal utilizationPercentage = employee.getUtilPerTeams().get(team.getId()).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        return (hourlyRate.multiply(utilizationPercentage)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateEmployeeHourlyRateOnTeamE(Employee employee, Team team){
        BigDecimal hourlyRate = employee.getActiveConfiguration().getHourlyRate();
        BigDecimal utilizationPercentage = employee.getUtilPerTeams().get(team.getId()).divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);
        return (hourlyRate.multiply(utilizationPercentage)).setScale(2, RoundingMode.HALF_UP);

    }

    public BigDecimal calculateEmployeeDayRateOnTeamE(Employee employee, Team team){
        if(employee.getActiveConfiguration() != null){
            HoursInDay = (long) employee.getActiveConfiguration().getDayWorkingHours();
        } else {
            HoursInDay = 8;
        }
        BigDecimal hourlyRate = calculateEmployeeHourlyRateOnTeamE(employee, team);
        return hourlyRate.multiply(BigDecimal.valueOf(HoursInDay)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateTeamDailyRate(Team team) {
        BigDecimal totalDayRate = BigDecimal.ZERO;
        double markupMultiplier = 0;
        double grossMargin = 0;
        if(team.getActiveConfiguration() != null) {
            markupMultiplier = team.getActiveConfiguration().getMarkupMultiplier();
            grossMargin = team.getActiveConfiguration().getGrossMargin();
        }
        for (Employee employee : team.getEmployees()) {
            BigDecimal dayRate = calculateEmployeeDayRateOnTeam(employee, team);
            if (markupMultiplier > 0) {
                BigDecimal markedUpHourlyRate = dayRate.multiply(BigDecimal.valueOf(markupMultiplier/100));
                totalDayRate = totalDayRate.add(markedUpHourlyRate);
            } else {
                totalDayRate = totalDayRate.add(dayRate);
            }
        }
        if(grossMargin > 0){
            return totalDayRate.divide(BigDecimal.valueOf(grossMargin/100), 2, RoundingMode.HALF_UP);
        }
        return totalDayRate;
    }


    public BigDecimal calculateTeamHourlyRate(Team team) {
        BigDecimal totalHourlyRate = BigDecimal.ZERO;
        double markupMultiplier = 0;
        double grossMargin = 0;
        if(team.getActiveConfiguration() != null) {
            markupMultiplier = team.getActiveConfiguration().getMarkupMultiplier();
            grossMargin = team.getActiveConfiguration().getGrossMargin();
        }
        for (Employee employee : team.getEmployees()) {
            BigDecimal hourlyRate = calculateEmployeeHourlyRateOnTeam(employee, team);
            if (markupMultiplier > 0) {
                BigDecimal markedUpHourlyRate = hourlyRate.multiply(BigDecimal.valueOf(markupMultiplier/100));
                totalHourlyRate = totalHourlyRate.add(markedUpHourlyRate);
            } else {
                totalHourlyRate = totalHourlyRate.add(hourlyRate);
            }
        }
        if(grossMargin > 0){
            return totalHourlyRate.divide(BigDecimal.valueOf(grossMargin/100), 2, RoundingMode.HALF_UP);
        }
        return totalHourlyRate;
    }
    public BigDecimal calculateTeamDailyRateE(Team team) {
        BigDecimal totalDayRate = BigDecimal.ZERO;
        double markupMultiplier = 0;
        double grossMargin = 0;
        if(team.getActiveConfiguration() != null) {
            markupMultiplier = team.getMarkupMultiplierTemporary();
            grossMargin = team.getGrossMarginTemporary();
        }
        for (Employee employee : team.getEmployees()) {
            BigDecimal dayRate = calculateEmployeeDayRateOnTeamE(employee, team);
            if (markupMultiplier > 0) {
               /* 1 represents the additional cost added to the base rate*/
                BigDecimal markedUpHourlyRate = dayRate.multiply(BigDecimal.valueOf(1 + markupMultiplier/100));
                totalDayRate = totalDayRate.add(markedUpHourlyRate);
            } else {
                totalDayRate = totalDayRate.add(dayRate);
            }
        }
        if(grossMargin > 0){
            return totalDayRate.divide(BigDecimal.valueOf(1 - grossMargin/100), 2, RoundingMode.HALF_UP);
        }
        return totalDayRate;
    }

    public BigDecimal calculateTeamHourlyRateE(Team team) {
        BigDecimal totalHourlyRate = BigDecimal.ZERO;
        double markupMultiplier = 0;
        double grossMargin = 0;
        if(team.getActiveConfiguration() != null) {
            markupMultiplier = team.getMarkupMultiplierTemporary();
            grossMargin = team.getGrossMarginTemporary();
        }
        for (Employee employee : team.getEmployees()) {
            BigDecimal hourlyRate = calculateEmployeeHourlyRateOnTeamE(employee, team);
            if (markupMultiplier > 0) {
                BigDecimal markedUpHourlyRate = hourlyRate.multiply(BigDecimal.valueOf(1 + markupMultiplier/100));
                totalHourlyRate = totalHourlyRate.add(markedUpHourlyRate);
            } else {
                totalHourlyRate = totalHourlyRate.add(hourlyRate);
            }
        }
        if(grossMargin > 0){
            return totalHourlyRate.divide(BigDecimal.valueOf(1 - grossMargin/100), 2, RoundingMode.HALF_UP);
        }
        return totalHourlyRate;
    }

}
