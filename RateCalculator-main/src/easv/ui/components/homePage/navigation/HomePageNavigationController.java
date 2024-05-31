package easv.ui.components.homePage.navigation;

import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.homePage.callBackFactory.CallBack;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePageNavigationController implements Initializable {
    private ImageView homePageLogo;
    private CallBack callBack;

    public HomePageNavigationController(CallBack callBack) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePageNavigation.fxml"));
        loader.setController(this);
        try{
            homePageLogo = loader.load();
            this.callBack=callBack;
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(this::addOnClickHandler);

    }

    private void addOnClickHandler(){
        if (homePageLogo != null) {
            this.homePageLogo.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (callBack != null) {
                    callBack.call();
                }
                event.consume();
            });
        } else {
            System.out.println("HomePageLogo is not initialized.");
        }
    }

    public ImageView getHomePageLogo() {
        return homePageLogo;
    }
}

