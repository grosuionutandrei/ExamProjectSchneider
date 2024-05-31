package easv.ui.pages.teamsPage;


import easv.be.Team;
import easv.be.TeamConfiguration;
import easv.be.TeamConfigurationEmployee;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.searchComponent.DataHandler;
import easv.ui.components.searchComponent.SearchController;
import easv.ui.components.teamsInfoComponent.TeamInfoController;
import easv.ui.pages.modelFactory.IModel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TeamsPageController implements Initializable, DataHandler<Team> {
    @FXML
    private Parent teamPage;
    @FXML
    private VBox teamsContainer;
    @FXML
    private LineChart<String, BigDecimal> lineChart;
    @FXML
    private ComboBox<Integer> yearComboBox;
    @FXML
    private ComboBox<TeamConfiguration> teamsHistory;
    @FXML
    private PieChart teamsPieChart;
    private StackPane firstLayout;
    @FXML
    private VBox searchField;
    private TeamInfoController selectedTeam;
    private IModel model;

    /** Initializes the controller with the necessary dependencies and loads the FXML component, not depend on FXML components being loaded*/
    public TeamsPageController(IModel model, StackPane firstLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TeamsManagementPage.fxml"));
        loader.setController(this);
        this.model = model;
        this.firstLayout = firstLayout;
        try {
            teamPage = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }
    public Parent getRoot() {
        return teamPage;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       displayTeams();
       intializeSearchField();
    }
    /** Populates the teamsContainer with the teams from model */
    public void displayTeams() {
        teamsContainer.getChildren().clear();
        ObservableList<HBox> teamInfoControllers = FXCollections.observableArrayList();
        model.getOperationalTeams()
                .forEach(t -> {
                    TeamInfoController teamInfoController = new TeamInfoController(t, model, this, firstLayout);
                    teamInfoControllers.add(teamInfoController
                            .getRoot());
                });
        teamsContainer.getChildren().setAll(teamInfoControllers);
    }

    public void clearTeams() {
        teamsContainer.getChildren().clear();
    }


    /** Adds green border to selected team and removes it after another is selected*/
    public void setSelectedComponentStyleToSelected(TeamInfoController selectedTeam) {
        if (this.selectedTeam != null) {
            this.selectedTeam.getRoot().getStyleClass().remove("teamComponentClicked");
        }
        this.selectedTeam = selectedTeam;
        this.selectedTeam.getRoot().getStyleClass().add("teamComponentClicked");
    }

    /** Populates LineChart based on selected year*/
    public void yearsComboBoxListener(Team team) {
        yearComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                populateChartForYear(team, newValue);

            }
        });
    }

    /**
     * Populates the lineChart with history from a selected year, it includes day rates and months
     * initializes a new series for an XYChart with String as the X-axis type and BigDecimal as the Y-axis type
     * format String into "Jan 01"
     *
     * @param selectedYear is the year that is selected from a combobox
     */
    private void populateChartForYear(Team team, int selectedYear) {
        XYChart.Series<String, BigDecimal> series = new XYChart.Series<>();
        series.setName("Day rate");
        /* Get the configurations for the selected year*/
        List<TeamConfiguration> configurations = team.getTeamConfigurationsHistory().stream()
                .filter(config -> config.getSavedDateWithoutTime().getYear() == selectedYear)
                .sorted(Comparator.comparing(TeamConfiguration::getSavedDateWithoutTime))
                .toList();
        /* Populate the series with sorted data from configurations*/
        for (TeamConfiguration config : configurations) {
            series.getData().add(new XYChart.Data<>(config.getSavedDateWithoutTime().format(DateTimeFormatter.ofPattern("MMM dd")), config.getTeamDayRate()));
        }
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

    /**
     * Populates the ComboBox with years for LineChart,
     * sorts them in descending order
     * sets the latest year as the initial value of the ComboBox
     */
    public void populateComboBoxWithYears(Team team) {
        List<TeamConfiguration> configurations = team.getTeamConfigurationsHistory();
        ObservableList<Integer> yearOptions = FXCollections.observableArrayList();
        if (configurations != null) {
            /*Collect years from configurations*/
            configurations.stream()
                    .map(config -> config.getSavedDateWithoutTime().getYear())
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

    /** Sets a list of team history dates in combobox of pieChart */
    public void setTeamHistoryDatesInComboBox(Team team) {
        List<TeamConfiguration> teamConfigurations = team.getTeamConfigurationsHistory();
        teamConfigurations.sort(Comparator.comparing(TeamConfiguration::getSavedDate).reversed());
        teamsHistory.getItems().clear();
        teamsHistory.getItems().addAll(teamConfigurations);
        /* Set the latest configuration as the default value*/
        if (!teamConfigurations.isEmpty()) {
            teamsHistory.setValue(teamConfigurations.get(0));
        }

    }

    /**
     * sets a list of team history dates in combobox of pieChart
     * adds listener in order to display pieChart info based on selected history configuration
     */
    public void historyComboBoxListener(Team team) {
        teamsHistory.setOnAction(event -> {
            TeamConfiguration selectedConfig = teamsHistory.getValue();
            if (selectedConfig != null) {
                displayEmployeesForDate(team, selectedConfig);
            }
        });
    }

    /**
     * displays pieChart data which is teamMembers of teamHistory configurations
     * gets employee name and rate for each to display in pieChart slice
     * sets team name into pieChart label
     */
    private void displayEmployeesForDate(Team team, TeamConfiguration selectedConfig) {
        teamsPieChart.getData().clear();
        String currency = team.getCurrency().toString();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<TeamConfigurationEmployee> teamMembers = selectedConfig.getTeamMembers();
        System.out.println(teamMembers + "in piechart");
        if (!teamMembers.isEmpty()) {
            for (TeamConfigurationEmployee employee : teamMembers) {
                String label = employee.getEmployeeName() + " " + currency + " ";
                pieChartData.add(new PieChart.Data(label, employee.getEmployeeDailyRate()));
            }
        } else {
            /* Add a default value to indicate no team members*/
            pieChartData.add(new PieChart.Data("No team members", 0));
        }
        /* binds each PieChart.Data object's name property to a concatenated string
         containing the name and day rate, ensuring that both are displayed in the pie chart.*/
        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(data.getName(), " ", data.pieValueProperty())
                )
        );

        teamsPieChart.setData(pieChartData);
        teamsPieChart.setTitle("Team history");
        teamsPieChart.setLabelLineLength(10);
        teamsPieChart.setLegendVisible(false);
        for (PieChart.Data data : pieChartData) {
            data.setPieValue(data.getPieValue());
        }
    }

    /** Adds the team search */
    private void intializeSearchField() {
        SearchController<Team> searchField = new SearchController<>(this);
        this.searchField.getChildren().add(searchField.getSearchRoot());
    }
    @Override
    public ObservableList<Team> getResultData(String filter) {
        return model.getTeamsFilterResults(filter);
    }

    @Override
    public void performSelectSearchOperation(int entityId) throws RateException {
        Team resultedTeam = model.getTeamById(entityId);
        TeamInfoController teamInfoController = new TeamInfoController(resultedTeam, model, this, firstLayout);
        this.teamsContainer.getChildren().clear();
        this.teamsContainer.getChildren().add(teamInfoController.getRoot());
    }

    @Override
    public void undoSearchOperation() throws RateException {
        teamsContainer.getChildren().clear();
        displayTeams();
    }

    public void clearCharts(){
        teamsPieChart.getData().clear();
        lineChart.getData().clear();
        yearComboBox.getItems().clear();
        teamsHistory.getItems().clear();
    }


}
