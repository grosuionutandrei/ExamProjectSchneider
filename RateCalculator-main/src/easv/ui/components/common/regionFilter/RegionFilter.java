package easv.ui.components.common.regionFilter;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RegionFilter<T, V> implements Initializable {

    @FXML
    private HBox filtersContainer;
    @FXML
    private MFXComboBox<T> regionsFilter;
    @FXML
    private MFXComboBox<V> countryFilter;
    @FXML
    private SVGPath regionRevertSvg, countryRevertSvg;

    @FXML
    private HBox regionRevertButton, countryRevertButton;
    private List<T> regions;

    private List<V> countries;
    private FilterHandler<T, V> filterHandler;

    private boolean regionFilterActive;




    public RegionFilter(FilterHandler<T, V> filterHandler) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegionFilter.fxml"));
        loader.setController(this);
        this.filterHandler = filterHandler;

        try {
            filtersContainer=loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //populate regions
        populateRegionsFilter();
        //populate countries
        populateCountries();
        //add region filter listener
        addRegionFilterListener();
        // add country filter listener
        addCountryFilterListener();
        // add undo region filter listener
        addUndoRegionButtonListener();
        // add undo country listener
        addUndoCountryButtonListener();
        // change the style off the revert button to look the same as comboboxes
         addFocusListener(regionsFilter,regionRevertButton);
         addFocusListener(countryFilter,countryRevertButton);
    }



    /**
     * populate regions
     */
    private void populateRegionsFilter() {
        this.regionsFilter.setItems(filterHandler.getRegionsData());
    }

    /**
     * populate countries
     */

    private void populateCountries() {
        this.countryFilter.setItems(filterHandler.getCountryData());
    }

    /**
     * initialize region filter listener
     */
    private void addRegionFilterListener() {
        this.regionsFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.countryFilter.clearSelection();
                this.countryFilter.setItems(filterHandler.filterCountriesByRegion(newValue));
                this.countryFilter.selectItem(countryFilter.getItems().getFirst());
                this.filterHandler.displaySelectedRegionTeams(newValue);
                this.regionFilterActive = true;
                showUndoButton(regionRevertButton,regionRevertSvg);
            }
        });
    }

    /**
     * initialize country filter listener
     */
    private void addCountryFilterListener() {
        this.countryFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                {
                    if (newValue != null) {
                        this.filterHandler.displaySelectedCountryTeams(newValue);
                    showUndoButton(countryRevertButton,countryRevertSvg);
                    }

                }
        );
    }

    /**
     * add region filter undo  event handler
     */
    private void addUndoRegionButtonListener() {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        this.regionRevertButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            pauseTransition.setOnFinished((e) -> {
                this.countryFilter.setItems(filterHandler.getCountryData());
                this.filterHandler.displayAllTeamsInTheSystem();
                this.regionFilterActive = false;
                this.countryFilter.clearSelection();
                this.regionsFilter.clearSelection();
                hideUndoButton(regionRevertButton, regionRevertSvg);
                hideUndoButton(countryRevertButton, countryRevertSvg);
            });
            pauseTransition.playFromStart();
        });
    }

    /**
     * add country undo button  event handler
     */
    private void addUndoCountryButtonListener() {
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(500));
        this.countryRevertButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            pauseTransition.setOnFinished((e) -> {
                if (regionFilterActive) {
                    this.filterHandler.displaySelectedRegionTeams(regionsFilter.getSelectionModel().getSelectedItem());
                    hideUndoButton(countryRevertButton, countryRevertSvg);
                } else {
                    this.countryFilter.clearSelection();
                    this.filterHandler.displayAllTeamsInTheSystem();
                    hideUndoButton(countryRevertButton, countryRevertSvg);
                }
            });
            pauseTransition.playFromStart();
        });
    }


    /**
     * hide  undo button
     */
    private void hideUndoButton(HBox button, SVGPath logo) {
        logo.setVisible(false);
        button.setDisable(true);
    }

    /**
     * show undo buttons
     */

    private void showUndoButton(HBox button, SVGPath logo) {
        logo.setVisible(true);
        button.setDisable(false);
    }


    private void addFocusListener(MFXTextField filterInput, HBox sibling) {
        filterInput.focusWithinProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                sibling.getStyleClass().add("countryFilterFocused");
            } else {
                sibling.getStyleClass().remove("countryFilterFocused");
            }
        });
    }
    public HBox getFilterRoot() {
        return filtersContainer;
    }

}
