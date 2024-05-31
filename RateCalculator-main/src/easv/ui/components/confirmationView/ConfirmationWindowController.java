package easv.ui.components.confirmationView;

import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;


public class ConfirmationWindowController {

    @FXML
    private Label confirmationTitle;
    @FXML
    private Label entityTitle;
    @FXML
    private Label errorMessage;
    @FXML
    private VBox confirmationWindow;
    private StackPane firstLayout;
    private OperationHandler operationHandler;
    @FXML
    private MFXProgressSpinner progressSpinner;

    public ConfirmationWindowController(StackPane firstLayout, OperationHandler operationHandler) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Confirmation.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        this.operationHandler = operationHandler;
        try {
            confirmationWindow = loader.load();

        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    public VBox getRoot() {
        return confirmationWindow;
    }

    public void setErrorMessage(String message) {
        this.errorMessage.setText(message);
    }

    @FXML
    private void cancelOperation(ActionEvent event) {
        firstLayout.getChildren().clear();
        firstLayout.setDisable(true);
        firstLayout.setVisible(false);
        ;
    }

    @FXML
    private void confirmOperation(ActionEvent event) throws RateException {
        progressSpinner.setVisible(true);
        operationHandler.performOperation();

    }

}
