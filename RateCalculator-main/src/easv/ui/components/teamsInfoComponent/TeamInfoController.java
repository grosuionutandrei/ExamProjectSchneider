package easv.ui.components.teamsInfoComponent;
import easv.Utility.TeamValidation;
import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import easv.be.TeamConfiguration;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.editPage.EditController;
import easv.ui.components.teamManagement.TeamManagementController;
import easv.ui.components.teamManagementEmployeesAdd.EmployeesToAdd;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.teamsPage.TeamsPageController;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class TeamInfoController implements Initializable {
    @FXML
    private HBox teamInfoComponent;
    @FXML
    private VBox editButton;
    @FXML
    private Label teamName, teamRegion, teamCountry, teamDailyRate, teamHourlyRate, teamDayCurrency, teamHourlyCurrency;
    private IModel model;
    private Team team;
    private TeamsPageController teamsPageController;
    private EmployeesToAdd employeesToAdd;
    private StackPane firstLayout;

    /** Initializes the controller with the necessary dependencies and loads the FXML component, not depend on FXML components being loaded*/
    public TeamInfoController(Team team , IModel model, TeamsPageController teamsPageController, StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamInfoComponent.fxml"));
        loader.setController(this);
        this.team = team;
        this.model = model;
        this.teamsPageController = teamsPageController;
        this.firstLayout=firstLayout;
        try {
            teamInfoComponent = loader.load();
        } catch (IOException e) {
             ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }
    public HBox getRoot() {
        return teamInfoComponent;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
        Platform.runLater(this::addClickListener);
        addEditAction();
        addClickListener();
    }

    /** When component is clicked adds and removes styling, populates charts with team info and prevents edit button from triggering charts*/
    private void addClickListener(){
        teamInfoComponent.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            if(event.getTarget()==editButton){
                return;
            }
            teamInfoComponent.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"),false);
            teamsPageController.setSelectedComponentStyleToSelected(this);
            populateCharts();
        });
    }
    public void populateCharts(){
        teamsPageController.yearsComboBoxListener(team);
            teamsPageController.populateComboBoxWithYears(team);
            teamsPageController.historyComboBoxListener(team);
            teamsPageController.setTeamHistoryDatesInComboBox(team);

    }
    /** Method overloading, used for refreshing charts after edit  operation*
     * Param team uses edited team*/
    public void populateCharts(Team team){
        teamsPageController.setTeamHistoryDatesInComboBox(team);
            teamsPageController.yearsComboBoxListener(team);
            teamsPageController.populateComboBoxWithYears(team);
            teamsPageController.historyComboBoxListener(team);

    }
    /** Opens edit view for the selected team*/
    private void addEditAction() {
        editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            TeamManagementController teamManagementController = new TeamManagementController(team, model, firstLayout, this, employeesToAdd, teamsPageController);
            firstLayout.getChildren().add(teamManagementController.getRoot());
            WindowsManagement.showStackPane(firstLayout);
            //stop the event to bubble up
            event.consume();
        });
    }

    /**
     * Updates the UI labels with the team's details if the team is not null
     * This includes setting the team's name, currency, daily and hourly rates, countries, and regions,
     * along with tooltips
     */
    public void setLabels() {
        if (team != null) {
            teamName.setText(team.getTeamName());
            teamName.setTooltip(new Tooltip(teamName.getText()));
            teamDayCurrency.setText(team.getCurrency().toString());
            teamHourlyCurrency.setText(team.getCurrency().toString());

            TeamConfiguration activeConfiguration = team.getActiveConfiguration();
            if (activeConfiguration != null && !team.getTeamMembers().isEmpty()) {
                teamDailyRate.setText(activeConfiguration.getTeamDayRate().setScale(2, RoundingMode.HALF_UP).toString());
                teamHourlyRate.setText(activeConfiguration.getTeamHourlyRate().setScale(2, RoundingMode.HALF_UP).toString());
            } else {
                teamDailyRate.setText("N/A");
                teamHourlyRate.setText("N/A");
            }

            if (team.getCountries() != null && !team.getCountries().isEmpty()) {
                StringBuilder countryNames = new StringBuilder();
                Iterator<Country> countryIterator = team.getCountries().iterator();
                while (countryIterator.hasNext()) {
                    Country country = countryIterator.next();
                    countryNames.append(country.getCountryName());
                    if (countryIterator.hasNext()) {
                        countryNames.append(", ");
                    }
                }
                teamCountry.setText(countryNames.toString());
                teamCountry.setTooltip(new Tooltip(teamCountry.getText()));
            } else {
                teamCountry.setText("N/A");
                teamCountry.setTooltip(new Tooltip("N/A"));
            }

            if (team.getRegions() != null && !team.getRegions().isEmpty()) {
                StringBuilder regionNames = new StringBuilder();
                Iterator<Region> regionIterator = team.getRegions().iterator();
                while (regionIterator.hasNext()) {
                    Region region = regionIterator.next();
                    regionNames.append(region.getRegionName());
                    if (regionIterator.hasNext()) {
                        regionNames.append(", ");
                    }
                }
                teamRegion.setText(regionNames.toString());
                teamRegion.setTooltip(new Tooltip(teamRegion.getText()));
            } else {
                teamRegion.setText("N/A");
                teamRegion.setTooltip(new Tooltip("N/A"));
            }


        }
    }


}
