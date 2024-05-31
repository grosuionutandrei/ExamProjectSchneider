package easv.ui.components.teamsManagementTeamMembers;

import easv.Utility.TeamValidation;
import easv.be.*;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.teamManagement.TeamManagementController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class TeamMembersController implements Initializable {
    @FXML
    private HBox membersComponent;
    @FXML
    private Label teamMemberName, utilizationInTeam, memberType;
    @FXML
    private MFXCheckbox removeTeamMember;
    @FXML
    private MFXTextField utilPercentageToAdd;
    private IModel model;
    private TeamManagementController teamManagementController;
    private Employee employee;
    private Team team;


    /** Initializes the controller with the necessary dependencies and loads the FXML component, not depend on FXML components being loaded*/
    public TeamMembersController(Employee employee, Team team, IModel model, TeamManagementController teamManagementController) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamMembersComponent.fxml"));
        loader.setController(this);
        this.model = model;
        this.employee = employee;
        this.team = team;
        this.teamManagementController = teamManagementController;
        try {
            membersComponent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }
    public HBox getRoot() {
        return membersComponent;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
        utilListener();
    }
    /** Checks if users typed value is valid, if it is not then makes the border red */
    private void utilListener() {
        utilPercentageToAdd.textProperty().addListener((observable, oldValue, newValue) -> {
            TeamValidation.validateUtilization(utilizationInTeam, utilPercentageToAdd, employee);

        });
    }
    /** Returns all team members and sets their new util if it was changed*/
    public Employee getEditedTeamMember(Team team) {
        if (!removeTeamMember.isSelected()) {
            if (TeamValidation.validateUtilization(utilizationInTeam, utilPercentageToAdd, employee)) {
                Employee editedEmployee = employee;
                String utilPercentageStr = utilPercentageToAdd.getText().trim();

                if (!utilPercentageStr.isEmpty()) {
                    BigDecimal utilPercentage = new BigDecimal(utilPercentageStr);
                    editedEmployee.getUtilPerTeams().put(team.getId(), utilPercentage);
                }
                return editedEmployee;
            }
        }
        return null;
    }

    /** Returns all team members who were selected to be deleted*/
    public Employee membersToDelete() {
        if (removeTeamMember.isSelected()) {
            Employee employeeToDelete = employee;
            return employeeToDelete;
        }
        return null;
    }
    public void setLabels() {
        if (employee != null) {
            teamMemberName.setText(employee.getName());
            teamMemberName.setTooltip(new Tooltip(teamMemberName.getText()));
            memberType.setText(employee.getEmployeeType().toString());
            memberType.setTooltip(new Tooltip(memberType.getText()));

            BigDecimal utilization = employee.getUtilPerTeams().get(team.getId());
            if (utilization != null) {
                utilizationInTeam.setText(utilization.toString() + "%");
            } else {
                utilizationInTeam.setText("N/A");
            }
            BigDecimal remainingUtilization = TeamValidation.calculateRemainingUtilization(employee.getUtilPerTeams());
            utilizationInTeam.setTooltip(new Tooltip(remainingUtilization.toString() + "%"));

        }
    }




}
