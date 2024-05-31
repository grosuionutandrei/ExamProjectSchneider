package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.Utility.WindowsManagement;
import easv.be.Country;
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

public class DeleteCountryController implements Initializable, OperationHandler {

    @FXML
    private VBox deleteComponent;
    private StackPane firstLayout;
    private IModel model;
    private Country country;
    private ConfirmationWindowController confirmationWindowController;
    private GeographyManagementController controller;
    private Service<Void> deleteCountry;

    public DeleteCountryController(StackPane firstLayout , IModel model, Country country, GeographyManagementController controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DeleteCountryComponent.fxml"));
        loader.setController(this);
        this.firstLayout=firstLayout;
        this.model= model;
        this.country = country;
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
        firstLayout.getChildren().clear();
        ConfirmationWindowController confirmationWindowController = new ConfirmationWindowController(firstLayout, this);
        firstLayout.getChildren().add(confirmationWindowController.getRoot());
        firstLayout.setDisable(false);
        firstLayout.setVisible(true);
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

        deleteCountry = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        model.deleteCountry(country);
                        return null;
                    }
                };
            }
        };


        deleteCountry.setOnSucceeded(event -> {
            WindowsManagement.closeStackPane(firstLayout);
            controller.updateCountryComponents();
        });

        deleteCountry.setOnFailed(event -> {
            confirmationWindowController.setErrorMessage(ErrorCode.DELETING_EMPLOYEES_FAILED.getValue());
        });

        deleteCountry.restart();
    }

}

