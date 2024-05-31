package easv.ui.pages.employeesPage.employeeInfo;

import easv.Utility.WindowsManagement;
import easv.be.*;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.editPage.EditController;
import easv.ui.pages.employeesPage.deleteEmployee.DeleteEmployeeController;
import easv.ui.pages.employeesPage.employeeMainPage.EmployeeMainPageController;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.Label;

public class EmployeeInfoController implements Initializable {

    @FXML
    private HBox employeeComponent;
    @FXML
    protected VBox deleteContainer;
    @FXML
    private Label employeeName, employeeType, country, team, dayRate, hourlyRate, hourlyCurrency, dayCurrency, region;
    @FXML
    private VBox editButton;
    private Employee employee;
    private StackPane firstLayout;
    private DeleteEmployeeController deleteEmployeeController;
    private EmployeeMainPageController employeeController;
    private IModel model;

    private final String NOT_AVAILABLE = "N/A";

    /** Initializes the controller with the necessary dependencies and loads the FXML component, not depend on FXML components being loaded*/
    public EmployeeInfoController(Employee employee, DeleteEmployeeController deleteEmployeeController, IModel model, StackPane firstLayout, EmployeeMainPageController employeeController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeComponent.fxml"));
        loader.setController(this);
        this.employee = employee;
        this.deleteEmployeeController = deleteEmployeeController;
        this.firstLayout = firstLayout;
        this.model = model;
        this.employeeController = employeeController;
        try {
            employeeComponent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }
    public HBox getRoot() {
        return employeeComponent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteContainer.getChildren().clear();
        this.deleteContainer.getChildren().add(deleteEmployeeController.getRoot());
        setLabels();
        addEditAction();
        setEmployeeComponentOnClick();

    }
    /**
     * Updates the UI labels with the employee's details if the employee is not null
     * This includes setting the employee's name, type, countries, teams, regions, day rate, hourly rate, and currency,
     * along with tooltips
     */
    public void setLabels() {
        if (employee != null) {
            employeeName.setText(employee.getName());
            employeeName.setTooltip(new Tooltip(employeeName.getText()));

            employeeType.setText(employee.getType().toString());

            /* Displaying multiple countries */
            StringBuilder countryNames = new StringBuilder();

            if (employee.getCountries().isEmpty()) {
                countryNames.append(NOT_AVAILABLE);
            } else {
                for (Country country : employee.getCountries()) {
                    if (employee.getCountries().size() > 1) {
                        countryNames.append(country.getCountryName()).append(", ");
                    } else {
                        countryNames.append(country.getCountryName());
                    }
                }
            }
            country.setText(countryNames.toString());
            country.setTooltip(new Tooltip(country.getText()));

            /* Displaying multiple teams */
            StringBuilder teamNames = new StringBuilder();
            if (employee.getTeams().isEmpty()) {
                teamNames.append(NOT_AVAILABLE);
            } else {
                for (Team team : employee.getTeams()) {
                    if (employee.getTeams().size() > 1) {
                        teamNames.append(team.getTeamName()).append(", ");
                    } else {
                        teamNames.append(team.getTeamName());
                    }
                }
            }
            team.setText(teamNames.toString());
            team.setTooltip(new Tooltip(team.getText()));

            /* Displaying multiple regions */
            StringBuilder regionNames = new StringBuilder();
            if (employee.getRegions().isEmpty()||employee.getRegions()==null) {
                regionNames.append(NOT_AVAILABLE);
            } else {
                for (Region region : employee.getRegions()) {
                    if (employee.getRegions().size() > 1) {
                        regionNames.append(region.getRegionName()).append(", ");
                    } else {
                        regionNames.append(region.getRegionName());
                    }
                }

            }
            region.setText(regionNames.toString());
            region.setTooltip(new Tooltip(region.getText()));
            dayRate.setText(employee.getActiveConfiguration().getDayRate().setScale(2, RoundingMode.HALF_UP).toString());
            hourlyRate.setText(employee.getActiveConfiguration().getHourlyRate().setScale(2, RoundingMode.HALF_UP).toString());
            hourlyCurrency.setText(employee.getCurrency().toString());
            dayCurrency.setText(employee.getCurrency().toString());
        }
    }



    private void addEditAction() {
        this.editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            EditController editController = new EditController(model, firstLayout, employee, this);
            this.firstLayout.getChildren().add(editController.getRoot());
            employeeController.setSelectedComponentStyleToSelected(this);
            employeeController.setEmployeesVboxContainerStyleToEdit();
            WindowsManagement.showStackPane(firstLayout);
        });
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName.setText(employeeName);
    }

    public void setEmployeeType(EmployeeType employeeType) {
        this.employeeType.setText(employeeType.toString());
    }

    public void setCountry(String country) {
        this.country.setText(country);
    }

    public void setTeam(String team) {
        this.team.setText(team);
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setDayRate(String value) {
        this.dayRate.setText(value);
    }

    public void setHourlyRate(String value) {
        this.hourlyRate.setText(value);
    }


    public void setEmployeesVboxContainerStyleToDefault() {
        this.employeeController.setEmployeesVboxContainerStyleToDefault();
    }

    public void refreshRates() {
        employeeController.setTotalRates();
    }


    //change the style on the clicked employee info  component
    private void setEmployeeComponentOnClick() {
        this.employeeComponent.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            employeeController.setSelectedComponentStyleToSelected(this);

        });
    }

    /**
     * check if filters are active
     */
    public boolean isFilterActive() {
        return employeeController.isFilterActive();
    }

}


