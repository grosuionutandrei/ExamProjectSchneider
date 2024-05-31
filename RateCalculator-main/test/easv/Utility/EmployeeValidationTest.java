package easv.Utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeValidationTest {


    /**test if the  overhead multiplier is between 0 and 100 inclusive**/
    @Test
    void isPercentageValidZero() {
        //Arrange
        BigDecimal  overHeadZero =  BigDecimal.ZERO;
        //Act
        boolean isInRange = EmployeeValidation.isPercentageValid(overHeadZero);
        //Assert
        assertTrue(isInRange);
    }


    /**test the upper limit of the overhead multiplier => 100 */
    @Test
    void isPercentageValidHundred(){
        BigDecimal overHeadHundred =  BigDecimal.valueOf(100);
        boolean isInRange =  EmployeeValidation.isPercentageValid(overHeadHundred);
        assertTrue(isInRange);
    }


    /**test if the percentage is valid with values over the limit => 101*/
    @Test
    void isPercentageValidOverLimit(){
        BigDecimal overHundred = BigDecimal.valueOf(101);
        boolean isInRange = EmployeeValidation.isPercentageValid(overHundred);
        assertFalse(isInRange);
    }


    /**test if the percentage is valid with values under the limit => 99*/
    @Test
    void isPercentageValidUnderLimit(){
        BigDecimal overHundred = BigDecimal.valueOf(99);
        boolean isInRange = EmployeeValidation.isPercentageValid(overHundred);
        assertTrue(isInRange);
    }

    @Test
    void isPercentageInLimits() {
    }


    /**test if the input is not negative with the value "-1" */
    @Test
    void isValueSmallerThanZero() {
        BigDecimal smallerThanZero = BigDecimal.valueOf(-1);
        boolean isInRange = EmployeeValidation.isValueSmallerThanZero(smallerThanZero);
        assertTrue(isInRange);
    }

    /**test if the input is not negative with the value 0*/
    @Test
    void isValueEqualZero(){
        BigDecimal equalZero = BigDecimal.ZERO;
        boolean isInRange = EmployeeValidation.isValueSmallerThanZero(equalZero);
        assertFalse(isInRange);
    }

    /**test if the input is not negative with the value "1"*/
    @Test
    void isValueBiggerThanZero(){
        BigDecimal equalZero = BigDecimal.ONE;
        boolean isInRange = EmployeeValidation.isValueSmallerThanZero(equalZero);
        assertFalse(isInRange);
    }


    /**test if the input working hours per day is bigger than zero with the value -1 */
    @Test
    void isValidWorkingDayHoursRange() {
        double smallerThanZero =-1;
        boolean isInRange = EmployeeValidation.isValidWorkingDayHoursRange(smallerThanZero);
        assertFalse(isInRange);
    }

    /**test if the input working hours is bigger than 0 with the value "0" */
    @Test
    void isValidWorkingDayHoursEqualZero() {
        double smallerThanZero =0;
        boolean isInRange = EmployeeValidation.isValidWorkingDayHoursRange(smallerThanZero);
        assertFalse(isInRange);
    }

    /**test if the input working hours is bigger than 0 with the value "1" */
    @Test
    void isValidWorkingDayHoursEqualOne() {
        double smallerThanZero =1;
        boolean isInRange = EmployeeValidation.isValidWorkingDayHoursRange(smallerThanZero);
        assertTrue(isInRange);
    }

    /**test if the input working hours is between   0  and 24  with the value "15" */
    @Test
    void isValidWorkingDayHoursInRange() {
        double between =15;
        boolean isInRange = EmployeeValidation.isValidWorkingDayHoursRange(between);
        assertTrue(isInRange);
    }


    /**test if the input working hours is between   0  and 24  with the value "23.99" */
    @Test
    void isValidWorkingDayHoursSamllerMaximum() {
        double value =23.99;
        boolean isInRange = EmployeeValidation.isValidWorkingDayHoursRange(value);
        assertTrue(isInRange);
    }


    /**test if the input working hours is between   0  and 24  with the value "24" */
    @Test
    void isValidWorkingDayHoursEqualMaximum() {
        double value =24;
        boolean isInRange = EmployeeValidation.isValidWorkingDayHoursRange(value);
        assertTrue(isInRange);
    }

    /**test if the input working hours is between   0  and 24  with the value "24.01" */
    @Test
    void isValidWorkingDayHoursOverMaximum() {
        double value =24.01;
        boolean isInRange = EmployeeValidation.isValidWorkingDayHoursRange(value);
        assertFalse(isInRange);
    }

}