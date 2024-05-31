package easv.Utility;

import easv.be.Currency;
import easv.be.Team;
import easv.exception.ExceptionHandler;
import easv.ui.components.common.errorWindow.ErrorWindowController;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.css.PseudoClass;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class CountryValidation {
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private final static String validNamePattern = "^[A-Za-z]+(\\s[A-Za-z]+)*$";



    public static boolean isTeamSelected(MFXComboBox<Team> teamsCB, ListView<String> teamsListView) {
        boolean isValid = true;
        if (teamsCB != null) {
            if (teamsCB.getSelectedItem() == null) {
                teamsCB.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("Please select a Team.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Please select a Team.");
//            pane.getChildren().add(errorWindowController.getRoot());
                return false;
            }
        }

        if(teamsListView != null) {
            if (teamsListView.getSelectionModel().getSelectedItem() == null) {
                teamsListView.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("Please select a Team to edit.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Please select a Team to edit.");
//            pane.getChildren().add(errorWindowController.getRoot());
                return false;
            }
        }
        return isValid;
    }

    public static boolean isTeamToRemoveSelected(ListView<String> teamsListView) {
        boolean isValid = true;
        if (teamsListView != null) {
            if (teamsListView.getSelectionModel().getSelectedItem() == null) {
                ExceptionHandler.errorAlertMessage("Please select a Team from the list to remove.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Please select a Team from the list to remove.");
//            pane.getChildren().add(errorWindowController.getRoot());
                return false;
            }
        }
        return isValid;
    }

    public static boolean isCountryNameValid(MFXTextField countryNameTF) {
        boolean isValid = true;

        if (countryNameTF.getText().isEmpty()) {
            countryNameTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Country name cannot be empty.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Country name cannot be empty.");
//            pane.getChildren().add(errorWindowController.getRoot());
            return false;
        }
        return isValid;
    }

    public static boolean isTeamsListValid(ListView<String> teamsListView) {
        boolean isValid = true;

        if(teamsListView != null) {
            if (teamsListView.getItems().isEmpty()) {
                teamsListView.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("Team list cannot be empty.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Team list cannot be empty.");
//            pane.getChildren().add(errorWindowController.getRoot());
                return false;
            }
        }
        return isValid;
    }

    public static boolean isTeamNameValid(MFXTextField teamNameTF) {
        boolean isValid = true;

        if (teamNameTF.getText().isEmpty()) {
            teamNameTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Team name cannot be empty.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Team name cannot be empty.");
//            pane.getChildren().add(errorWindowController.getRoot());
            return false;
        }
        return isValid;
    }

    public static boolean isCurrencySelected(MFXComboBox<String> currencyCB) {
        boolean isValid = true;

        if(currencyCB != null) {
            if (currencyCB.getSelectedItem() == null) {
                currencyCB.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
                ExceptionHandler.errorAlertMessage("Please select a Currency.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Please select a Currency.");
//            pane.getChildren().add(errorWindowController.getRoot());
                return false;
            }
        }
        return isValid;
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
}
