package easv.ui.components.homePage.controllerFactory;

import easv.ui.components.map.WorldMap;
import easv.ui.pages.PageControlable;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.layout.StackPane;

public class CreateMapFactory implements PageControllerFactory{
    private final IModel model;
    private final StackPane firstLayout;
    public CreateMapFactory(IModel model, StackPane firstLayout) {
        this.model = model;
        this.firstLayout = firstLayout;
    }
    @Override
    public PageControlable createController() {
        return  new WorldMap(firstLayout,model);
    }
}
