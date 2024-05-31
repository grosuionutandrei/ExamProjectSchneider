package easv.ui.components.homePage.callBackFactory;

import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.modelFactory.IModel;

import easv.ui.pages.teamsPage.TeamsPageController;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigateToModeling  implements CallBack, Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;
    private StackPane modalLayout;



    public NavigateToModeling(PageManager pageManager,IModel model, StackPane modalLayout) {
        this.pageManager= pageManager;
        this.model= model;
        this.modalLayout= modalLayout;

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
        TeamsPageController teamsPageController= new TeamsPageController(model, modalLayout);

        root= teamsPageController.getRoot();
        System.out.println(root);

    }


    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;
    }
}
