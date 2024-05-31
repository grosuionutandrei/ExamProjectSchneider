package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.employeesPage.employeeMainPage.EmployeeMainPageController;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigateToEmployees implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;
    private StackPane modalLayout;

    public NavigateToEmployees(PageManager pageManager, IModel model, StackPane modalLayout) {
        this.pageManager = pageManager;
        this.model = model;
        this.modalLayout = modalLayout;
    }

    @Override
    public void call() {
        if (isOpened) {
            return;
        }
        initializeRoot();

        pageManager.changePage(root,this);
        isOpened= true;
    }

    private void initializeRoot(){
        EmployeeMainPageController employeeMainPageController = new EmployeeMainPageController(modalLayout);
        model.setDisplayer(employeeMainPageController);
        root= employeeMainPageController.getRoot();
        pageManager.changePage(root, this);
        isOpened = true;
    }


    @Override
    public void modifyDisplay(boolean val) {
        isOpened = val;

    }
}
