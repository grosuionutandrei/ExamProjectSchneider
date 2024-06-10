package easv.ui.components.homePage.controllerFactory;

import easv.ui.pages.PageControlable;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.layout.StackPane;

public class GeographyControllerFactory implements PageControllerFactory{
    private final IModel model;
    private final StackPane firstLayout;
    private final StackPane secondLayout;

    public GeographyControllerFactory(IModel model, StackPane firstLayout, StackPane secondLayout) {
        this.model = model;
        this.firstLayout = firstLayout;
        this.secondLayout = secondLayout;
    }

    @Override
    public PageControlable createController() {
        return new GeographyManagementController(model,firstLayout,secondLayout);
    }
}
