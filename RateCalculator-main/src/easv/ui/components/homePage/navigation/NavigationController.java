package easv.ui.components.homePage.navigation;
import easv.ui.components.homePage.callBackFactory.CallBack;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavigationController implements Initializable {
    @FXML
    private HBox navComponent;
    @FXML
    private HBox navIcon;
    @FXML
    private Label navText;
    @FXML
    private HBox navArrow;
    private CallBack callback;
    private SVGPath icon;
    private String iconText;

    public NavigationController(SVGPath icon, CallBack callback, String iconText) {
        FXMLLoader loader =  new FXMLLoader(getClass().getResource("Navigation.fxml"));
        loader.setController(this);
        this.callback= callback;
        this.icon = icon;
        this.iconText=iconText;
        try {
            navComponent= loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
     navIcon.getChildren().add(icon);
     navText.setText(iconText);
     addOnClickListener();
     addOnHoverListener();
     addOnExitListener();
    }
    public HBox getNavComponent() {
        return navComponent;
    }

    private void addOnClickListener(){
        navComponent.addEventHandler(MouseEvent.MOUSE_CLICKED, event->{
            callback.call();
        });
    }

    private void addOnHoverListener(){
        navComponent.addEventHandler(MouseEvent.MOUSE_ENTERED,event -> {
            for(Node node: navComponent.getChildren() ){
                recursiveStyling(node);
            }
            navArrow.setVisible(true);
        });
    }
    private void addOnExitListener(){
        navComponent.addEventHandler(MouseEvent.MOUSE_EXITED,event -> {
            for(Node node: navComponent.getChildren() ){
                recursiveRemoveStyling(node);
            }
            navArrow.setVisible(false);
        });
    }

    private void recursiveStyling(Node node){
        if(node == null){
            return;
        }
        node.getStyleClass().add("hover");
        if(node instanceof Parent parent){
            for(Node child : parent.getChildrenUnmodifiable()){
                recursiveStyling(child);
            }
        }
    }

    private void recursiveRemoveStyling(Node node){
        if(node == null){
            return;
        }
        node.getStyleClass().remove("hover");
        if(node instanceof Parent parent){
            for(Node child : parent.getChildrenUnmodifiable()){
                recursiveRemoveStyling(child);
            }
        }
    }
}
