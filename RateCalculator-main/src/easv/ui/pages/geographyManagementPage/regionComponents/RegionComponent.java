package easv.ui.pages.geographyManagementPage.regionComponents;

import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.be.Region;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class RegionComponent extends HBox implements Initializable {

    @FXML
    private HBox regionInfoComponent;
    @FXML
    private Label nameLB, countryLB, teamLB;
    @FXML
    private VBox editButton, deleteContainer;

    private StackPane pane;
    private StackPane secondPane;
    private Region region;
    private IModel model;
    private DeleteRegionController deleteRegionController;
    private GeographyManagementController controller;

    public RegionComponent(IModel model, StackPane pane, Region region, DeleteRegionController deleteRegionController, GeographyManagementController controller) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegionComponent.fxml"));
        loader.setController(this);
        this.model = model;
        this.region = region;
        this.deleteRegionController = deleteRegionController;
        this.pane = pane;
        this.controller = controller;
        try {
            loader.load();
            this.getChildren().add(regionInfoComponent);
        } catch (IOException e) {
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
        this.deleteContainer.getChildren().add(deleteRegionController.getRoot());
    }

    private void setLabels() {
        if(region != null) {
            nameLB.setText(region.getRegionName());

            List<Country> validCountries = Optional.ofNullable(region.getCountries())
                    .orElseGet(List::of) // If list of teams is null, returns an empty list
                    .stream()
                    .filter(country -> country != null)
                    .toList();
            String numberOfCountries = "" + validCountries.size();
            countryLB.setText(numberOfCountries);

            int numberOfTeams = Optional.ofNullable(region.getCountries()) // If list of countries is null, returns an empty list
                    .orElseGet(List::of)
                    .stream()
                    .filter(country -> country != null)
                    .mapToInt(country -> Optional.ofNullable(country.getTeams())
                            .map(List::size)
                            .orElse(0))
                    .sum();
            teamLB.setText(numberOfTeams + "");
        }
    }

    private void setEditButton() {
        this.editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ManageRegionController manageRegionController = new ManageRegionController(model, pane, secondPane, region, controller);
            this.pane.getChildren().add(manageRegionController.getRoot());
            WindowsManagement.showStackPane(pane);
        });
    }

    public HBox getRoot() {
        return regionInfoComponent;
    }

    public Region getRegion() {
        return region;
    }
}
