package easv.ui.components.homePage.sideNavigation;

import easv.be.Navigation;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
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
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**dynamically creates the navigation controllers, received trough dependency injection,
 * by calling the NavigationFactory to create the actual component*/
public class SideNavigationController implements Initializable {

    @FXML
    private VBox upperSection,lowerSection;
    @FXML
    private ScrollPane sideNavigationContainer;
    private boolean isExpanded;
    private static   final double expandedWidth=300;
    private static   final double originalWidth = 90;
    private List<Navigation> upperSectionNavigation;
    private List<Navigation> lowerSectionNavigation;



    public SideNavigationController(List<Navigation> upperSectionNavigation, List<Navigation>lowerSectionNavigation) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SideNavigation.fxml"));
        loader.setController(this);
        this.upperSectionNavigation= new ArrayList<>(upperSectionNavigation);
        this.lowerSectionNavigation= new ArrayList<>(lowerSectionNavigation);
        try {
            sideNavigationContainer =loader.load();

        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateNavigation(this.upperSection,upperSectionNavigation);
        populateNavigation(this.lowerSection,lowerSectionNavigation);
        addOnEnterListener();
        addOnExitListener();
    }

    private void populateNavigation(VBox section, List<Navigation> navigationList) {
        section.getChildren().clear();
        List<HBox> navigations = new ArrayList<>();
        for (Navigation navigation : navigationList) {
            navigations.add(NavigationFactory.getNavigationComponent(navigation));
        }
        section.getChildren().addAll(navigations);
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

    /**add new navigation component
     *@param navigation enum that will define the type of navigation component
     *@param section the section where the new navigation component will be added(upper section/lower section)*/
    public void addNavigation(Navigation navigation,Section section) {
        switch(section){
            case UPPER -> {this.upperSectionNavigation.add(navigation);
                upperSection.getChildren().add(NavigationFactory.getNavigationComponent(navigation));}
            case LOWER -> {
                this.lowerSectionNavigation.add(navigation);
                lowerSection.getChildren().add(NavigationFactory.getNavigationComponent(navigation));
            }
        }
    }

    /**remove navigation component from the section navigation
     *@param navigation the component that needs to be removed
     *@param section the section from where to remove(upper section/lower section)*/
    public void removeNavigation(Navigation navigation,Section section){
        switch(section){
            case UPPER -> {
                this.upperSectionNavigation.remove(navigation);
                populateNavigation(upperSection,upperSectionNavigation);
            }
            case LOWER -> {
                this.lowerSectionNavigation.remove(navigation);
                populateNavigation(lowerSection,lowerSectionNavigation);
            }
        }
    }

    public enum Section{
        UPPER,LOWER
    }

}
