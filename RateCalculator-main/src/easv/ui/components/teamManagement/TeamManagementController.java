package easv.ui.components.teamManagement;

import easv.Utility.WindowsManagement;
import easv.be.Employee;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamManagementEmployeesAdd.EmployeesToAdd;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.components.teamsManagementTeamMembers.TeamMembersController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TeamManagementController implements Initializable {
    @FXML
    private GridPane teamManagementComponent;
    @FXML
    private VBox teamMembersContainer, allEmployeesContainer;
    @FXML
    private TextField grossMargin, markUp;
    @FXML
    private HBox closeButton, saveButton;
    @FXML
    private MFXProgressSpinner operationSpinner;
    @FXML
    private Label spinnerLB;
    private IModel model;
    private StackPane firstLayout;
    private Team team;
    private TeamInfoController teamInfoController;
    private TeamsPageController teamsPageController;

    /** Holds controllers for components in order to track the  changes in them*/
    private List<EmployeesToAdd> employeesToAddList;
    private List<TeamMembersController> teamMembersToAddList;
    private Service<Void> saveTeam;

    /** Initializes the controller with the necessary dependencies and loads the FXML component, not depend on FXML components being loaded*/
    public TeamManagementController(Team team, IModel model, StackPane firstLayout, TeamInfoController teamInfoController, EmployeesToAdd employeesToAdd, TeamsPageController teamsPageController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamManagementComponent.fxml"));
        loader.setController(this);
        this.firstLayout = firstLayout;
        this.model = model;
        this.team = team;
        this.teamInfoController = teamInfoController;
        this.teamsPageController = teamsPageController;
        employeesToAddList = new ArrayList<>();
        teamMembersToAddList = new ArrayList<>();
        try {
            teamManagementComponent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public GridPane getRoot() {
        return teamManagementComponent;
    }
    /** Handles the setup that requires the FXML components to be loaded*/
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayTeamMembers();
        displayAllEmployees();
        populateTextFields();
        addCloseButtonAction();
        Platform.runLater(this::editAction);
    }

    /** Displays only team members for selected team and adds created controller to the list*/
    public void displayTeamMembers() {
        teamMembersToAddList.clear();
        teamMembersContainer.getChildren().clear();
        for (Employee employee : team.getTeamMembers()) {
            TeamMembersController teamMembersController = new TeamMembersController(employee, team, model, this);
            teamMembersContainer.getChildren().add(teamMembersController.getRoot());
            teamMembersToAddList.add(teamMembersController);
        }
    }

    /** Displays all employees in the system  with their left util and adds created controller to the list*/
    public void displayAllEmployees() {
        employeesToAddList.clear();
        allEmployeesContainer.getChildren().clear();
        model.getAllEmployees()
                .forEach(e -> {
                    EmployeesToAdd employeesToAdd = new EmployeesToAdd(e, model, this);
                    allEmployeesContainer.getChildren().add(employeesToAdd.getRoot());
                    employeesToAddList.add(employeesToAdd);
                });
    }

    /** When save edit button is clicked retrieves edited info from user and proceeds to call save method with needed parameters*/

    private void editAction() {
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            enableSpinner();
            returnAllEmployees();
            returnEmployeesToDelete();
            getTeam();
            saveTeamOperation(returnAllEmployees(), returnEmployeesToDelete(), getTeam(), team);
        });
    }

    /**
     * Executes the save team operation using the provided parameters
     * Success shows a message, refreshes the teams and graphs
     * Fails shows error message and closes window
     */
    private void saveTeamOperation(List<Employee> employees, List<Employee> employeesToDelete, Team editedTeam, Team originalTeam) {
        saveTeam = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        model.performEditTeam(employees, employeesToDelete, editedTeam, originalTeam);
                        return null;
                    }
                };
            }
        };
        saveTeam.setOnSucceeded(event -> {
            PauseTransition closeTheSpinner = new PauseTransition(Duration.millis(500));
            closeTheSpinner.setOnFinished((e) -> {
                operationSpinner.setVisible(false);
                spinnerLB.setText("Operation Succesfull");
            });

            PauseTransition pauseTransition = new PauseTransition(Duration.millis(1500));
            pauseTransition.setOnFinished((e) -> {
                WindowsManagement.closeStackPane(firstLayout);
            });
            teamsPageController.clearTeams();
            teamsPageController.displayTeams();
            /* Refresh charts and graphs*/
            teamInfoController.populateCharts(editedTeam);
            pauseTransition.playFromStart();
            closeTheSpinner.playFromStart();

        });
        saveTeam.setOnFailed(event -> {
            saveTeam.getException().printStackTrace();
            showOperationStatus(ErrorCode.OPERATION_DB_FAILED.getValue(), Duration.seconds(5));
            operationSpinner.setVisible(false);
        });
        saveTeam.restart();
    }
    /** Displays a status message for a specified duration */
    private void showOperationStatus(String message, Duration duration) {
        spinnerLB.setText(message);
        PauseTransition delay = new PauseTransition(duration);
        delay.setOnFinished(event -> {
                    spinnerLB.setText("");
                    WindowsManagement.closeStackPane(firstLayout);
                }
        );
        delay.playFromStart();
    }

    private void enableSpinner() {
        spinnerLB.setText("Processing...");
        operationSpinner.setVisible(true);
        operationSpinner.setDisable(false);
    }
    /** Creates a new Team (editedTeam) object using the copy constructor */
    public Team getTeam() {
        String grossMarginString = grossMargin.getText();
        double grossMargin = 0.0; /* Default value if null or empty*/
        if (grossMarginString != null && !grossMarginString.isEmpty()) {
            grossMargin = Double.parseDouble(grossMarginString);
        }

        String markUpString = markUp.getText();
        double markUp = 0.0; /* Default value if null or empty*/
        if (markUpString != null && !markUpString.isEmpty()) {
            markUp = Double.parseDouble(markUpString);
        }
        Team editedTeam = new Team(team);
        editedTeam.setGrossMarginTemporary(grossMargin);
        editedTeam.setMarkupMultiplierTemporary(markUp);
        return editedTeam;
    }

    /** Retrieves a list of employees to be deleted from the team */
    public List<Employee> returnEmployeesToDelete() {
        List<Employee> employeesToDeleteList = new ArrayList<Employee>();
        for (TeamMembersController teamMembersToAdd : teamMembersToAddList) {
            Employee employeesToDelete = teamMembersToAdd.membersToDelete();
            if (employeesToDelete != null) {
                employeesToDeleteList.add(employeesToDelete);
            }
        }

        return employeesToDeleteList;
    }
    /** Retrieves a list of all employees to be included in the team, combining edited employees and team members
     * Uses two lists that hold created controllers in order to know what change has happened in each component created
     * */
    public List<Employee> returnAllEmployees() {
        List<Employee> editedEmployeesList = new ArrayList<Employee>();
        for (EmployeesToAdd employeesToAdd : employeesToAddList) {
            Employee editedEmployee = employeesToAdd.getEditedEmployee(team);
            if (editedEmployee != null) {
                editedEmployeesList.add(editedEmployee);
            }
        }
        List<Employee> editedTeamMembersList = new ArrayList<Employee>();
        for (TeamMembersController teamMembersToAdd : teamMembersToAddList) {
            Employee editedTeamMember = teamMembersToAdd.getEditedTeamMember(team);
            if (editedTeamMember != null) {
                editedTeamMembersList.add(editedTeamMember);
            }

        }
        List<Employee> employeesList = new ArrayList<Employee>();
        employeesList.addAll(editedTeamMembersList);
        employeesList.addAll(editedEmployeesList);
        return employeesList;


    }
    public void populateTextFields() {
        if (team != null && team.getActiveConfiguration() != null) {
            double margin = team.getActiveConfiguration().getGrossMargin();
            grossMargin.setText(String.valueOf(margin));  /* Convert double to String*/
            double markup = team.getActiveConfiguration().getMarkupMultiplier();
            markUp.setText(String.valueOf(markup));  /* Convert double to String*/

        } else {
            grossMargin.setText("0");
            markUp.setText("0");
        }
    }

    /**
     * closes manage popup
     */
    private void addCloseButtonAction() { closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
    {
        WindowsManagement.closeStackPane(firstLayout);
    });
    }


}
