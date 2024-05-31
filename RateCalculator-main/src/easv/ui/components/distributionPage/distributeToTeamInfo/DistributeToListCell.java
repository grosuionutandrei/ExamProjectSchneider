package easv.ui.components.distributionPage.distributeToTeamInfo;

import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributeFromController;
import easv.ui.pages.distribution.ControllerMediator;
import easv.ui.pages.distribution.DistributionType;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;


public class DistributeToListCell extends ListCell<Team> {
    private IModel model;
    private DistributeFromController controller;
    private ControllerMediator distributeToMediator;
    private DistributionType distributionType;
    private StackPane  modalLayout;


    public DistributeToListCell(IModel model, ControllerMediator distributeToMediator,DistributionType distributionType,StackPane modalLayout) {
        this.model = model;
        this.distributeToMediator= distributeToMediator;
        this.distributionType = distributionType;
        this.modalLayout = modalLayout;
    }

    @Override
    protected void updateItem(Team team, boolean empty) {
        super.updateItem(team, empty);

        if (empty || team == null) {
            setGraphic(null);
            setText(null);
        } else {
            if(isSelected()){
                setTextFill(Color.BLACK);
            }
            if (controller == null) {
                controller = new DistributeFromController(model, team,distributeToMediator, distributionType,modalLayout);
            } else {
                controller.setTeamToDisplay(team);
                controller.populateComponentWithValues();
            }
            setGraphic(controller.getRoot());
        }
    }
}

