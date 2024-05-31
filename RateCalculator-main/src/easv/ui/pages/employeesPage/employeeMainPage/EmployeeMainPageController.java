package easv.ui.pages.employeesPage.employeeMainPage;

import easv.Utility.DisplayEmployees;
import easv.be.Country;
import easv.be.Employee;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.searchComponent.EmployeeSearchHandler;
import easv.ui.components.searchComponent.SearchController;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.pages.modelFactory.ModelFactory;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXScrollPane;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class EmployeeMainPageController implements Initializable, DisplayEmployees {
    @FXML
    private VBox employeesContainer;
    @FXML
    private Parent employeePage;
    private IModel model;
    @FXML
    private MFXProgressSpinner progressBar;
    @FXML
    private MFXComboBox<Country> countriesFilterCB;
    @FXML
    private MFXComboBox<Team> teamsFilterCB;
    @FXML
    private MFXComboBox<Region> regionsFilter;
    @FXML
    private MFXScrollPane employeesScrollPane;

    public VBox getEmployeesContainer() {
        return employeesContainer;
    }

    private StackPane firstLayout;
    @FXML
    private PopupControl popupWindow;
    @FXML
    private TextField searchField, dayRateField, hourlyRateField;
    @FXML
    private ListView<Employee> searchResponseHolder;
    @FXML
    private Button goBackButton;
    private Service<Void> loadEmployeesFromDB;
    @FXML
    private HBox countryRevertButton, teamRevertButton, regionRevertButton;
    @FXML
    private VBox employeesVboxContainer;



    @FXML
    private SVGPath svgPathButton;
    private boolean filterActive = false;

    @FXML
    private boolean regionFilterActive = false;
    @FXML
    private boolean countryFilterActive = false;
    @FXML
    private boolean teamFilterActive = false;
    @FXML
    private SVGPath teamRevertSvg;
    @FXML
    private SVGPath countryRevertSvg;
    @FXML
    private SVGPath regionRevertSvg;
    @FXML
    private SVGPath svgPath;
    private ObservableList<Team> teams;
    private ObservableList<Country> countries;
    private EmployeeInfoController selectedToEdit;
    private Service<Boolean> calculateEditOperationPerformedEdit;
    private String dayRateValue;
    private String hourlyRateValue;
    @FXML
    private GridPane employeeSearchContainer;
    private SearchController<Employee> employeeSearch;
    private EmployeeSearchHandler employeeSearchHandler;


    public EmployeeMainPageController(StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeesMainPage.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        try {
            employeePage = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    public Parent getRoot() {
        return employeePage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            model = ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
            progressBar.setVisible(true);
            employeeSearchHandler = new EmployeeSearchHandler(model);
            employeeSearch = new SearchController<>(employeeSearchHandler);
            employeeSearchContainer.add(employeeSearch.getSearchRoot(), 0, 0);

            initializeEmployeeLoadingService();

            /*populate the filter combo boxes  with the associated values*/
            populateFilterComboBox();
            /*add listener for the region to change the countries filter combo box  values  */
            addRegionFilterListener();
            /* add the listener that will change the team list based on the selected country */
            addCountryFilterListener();
            /*add teams filter listener that will change the displayed employees based on the selected team*/
            addTeamFilterListener();
            /*undo the region filter */
            revertRegionFilter(regionRevertButton, regionRevertSvg);

            /*undo the country filter*/
            revertCountryFilter(countryRevertButton, countryRevertSvg);
            revertTeamFilter(teamRevertButton,teamRevertSvg);

            /*change the style for the revert button to have the same style like the filter inputs , when are on focus  */
            addFocusListener(countriesFilterCB, countryRevertButton);
            addFocusListener(teamsFilterCB, teamRevertButton);

            addFocusListener(regionsFilter, regionRevertButton);

        } catch (RateException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }
    /** Displays components for each employee in the scrollpane */


    public void displayEmployees() {
        employeesContainer.getChildren().clear();
        model.getUsersToDisplay()
                .forEach(e -> {
                    DeleteEmployeeController deleteEmployeeController = new DeleteEmployeeController(firstLayout, model, e);
                    EmployeeInfoController employeeInfoController = new EmployeeInfoController(e, deleteEmployeeController, model, firstLayout, this);
                    employeesContainer.getChildren().add(employeeInfoController.getRoot());
                });
    }

    /**
     * Initializes and starts a background service to load employee data from the database
     * This method initializes a JavaFX Service to perform the data loading in a background thread and
     * defines success and failure handlers for the service
     * This method to start loading employee data from the database while keeping the UI responsive
     */
    private void initializeEmployeeLoadingService() {
        progressBar.setVisible(true);
        loadEmployeesFromDB = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        model.returnEmployees();
                        return null;
                    }

                    ;
                };
            }
        };

        loadEmployeesFromDB.setOnSucceeded((event) -> {
            /* Update the UI with loaded employees*/
            displayEmployees();
            progressBar.setVisible(false);
        });
        loadEmployeesFromDB.setOnFailed((event) -> {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_EMPLOYEES_FAILED.getValue());


        });
        loadEmployeesFromDB.restart();
    }



    private void addFocusListener(MFXTextField filterInput, HBox sibling) {
        filterInput.focusWithinProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                sibling.getStyleClass().add("countryFilterFocused");
            } else {
                sibling.getStyleClass().remove("countryFilterFocused");
            }
        });
    }

    /**
     * populate filter combo boxes with values
     */
    private void populateFilterComboBox() {
        countriesFilterCB.setItems(model.getOperationalCountries().sorted());
        teamsFilterCB.setItems(model.getOperationalTeams().sorted());
        regionsFilter.setItems(model.getOperationalRegions().sorted());
    }

    /**show the rates off the selected filter teams*/
    public void setTotalRates(){
        dayRateField.setText(model.calculateGroupDayRate() + " " );
        hourlyRateField.setText(model.calculateGroupHourRate()+" ");
    }


    /**
     * add  region selection listener  that will filter the countries by regions and teams by countries in the region
     */
    private void addRegionFilterListener() {
        this.regionsFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                regionFilterActive = true;
                ObservableList<Country> regionCountries = FXCollections.observableArrayList(newValue.getCountries());
                if (this.countriesFilterCB.getSelectionModel().getSelectedItem() != null) {
                    this.countriesFilterCB.clearSelection();
                }
                this.countriesFilterCB.setItems(regionCountries);
                //save the selected countries in the model , in order to be displayed , change the name toFilterByRegion
                countriesFilterCB.selectItem(regionCountries.getFirst());
                countriesFilterCB.positionCaret(0);
                model.filterByRegion(newValue, newValue.getCountries());
                ObservableList<Team> regionCountriesTeams = FXCollections.observableArrayList();
                for (Country country : regionCountries) {
                    regionCountriesTeams.addAll(country.getTeams());
                }
                this.teamsFilterCB.setItems(regionCountriesTeams);
                this.teamsFilterCB.selectItem(regionCountriesTeams.getFirst());
                showRevertButtonByFilterActive(regionRevertButton, regionRevertSvg);
                setTotalRates();
            }
        });
    }



    /**
     * add country selection listener
     */
    private void addCountryFilterListener() {
        this.countriesFilterCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                countryFilterActive = true;
                ObservableList<Team> countryTeams = FXCollections.observableArrayList(newValue.getTeams());
                this.teamsFilterCB.clearSelection();
              try {
                  if (!countryTeams.isEmpty()) {
                      this.teamsFilterCB.setItems(countryTeams);
                  }
                  this.teamsFilterCB.selectItem(countryTeams.getFirst());
              }catch (IndexOutOfBoundsException e){
                  System.out.println(e.getMessage());
              }
                model.filterByCountryTeams(newValue);
                showRevertButtonByFilterActive(countryRevertButton, countryRevertSvg);
                setTotalRates();
            }
        });
    }

    /**
     * add teams filter selection listener
     */

    private void addTeamFilterListener() {
        this.teamsFilterCB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                teamFilterActive= true;
                model.filterEmployeesByTeam(newValue);
                showRevertButtonByFilterActive(teamRevertButton, teamRevertSvg);
             setTotalRates();
            }
        });
    }

    public void setSelectedComponentStyleToSelected(EmployeeInfoController selectedToEdit) {
        if (this.selectedToEdit != null) {
            this.selectedToEdit.getRoot().getStyleClass().remove("employeeComponentClicked");
        }
        this.selectedToEdit = selectedToEdit;
        this.selectedToEdit.getRoot().getStyleClass().add("employeeComponentClicked");
    }


    /**
     * show revert button if the filter is applied
     */
    private void showRevertButtonByFilterActive(HBox button, SVGPath revertSvg) {
        revertSvg.setVisible(true);
        button.setDisable(false);
    }


    private void hideRevertButton(SVGPath svgPath, HBox button) {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(100));
        pauseTransition.setOnFinished((event) -> {
            svgPath.setVisible(false);
            button.setDisable(true);
        });
        pauseTransition.playFromStart();
    }


    /**
     * change the style off the employees container, so the employee component can not be seen from the dit page
     */
    public void setEmployeesVboxContainerStyleToEdit() {
        this.employeesVboxContainer.getStyleClass().add("employeesVboxContainerOnEdit");
        this.employeesScrollPane.getStyleClass().add("employeesVboxContainerOnEdit");
    }

    /**
     * change back the style of the employee component when close, the edit window
     */
    public void setEmployeesVboxContainerStyleToDefault() {
        this.employeesVboxContainer.getStyleClass().remove("employeesVboxContainerOnEdit");
        this.employeesScrollPane.getStyleClass().remove("employeesVboxContainerOnEdit");
    }





    private void revertRegionFilter(HBox button, SVGPath revertIcon) {
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            undoRegionFilter();
            hideRevertButton(revertIcon, button);
            hideRevertButton(this.teamRevertSvg, teamRevertButton);
            hideRevertButton(this.countryRevertSvg, countryRevertButton);
        });
    }

    /**
     * undo the country filter
     */
    private void revertCountryFilter(HBox button, SVGPath countryRevertSvg) {
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            undoCountryFilter();
            hideRevertButton(countryRevertSvg, button);
            hideRevertButton(this.teamRevertSvg, teamRevertButton);
        });
    }


    /***/

    private void revertTeamFilter(HBox button, SVGPath revertIcon) {
        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            undoTeamFilter();
            hideRevertButton(revertIcon, button);
        });
    }



    /**undo the countries filters , is the region filter is active , than show the region employees , else show all employees*/
    @FXML
    private void undoCountryFilter() {
        if (regionFilterActive && countriesFilterCB.getSelectionModel().getSelectedItem() != null) {
            model.returnEmployeesByRegion(regionsFilter.getSelectionModel().getSelectedItem());
            teamsFilterCB.clearSelection();
         //   model.performRegionTeamsOverheadCalculations();
            // searchField.clear();
            setTotalRates();
            countryFilterActive = false;
            teamFilterActive=false;
        } else {
            model.performEmployeeSearchUndoOperation();
            teamsFilterCB.clearSelection();
            teamsFilterCB.setItems(FXCollections.observableArrayList(model.getOperationalTeams()));
            countriesFilterCB.clearSelection();
            countryFilterActive = false;
            teamFilterActive=false;
            setTotalRatesDefault();
        }
    }
    @FXML
    private void undoRegionFilter() {
        regionsFilter.clearSelection();
        model.performEmployeeSearchUndoOperation();
        countriesFilterCB.clearSelection();
        ObservableList<Country> countriesInSystem = FXCollections.observableArrayList();
        countriesInSystem.setAll(model.getOperationalCountries());
        countriesFilterCB.setItems(countriesInSystem);
        teamsFilterCB.clearSelection();
        ObservableList<Team> teamsInSystem = FXCollections.observableArrayList();
        teamsInSystem.setAll(model.getOperationalTeams());
        teamsFilterCB.setItems(teamsInSystem);
        setTotalRatesDefault();
        regionFilterActive = false;
        countryFilterActive=false;
        teamFilterActive =false;
    }


    public void setTotalRatesDefault() {
        dayRateField.setText("");
        hourlyRateField.setText("");
    }

    private void undoTeamFilter() {
        if (countryFilterActive && teamsFilterCB.getSelectionModel().getSelectedItem() != null) {
            model.returnEmployeesByCountry(countriesFilterCB.getSelectionModel().getSelectedItem());
            teamsFilterCB.clearSelection();
            // searchField.clear();
            setTotalRates();
            teamFilterActive=false;
        } else if (regionFilterActive && teamsFilterCB.getSelectionModel().getSelectedItem() != null) {
            model.returnEmployeesByRegion(regionsFilter.getSelectionModel().getSelectedItem());
            teamsFilterCB.clearSelection();
            // searchField.clear();
            setTotalRates();
            teamFilterActive=false;
        } else {
            model.performEmployeeSearchUndoOperation();
            teamsFilterCB.clearSelection();
            teamsFilterCB.setItems(FXCollections.observableArrayList(model.getOperationalTeams()));
//            countriesFilterCB.clearSelection();
//            countryFilterActive = false;
            setTotalRatesDefault();
            teamFilterActive=false;
        }
    }


    /**are filters active*/
    public boolean isFilterActive(){
        return regionFilterActive || countryFilterActive || teamFilterActive;
    }


}







