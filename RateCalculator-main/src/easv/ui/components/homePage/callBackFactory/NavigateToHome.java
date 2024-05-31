package easv.ui.components.homePage.callBackFactory;
import easv.ui.components.common.PageManager;
import easv.ui.components.map.WorldMap;
import easv.ui.components.homePage.openPageObserver.Subject;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class NavigateToHome implements CallBack, Subject {

        private PageManager pageManager;
        private Parent root;
        private boolean isOpened;
        private IModel model;
        private StackPane modalLayout;

    public NavigateToHome(PageManager pageManager, IModel model, StackPane modalLayout) {
            this.pageManager = pageManager;
            this.modalLayout= modalLayout;
            this.model=model;
        }

        @Override
        public void call() {
        if (isOpened){
            return;
        }
            initializeRoot();
            pageManager.changePage(root,this);
            isOpened= true;
        }

        private void initializeRoot(){
            WorldMap  worldMap= new WorldMap(modalLayout,model);
            root= worldMap.getRoot();
        }

    @Override
    public void modifyDisplay(boolean val) {
        isOpened=val;
    }
}
