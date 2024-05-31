package easv.ui.components.common.errorWindow;

import easv.Utility.WindowsManagement;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ErrorWindowController implements Initializable {

    @FXML
    private VBox confirmationWindow;

    @FXML
    private MFXButton closeButton;

    @FXML
    private TextArea errorMessage;

    private StackPane errorHolder;
    private String errorMessageValue;


    public ErrorWindowController(StackPane errorHolder, String message) {
        this.errorHolder = errorHolder;
        this.errorMessageValue = message;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ErrorWindowView.fxml"));
        loader.setController(this);
        try {
            confirmationWindow = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //add event handler to close the window
        this.closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> WindowsManagement.closeStackPane(errorHolder));
        // display the error message
        this.errorMessage.setText(errorMessageValue);
    }

    public VBox getRoot(){
        return this.confirmationWindow;
    }

}
