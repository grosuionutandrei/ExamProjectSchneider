package easv.ui.components.homePage.controllerFactory;

import easv.ui.pages.PageControlable;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
import javafx.scene.layout.StackPane;

public class TeamPageControllerFactory implements PageControllerFactory{
    private final IModel model;
    private final StackPane firstLayout;
    public TeamPageControllerFactory(IModel model, StackPane firstLayout) {
        this.model = model;
        this.firstLayout = firstLayout;
    }

    @Override
    public PageControlable createController() {
        return new TeamsPageController(model,firstLayout);
    }
}
