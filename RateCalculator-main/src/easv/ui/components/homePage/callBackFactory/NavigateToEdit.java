package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import javafx.scene.Parent;

public class NavigateToEdit implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root ;
    private boolean isOpened;

    public NavigateToEdit(PageManager pageManager) {
        this.pageManager = pageManager;
    }


    @Override
    public void call() {
        if(isOpened){
            return;
        }
        initializeEditController();
        pageManager.changePage(root,this);
        isOpened=true;
    }

    private void initializeEditController(){
     //   ModelingController editController = new ModelingController();
       // root= editController.getModelingPage();
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;

    }
}
