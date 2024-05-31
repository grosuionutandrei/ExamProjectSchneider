package easv.ui.components.map.popUpInfo;

import easv.Utility.WindowsManagement;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.components.map.popUpInfo.teamComponent.TeamComponentController;
import easv.ui.pages.modelFactory.IModel;
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

public class CountryInfoContainer implements Initializable {
    @FXML
    private VBox countryPopUp;
    @FXML
    private HBox closeButton;
    @FXML
    private VBox teamsContainer;
    private IModel model;
    private StackPane parent;
    private Service<List<Team>>teamsInitializer;
    private boolean isOperational;

    public CountryInfoContainer(IModel model, StackPane parent, boolean isOperational) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CountryInfo.fxml"));
        loader.setController(this);
        this.model = model;
        this.parent = parent;
        this.isOperational = isOperational;
        try {
            countryPopUp = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeWindow();
        initializeWindow();
    }

    public VBox getRoot() {
        return countryPopUp;
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

    private void initializeNoOperationalCountry() {
        teamsContainer.getChildren().add(new Label("No operations in this country"));
    }

    /**
     * initialize window different based on the country operational status
     * if the country is operational , retrieve data from the db else not
     */

    private void initializeWindow() {
        if (isOperational) {
            initializeNoOperationalCountry();
            return;
        }
        initializeService();
    }


}
