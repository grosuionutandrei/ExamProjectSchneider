package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import easv.ui.pages.modelFactory.IModel;

import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigateToGeography implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;
    private StackPane pane, secondLayout;



    public NavigateToGeography(PageManager pageManager, IModel model, StackPane pane, StackPane secondLayout) {
        this.pageManager= pageManager;
        this.pane = pane;
        this.secondLayout = secondLayout;
        this.model = model;
    }

    @Override
    public void call() {
        if(isOpened){
            return;
        }
        initializePage();
        pageManager.changePage(root,this);
        isOpened=true;
    }


    private void initializePage(){
        GeographyManagementController geographyManagementController = new GeographyManagementController(model, pane, secondLayout);
        root=geographyManagementController.getCreatePage();
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;
    }
}
