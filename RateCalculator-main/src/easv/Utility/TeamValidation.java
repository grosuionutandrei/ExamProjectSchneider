package easv.Utility;

import easv.be.Employee;
import easv.exception.ExceptionHandler;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;

import java.math.BigDecimal;
import java.util.Map;

public class TeamValidation {
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");

    public static boolean isPercentageValid(MFXTextField addUtil, Label utilLeft) {
        String percentageText = addUtil.getText().trim();
        String remainingUtil = utilLeft.getText().replace("%", "").trim();

        // Validate input is not empty and is a valid number
        if (percentageText.isEmpty() || !isNumeric(percentageText)) {
            addUtil.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            addUtil.setTooltip(new Tooltip("Please enter a valid number."));
            return false;
        }

        BigDecimal inputUtilization = new BigDecimal(percentageText);
        BigDecimal remainingU = new BigDecimal(remainingUtil);
        if (remainingU != null && inputUtilization.compareTo(remainingU) > 0) {
            addUtil.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            addUtil.setTooltip(new Tooltip("Utilization exceeds the remaining available utilization for the employee."));
            return false;
        } else {
            addUtil.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
            addUtil.setTooltip(null);
            return true;
        }
    }

    public static BigDecimal calculateRemainingUtilization(Map<Integer, BigDecimal> utilPerTeams) {
        BigDecimal totalUtilization = BigDecimal.ZERO;
        for (BigDecimal utilization : utilPerTeams.values()) {
            if (utilization != null) {
                totalUtilization = totalUtilization.add(utilization);
            }
        }
        return BigDecimal.valueOf(100).subtract(totalUtilization);
    }

    private static boolean isNumeric(String str) {
        try {
            new BigDecimal(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static boolean isValidUtilization(Label utilizationInTeam, MFXTextField utilPercentageToAdd, Employee employee) {
        String currentUtilInTeamStr = utilizationInTeam.getText().replace("%", "").trim();
        String newUtilStr = utilPercentageToAdd.getText().trim();

        if (currentUtilInTeamStr.isEmpty() || newUtilStr.isEmpty()) {
            return false;
        }

        try {
            BigDecimal currentUtilInTeam = new BigDecimal(currentUtilInTeamStr);
            BigDecimal newUtil = new BigDecimal(newUtilStr);
            BigDecimal remainingUtilization = calculateRemainingUtilization(employee.getUtilPerTeams());
            return newUtil.compareTo(currentUtilInTeam.add(remainingUtilization)) <= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public static boolean validateUtilization( Label utilizationInTeam, MFXTextField utilPercentageToAdd, Employee employee) {
        boolean isValid = true;

        String utilPercentageStr = utilPercentageToAdd.getText().trim();
        if (utilPercentageStr.isEmpty()) {
            /* Do not validate if field is empty*/
            return true;
        }
        if (!isValidUtilization( utilizationInTeam, utilPercentageToAdd, employee)) {
            utilPercentageToAdd.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            utilPercentageToAdd.setTooltip(new Tooltip("Utilization exceeds the remaining available utilization for the employee."));
            isValid = false;
        } else {
            utilPercentageToAdd.setTooltip(null);
            utilPercentageToAdd.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false);
        }

        return isValid;
    }
}
