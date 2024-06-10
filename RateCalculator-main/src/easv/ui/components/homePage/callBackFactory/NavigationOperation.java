package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.controllerFactory.PageControllerFactory;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.PageControlable;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigationOperation implements CallBack, Subject {

    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
   // private IModel model;

   // private StackPane firstLayout;
    private PageControllerFactory pageControllerFactory;

    public NavigationOperation(PageManager pageManager, PageControllerFactory pageControllerFactory) {
        this.pageManager = pageManager;
     //   this.model = model;
     //   this.firstLayout=firstLayout;
        this.pageControllerFactory = pageControllerFactory;
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
     //   CreateController createController = new CreateController(model,firstLayout);
//        System.out.println(pageController.getPageRoot()  + "pageController");
//        System.out.println(pageController.getClass() + "class");
        PageControlable pageControlable = this.pageControllerFactory.createController();
        root =pageControlable.getPageRoot();
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened = val;
    }
}
