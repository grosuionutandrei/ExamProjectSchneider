package easv.Utility;

import easv.exception.ExceptionHandler;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.css.PseudoClass;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class EmployeeValidation {
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private static final String VALID_NAME_FORMAT = "Please enter the name of the Employee (e.g., Nelson Mandela).";
    private static final String VALID_COUNTRY_FORMAT = "Please select a Country (e.g., Denmark).";
    private static final String VALID_TEAM_FORMAT = "Please select the Team of the Employee (e.g., Project Management).";
    private static final String VALID_VALUE_FORMAT = "Please enter the value in this format: e.g., 150000.50 or 100.";
    private static final String VALID_HOURS_FORMAT = "Please enter the hours in this format: e.g., 5.5 or 8.";
    private static final String VALID_PERCENTAGE_FORMAT = "Please enter the percentage in this format: e.g., 59 or 70.50.";
    private static final String VALID_OVERHEAD_COST_FORMAT = "Please select one of the options: Overhead or Resource.";
    private static final String VALID_CURRENCY_FORMAT = "Please select one of the currencies: EUR or USD.";
    private static final String VALID_REGION_FORMAT = "Please select a Region (e.g., Europe).";

    private final static String validNamePattern = "^[A-Za-z]+(\\s[A-Za-z]+)*$";
    private final static String validPercentagePattern = "^\\d{0,3}([.,]\\d{1,2})?$";
    private final static String validNumberPattern = "^\\d{1,12}([.,]\\d{1,2})?$";

    private final static String INVALID_MARKUP = "The  multiplier should be between 0 and 100";
    private final static String INVALID_FORMAT = "Please insert a value in the following formats : '0 00 00,0 00,00 00.0 00.00'";


    /**
     * validate the user inputs for the add operation
     */
    public static boolean arePercentagesValid(MFXTextField overheadMultiplier, List<Integer> utilizationPercentages) {
        boolean isValid = true;
        if (utilizationPercentages.stream().mapToInt(Integer::intValue).sum() > 100) {
            ExceptionHandler.errorAlertMessage("The sum of the Utilization % should be less than or equal to 100.");
            return false;
        }
        for (int percentage : utilizationPercentages) {
            String utilizationText = String.valueOf(percentage);
            if (!utilizationText.matches(validPercentagePattern)) {
                ExceptionHandler.errorAlertMessage("The Utilization % should be a number.");
                return false;
            } else {
                if (percentage < 0 || percentage > 100) {
                    ExceptionHandler.errorAlertMessage("The Utilization % should be between 0 and 100.");
                    return false;
                }
            }
        }

        String overheadMultiplierText = overheadMultiplier.getText();
        if (overheadMultiplierText.isEmpty()) {
            overheadMultiplier.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Overhead Multiplier % field cannot be empty.");
            return false;
        } else if (!overheadMultiplierText.matches(validPercentagePattern)) {
            overheadMultiplier.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Overhead Multiplier % should be a number.");
            return false;
        } else {
            BigDecimal multiplierValue = new BigDecimal(convertToDecimalPoint(overheadMultiplierText));
            if (multiplierValue.compareTo(BigDecimal.ZERO) < 0 || multiplierValue.compareTo(new BigDecimal("100")) > 0) {
                overheadMultiplier.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Overhead Multiplier % should be between 0 and 100.");
                return false;
            }
        }
        return isValid;
    }


    /**
     * validate the user inputs for the add operation
     */
    public static boolean arePercentagesValid(MFXTextField overheadMultiplier) {
        boolean isValid = true;

        String overheadMultiplierText = overheadMultiplier.getText();
        if (overheadMultiplierText.isEmpty()) {
            overheadMultiplier.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Overhead Multiplier % field cannot be empty.");
            return false;
        } else if (!overheadMultiplierText.matches(validPercentagePattern)) {
            overheadMultiplier.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Overhead Multiplier % should be a number.");
            return false;
        } else {
            BigDecimal multiplierValue = new BigDecimal(convertToDecimalPoint(overheadMultiplierText));
            if (!isPercentageValid(multiplierValue)) {
                overheadMultiplier.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Overhead Multiplier % should be between 0 and 100.");
                return false;
            }
        }
        return isValid;
    }

    public static boolean isPercentageValid(MFXTextField percentageField) {
        boolean isValid = true;
        String percentageText = percentageField.getText();
        if (percentageText.isEmpty()) {
            percentageField.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Utilization % field cannot be empty.");
            return false;
        } else if (!percentageText.matches(validPercentagePattern)) {
            percentageField.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Utilization % should be a number.");
            return false;
        } else {
            int multiplierValue = Integer.parseInt(percentageText);
            if (!isPercentageInLimits(multiplierValue)) {
                percentageField.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Utilization % should be between 0 and 100.");
                return false;
            }
            return isValid;
        }
    }


    public static boolean areNumbersValid(MFXTextField salary, MFXTextField hours, MFXTextField fixedAmount, MFXTextField dailyWorkingHours) {
        boolean isValid = true;

        String salaryText = salary.getText();
        if (salaryText.isEmpty()) {
            salary.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Salary field cannot be empty.");
            return false;
        } else if (!salaryText.matches(validNumberPattern)) {
            salary.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Salary should be a number.");
            return false;
        } else {
            BigDecimal salaryValue = new BigDecimal(convertToDecimalPoint(salaryText));
            if (isValueSmallerThanZero(salaryValue)) {
                salary.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Salary should be equal or greater than 0.");
                return false;
            }
        }

        String hoursText = hours.getText();
        if (hoursText.isEmpty()) {
            hours.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Working Hours field cannot be empty.");
            return false;
        } else if (!hoursText.matches(validNumberPattern)) {
            hours.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Working Hours should be a number.");
            return false;
        } else {
            BigDecimal hoursValue = new BigDecimal(convertToDecimalPoint(hoursText));
            if (isValueSmallerThanZero(hoursValue)) {
                hours.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Working Hours should be equal or greater than 0.");
                return false;
            }
        }

        String fixedAmountText = fixedAmount.getText();
        if (fixedAmountText.isEmpty()) {
            fixedAmount.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Fixed Annual Amount field cannot be empty.");
            return false;
        } else if (!fixedAmountText.matches(validNumberPattern)) {
            fixedAmount.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Fixed Annual Amount should be a number.");
            return false;
        } else {
            BigDecimal fixedAmountValue = new BigDecimal(convertToDecimalPoint(fixedAmountText));
            if (isValueSmallerThanZero(fixedAmountValue)) {
                fixedAmount.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Fixed Annual Amount should be equal or greater than 0.");
                return false;
            }
        }

        String dailyHours = dailyWorkingHours.getText();
        if (dailyHours.isEmpty()) {
            dailyWorkingHours.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Daily Working Hours field cannot be empty.");
            return false;
        } else if (!dailyHours.matches(validNumberPattern)) {
            dailyWorkingHours.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("The Daily Working Hours should be a number.");
            return false;
        } else {
            double workingHoursDay = Double.parseDouble(convertToDecimalPoint(dailyHours));
            if (!isValidWorkingDayHoursRange(workingHoursDay)) {
                dailyWorkingHours.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("The Daily Working Hours should be equal or greater than 0.");
                return false;
            }
        }
        return isValid;
    }

    public static boolean areNamesValid(MFXTextField name, ListView teamList) {
        boolean isValid = true;

        if (name.getText().isEmpty()) {
            name.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Name field cannot be empty.");
            return false;
        }
        if (teamList.getItems().isEmpty()) {
            ExceptionHandler.errorAlertMessage("Team(s) list cannot be empty.");
            return false;
        }
        return isValid;
    }


    public static boolean areNamesValid(MFXTextField name) {
        boolean isValid = true;

        if (name.getText().isEmpty()) {
            name.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Name field cannot be empty.");
            return false;
        }
        return isValid;
    }


    public static boolean isItemSelected(MFXComboBox currency, MFXComboBox overOrResource) {
        boolean isValid = true;

        if (currency.getSelectedItem() == null || currency.getText().isEmpty()) {
            currency.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Please select a currency.");
            return false;
        }

        if (overOrResource.getSelectedItem() == null || overOrResource.getText().isEmpty()) {
            overOrResource.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Please select Overhead or Resource.");
            return false;
        }
        return isValid;
    }






    /**
     * check if the  input is a valid number between 0 an 100
     */
    private static boolean isValueWithinValidRange(String inputValue) {
        double value = Double.parseDouble(convertToDecimalPoint(inputValue));
        return value > 0 && value <= 100;
    }

    public static void addNameToolTip(MFXTextField nameTF) {
        Tooltip errorTooltip = new Tooltip(VALID_NAME_FORMAT);
        nameTF.setTooltip(errorTooltip);
    }

    public static void addRegionToolTip(MFXComboBox countryCB) {
        Tooltip tooltip = new Tooltip(VALID_REGION_FORMAT);
        countryCB.setTooltip(tooltip);
    }

    public static void addCountryToolTip(MFXComboBox countryCB) {
        Tooltip tooltip = new Tooltip(VALID_COUNTRY_FORMAT);
        countryCB.setTooltip(tooltip);
    }

    public static void addTeamToolTip(MFXComboBox teamCB) {
        Tooltip tooltip = new Tooltip(VALID_TEAM_FORMAT);
        teamCB.setTooltip(tooltip);
    }

    public static void addValueToolTip(MFXTextField salaryTF, MFXTextField workingHoursTF, MFXTextField fixedValueTF) {
        Tooltip errorTooltip = new Tooltip(VALID_VALUE_FORMAT);
        salaryTF.setTooltip(errorTooltip);
        workingHoursTF.setTooltip(errorTooltip);
        fixedValueTF.setTooltip(errorTooltip);
    }

    public static void addPercentageToolTip(MFXTextField utilizationPercentageTF, MFXTextField overheadMultiplierTF) {
        Tooltip errorTooltip = new Tooltip(VALID_PERCENTAGE_FORMAT);
        utilizationPercentageTF.setTooltip(errorTooltip);
        overheadMultiplierTF.setTooltip(errorTooltip);
    }

    public static void addOverOrResourceToolTip(MFXComboBox overOrResourceCB) {
        Tooltip tooltip = new Tooltip(VALID_OVERHEAD_COST_FORMAT);
        overOrResourceCB.setTooltip(tooltip);
    }

    public static void addDailyWorkingHoursToolTip(MFXTextField hoursTF) {
        Tooltip tooltip = new Tooltip(VALID_HOURS_FORMAT);
        hoursTF.setTooltip(tooltip);
    }

    public static void addCurrencyToolTip(MFXComboBox currencyCB) {
        Tooltip tooltip = new Tooltip(VALID_CURRENCY_FORMAT);
        currencyCB.setTooltip(tooltip);
    }

    public static void listenerForEmptyFieldsAfterSaving(MFXTextField textField) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        textField.textProperty().addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((event) -> {
                if (newValue.isEmpty()) {
                    textField.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
                    return;
                }
            });
            pauseTransition.playFromStart();
        }));
    }

    /**
     * add validation for the  percentage input fields that can not be empty
     *
     * @param percentageDisplayer the container off the percentage values
     */
    public static void addNonEmptyPercentageListener(MFXTextField percentageDisplayer) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        percentageDisplayer.textProperty().addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((event) -> {
                try {
                    if (!newValue.matches("^\\d{0,3}([.,]\\d{1,2})?$")) {
                        percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                        return;
                    }
                    percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValueWithinValidRange(newValue));
                } catch (NumberFormatException e) {
                    percentageDisplayer.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                }
            });
            pauseTransition.playFromStart();
        }));

    }



    /**
     * add validation listeners for the input fields, to not be empty and to be digits
     * if later we need to check for the cap than we need to modify the method
     */

    public static void addInputDigitsListeners(MFXTextField normalDigitInputs) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        normalDigitInputs.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((event -> {
                Double value = null;
                try {
                    String convertToDecimalPoint = convertToDecimalPoint(newValue);
                    value = Double.parseDouble(convertToDecimalPoint);
                } catch (NumberFormatException e) {
                    normalDigitInputs.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                    return;
                }
                double valueConverted = parseMultiplierToNumber(newValue, Locale.GERMANY);
                if (Double.isNaN(valueConverted)) {
                    valueConverted = parseMultiplierToNumber(newValue, Locale.US);
                }
                boolean isValueValid = !Double.isNaN(valueConverted) && valueConverted > 0;
                normalDigitInputs.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isValueValid);
            }));
            pauseTransition.playFromStart();
        });
    }


    /**
     * convert the format of the input strings to  accept US and Europe values
     */
    private static double parseMultiplierToNumber(String numberStr, Locale locale) {

        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(locale);
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        if (locale.equals(Locale.GERMANY)) {
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');
        } else if (locale.equals(Locale.US)) {
            symbols.setGroupingSeparator(',');
            symbols.setDecimalSeparator('.');
        }
        format.setDecimalFormatSymbols(symbols);
        try {
            Number number = format.parse(numberStr);
            return number.doubleValue();
        } catch (ParseException e) {
            return Double.NaN;
        }
    }


    //convert form comma decimal to point decimal
    private static String convertToDecimalPoint(String value) {
        String validFormat = null;
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }


    /**
     * Add validation for name and country
     */
    public static void addLettersOnlyInputListener(MFXTextField input) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(300));
        input.textProperty().addListener(((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((e) -> {
                input.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, !isInputValid(newValue));
            });
            pauseTransition.playFromStart();
        }));
    }

    /**
     * check if the inserted value contains only letters and has no empty space at the end
     */
    private static boolean isInputValid(String value) {
        return !value.isEmpty() && value.matches(validNamePattern);
    }

    public static boolean isTeamToRemoveSelected(ListView teamList) {
        boolean isValid = true;
        if (teamList.getSelectionModel().getSelectedItem() == null) {
            ExceptionHandler.errorAlertMessage("Please select a team from the list to remove.");
            return false;
        }
        return isValid;
    }

    public static boolean isTeamSelected(MFXComboBox teamCB) {
        boolean isValid = true;
        if (teamCB.getSelectedItem() == null) {
            ExceptionHandler.errorAlertMessage("Please select a team to add.");
            return false;
        }
        return isValid;
    }

    //check if the multiplier is between 0 and 100
    public static boolean isPercentageValid(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) >= 0 && value.compareTo(new BigDecimal("100")) <= 0;
    }


    //check if the percentage is between 0 and 100;
    public static boolean isPercentageInLimits(int percentage) {
        return percentage > 0 && percentage <= 100;
    }

    // check if the salary is bigger than zero
    public static boolean isValueSmallerThanZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    //check if the day working hours is between 1 and 24

    public static boolean isValidWorkingDayHoursRange(double workingDayHours) {
        return workingDayHours > 0 && workingDayHours <= 24;
    }

}
