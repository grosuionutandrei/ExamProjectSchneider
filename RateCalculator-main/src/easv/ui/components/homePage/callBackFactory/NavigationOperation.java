package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.controllerFactory.PageControllerFactory;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.PageControlable;
import javafx.scene.Parent;


public class NavigationOperation implements CallBack, Subject {

    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private PageControllerFactory pageControllerFactory;

    public NavigationOperation(PageManager pageManager, PageControllerFactory pageControllerFactory) {
        this.pageManager = pageManager;
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
        PageControlable pageControlable = this.pageControllerFactory.createController();
        root =pageControlable.getPageRoot();
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened = val;
    }
}
