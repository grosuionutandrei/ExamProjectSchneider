package easv.ui.pages.createEmployeePage;
import easv.Utility.EmployeeValidation;
import easv.Utility.WindowsManagement;
import easv.be.*;
import easv.be.Currency;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;


public class CreateController implements Initializable {

    @FXML
    private Parent createPage;
    @FXML
    private MFXTextField nameTF, salaryTF, workingHoursTF, annualAmountTF, utilPercentageTF, multiplierTF, dayWorkingHours;
    @FXML
    private MFXComboBox countryCB, teamCB, regionCB, currencyCB, overOrResourceCB;
    @FXML
    private ImageView clearIMG, employeeIMG;
    @FXML
    private HBox inputsParent;
    @FXML
    private MFXProgressSpinner operationSpinner;
    @FXML
    private Label spinnerLB;
    @FXML
    private Button addTeamBT, removeTeamBT;
    @FXML
    private ListView teamsListView;

    private ObservableList<Country> countries;
    private ObservableList<Team> teams;
    private ObservableList<Region> regions;
    private List<Team> teamsList;
    private List<Integer> teamsUtilizationList;
    private IModel model;
    private Service<Void> saveEmployee;
    private StackPane firstLayout;


    public CreateController(IModel model, StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Create.fxml"));
        loader.setController(this);
        this.model=model;
        this.firstLayout= firstLayout;
        try {
            createPage=loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        teamsList = new ArrayList<>();
        teamsUtilizationList = new ArrayList<>();

        populateComboBoxes();
        addListenersToInputs();
        addTeamButtonListener();
        removeTeamListener();
        addTooltips();
        addRegionSelectionListener(regionCB, countryCB);
        addCountrySelectionListener(countryCB, teamCB);
        listenerForEmptyFieldsAfterSaving();
    }


    /**
     * Saves the employee after validating all inputs.
     */
    @FXML
    private void saveEmployee() throws RateException {
        if(EmployeeValidation.areNamesValid(nameTF, teamsListView) &&
           EmployeeValidation.areNumbersValid(salaryTF, workingHoursTF, annualAmountTF, dayWorkingHours) &&
           EmployeeValidation.arePercentagesValid(multiplierTF, teamsUtilizationList) &&
           EmployeeValidation.isItemSelected(currencyCB, overOrResourceCB))
        {
            enableSpinner();
            firstLayout.getChildren().add(operationSpinner);
            WindowsManagement.showStackPane(firstLayout);

            // Gather input values
            String name = nameTF.getText().trim();
            EmployeeType employeeType = (EmployeeType) overOrResourceCB.getSelectedItem();
            List<Team> teamsToSave = getSelectedTeams();
            Currency currency = getCurrency();
            BigDecimal annualSalary = new BigDecimal(convertToDecimalPoint(salaryTF.getText().trim()));
            BigDecimal fixedAnnualAmount = new BigDecimal(convertToDecimalPoint(annualAmountTF.getText().trim()));
            BigDecimal overheadMultiplier = new BigDecimal(convertToDecimalPoint(multiplierTF.getText().trim()));
            BigDecimal utilizationPercentage = new BigDecimal(teamsUtilizationList.stream().mapToInt(Integer::intValue).sum());
            BigDecimal workingHours = new BigDecimal(convertToDecimalPoint(workingHoursTF.getText().trim()));
            LocalDateTime savedDate = LocalDateTime.now();
            boolean isActive = true;
            double dailyWorkingHours = Double.parseDouble( convertToDecimalPoint(dayWorkingHours.getText()));

            // Create employee and configuration objects
            Employee employee = new Employee(name, employeeType, currency);
            setUtilPercentageForTeams(employee, teamsToSave);
            Configuration configuration = new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, savedDate, isActive,dailyWorkingHours);
            employee.setActiveConfiguration(configuration);

            // Compute rates and save employee
            BigDecimal dayRate = model.getComputedDayRate(employee);
            BigDecimal hourlyRate = model.getComputedHourlyRate(employee, dailyWorkingHours);
            configuration.setDayRate(dayRate);
            configuration.setHourlyRate(hourlyRate);
            saveEmployeeOperation(employee, configuration, teamsToSave);
        }
    }

    /**
     * Sets the utilization percentage for teams in the employee object.
     * @param employee the employee object
     * @param teamsToSave the list of teams to save
     */
    private void setUtilPercentageForTeams(Employee employee, List<Team> teamsToSave) {
        Map<Integer,BigDecimal> utilPerTeams = new HashMap<>();
        for (int i = 0; i < teamsToSave.size(); i++) {
            utilPerTeams.put(teamsToSave.get(i).getId(), new BigDecimal(teamsUtilizationList.get(i)));
        }
        employee.setUtilPerTeams(utilPerTeams);
    }

    /**
     * Gets the selected currency from the combo box.
     * @return the selected currency
     */
    private Currency getCurrency() {
        if (currencyCB.getSelectedItem().equals(Currency.EUR.name())) {
            return Currency.EUR;
        } else {
            return Currency.USD;
        }
    }

    /**
     * Saves the employee operation using a background service.
     * @param employee the employee object
     * @param configuration the configuration object
     * @param teams the list of teams to save
     */
    private void saveEmployeeOperation(Employee employee, Configuration configuration, List<Team> teams) {
        saveEmployee = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        model.addNewEmployee(employee, configuration, teams);
                        return null;
                    }
                };
            }
        };

        saveEmployee.setOnSucceeded(event -> {
            showOperationStatus("Operation Successful!", Duration.seconds(2));
            WindowsManagement.closeStackPane(firstLayout);
            closeWindowSpinner(firstLayout);

        });

        saveEmployee.setOnFailed(event -> {
                showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
                WindowsManagement.closeStackPane(firstLayout);
                closeWindowSpinner(firstLayout);
        });
        saveEmployee.restart();
    }

    /**
     * Shows the operation status with a message.
     * @param message the status message
     * @param duration the duration to display the message
     */
    private void showOperationStatus(String message, Duration duration) {
        spinnerLB.setText(message);
        PauseTransition delay = new PauseTransition(duration);
        delay.setOnFinished(event -> spinnerLB.setText(""));
        delay.play();
    }

    /**
     * Gets the list of selected teams.
     * @return the list of selected teams
     */
    private List<Team> getSelectedTeams() {
        return teamsList;
    }

    /**
     * Adds a listener to the add team button.
     */
    private void addTeamButtonListener(){
        addTeamBT.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(EmployeeValidation.isTeamSelected(teamCB) && EmployeeValidation.isPercentageValid(utilPercentageTF)){
                    Team team = (Team) teamCB.getSelectedItem();
                    BigDecimal utilizationPercentage = new BigDecimal(utilPercentageTF.getText());
                    team.setUtilizationPercentage(utilizationPercentage);
                    teamsList.add(team);
                    teamsUtilizationList.add(Integer.valueOf(utilPercentageTF.getText()));
                    String teamWithUtilization = team.getTeamName() + ",  " + utilPercentageTF.getText() + "%";
                    teamsListView.getItems().add(teamWithUtilization);
                    regionCB.clearSelection();
                    countryCB.clearSelection();
                    teamCB.clearSelection();
                    utilPercentageTF.clear();
                }
        });
    }

    /**
     * Adds a listener to the remove team button.
     */
    private void removeTeamListener(){
        removeTeamBT.addEventHandler(MouseEvent.MOUSE_CLICKED, (e)->{
            if(EmployeeValidation.isTeamToRemoveSelected(teamsListView)){
                teamsList.remove(teamsListView.getSelectionModel().getSelectedIndex());
                teamsUtilizationList.remove(teamsListView.getSelectionModel().getSelectedIndex());
                teamsListView.getItems().remove(teamsListView.getSelectionModel().getSelectedIndex());
        }});
    }

    private void clearFields(){
         inputsParent.getChildren().forEach((child)->{
           if(child instanceof VBox){
               ((VBox) child).getChildren().forEach((input)->{
                   if(input instanceof  MFXTextField){
                       ((MFXTextField) input).clear();
                   }
                   if(input instanceof MFXComboBox<?>){
                       ((MFXComboBox<?>) input).clear();
                       ((MFXComboBox<?>) input).clearSelection();
                   }
               });
           }
         });
         teamCB.clearSelection();
         teamsListView.getItems().clear();
         teamsList.clear();
         teamsUtilizationList.clear();
    }

    private void listenerForEmptyFieldsAfterSaving(){
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(nameTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(annualAmountTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(workingHoursTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(multiplierTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(utilPercentageTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(salaryTF);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(dayWorkingHours);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(currencyCB);
        EmployeeValidation.listenerForEmptyFieldsAfterSaving(overOrResourceCB);
    }

    private void populateComboBoxes() {
            regions = model.getOperationalRegions();
            countries = model.getOperationalCountries();
            teams = model.getOperationalTeams();
            ObservableList<String> currencies = FXCollections.observableArrayList(Currency.EUR.name(), Currency.USD.name());
            ObservableList<EmployeeType> overOrResource = FXCollections.observableArrayList(EmployeeType.Overhead, EmployeeType.Resource);
            regionCB.setItems(regions);
            countryCB.setItems(countries);
            teamCB.setItems(teams);
            currencyCB.setItems(currencies);
            overOrResourceCB.setItems(overOrResource);
    }

    private void addListenersToInputs(){
        //Listeners for the percentages
        EmployeeValidation.addNonEmptyPercentageListener(multiplierTF);
        //
        EmployeeValidation.addInputDigitsListeners(salaryTF);
        EmployeeValidation.addInputDigitsListeners(workingHoursTF);
        EmployeeValidation.addInputDigitsListeners(annualAmountTF);
        EmployeeValidation.addInputDigitsListeners(dayWorkingHours);
        //
        EmployeeValidation.addLettersOnlyInputListener(nameTF);
        EmployeeValidation.addLettersOnlyInputListener(overOrResourceCB);
        EmployeeValidation.addLettersOnlyInputListener(currencyCB);
    }

    private void addRegionSelectionListener(MFXComboBox<Region> region, MFXComboBox<Country> countries) {
        region.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                countries.clearSelection();
                ObservableList<Country> regionCountries= FXCollections.observableArrayList(newValue.getCountries());
                countries.setItems(regionCountries);
                countries.selectItem(regionCountries.get(0));
            }
        });
    }

    private void addCountrySelectionListener(MFXComboBox<Country> country, MFXComboBox<Team> teams) {
        country.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                teams.clearSelection();
                ObservableList<Team> countryTeams = FXCollections.observableArrayList(newValue.getTeams());
                teams.setItems(countryTeams);
                teams.selectItem(countryTeams.get(0));
            }
        });
    }

    private void addTooltips(){
        EmployeeValidation.addNameToolTip(nameTF);
        EmployeeValidation.addCountryToolTip(countryCB);
        EmployeeValidation.addTeamToolTip(teamCB);
        EmployeeValidation.addOverOrResourceToolTip(overOrResourceCB);
        EmployeeValidation.addCurrencyToolTip(currencyCB);
        EmployeeValidation.addValueToolTip(salaryTF, workingHoursTF, annualAmountTF);
        EmployeeValidation.addPercentageToolTip(utilPercentageTF, multiplierTF);
        EmployeeValidation.addRegionToolTip(regionCB);
        EmployeeValidation.addDailyWorkingHoursToolTip(dayWorkingHours);
    }

    private void enableSpinner() {
        spinnerLB.setText("Processing...");
        operationSpinner.setVisible(true);
        operationSpinner.setDisable(false);
    }

    //convert form comma decimal to point decimal
    private String convertToDecimalPoint(String value) {
        String validFormat = null;
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }

    public Parent getCreatePage() {
        return createPage;
    }
    @FXML
    private void clearInputs(){
        clearFields();
    }

    private void closeWindowSpinner(StackPane stackPane){
        PauseTransition pauseTransition =  new PauseTransition(Duration.millis(2000));
        pauseTransition.setOnFinished((e)->{
            WindowsManagement.closeStackPane(firstLayout);
            clearFields();
        });
        pauseTransition.playFromStart();
    }
}
