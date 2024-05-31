package easv;

import easv.be.Configuration;
import easv.be.Currency;
import easv.be.Employee;
import easv.be.EmployeeType;
import easv.bll.EmployeesLogic.RateCalculator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

class RateCalculatorTest {

    private RateCalculator rateCalculator;
    private Employee employeeOverhead;

    private Employee employeeResource;
    private Configuration configuration;

    @BeforeEach
    void setUp() {
        rateCalculator = new RateCalculator();
        configuration = new Configuration();
        configuration.setAnnualSalary(new BigDecimal("50000"));
        configuration.setOverheadMultiplier(new BigDecimal("20"));
        configuration.setFixedAnnualAmount(new BigDecimal("10000"));
        configuration.setWorkingHours(new BigDecimal("2000"));
        configuration.setDayWorkingHours(8);
        configuration.setUtilizationPercentage(new BigDecimal(70));

        employeeOverhead = new Employee("Test employee", EmployeeType.Overhead, Currency.USD);
        employeeOverhead.setActiveConfiguration(configuration);

        employeeResource = new Employee("Test Resource", EmployeeType.Resource, Currency.USD);
        employeeResource.setActiveConfiguration(configuration);


    }


    @Test
    void calculateEmployeeTotalDayRate() {
        BigDecimal employeeDayRate = this.rateCalculator.calculateEmployeeTotalDayRate(this.employeeOverhead).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedResult = BigDecimal.valueOf(196.00).setScale(2, RoundingMode.HALF_UP);
        Assertions.assertEquals(expectedResult, employeeDayRate);

    }


    /**
     * test the formula to calculate day rate for an overhead employee
     */
    @Test
    void calculateEmployeeDayRateWithoutUtilization() {
        BigDecimal employeeDayRate = this.rateCalculator.calculateEmployeeTotalDayRate(this.employeeOverhead).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expectedResult = BigDecimal.valueOf(196.00).setScale(2, RoundingMode.HALF_UP);
        Assertions.assertEquals(expectedResult, employeeDayRate);
    }

    /**
     * test the formula for the hour rate for an overhead employee
     */
    @Test
    void calculateEmployeeHourlyRateWithoutUtilization() {
        BigDecimal employeeHourRate = this.rateCalculator.calculateEmployeeHourlyRateWithoutUtilization(employeeOverhead).setScale(1, RoundingMode.HALF_UP);
        BigDecimal expectedResult = BigDecimal.valueOf(35.0).setScale(1, RoundingMode.HALF_UP);
        Assertions.assertEquals(expectedResult, employeeHourRate);
    }

    /**test the hour rate formula for an employee that is a resource*/
    @Test
    void calculateEmployeeHourlyRateForResource() {
        BigDecimal employeeHourRate = this.rateCalculator.calculateEmployeeHourlyRateWithoutUtilization(employeeResource).setScale(1, RoundingMode.HALF_UP);
        BigDecimal expectedResult = BigDecimal.valueOf(25).setScale(1, RoundingMode.HALF_UP);
        Assertions.assertEquals(expectedResult, employeeHourRate);
    }

}