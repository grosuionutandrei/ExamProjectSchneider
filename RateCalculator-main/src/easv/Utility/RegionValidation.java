package easv.Utility;

import easv.be.Country;
import easv.exception.ExceptionHandler;
import easv.ui.components.common.errorWindow.ErrorWindowController;
import javafx.animation.PauseTransition;
import javafx.scene.layout.StackPane;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.css.PseudoClass;
import javafx.scene.control.ListView;
import javafx.util.Duration;

public class RegionValidation {
    private static final PseudoClass ERROR_PSEUDO_CLASS = PseudoClass.getPseudoClass("error");
    private final static String validNamePattern = "^[A-Za-z]+(\\s[A-Za-z]+)*$";


    public static boolean isCountrySelected(MFXComboBox<Country> countriesCB) {
        boolean isValid = true;

        if (countriesCB.getSelectedItem() == null) {
            countriesCB.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Please select a Country.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Please select a Country.");
//            pane.getChildren().add(errorWindowController.getRoot());
            return false;
        }
        return isValid;
    }

    public static boolean isCountryToRemoveSelected(ListView<Country> countriesListView) {
        boolean isValid = true;
        if (countriesListView.getSelectionModel().getSelectedItem() == null) {
            ExceptionHandler.errorAlertMessage("Please select a Country from the list to remove.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Please select a Country from the list to remove.");
//            pane.getChildren().add(errorWindowController.getRoot());
            return false;
        }
        return isValid;
    }

    public static boolean isRegionNameValid(MFXTextField regionNameTF) {
        boolean isValid = true;

        if (regionNameTF.getText().isEmpty()) {
            regionNameTF.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Region name cannot be empty.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Region name field cannot be empty.");
//            pane.getChildren().add(errorWindowController.getRoot());
            return false;
        }
        return isValid;
    }

    public static boolean isCountryListValid(ListView<Country> countriesListView) {
        boolean isValid = true;

        if (countriesListView.getItems().isEmpty()) {
            countriesListView.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true);
            ExceptionHandler.errorAlertMessage("Country list cannot be empty.");
//            ErrorWindowController errorWindowController = new ErrorWindowController(pane, "Country list cannot be empty.");
//            pane.getChildren().add(errorWindowController.getRoot());
            return false;
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
