package easv.ui.pages.geographyManagementPage.geographyMainPage;

import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.searchComponent.SearchController;
import easv.ui.pages.geographyManagementPage.countryComponents.CountryComponent;
import easv.ui.pages.geographyManagementPage.countryComponents.DeleteCountryController;
import easv.ui.pages.geographyManagementPage.countryComponents.ManageCountryController;
import easv.ui.pages.geographyManagementPage.regionComponents.DeleteRegionController;
import easv.ui.pages.geographyManagementPage.regionComponents.ManageRegionController;
import easv.ui.pages.geographyManagementPage.regionComponents.RegionComponent;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GeographyManagementController implements Initializable, GeographyInterface {
    @FXML
    private Parent createPage;
    @FXML
    private VBox regionsVBox, countriesVBox;
    @FXML
    private Label operationStatusLB;
    @FXML
    private MFXProgressSpinner progressBar;
    @FXML
    private Button addRegionBTN, addCountryBTN;
    @FXML
    private HBox regionSearchContainer;

    private IModel model;
    private StackPane pane;
    private StackPane secondPane;
    private Service<Void> loadRegionsAndCountriesFromDB;
    private ObservableList<Region> regions;
    private ObservableList<Country> countries;
    private ObservableList<Team> teams;

    public GeographyManagementController(IModel model, StackPane pane, StackPane secondPane) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GeographyManagementPage.fxml"));
        loader.setController(this);
        this.model = model;
        this.pane = pane;
        this.secondPane = secondPane;
        try {
            createPage = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeGeographyLoadingService();
        addRegionButtonListener();
        addCountryButtonListener();
        initializeSearchComponents();

    }

    /**
     * Adds a listener to the add region button.
     */
    private void addRegionButtonListener() {
        addRegionBTN.setOnAction(event -> {
            ManageRegionController manageRegionController = new ManageRegionController(model, pane, secondPane, null, this);
            this.pane.getChildren().add(manageRegionController.getRoot());
            WindowsManagement.showStackPane(pane);
        });
    }

    /**
     * Adds a listener to the add country button.
     */
    private void addCountryButtonListener() {
        addCountryBTN.setOnAction(event -> {
            ManageCountryController manageCountryController = new ManageCountryController(model, pane, secondPane, null, this);
            this.pane.getChildren().add(manageCountryController.getRoot());
            WindowsManagement.showStackPane(pane);
        });
    }

    /**
     * Initializes the service for loading regions and countries from the database.
     */
    private void initializeGeographyLoadingService() {
        enableProgressBar();
        loadRegionsAndCountriesFromDB = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        regions = FXCollections.observableArrayList(model.getOperationalRegions());
                        countries = FXCollections.observableArrayList(model.getOperationalCountries());
                        teams = FXCollections.observableArrayList(model.getOperationalTeams());
                        return null;
                    }
                };
            }
        };

        loadRegionsAndCountriesFromDB.setOnSucceeded((event) -> {
            // Update the UI with loaded Regions and Countries
            displayRegions();
            displayCountries();

            // Hide the progress bar
            disableProgressBar();
        });
        loadRegionsAndCountriesFromDB.setOnFailed((event) -> {

            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_GEOGRAPHY_FAILED.getValue());


        });
        loadRegionsAndCountriesFromDB.restart();
    }

    /**
     * Displays the list of countries.
     */
    private void displayCountries() {
        countriesVBox.getChildren().clear();
        countries.forEach(this::addCountryComponent);
    }

    /**
     * Displays the list of regions.
     */
    private void displayRegions() {
        regionsVBox.getChildren().clear();
        regions.forEach(this::addRegionComponent);
    }

    /**
     * Adds a region component to the UI.
     * @param region the region object
     */
    public void addRegionComponent(Region region) {
        DeleteRegionController deleteRegionController = new DeleteRegionController(pane, model, region, this);
        RegionComponent regionComponent = new RegionComponent(model, pane, region, deleteRegionController, this);
        regionsVBox.getChildren().add(regionComponent.getRoot());
        if (!regions.contains(region))
            regions.add(region);
    }

    /**
     * Updates the region components.
     */
    public void updateRegionComponents() {
        initializeGeographyLoadingService();
    }

    /**
     * Enables the progress bar.
     */
    private void enableProgressBar() {
        progressBar.setDisable(false);
        progressBar.setVisible(true);
        enableStackPane(progressBar);
    }

    /**
     * Disables the progress bar.
     */
    private void disableProgressBar() {
        progressBar.setDisable(true);
        progressBar.setVisible(false);
        disableStackPane();
    }

    /**
     * Enables the stack pane.
     * @param node the node to be enabled
     */
    private void enableStackPane(Node node) {
        pane.getChildren().clear();
        pane.getChildren().add(node);
        pane.setDisable(false);
        pane.setVisible(true);
    }

    /**
     * Disables the stack pane.
     */
    private void disableStackPane() {
        pane.getChildren().clear();
        pane.setDisable(true);
        pane.setVisible(false);
    }

    /**
     * Retrieves the parent node.
     * @return the parent node
     */
    public Parent getCreatePage() {
        return createPage;
    }

    /**
     * Shows the operation status.
     * @param value the status message
     * @param duration the duration for which the status message will be displayed
     */
    public void showOperationStatus(String value, Duration duration) {
        operationStatusLB.setText(value);
        PauseTransition delay = new PauseTransition(duration);
        delay.setOnFinished(event -> operationStatusLB.setText(""));
        delay.play();
    }

    public void addCountryComponent(Country country) {
        DeleteCountryController deleteCountryController = new DeleteCountryController(pane, model, country, this);
        CountryComponent countryComponent = new CountryComponent(model, pane, country, deleteCountryController, this, secondPane);
        countriesVBox.getChildren().add(countryComponent.getRoot());
        if (!countries.contains(country))
            countries.add(country);
    }

    public void updateCountryComponents() {
        initializeGeographyLoadingService();
    }


    //FILTER RELATED LOGIC
    private void initializeSearchComponents() {
        SearchRegionHandler searchRegionHandler = new SearchRegionHandler(this);
        SearchController<Region> searchRegion = new SearchController<>(searchRegionHandler);
        regionSearchContainer.getChildren().add(0, searchRegion.getSearchRoot());

    }


    @Override
    public ObservableList<Region> getRegionsForSearch(String filter) {
        return model.getRegionFilterResults(filter);
    }

    @Override
    public void undoSearchOperationRegion() {
        this.regionsVBox.getChildren().clear();
        this.displayRegions();
    }

    @Override
    public void performSelectSearchOperationRegion(int entityId) {
        this.regionsVBox.getChildren().clear();
        Region region = model.getRegionById(entityId);
        DeleteRegionController deleteRegionController = new DeleteRegionController(pane, model, region, this);
        RegionComponent regionComponent = new RegionComponent(model, pane, region, deleteRegionController, this);
        System.out.println(region);
        regionsVBox.getChildren().add(regionComponent.getRoot());
    }

    @Override
    public ObservableList<Country> getCountriesForSearch(String filter) {
        return null;
    }

    @Override
    public void performSelectSearchOperationCountry(int entityId) {

    }

    @Override
    public void undoSearchOperationCountry() {

    }

    @Override
    public void performSelectSearchOperationTo(int entityId) {

    }
}
