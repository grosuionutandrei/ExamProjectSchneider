package easv.ui.components.homePage.controllerFactory;

import easv.ui.pages.PageControlable;
import easv.ui.pages.createEmployeePage.CreateController;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.layout.StackPane;

public class CreateControllerFactory implements PageControllerFactory {
    private final IModel model;
    private final StackPane firstLayout;
    public CreateControllerFactory(IModel model, StackPane firstLayout) {
        this.model = model;
        this.firstLayout = firstLayout;
    }
    @Override
    public PageControlable createController() {
        return new CreateController(model,firstLayout);
    }
}
