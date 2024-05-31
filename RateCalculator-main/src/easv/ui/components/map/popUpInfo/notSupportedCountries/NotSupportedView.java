package easv.ui.components.map.popUpInfo.notSupportedCountries;

import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.map.popUpInfo.teamComponent.TeamComponentController;
import easv.ui.pages.modelFactory.IModel;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NotSupportedView implements Initializable {
   @FXML
    private MFXComboBox<Country> countriesInput;

    @FXML
    private VBox notSupportedPopup;
    @FXML
    private HBox closeButton;
    @FXML
    private VBox teamsContainer;
    private IModel model;
    private StackPane parent;
    private Service<List<Team>> teamsInitializer;


    public NotSupportedView(IModel model, StackPane parent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NotSupportedInfo.fxml"));
        loader.setController(this);
        this.model = model;
        this.parent = parent;
        try {
            notSupportedPopup = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeWindow();
        populateCountriesInput();
        addCountriesInputListener();
    }

    /**populate the countries input with values*/
    private void populateCountriesInput(){
        this.countriesInput.setItems(model.getUnsoportedCountries());
        this.countriesInput.selectItem(countriesInput.getItems().getFirst());
        model.setSelectedCountry(countriesInput.getSelectionModel().getSelectedItem().getCountryName());
        initializeService();
        this.countriesInput.setText("Countries");
    }


    private void addCountriesInputListener(){
        countriesInput.getSelectionModel().selectedItemProperty().addListener((e)->{
            model.setSelectedCountry(countriesInput.getSelectionModel().getSelectedItem().getCountryName());
            initializeService();
        });
    }




    public VBox getRoot() {
        return notSupportedPopup;
    }

    private void closeWindow() {
        closeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            WindowsManagement.closeStackPane(this.parent);
            this.teamsContainer.getChildren().clear();
        });
    }

    public void populatePieChart(List<Team> countryTeams) {
        List<Parent> teamComponentControllers = new ArrayList<>();
        if (countryTeams.isEmpty()) {
            initializeNoDataCountryInfo();
            return;
        }
        for (Team team : countryTeams) {
            TeamComponentController teamComponentController = new TeamComponentController(team);
            teamComponentControllers.add(teamComponentController.getRoot());
        }
        teamsContainer.getChildren().addAll(teamComponentControllers);
    }

    public void initializeService() {
        if (teamsInitializer != null) {
            if (teamsInitializer.isRunning()) {
                teamsInitializer.cancel();
            }
            teamsInitializer.reset();
        }
        teamsInitializer = new Service<>() {
            @Override
            protected Task<List<Team>> createTask() {
                return new Task<>() {
                    @Override
                    protected List<Team> call() throws RateException {
                        return model.getCountryTeams();
                    }
                };
            }
        };

        teamsInitializer.setOnSucceeded(event -> {
            teamsContainer.getChildren().clear();
            populatePieChart(teamsInitializer.getValue());
        });

        teamsInitializer.setOnFailed(event -> {
            ExceptionHandler.errorAlertMessage(ErrorCode.CONNECTION_FAILED.getValue());
        });
        teamsInitializer.start();
    }


    /**
     * display a message if no data is in the system related to the country teams overhead
     */
    private void initializeNoDataCountryInfo() {
        teamsContainer.getChildren().add(new Label("No data present for this country"));
    }


}
