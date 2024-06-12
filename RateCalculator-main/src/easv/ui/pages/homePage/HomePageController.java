package easv.ui.pages.homePage;
import easv.be.Navigation;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.NavigationFactory.NavigationFactory;
import easv.ui.components.homePage.callBackFactory.CallBackFactory;
import easv.ui.components.homePage.openPageObserver.Observable;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.components.homePage.sideNavigation.SideNavigationController;
import easv.ui.pages.modelFactory.IModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HomePageController implements Initializable, PageManager {
    private Parent root;
    @FXML
    private StackPane menu, firstLayout, secondLayout;
    @FXML
    private VBox pageContainer;
    @FXML
    private SideNavigationController sideNavigation;
    @FXML
    private HBox header;

    private IModel model;
    private List<Navigation> upperNavigation;
    private List<Navigation> lowerNavigation;


    /***
     * creates a home page with the necessary navigation components required by the client
     *in this case Main class is the client*/


    public HomePageController(IModel model, List<Navigation> upperNavigation, List<Navigation> lowerNavigation) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
        loader.setController(this);
        this.upperNavigation = upperNavigation;
        this.lowerNavigation = lowerNavigation;
        this.model = model;
        try {
            root = loader.load();

        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    public Parent getRoot() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeWorldMap();
        NavigationFactory.setModalWindow(this.firstLayout);
        NavigationFactory.setSecondLayout(this.secondLayout);
        NavigationFactory.setModel(this.model);
        NavigationFactory.setPageHolder(this);

        sideNavigation = new SideNavigationController(upperNavigation, lowerNavigation);
        initializeSideMenu(menu, sideNavigation.getRoot());

          //initialize  initial home page
        Platform.runLater(NavigationFactory::initializeHomePage);

    }

    private void initializeSideMenu(StackPane stackPane, ScrollPane hBox) {
        StackPane.setAlignment(hBox, Pos.CENTER_LEFT);
        stackPane.getChildren().add(hBox);
    }

    private void initializeWorldMap() {
        header.getChildren().add(0, NavigationFactory.getHomePageNavigation());
    }


    /**
     * this method will be called by the navigation components in order to display new page,
     * the navigation components are relying on the CallBack interface  to provide their functionality,
     * each navigation component will, receive their CallBack implementation form the CallBackFactory , when they are created
     */
    @Override
    public void changePage(Parent page, Subject subject) {
        CallBackFactory.getObserver().modifyDisplay(subject);
        this.pageContainer.getChildren().clear();
        this.pageContainer.getChildren().add(page);
    }



}
