package easv.ui.components.distributionPage.distributeFromTeamInfo;

import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Currency;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.common.errorWindow.ErrorWindowController;
import easv.ui.pages.distribution.ControllerMediator;
import easv.ui.pages.distribution.DistributionType;
import easv.ui.pages.modelFactory.IModel;
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

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DistributeFromController implements Initializable, DistributionFromComponentInterface {
    @FXML
    private HBox teamComponent;
    @FXML
    private Label teamRegions;
    @FXML
    private Label teamCountries;
    @FXML
    private Label teamName;
    @FXML
    private Label dayRate, dayCurrency, hourlyRate, hourlyCurrency;
    private IModel model;
    private Team teamToDisplay;
    private  ControllerMediator controllerMediator;
    private  DistributionType distributionType;
    private static final String EMPTY_VALUE = "";
    private  StackPane modalLayout;

    public DistributeFromController(IModel model, Team teamToDisplay, ControllerMediator distributionControllerMediator, DistributionType distributionType, StackPane modalLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DistributeFromTeamInfo.fxml"));
        loader.setController(this);
        this.model = model;
        this.teamToDisplay = new Team(teamToDisplay);
        this.controllerMediator = distributionControllerMediator;
        this.distributionType = distributionType;
        this.modalLayout = modalLayout;
        try {
            teamComponent = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateComponentWithValues();
        Platform.runLater(this::addClickListener);
    }



    /**
     * pupulate the component with the team values
     */
    public void populateComponentWithValues() {
        String regions = teamToDisplay.getRegions().stream()
                .map(Region::getRegionName)
                .collect(Collectors.joining(", "));
        this.teamRegions.setText(regions);
        /*add tooltip for the regions to display the hole value*/
        addInfoToolTip(this.teamRegions);
        String countries = teamToDisplay.getCountries().stream()
                .map(Country::getCountryName)
                .collect(Collectors.joining(","));
        this.teamCountries.setText(countries);
        /*add tooltip for the countries to display the whole value*/
        addInfoToolTip(this.teamCountries);
        this.teamName.setText(teamToDisplay.getTeamName());
        addInfoToolTip(teamName);
        if (this.teamToDisplay.getActiveConfiguration() != null) {
            this.dayRate.setText(teamToDisplay.getActiveConfiguration().getTeamDayRate().setScale(2, RoundingMode.HALF_UP) + "");
            addInfoToolTip(this.dayRate);
            dayCurrency.setText(Currency.USD.toString());
            this.hourlyRate.setText(teamToDisplay.getActiveConfiguration().getTeamHourlyRate().setScale(2, RoundingMode.HALF_UP) + "");
            addInfoToolTip(this.hourlyRate);
            this.hourlyCurrency.setText(Currency.USD.toString());
        }

    }

    public HBox getRoot() {
        return teamComponent;
    }

    private void addInfoToolTip(Label label) {
        Tooltip toolTip = new Tooltip();
        toolTip.getStyleClass().add("tooltipinfo");
        toolTip.textProperty().bind(label.textProperty());
        label.setTooltip(toolTip);
    }

    private void addClickListener() {
        this.teamComponent.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (this.distributionType.equals(DistributionType.DISTRIBUTE_FROM)) {
                if (teamToDisplay.getEmployees() == null || teamToDisplay.getEmployees().isEmpty()) {
                    showInfoError(ErrorCode.NO_EMPLOYEES.getValue());
                    return;
                }

                if (model.getInsertedDistributionPercentageFromTeams() != null) {
                    if (model.isTeamSelectedToDistribute(teamToDisplay.getId())) {
                        showInfoError(ErrorCode.DISTRIBUTE_TO.getValue() + "\n" + teamToDisplay.getTeamName());
                        return;
                    }
                } else {
                    if (model.isTeamSelectedToDistribute(teamToDisplay.getId())) {
                        showInfoError(ErrorCode.DISTRIBUTE_TO.getValue() + "\n" + teamToDisplay.getTeamName());
                        return;
                    }
                }

                if (model.getSelectedTeamToDistributeFrom() != null) {
                    if (model.getSelectedTeamToDistributeFrom().getId() == teamToDisplay.getId()) {
                        showInfoError(ErrorCode.DISTRIBUTE_TO.getValue() + "\n" + teamToDisplay.getTeamName());
                        return;
                    }
                }

                //save a copy of the team into the model, in order to perform simulations without affecting the teams original values
                model.setDistributeFromTeam(new Team(teamToDisplay));
                controllerMediator.addTeamToDistributeFrom(teamToDisplay);
                this.teamComponent.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
                this.controllerMediator.setTheSelectedComponentToDistributeFrom(this);
                this.teamComponent.getStyleClass().add("teamComponentClicked");
                return;
            }

            if (this.distributionType.equals(DistributionType.DISTRIBUTE_TO)) {
                if (teamToDisplay.getEmployees() == null || teamToDisplay.getEmployees().isEmpty()) {
                    showInfoError(ErrorCode.NO_EMPLOYEES.getValue());
                    return;
                }

                if (model.getSelectedTeamToDistributeFrom() != null) {
                    if (model.getSelectedTeamToDistributeFrom().getId() == teamToDisplay.getId()) {
                        showInfoError(ErrorCode.DISTRIBUTE_FROM.getValue());
                        return;
                    }
                }

                /*when the team to distribute  is selected from the list will be added
                  in the model insertedDistributionPercentageFromTeams without overhead percentage */
                if (model.getInsertedDistributionPercentageFromTeams() != null) {
                    if (!model.isTeamSelectedToDistribute(teamToDisplay.getId())) {
                        model.addDistributionPercentageTeam(new Team(teamToDisplay), EMPTY_VALUE);
                        controllerMediator.addDistributeToTeam(new Team(teamToDisplay));
                    } else {
                        showInfoError(ErrorCode.DISTRIBUTE_TO.getValue());
                    }
                } else {
                    model.addDistributionPercentageTeam(new Team(teamToDisplay), EMPTY_VALUE);
                    controllerMediator.addDistributeToTeam(new Team(teamToDisplay));
                }

            }

        });
    }

    @Override
    public void setTheStyleClassToDefault() {
        this.teamComponent.getStyleClass().remove("teamComponentClicked");
    }

    public void setTeamToDisplay(Team team) {
        this.teamToDisplay = team;
    }


    /**
     * display an information  message for the user when the input is invalid
     */
    private void showInfoError(String errorValue) {
        ErrorWindowController errorWindowController = new ErrorWindowController(modalLayout, errorValue);
        modalLayout.getChildren().add(errorWindowController.getRoot());
        WindowsManagement.showStackPane(modalLayout);
    }

    @Override
    public void setDayRate(String value) {
        this.dayRate.setText(value);
    }

    @Override
    public void setHourlyRate(String value) {
        this.hourlyRate.setText(value);
    }

    //display the original value if no distribute to teams are selected
    public void setBackToOriginal(){
        this.dayRate.setText(teamToDisplay.getActiveConfiguration().getTeamDayRate()+"");
        this.hourlyRate.setText(teamToDisplay.getActiveConfiguration().getTeamHourlyRate()+"");
    }




}
