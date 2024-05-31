package easv.Utility;
import javafx.scene.layout.StackPane;

public class WindowsManagement {

    public static void closeStackPane(StackPane stackPane){
        stackPane.getChildren().clear();
        stackPane.setVisible(false);
        stackPane.setDisable(true);
    }

    public static  void showStackPane(StackPane stackPane){
        stackPane.setVisible(true);
        stackPane.setDisable(false);
    }
}
