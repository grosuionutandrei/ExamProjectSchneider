package easv.exception;

import javafx.scene.control.Alert;

public class ExceptionHandler {

    public static void infoError(RateException eventException) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(eventException.getErrorCode().getValue()+"\\n"+eventException.getMessage());
        alert.show();
    }

    public static void warningError(RateException eventException) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(eventException.getErrorCode().getValue()+"\\n"+eventException.getMessage());
        alert.show();
    }

    public static void errorAlert(RateException eventException) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(eventException.getErrorCode().getValue()+"\\n"+eventException.getMessage());
        alert.show();
    }

    public static void errorAlertMessage(String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(errorMessage);
        alert.show();
    }

    public static void errorConfirmationMessage(String errorMessage){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
