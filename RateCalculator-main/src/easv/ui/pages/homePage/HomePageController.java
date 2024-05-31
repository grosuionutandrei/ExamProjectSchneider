package easv.ui.pages.homePage;

import easv.be.Navigation;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.callBackFactory.CallBackFactory;
import easv.ui.components.homePage.navigation.HomePageNavigationController;
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
    private Observable observer;
    private IModel model;



    public HomePageController( IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
        loader.setController(this);
        this.model = model;
        try {
            root = loader.load();
            observer =CallBackFactory.getObserver();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public Parent getRoot() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sideNavigation = new SideNavigationController();
        Platform.runLater(() -> {
            initializeSideMenu(menu, sideNavigation.getRoot());
        });
        Platform.runLater(() -> {
            sideNavigation.getRoot().getWidth();
        });
        initializeWorldMap();
        CallBackFactory.setPageHolder(this);
        CallBackFactory.setModel(this.model);
        CallBackFactory.setModalWindow(this.firstLayout);
        CallBackFactory.setSecondLayout(this.secondLayout);
        Platform.runLater(()->{
            CallBackFactory.createCallBack(Navigation.HOME).call();
        });
    }

    private void initializeSideMenu(StackPane stackPane, ScrollPane hBox) {
        StackPane.setAlignment(hBox, Pos.CENTER_LEFT);
        stackPane.getChildren().add(hBox);
    }

    private void initializeWorldMap() {
        initializeHomePageLogo();
    }
    private void initializeHomePageLogo(){
        HomePageNavigationController homePageNavigationController = new HomePageNavigationController(CallBackFactory.createCallBack(Navigation.HOME));
        header.getChildren().add(0,homePageNavigationController.getHomePageLogo());
    }


    /**
     * this method will be called by the navigation components in order to display new page,
     * the navigation components are relying on the CallBack interface  to provide their functionality,
     * each navigation component will, receive their CallBack implementation form the CallBackFactory , when they are created */
    @Override
    public void changePage(Parent page,Subject subject) {
        observer.modifyDisplay(subject);
        this.pageContainer.getChildren().clear();
        this.pageContainer.getChildren().add(page);
    }
}
