package easv.ui.pages.geographyManagementPage.regionComponents;

import easv.Utility.CountryValidation;
import easv.Utility.RegionValidation;
import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Region;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManageRegionController implements Initializable {

    @FXML
    private VBox manageWindow;
    @FXML
    private MFXTextField regionNameTF;
    @FXML
    private MFXComboBox<Country> countriesCB;
    @FXML
    private ListView<Country> countriesListView;
    @FXML
    private Button addCountryBTN, removeCountryBTN, saveBTN, cancelBTN;
    @FXML
    private MFXProgressSpinner progressSpinner;

    private IModel model;
    private StackPane pane;
    private StackPane secondPane;
    private Region region;
    private List<Country> countriesList;
    private Service<Void> saveRegion;
    private GeographyManagementController controller;
    private boolean isEditOperation = false;

    public ManageRegionController(IModel model, StackPane pane, StackPane secondPane, Region region, GeographyManagementController controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ManageRegionPage.fxml"));
        loader.setController(this);
        this.model = model;
        this.pane = pane;
        this.secondPane = secondPane;
        this.region = region;
        this.controller = controller;
        try {
            manageWindow = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        countriesList = new ArrayList<>();

        setFields();
        addCountryListener();
        removeCountryListener();

        saveRegionListener();
        cancelOperationListener();
        addListenersForInputs();
    }

    /**
     * Adds a listener to the add country button.
     */
    private void addCountryListener() {
        addCountryBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(RegionValidation.isCountrySelected(countriesCB)){
                Country country = countriesCB.getSelectedItem();
                countriesList.add(country);
                countriesListView.getItems().add(country);

                countriesCB.clearSelection();
            }
        });
    }

    /**
     * Adds a listener to the remove country button.
     */
    private void removeCountryListener() {
        removeCountryBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(RegionValidation.isCountryToRemoveSelected(countriesListView)){
                countriesList.remove(countriesListView.getSelectionModel().getSelectedIndex());
                countriesListView.getItems().remove(countriesListView.getSelectionModel().getSelectedIndex());
            }});
    }

    /**
     * Adds listeners for inputs.
     */
    private void addListenersForInputs(){
        CountryValidation.addLettersOnlyInputListener(regionNameTF);
        CountryValidation.addLettersOnlyInputListener(countriesCB);
    }

    /**
     * Sets the fields for the region.
     */
    private void setFields() {
        if(region != null){
            isEditOperation = true;
            regionNameTF.setText(region.getRegionName());
            if (region.getCountries().getFirst() != null) {
                countriesListView.getItems().addAll(region.getCountries());
                countriesList.addAll(region.getCountries());
            }
        }
        countriesCB.getItems().addAll(model.getOperationalCountries());
    }

    /**
     * Adds a listener to the save button for saving region operation.
     */
    private void saveRegionListener() {
        saveBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(RegionValidation.isRegionNameValid(regionNameTF) && RegionValidation.isCountryListValid(countriesListView)){
                enableProgressBar();
                if(isEditOperation) {
                    region.setRegionName(regionNameTF.getText());
                    saveRegionOperation(region, countriesList);
                } else {
                    String name = regionNameTF.getText();
                    region = new Region(name);
                    saveRegionOperation(region, countriesList);
                }
            }
        });

    }

    /**
     * Enables the progress bar.
     */
    private void enableProgressBar() {
        progressSpinner.setVisible(true);
        progressSpinner.setDisable(false);
    }

    /**
     * Disables the progress bar.
     */
    private void disableProgressBar() {
        progressSpinner.setVisible(false);
        progressSpinner.setDisable(true);
    }

    /**
     * Executes the save region operation.
     * @param region the region object
     * @param countries the list of countries
     */
    private void saveRegionOperation(Region region, List<Country> countries) {
        saveRegion = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        Thread.sleep(200);
                        if(isEditOperation)
                            model.updateRegion(region, countries);
                        else
                            model.addNewRegion(region, countries);
                        return null;
                    }
                };
            }
        };

        saveRegion.setOnSucceeded(event -> {
            controller.showOperationStatus("Operation Successful!", Duration.seconds(2));
            controller.updateRegionComponents();
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();

        });

        saveRegion.setOnFailed(event -> {
            controller.showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
            WindowsManagement.closeStackPane(pane);
            disableProgressBar();
        });
        saveRegion.restart();
    }

    /**
     * Adds a listener to the cancel button to cancel the operation.
     */
    private void cancelOperationListener() {
        cancelBTN.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            WindowsManagement.closeStackPane(pane);
        });
    }

    /**
     * Retrieves the root VBox.
     * @return the root VBox
     */
    public VBox getRoot(){
        return manageWindow;
    }

}
