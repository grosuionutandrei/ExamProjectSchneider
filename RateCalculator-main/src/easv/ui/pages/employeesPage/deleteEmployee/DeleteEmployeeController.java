package easv.ui.pages.employeesPage.deleteEmployee;

import easv.Utility.WindowsManagement;
import easv.be.Employee;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.confirmationView.ConfirmationWindowController;
import easv.ui.components.confirmationView.OperationHandler;
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

public class DeleteEmployeeController implements Initializable, OperationHandler {

    @FXML
    private VBox deleteComponent;
    @FXML
    private VBox deleteContainer;
    private StackPane firstLayout;
    private IModel model;
    private Employee employee;
    private VBox employeesContainer;
    private HBox employeeComponent;
    private ConfirmationWindowController confirmationWindowController;
    private Service<Void> deleteEmployee;

    /** Initializes the controller with the necessary dependencies and loads the FXML component, not depend on FXML components being loaded*/
    public DeleteEmployeeController(StackPane firstLayout , IModel model, Employee employee) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DeleteEmployeeComponenet.fxml"));
        loader.setController(this);
        this.firstLayout=firstLayout;
        this.model= model;
        this.employee=employee;
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
        confirmationWindowController = new ConfirmationWindowController(firstLayout, this);
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
    /** Uses operation handler interface method to perform a method in confirmation popup, calls service to start*/
    @Override
    public void performOperation() {
        initializeDelete();
    }
    /**
     * Initializes and starts a background service to delete employee data from the database
     * This method initializes a JavaFX Service to perform the data loading in a background thread and
     * defines success and failure handlers for the service
     * This method to start loading employee data from the database while keeping the UI responsive
     */
    private void initializeDelete() {

        deleteEmployee = new Service<Void>() {

            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(2000);
                        model.deleteEmployee(employee);
                        return null;
                    }
                };
            }
        };


        deleteEmployee.setOnSucceeded(event -> WindowsManagement.closeStackPane(firstLayout));

        deleteEmployee.setOnFailed(event -> {
            confirmationWindowController.setErrorMessage(ErrorCode.DELETING_EMPLOYEES_FAILED.getValue());
        });

        deleteEmployee.restart();
    }

}

