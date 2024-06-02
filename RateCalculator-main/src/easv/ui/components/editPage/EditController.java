package easv.ui.components.editPage;

import easv.Utility.EmployeeValidation;
import easv.Utility.WindowsManagement;
import easv.be.*;
import easv.be.Currency;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.common.errorWindow.ErrorWindowController;
import easv.ui.pages.employeesPage.employeeInfo.EmployeeInfoController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class EditController implements Initializable {

    @FXML
    private StackPane componentParent;
    @FXML
    private HBox closeButton;
    @FXML
    private HBox saveButton;
    @FXML
    private ScrollPane teamsAndCountries;

    private IModel model;
    @FXML
    private MFXTextField multiplierTF;
    @FXML
    private MFXTextField nameInput, salaryTF, workingHoursTF, annualAmountTF, dayWorkingHoursInput, percentageDisplayer;

    @FXML
    private MFXComboBox<Region> regionComboBox;
    @FXML
    private MFXComboBox<Country> countryCB;
    @FXML
    private MFXComboBox<String> currencyCB;
    @FXML
    private MFXComboBox<Team> teamComboBox;
    @FXML
    private MFXComboBox<EmployeeType> overOrResourceCB;
    @FXML
    private MFXComboBox<Configuration> configurations;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private LineChart<String, BigDecimal> lineChart;
    @FXML
    private static final String EMPTY = "";
    @FXML
    private static String NOT_AVAILABLE = "N/A";

    @FXML
    private StackPane spinnerLayer;

    private StackPane firstLayout;

    private Employee employee;
    private EmployeeInfoController employeeDisplayer;
    private Service<Employee> editService;

    private Service<Boolean> calculateEditOperationPerformedEdit;

    public EditController(IModel model, StackPane firstLayout, Employee employee, EmployeeInfoController employeeDisplayer) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditStackPane.fxml"));
        loader.setController(this);
        this.model = model;
        this.firstLayout = firstLayout;
        this.employee = employee;
        this.employeeDisplayer = employeeDisplayer;
        try {
            componentParent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_INPUT.getValue());
        }
    }

    private void addCloseButtonAction() {
        this.closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            employeeDisplayer.setEmployeesVboxContainerStyleToDefault();
            WindowsManagement.closeStackPane(firstLayout);
        });
    }

    public StackPane getRoot() {
        return componentParent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addCloseButtonAction();
        //initialize the percentage inputs listeners
        initializePercentageInputValidationListeners();
        //initialize the digits inputs listeners
        initializeDigitsValidationListeners();
        // initialize the letters inputs listeners
        initializeLettersValidationListeners();
        //populate inputs with the selected employee to edit data
        populateInputs();
        //populate inputs with the values of the selected configuration from history(dropdown menu)
        populateSelectedConfiguration();
        // save the edit configuration
        saveEdit();
        //show history graph on screen
        populateComboBoxWithYears(employee);
        //add combobox  listener
        yearsComboBoxListener(employee);
        //show all history
        populateChartInitial(employee);

    }

    /**
     * populate input fields with the employee data
     */
    private void populateInputs() {
        //set the configuration
        this.percentageDisplayer.setText(calculateEmploueeUtilizatiion(employee));
        //setRegionInfo
        if (employee.getRegions() != null && (!employee.getRegions().isEmpty())) {
            this.regionComboBox.setItems(FXCollections.observableArrayList(employee.getRegions()));
            if (!employee.getRegions().isEmpty()) {
                String employeeRegionName = employee.getRegions().get(0).getRegionName();
                Region regionToSelect = regionComboBox.getItems().stream()
                        .filter(c -> c.getRegionName().equals(employeeRegionName))
                        .findFirst()
                        .orElse(null);
                this.regionComboBox.selectItem(regionToSelect);
            }
        } else {
            this.regionComboBox.setText(NOT_AVAILABLE);
        }


        if (employee.getCountries() != null && (!employee.getCountries().isEmpty())) {
            //set Country Info
            this.countryCB.setItems(FXCollections.observableArrayList(employee.getCountries()));
            String employeeCountryName = employee.getCountries().get(0).getCountryName();
            Country countryToSelect = countryCB.getItems().stream()
                    .filter(c -> c.getCountryName().equals(employeeCountryName))
                    .findFirst()
                    .orElse(null);
            countryCB.selectItem(countryToSelect);
        } else {
            this.countryCB.setText(NOT_AVAILABLE);
        }
        this.nameInput.setText(employee.getName());

        //set team info
        if (employee.getTeams() != null && (!employee.getTeams().isEmpty())) {
            this.teamComboBox.setItems(FXCollections.observableArrayList(employee.getTeams()));
            String employeeTeamName = employee.getTeams().get(0).getTeamName();
            Team teamToSelect = teamComboBox.getItems().stream()
                    .filter(c -> c.getTeamName().equals(employeeTeamName))
                    .findFirst()
                    .orElse(null);
            teamComboBox.selectItem(teamToSelect);
        } else {
            teamComboBox.setText(NOT_AVAILABLE);
        }
        //set configuration info
        Configuration config = employee.getActiveConfiguration();
        if (config != null) {
            setInputsValuesWithConfiguration(config);
        } else {
            setInputsValuesConfigurationNull();
        }

        //set currency inputs
        this.currencyCB.setItems(FXCollections.observableArrayList(Arrays.stream(Currency.values()).map(Enum::name).toList()));
        this.currencyCB.selectItem(employee.getCurrency().name());
        //set resource fields
        this.overOrResourceCB.setItems(FXCollections.observableArrayList(EmployeeType.values()));
        this.overOrResourceCB.selectItem(employee.getType());
        //set configurations items
        this.configurations.setItems(FXCollections.observableArrayList(employee.getConfigurations()));
       // this.configurations.selectItem(employee.getActiveConfiguration());
    }


    /**
     * save the edited employee
     */
    private void saveEdit() {
        this.saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (EmployeeValidation.areNamesValid(nameInput) &&
                    EmployeeValidation.areNumbersValid(salaryTF, workingHoursTF, annualAmountTF, dayWorkingHoursInput) &&
                    EmployeeValidation.isOverheadMultiplierValid(multiplierTF) &&
                    EmployeeValidation.isItemSelected(currencyCB, overOrResourceCB)) {
                Configuration editedConfiguration = getConfiguration();
                Employee editedEmployee = getEmployee(editedConfiguration);
                if (model.isEditOperationPerformed(employee, editedEmployee)) {
                    spinnerLayer.setDisable(false);
                    spinnerLayer.setVisible(true);
                    initializeService(employee, editedEmployee);
                } else {
                    employeeDisplayer.setEmployeesVboxContainerStyleToDefault();
                    WindowsManagement.closeStackPane(this.firstLayout);
                }
            }
        });
    }

    /**
     * create the employee object with the edited values
     */
    private Employee getEmployee(Configuration editedConfiguration) {
        Currency currency = Currency.valueOf(this.currencyCB.getSelectedItem());
        String name = this.nameInput.getText();
        EmployeeType employeeType = overOrResourceCB.getSelectedItem();
        Employee editedEmployee = new Employee(name, employeeType, currency);
        editedEmployee.setConfigurations(employee.getConfigurations());
        editedEmployee.setActiveConfiguration(editedConfiguration);
        editedEmployee.setId(employee.getId());
        editedEmployee.setUtilPerTeams(employee.getUtilPerTeams());
        return editedEmployee;
    }

    /**
     * create the Configuration object from the inputs fields
     */
    private Configuration getConfiguration() {
        BigDecimal annualSalary = new BigDecimal(convertToDecimalPoint(salaryTF.getText()));
        BigDecimal fixedAnnualAmount = new BigDecimal(convertToDecimalPoint(annualAmountTF.getText()));
        BigDecimal overheadMultiplier = new BigDecimal(convertToDecimalPoint(multiplierTF.getText()));
        BigDecimal utilizationPercentage = BigDecimal.ZERO;
        if (!percentageDisplayer.getText().isEmpty()) {
            utilizationPercentage = new BigDecimal(convertToDecimalPoint(percentageDisplayer.getText()));
        }
        BigDecimal workingHours = new BigDecimal(convertToDecimalPoint(workingHoursTF.getText()));
        double dayWorkingHours = Double.parseDouble(convertToDecimalPoint(dayWorkingHoursInput.getText()));
        return new Configuration(annualSalary, fixedAnnualAmount, overheadMultiplier, utilizationPercentage, workingHours, LocalDateTime.now(), true, dayWorkingHours);
    }


    /**
     * populate the input fields with the selected configuration values
     */
    private void populateSelectedConfiguration() {
        this.configurations.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setInputsValuesWithConfiguration(newValue);
            }
        });
    }

    /**
     * set the input values with the configuration selected from the history values
     *
     * @param configuration the configuration object that is active for an employee
     */
    private void setInputsValuesWithConfiguration(Configuration configuration) {
        this.multiplierTF.setText(String.valueOf(configuration.getOverheadMultiplier()));
        this.salaryTF.setText(String.valueOf(configuration.getAnnualSalary()));
        this.workingHoursTF.setText(String.valueOf(configuration.getWorkingHours()));
        this.annualAmountTF.setText(String.valueOf(configuration.getFixedAnnualAmount()));
        this.dayWorkingHoursInput.setText(configuration.getDayWorkingHours() + "");
    }


    /**
     * set the  input value with no data if the config is null
     */
    private void setInputsValuesConfigurationNull() {
        this.multiplierTF.setText(NOT_AVAILABLE);
        this.salaryTF.setText(NOT_AVAILABLE);
        this.workingHoursTF.setText(NOT_AVAILABLE);
        this.annualAmountTF.setText(NOT_AVAILABLE);
        this.dayWorkingHoursInput.setText(NOT_AVAILABLE);
    }


    /**
     * calculate utilizationPercentage for employee
     */
    private String calculateEmploueeUtilizatiion(Employee employee) {
        Map<Integer, BigDecimal> employeeUtilization = null;
        try {
            employeeUtilization = model.getEmployeeUtilizationInTeams(employee.getId());
        } catch (RateException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.EMPLOYEE_UTILIZATION.getValue());
        }
        BigDecimal utilization = BigDecimal.ZERO;
        if (employeeUtilization != null) {
            for (BigDecimal util : employeeUtilization.values()) {
                utilization = utilization.add(util);
                System.out.println(util+ "from edit");
            }
            return utilization.toString();
        }else{
            System.out.println("is null");
            return EMPTY;
        }

    }




    /**
     * call the EmployeeInfoController to update the edited userValues,and to update the performed calculations
     */
    private void updateUserValues(Employee employee) {
        try {
            this.employeeDisplayer.setEmployeeName(employee.getName());
            this.employeeDisplayer.setEmployeeType(employee.getEmployeeType());
            this.employeeDisplayer.setEmployee(employee);
            this.employeeDisplayer.setDayRate(employee.getActiveConfiguration().getDayRate().toString());
            this.employeeDisplayer.setHourlyRate(employee.getActiveConfiguration().getHourlyRate().toString());
            if (employeeDisplayer.isFilterActive()) {
                this.employeeDisplayer.refreshRates();
            }
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
            pauseTransition.setOnFinished((e) -> WindowsManagement.closeStackPane(this.firstLayout));
            pauseTransition.playFromStart();
        } catch (NullPointerException e) {

            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
            WindowsManagement.closeStackPane(this.spinnerLayer);
        }

    }


    /**
     * initialize  percentage inputs validation  listeners
     */
    private void initializePercentageInputValidationListeners() {
        EmployeeValidation.addNonEmptyPercentageListener(percentageDisplayer);
        EmployeeValidation.addNonEmptyPercentageListener(multiplierTF);
    }

    /**
     * initialize digits inputs validation listeners
     */
    private void initializeDigitsValidationListeners() {
        EmployeeValidation.addInputDigitsListeners(salaryTF);
        EmployeeValidation.addInputDigitsListeners(workingHoursTF);
        EmployeeValidation.addInputDigitsListeners(annualAmountTF);
        EmployeeValidation.addInputDigitsListeners(dayWorkingHoursInput);
    }

    /**
     * initialize letters inputs validation listeners
     */
    private void initializeLettersValidationListeners() {
        EmployeeValidation.addLettersOnlyInputListener(nameInput);
        EmployeeValidation.addLettersOnlyInputListener(countryCB);
    }

    private void initializeService(Employee originalEmployee, Employee editedEmployee) {
        this.editService = new Service<>() {
            @Override
            protected Task<Employee> createTask() {
                return new Task<Employee>() {
                    @Override
                    protected Employee call() throws Exception {
                        return model.updateEditedEmployee(originalEmployee, editedEmployee);
                    }
                };
            }
        };

        this.editService.setOnSucceeded((edit) -> {
            if (editService.getValue()!=null) {
                updateUserValues(editedEmployee);
                employeeDisplayer.setEmployeesVboxContainerStyleToDefault();
            } else {
                this.spinnerLayer.setVisible(false);
                this.spinnerLayer.setDisable(true);
                ErrorWindowController errorWindowController = new ErrorWindowController(spinnerLayer,ErrorCode.OPERATION_DB_FAILED.getValue());
                ExceptionHandler.errorAlertMessage(ErrorCode.OPERATION_DB_FAILED.getValue());
            }
        });
        this.editService.setOnFailed((error) -> {
            PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
            pauseTransition.setOnFinished((e) -> {
                ExceptionHandler.errorAlertMessage(ErrorCode.OPERATION_DB_FAILED.getValue());
                this.spinnerLayer.setVisible(false);
                this.spinnerLayer.setDisable(true);
            });
        });
        editService.restart();
    }

    /**
     * convert from comma to point
     */
    private String convertToDecimalPoint(String value) {
        String validFormat = null;
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }

    /* listener that listens changes in selected years of combobox and calls a method to populate pieChart*/
    public void yearsComboBoxListener(Employee employee) {
        yearComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateChartForYear(employee, newValue);
            }
        });
    }

    // EMPLOYEE HISTORY


    //show full history ,initial
    private void populateChartInitial(Employee employee) {
        List<XYChart.Series<String, BigDecimal>> chartSeries = new ArrayList<>();
        XYChart.Series<String, BigDecimal> daySeries = new XYChart.Series<>();
        daySeries.setName("DayRates");

        XYChart.Series<String, BigDecimal> hourSeries = new XYChart.Series<>();
        hourSeries.setName("HourRates");

        for (Configuration config : employee.getConfigurations()) {
            daySeries.getData().add(new XYChart.Data<>(config.getSavedDate().format(DateTimeFormatter.ofPattern("MMM dd")), config.getDayRate()));
            hourSeries.getData().add(new XYChart.Data<>(config.getSavedDate().format(DateTimeFormatter.ofPattern("MMM dd")), config.getHourlyRate()));
        }

        chartSeries.add(daySeries);
        chartSeries.add(hourSeries);

        lineChart.getData().clear();
        lineChart.getData().addAll(chartSeries);
    }


    /**
     * populates the lineChart with history from a selected year, it includes day rates and months
     * initializes a new series for an XYChart with String as the X-axis type and BigDecimal as the Y-axis type
     * format String into "Jan 01"
     *
     * @param selectedYear is the year that is selected from a combobox
     */
    private void populateChartForYear(Employee employee, int selectedYear) {

        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
        series.setName(employee.getName());
        /* Get the configurations for the selected year*/
        List<Configuration> configurations = employee.getConfigurations().stream()
                .filter(config -> config.getSavedDate().getYear() == selectedYear)
                .sorted(Comparator.comparing(Configuration::getSavedDate))
                .toList();
        /* Populate the series with sorted data from configurations*/
        for (Configuration config : configurations) {
            series.getData().add(new XYChart.Data<>(config.getSavedDate().format(DateTimeFormatter.ofPattern("MMM dd")), config.getDayRate()));
        }
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

    /**
     * populates the ComboBox with years based on the employee configurations history
     * if configurations exist extracts only years from the configurations, sorts them in descending order
     * sets the latest year as the initial value of the ComboBox
     *
     * @param employee the employee whose configurations history is used to populate the ComboBox
     */
    public void populateComboBoxWithYears(Employee employee) {
        List<Configuration> configurations = employee.getConfigurations();
        ObservableList<Integer> yearOptions = FXCollections.observableArrayList();
        if (configurations != null) {
            /*Collect years from configurations*/
            configurations.stream()
                    .map(config -> config.getSavedDate().getYear())
                    .distinct()
                    .sorted(Collections.reverseOrder())
                    .forEach(yearOptions::add);
        }
        yearComboBox.setItems(yearOptions);
        /* Set the latest year as the initial value of the ComboBox*/
        if (!yearOptions.isEmpty()) {
            yearComboBox.setValue(yearOptions.get(0));
        }
    }

}






