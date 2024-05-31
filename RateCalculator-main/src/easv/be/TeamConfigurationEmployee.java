package easv.be;

import java.util.Objects;

public class TeamConfigurationEmployee {
    private String employeeName ;
    private double employeeDailyRate;
    private double employeeHourlyRate;
    private Currency currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeamConfigurationEmployee that = (TeamConfigurationEmployee) o;
        return Double.compare(employeeDailyRate, that.employeeDailyRate) == 0 && Double.compare(employeeHourlyRate, that.employeeHourlyRate) == 0 && Objects.equals(employeeName, that.employeeName) && currency == that.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeName, employeeDailyRate, employeeHourlyRate, currency);
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TeamConfigurationEmployee(String employeeName, double employeeDailyRate, double employeeHourlyRate, Currency currency) {
        this.employeeName = employeeName;
        this.employeeDailyRate = employeeDailyRate;
        this.employeeHourlyRate = employeeHourlyRate;
        this.currency= currency;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public double getEmployeeDailyRate() {
        return employeeDailyRate;
    }

    public void setEmployeeDailyRate(double employeeDailyRate) {
        this.employeeDailyRate = employeeDailyRate;
    }

    public double getEmployeeHourlyRate() {
        return employeeHourlyRate;
    }

    public void setEmployeeHourlyRate(double employeeHourlyRate) {
        this.employeeHourlyRate = employeeHourlyRate;
    }

    @Override
    public String toString() {
        return currency.toString();
    }
}
