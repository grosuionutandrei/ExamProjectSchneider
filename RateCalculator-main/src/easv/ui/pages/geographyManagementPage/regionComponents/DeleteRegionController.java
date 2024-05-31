package easv.ui.pages.geographyManagementPage.regionComponents;

import easv.Utility.WindowsManagement;
import easv.be.Region;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.confirmationView.ConfirmationWindowController;
import easv.ui.components.confirmationView.OperationHandler;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import easv.ui.pages.modelFactory.IModel;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeleteRegionController implements Initializable, OperationHandler {

    @FXML
    private VBox deleteComponent;
    private StackPane pane;
    private IModel model;
    private Region region;
    private ConfirmationWindowController confirmationWindowController;
    private GeographyManagementController controller;
    private Service<Void> deleteEmployee;

    public DeleteRegionController(StackPane pane , IModel model, Region region, GeographyManagementController controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DeleteRegionComponent.fxml"));
        loader.setController(this);
        this.pane = pane;
        this.model= model;
        this.region = region;
        this.controller = controller;
        try {
            deleteComponent = loader.load();

        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }


    public VBox getRoot() {
        return deleteComponent;
    }

    private void deleteOperation() {
        pane.getChildren().clear();
        ConfirmationWindowController confirmationWindowController = new ConfirmationWindowController(pane, this);
        pane.getChildren().add(confirmationWindowController.getRoot());
        pane.setDisable(false);
        pane.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteComponent.addEventHandler(MouseEvent.MOUSE_CLICKED, this::addEventHandler);
    }
    private void addEventHandler(MouseEvent event) {
        deleteOperation();
    }

    @Override
    public void performOperation() {
        initializeDelete();


    }
    private void initializeDelete() {

        deleteEmployee = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        model.deleteRegion(region);
                        return null;
                    }
                };
            }
        };


        deleteEmployee.setOnSucceeded(event -> {
            WindowsManagement.closeStackPane(pane);
            controller.updateRegionComponents();
        });


        deleteEmployee.setOnFailed(event -> {
            confirmationWindowController.setErrorMessage(ErrorCode.DELETING_EMPLOYEES_FAILED.getValue());
        });

        deleteEmployee.restart();
    }

}

