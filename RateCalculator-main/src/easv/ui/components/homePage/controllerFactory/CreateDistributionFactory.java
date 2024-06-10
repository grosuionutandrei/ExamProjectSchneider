package easv.ui.components.homePage.controllerFactory;

import easv.ui.pages.PageControlable;
import easv.ui.pages.distribution.DistributionController;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.layout.StackPane;

public class CreateDistributionFactory implements PageControllerFactory{
    private final IModel model;
    private final StackPane firstLayout;
    public CreateDistributionFactory(IModel model, StackPane firstLayout) {
        this.model = model;
        this.firstLayout = firstLayout;
    }

    @Override
    public PageControlable createController() {
        return new DistributionController(model,firstLayout);
    }
}
