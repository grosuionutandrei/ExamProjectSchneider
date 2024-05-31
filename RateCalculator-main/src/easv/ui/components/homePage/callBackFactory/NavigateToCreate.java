package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.createEmployeePage.CreateController;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigateToCreate implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;

    private StackPane firstLayout;

    public NavigateToCreate(PageManager pageManager, IModel model, StackPane firstLayout) {
        this.pageManager = pageManager;
        this.model = model;
        this.firstLayout=firstLayout;

    }


    @Override
    public void call() {
        if (isOpened) {
            return;
        }
        initializePage();
        pageManager.changePage(root, this);
        isOpened = true;
    }
    private void initializePage() {
        CreateController createController = new CreateController(model,firstLayout);
        root = createController.getCreatePage();
    }


    @Override
    public void modifyDisplay(boolean val) {
        isOpened = val;
    }
}
