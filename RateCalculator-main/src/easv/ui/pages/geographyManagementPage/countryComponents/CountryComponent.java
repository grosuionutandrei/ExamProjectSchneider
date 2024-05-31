package easv.ui.pages.geographyManagementPage.countryComponents;

import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Team;
import easv.ui.pages.geographyManagementPage.geographyMainPage.GeographyManagementController;
import easv.ui.pages.modelFactory.IModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Optional;


public class CountryComponent extends HBox implements Initializable {

    @FXML
    private HBox countryInfoComponent;
    @FXML
    private Label nameLB, teamLB, employeeLB;
    @FXML
    private VBox addTeamButton, editButton, deleteContainer;

    private StackPane pane;
    private StackPane secondPane;
    private Country country;
    private IModel model;
    private DeleteCountryController deleteCountryController;
    private GeographyManagementController controller;

    public CountryComponent(IModel model, StackPane pane, Country country, DeleteCountryController deleteCountryController, GeographyManagementController controller, StackPane secondPane) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CountryComponent.fxml"));
        loader.setController(this);
        this.model = model;
        this.country = country;
        this.deleteCountryController = deleteCountryController;
        this.pane = pane;
        this.secondPane = secondPane;
        this.controller = controller;
        try {
            loader.load();
            this.getChildren().add(countryInfoComponent);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLabels();
        setEditButton();
        displayDelete();
    }

    private void displayDelete() {
        deleteContainer.getChildren().clear();
        this.deleteContainer.getChildren().add(deleteCountryController.getRoot());
    }

    private void setLabels() {
        if(country != null) {
            nameLB.setText(country.getCountryName());

            List<Team> validTeams = Optional.ofNullable(country.getTeams())
                    .orElseGet(List::of) // If list of teams is null, returns an empty list
                    .stream()
                    .filter(team -> team != null)
                    .toList();
            String numberOfTeams = "" + validTeams.size();
            teamLB.setText(numberOfTeams);

            int numberOfEmployees = Optional.ofNullable(country.getTeams())
                    .orElseGet(List::of)
                    .stream()
                    .filter(team -> team != null) // Filter out null teams
                    .mapToInt(team -> Optional.ofNullable(team.getEmployees())
                            .map(List::size)
                            .orElse(0)) // If employees is null, return 0
                    .sum();
            employeeLB.setText(numberOfEmployees + "");
        }
    }

    private void setEditButton() {
        this.editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ManageCountryController managecountryController = new ManageCountryController(model, pane, secondPane, country, controller);
            this.pane.getChildren().add(managecountryController.getRoot());
            WindowsManagement.showStackPane(pane);
        });
    }

    public HBox getRoot() {
        return countryInfoComponent;
    }

    public Country getCountry() {
        return country;
    }
}
