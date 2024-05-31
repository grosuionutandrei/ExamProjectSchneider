package easv.ui.components.distributionPage.distributeToTeamInfo;

import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Currency;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.common.errorWindow.ErrorWindowController;
import easv.ui.pages.distribution.ControllerMediator;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DistributeToController implements Initializable,DistributeToInterface {
    @FXML
    private  HBox teamComponentDistributeFrom;
    @FXML
    private Label teamRegions;
    @FXML
    private Label teamCountries;
    @FXML
    private Label teamName;
    @FXML
    private Label dayRate, dayCurrency, hourlyRate, hourlyCurrency;
    @FXML
    private MFXTextField distributionPercentage;

    @FXML
    private VBox removeTeam;
    private IModel model;
    private Team teamToDisplay;
    private ControllerMediator distributionMediator;
    private StackPane modalLayout;
    private final static PseudoClass INVALID_INPUT = PseudoClass.getPseudoClass("inputError");

    public DistributeToController(IModel model, Team teamToDisplay, ControllerMediator distributionMediator,StackPane modalLayout) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DistributeToTeamInfo.fxml"));
        loader.setController(this);
        this.model = model;
        this.teamToDisplay = teamToDisplay;
        this.distributionMediator = distributionMediator;
        this.modalLayout = modalLayout;
        try {
            teamComponentDistributeFrom = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        populateComponentWithValues();
        //retrieve the value off the overhead input
        addInputPercentageListener();
        //add  remove team from overhead listener
        removeTeamFromDistribution();
    }

    /**
     * pupulate the component with the team values
     */
    private void populateComponentWithValues() {
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
            this.hourlyRate.setText(teamToDisplay.getActiveConfiguration().getTeamHourlyRate().setScale(2,RoundingMode.HALF_UP) + "");
            addInfoToolTip(this.hourlyRate);
            this.hourlyCurrency.setText(Currency.USD.toString());
        }
    }


    public HBox getRoot() {
        return teamComponentDistributeFrom;
    }
    private void addInfoToolTip(Label label) {
        Tooltip toolTip = new Tooltip();
        toolTip.getStyleClass().add("tooltipinfo");
        toolTip.setText(label.getText());
        label.setTooltip(toolTip);
    }



    public String getOverheadPercentage() {
        return this.distributionPercentage.getText();
    }


    private void addInputPercentageListener() {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        this.distributionPercentage.textProperty().addListener((observable, oldValue, newValue) -> {
            pauseTransition.setOnFinished((interupt) -> {
                if (!newValue.isEmpty()) {

                    // Validate the new value format
                    if (!newValue.matches("^\\d{0,3}([.,]\\d{1,2})?$")) {
                        showInfoError(ErrorCode.INVALID_OVERHEADVALUE.getValue() + "\n" + teamToDisplay.getTeamName() + "\n" + ErrorCode.INVALID_OVERHEAD_MESSAGE.getValue());
                        teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, true);
                        model.setDistributionPercentageTeam(teamToDisplay, newValue);
                        distributionMediator.updateTotalOverheadValue();
                    } else {
                        // Convert new value to a double
                        Double overheadInserted = validatePercentageValue(newValue);
                        if (!(overheadInserted > 0 && overheadInserted <= 100)) {
                            teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, true);
                            model.setDistributionPercentageTeam(teamToDisplay, newValue);
                            distributionMediator.updateTotalOverheadValue();
                            showInfoError(ErrorCode.INVALID_OVERHEADVALUE.getValue() + "\n" + teamToDisplay.getTeamName() + "\n" + ErrorCode.INVALID_OVERHEAD_MESSAGE.getValue());

                        }else{
                            model.setDistributionPercentageTeam(teamToDisplay, newValue);
                            distributionMediator.updateTotalOverheadValue();
                            teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, false);
                        }

                    }
                } else {
                    // Handle the case where the input is empty
                    teamComponentDistributeFrom.pseudoClassStateChanged(INVALID_INPUT, false);
                    model.setDistributionPercentageTeam(teamToDisplay, newValue);
                    distributionMediator.updateTotalOverheadValue();
                }
                    });
            pauseTransition.playFromStart();
        });
    }




    private  String convertToDecimalPoint(String value) {
        String validFormat = "";
        if(value== null){
            return validFormat;
        }
        if (value.contains(",")) {
            validFormat = value.replace(",", ".");
        } else {
            validFormat = value;
        }
        return validFormat;
    }

    /**convert string to double , if the input is invalid than the value returned will be null;*/
    private Double validatePercentageValue(String newValue){
        String decimalPoint = convertToDecimalPoint(newValue);
        Double overheadValue= null;
        try{
              overheadValue =  Double.parseDouble(decimalPoint);
        }catch(NumberFormatException e){
            return overheadValue;
        }
        return overheadValue;
    }




    public void changeStyleToError() {
        this.getRoot().pseudoClassStateChanged(INVALID_INPUT,true);
    }

    public void setDayRate(String value) {
        this.dayRate.setText(value);
        this.dayRate.getTooltip().setText(value);
    }

    public void setHourlyRate(String value) {
        this.hourlyRate.setText(value);
        this.hourlyRate.getTooltip().setText(value);
    }

    /**add remove team from distribution simulation table listener */
    private void removeTeamFromDistribution(){
        this.removeTeam.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
                 boolean teamRemoved = distributionMediator.removeTeamFromDistributionView(teamToDisplay.getId());
                 if(teamRemoved){
                     model.removeDistributionPercentageTeam(teamToDisplay);
                    distributionMediator.updateTotalOverheadValue();
                 }
        });}


    /**display an information  message for the user when the input is invalid*/
    private void showInfoError(String errorValue){
        ErrorWindowController errorWindowController = new ErrorWindowController(modalLayout,errorValue);
        modalLayout.getChildren().add(errorWindowController.getRoot());
        WindowsManagement.showStackPane(modalLayout);
    }




}
