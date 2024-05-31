package easv.ui.components.homePage.sideNavigation;

import easv.be.Navigation;
import easv.ui.components.homePage.callBackFactory.CallBackFactory;
import easv.ui.components.homePage.NavigationFactory.NavigationFactory;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class SideNavigationController implements Initializable {
    @FXML
    private VBox iconsContainer;
    @FXML
    private ScrollPane sideNavigationContainer;
    @FXML
    private Line theLine;
    private boolean isExpanded;
    private static   final double expandedWidth=300;
    private static   final double originalWidth = 90;



    public SideNavigationController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SideNavigation.fxml"));
        loader.setController(this);
        try {
            sideNavigationContainer =loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateNavigation(this.iconsContainer);

    }

    private void populateNavigation(VBox vBox) {
        HBox distribution = NavigationFactory.getNavigationComponent(Navigation.DISTRIBUTION, CallBackFactory.createCallBack(Navigation.DISTRIBUTION));
        HBox create = NavigationFactory.getNavigationComponent(Navigation.CREATE, CallBackFactory.createCallBack(Navigation.CREATE));
        HBox employees = NavigationFactory.getNavigationComponent(Navigation.EMPLOYEES, CallBackFactory.createCallBack(Navigation.EMPLOYEES));
        HBox modeling = NavigationFactory.getNavigationComponent(Navigation.MODELING, CallBackFactory.createCallBack(Navigation.MODELING));
        HBox geography = NavigationFactory.getNavigationComponent(Navigation.GEOGRAPHY, CallBackFactory.createCallBack(Navigation.GEOGRAPHY));
        vBox.getChildren().add(1, distribution);
        vBox.getChildren().add(2, create);
        vBox.getChildren().add(3, employees);
        vBox.getChildren().add(4, modeling);
        vBox.getChildren().add(8, geography);
        addOnEnterListener();
        addOnExitListener();
    }
    public ScrollPane getRoot() {
        return sideNavigationContainer;
    }


    private void addOnEnterListener(){
       sideNavigationContainer.addEventHandler(MouseEvent.MOUSE_ENTERED, event->{
           if(isExpanded){
               return;
           }
           Timeline timeline  = new Timeline();
           KeyValue keyValue= new KeyValue(sideNavigationContainer.prefWidthProperty(),expandedWidth);
           KeyFrame keyFrame = new KeyFrame(Duration.millis(500),keyValue);
           timeline.getKeyFrames().add(keyFrame);
           timeline.play();
           isExpanded=true;
       });
    }
    private void addOnExitListener(){
        sideNavigationContainer.addEventHandler(MouseEvent.MOUSE_EXITED, event->{
            Timeline timeline  = new Timeline();
            KeyValue keyValue= new KeyValue(sideNavigationContainer.prefWidthProperty(),originalWidth);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(500),keyValue);
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
            isExpanded=false;
        });
    }


}
