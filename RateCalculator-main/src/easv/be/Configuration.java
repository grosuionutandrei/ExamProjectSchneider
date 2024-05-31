package easv.be;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.time.format.DateTimeFormatter;

public class Configuration {
    private int configurationId;
    private BigDecimal annualSalary;
    private BigDecimal fixedAnnualAmount;
    private BigDecimal overheadMultiplier;
    private BigDecimal utilizationPercentage;
    private BigDecimal workingHours;
    private LocalDateTime savedDate;
    private boolean active;
    private BigDecimal dayRate;
    private BigDecimal hourlyRate;

    //static  default value because it will be available for all objects
    private static final int  DAY_WORKING_HORS=8;


    private  double dayWorkingHours;


    /**empty constructor , used for unit tests*/
    public Configuration() {
    }

    public Configuration(BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours) {
        this.annualSalary = annualSalary;
        this.fixedAnnualAmount = fixedAnnualAmount;
        this.overheadMultiplier = overheadMultiplier;
        this.utilizationPercentage = utilizationPercentage;
        this.workingHours = workingHours;
    }


    public Configuration(BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, LocalDateTime savedDate, boolean active,double dayWorkingHours ) {
        this(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours);
        this.savedDate = savedDate;
        this.active = active;
        this.dayWorkingHours = dayWorkingHours;
    }


    public Configuration(int configurationId, BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours, LocalDateTime savedDate, boolean active,double dayWorkingHours) {
        this(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, savedDate, active,dayWorkingHours);
        this.configurationId = configurationId;

    }


    /**
     * Constructor used to create Configuration objects with active configuration, without marginMultiplier and grossMargin.
     *
     * @param configurationId       Unique identifier for the configuration.
     * @param annualSalary          The annual salary.
     * @param fixedAnnualAmount     The fixed annual amount.
     * @param overheadMultiplier    The overhead multiplier.
     * @param utilizationPercentage The utilization percentage.
     * @param workingHours          The working hours.
     * @param configurationDate     The date when the configuration was created
     * @param active                The active status of the configuration.
     * @param dayRate               The dayRate calculated
     * @param hourlyRate            The hourluRate calculated;
     * @param dayWorkingHours       The amount of working hours in a day, if is 0 will be set to default 8 hours;
     */

    public Configuration(int configurationId, BigDecimal annualSalary, BigDecimal fixedAnnualAmount, BigDecimal overheadMultiplier, BigDecimal utilizationPercentage, BigDecimal workingHours,LocalDateTime configurationDate, boolean active , BigDecimal dayRate, BigDecimal hourlyRate, double dayWorkingHours) {
        this(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours);
        this.configurationId = configurationId;
        this.savedDate = configurationDate;
        this.active = active;
        if(dayWorkingHours==0){
            this.dayWorkingHours=DAY_WORKING_HORS;
        }else{
            this.dayWorkingHours= dayWorkingHours;
        }
        this.dayRate=dayRate;
        this.hourlyRate=hourlyRate;
    }


    /**copy constructor for when the employee configurations are edited*/
    public Configuration(Configuration other) {
        this(other.configurationId,
                other.annualSalary,
                other.fixedAnnualAmount,
                other.overheadMultiplier,
                other.utilizationPercentage,
                other.workingHours,
                other.savedDate,
                other.active,
                other.dayRate,
                other.hourlyRate,
                other.dayWorkingHours);
    }




    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String activeStatus = this.isActive() ? "active" : "";
        return savedDate.format(formatter) + " " + activeStatus;
    }


    /**
     * do not use the equal for comparison , is used only for the view
     **/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return Objects.equals(savedDate.toString(), that.savedDate.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(savedDate);
    }



/**used to compare to configuration objects if are equal */
public boolean isEqualTo(Configuration other) {
    if (other == null) {
        return false;
    }
    return Objects.equals(this.getAnnualSalary(), other.getAnnualSalary())
            && Objects.equals(this.getFixedAnnualAmount(), other.getFixedAnnualAmount())
            && Objects.equals(this.getOverheadMultiplier(), other.getOverheadMultiplier())
            && Objects.equals(this.getUtilizationPercentage(), other.getUtilizationPercentage())
            && Objects.equals(this.getWorkingHours(), other.getWorkingHours())
            && Objects.equals(this.getDayWorkingHours(),other.getDayWorkingHours());
}




    public double getDayWorkingHours() {
        return dayWorkingHours;
    }

    public void setDayWorkingHours(double dayWorkingHours) {
        this.dayWorkingHours = dayWorkingHours;
    }


    public LocalDateTime getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(LocalDateTime savedDate) {
        this.savedDate = savedDate;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
    this.active = active;
    }


    public int getConfigurationId() {
        return configurationId;
    }

    public void setConfigurationId(int configurationId) {
        this.configurationId = configurationId;
    }

    public BigDecimal getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(BigDecimal annualSalary) {
        this.annualSalary = annualSalary;
    }

    public BigDecimal getFixedAnnualAmount() {
        return fixedAnnualAmount;
    }

    public void setFixedAnnualAmount(BigDecimal fixedAnnualAmount) {
        this.fixedAnnualAmount = fixedAnnualAmount;
    }

    public BigDecimal getOverheadMultiplier() {
        return overheadMultiplier;
    }

    public void setOverheadMultiplier(BigDecimal overheadMultiplier) {
        this.overheadMultiplier = overheadMultiplier;
    }

    public BigDecimal getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(BigDecimal utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }

    public BigDecimal getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(BigDecimal workingHours) {
        this.workingHours = workingHours;
    }

    public BigDecimal getDayRate() {
        return dayRate;
    }

    public void setDayRate(BigDecimal dayRate) {
        this.dayRate = dayRate;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }



    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
