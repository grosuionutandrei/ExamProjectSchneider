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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class SideNavigationController implements Initializable {
    @FXML
    private VBox iconsContainer;
    @FXML
    private VBox upperSection,lowerSection;
    @FXML
    private ScrollPane sideNavigationContainer;
    @FXML
    private Line theLine;
    private boolean isExpanded;
    private static   final double expandedWidth=300;
    private static   final double originalWidth = 90;
    private List<Navigation> upperSectionNavigation;
    private List<Navigation> lowerSectionNavigation;


//Todo modify the sideNavigationController to receive a list of Enums
    public SideNavigationController(List<Navigation> upperSectionNavigation,List<Navigation>lowerSectionNavigation) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SideNavigation.fxml"));
        loader.setController(this);
        this.upperSectionNavigation= new ArrayList<>(upperSectionNavigation);
        this.lowerSectionNavigation= new ArrayList<>(lowerSectionNavigation);
        try {
            sideNavigationContainer =loader.load();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateUpperNavigation(this.upperSection,upperSectionNavigation);
        populateLowerNavigation(this.lowerSection,lowerSectionNavigation);
        addOnEnterListener();
        addOnExitListener();
    }


    private void populateUpperNavigation(VBox upperSection,List<Navigation>  upperSectionNavigation) {
       List<HBox> navigations = new ArrayList<>();
       for(Navigation navigation : upperSectionNavigation){
           navigations.add(NavigationFactory.getNavigationComponent(navigation));
       }

       upperSection.getChildren().addAll(navigations);
//        HBox distribution = NavigationFactory.getNavigationComponent(Navigation.DISTRIBUTION);
//        HBox create = NavigationFactory.getNavigationComponent(Navigation.CREATE);
//        HBox employees = NavigationFactory.getNavigationComponent(Navigation.EMPLOYEES);
//        HBox modeling = NavigationFactory.getNavigationComponent(Navigation.MODELING);
     //   HBox geography = NavigationFactory.getNavigationComponent(Navigation.GEOGRAPHY);
//        upperSection.getChildren().add( distribution);
//        upperSection.getChildren().add( create);
//        upperSection.getChildren().add(employees);
//        upperSection.getChildren().add( modeling);
   //    lowerSection.getChildren().add( geography);
    }

    private void populateLowerNavigation(VBox lowerSection,List<Navigation> lowerNavigation){
      List<HBox> navigations = new ArrayList<>();
        for(Navigation navigation:lowerNavigation){
          navigations.add(NavigationFactory.getNavigationComponent(navigation));
      }
        lowerSection.getChildren().addAll(navigations);
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
            case UPPER -> {  this.upperSectionNavigation.add(navigation);
                populateUpperNavigation(upperSection,upperSectionNavigation);}
            case LOWER -> {
                this.lowerSectionNavigation.add(navigation);
                populateLowerNavigation(lowerSection,lowerSectionNavigation);
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
                populateUpperNavigation(upperSection,upperSectionNavigation);
            }
            case LOWER -> {
                this.lowerSectionNavigation.remove(navigation);
                populateLowerNavigation(lowerSection,lowerSectionNavigation);
            }
        }
    }

    public enum Section{
        UPPER,LOWER
    }

}
