package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.distribution.DistributionController;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigateToDistribution implements CallBack,Subject {
    private PageManager pageManager;
    private Parent root;
    private boolean isOpened;
    private IModel model;
    private StackPane secondLayout;

    public NavigateToDistribution(PageManager pageManager, IModel model,StackPane secondLayout) {
        this.pageManager= pageManager;
        this.model= model;
        this.secondLayout=secondLayout;
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
        DistributionController distributionController = new DistributionController(model,secondLayout);
        root=distributionController.getDistributionPage();
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;
    }
}
