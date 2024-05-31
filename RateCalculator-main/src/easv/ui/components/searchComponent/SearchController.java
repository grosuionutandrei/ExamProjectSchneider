package easv.ui.components.searchComponent;

import easv.be.Country;
import easv.be.Employee;
import easv.be.Region;
import easv.be.Team;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchController<T> implements Initializable {
    private DataHandler<T> dataHandler;
    @FXML
    private HBox button;

    @FXML
    private ListView<T> searchResponseHolder;
    @FXML
    private SVGPath searchSVG;
    @FXML
    private PopupControl popupWindow;

    @FXML
    private HBox searchWindowContainer;
    @FXML
    private TextField searchField;


    public SearchController(DataHandler<T> dataHandler) {
        this.dataHandler = dataHandler;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchView.fxml"));
        loader.setController(this);

        try {
            searchWindowContainer = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }

    }

    public HBox getSearchRoot() {
        return searchWindowContainer;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadSearchSVG();
        createPopUpWindow();
        searchFieldListener();
        try {
            addSelectionListener();
        } catch (RateException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.SEARCH_FAILED.getValue());
        }
        goBack();
        addFocusListener(searchField,button);
    }

    private void createPopUpWindow() {
        popupWindow = new PopupControl();
        searchResponseHolder = new ListView<>();
        popupWindow.getScene().setRoot(searchResponseHolder);

    }
    /**
     * Configures and displays a popup window below the search field with specified dimensions and styles
     */
    private void configurePopUpWindow() {
        Bounds boundsInScreen = searchField.localToScreen(searchField.getBoundsInLocal());
        searchResponseHolder.setPrefWidth(searchField.getWidth());
        searchResponseHolder.setMaxWidth(searchField.getWidth());
        searchResponseHolder.setMaxHeight(250);
        popupWindow.getScene().getStylesheets().add("/easv/ui/styling/EmployeePage.css");
        ((Parent) popupWindow.getScene().getRoot()).getStyleClass().add("popupView");
        searchResponseHolder.getStylesheets().add("/easv/ui/styling/EmployeePage.css");
        popupWindow.setPrefWidth(searchField.getWidth());
        popupWindow.setMaxWidth(searchField.getWidth());
        popupWindow.setMaxHeight(250);
        popupWindow.show(searchField, boundsInScreen.getMinX(), boundsInScreen.getMaxY());
    }
    /**
     * Adds listeners to the search field to handle text input and focus changes
     * This method performs the following steps:
     * 1. Adds a listener to the search field's text property:
     *    - If the text is not empty, updates the search response holder with results and shows the popup window.
     *    - If the text is empty, clears the search field, hides the popup window, and loads the revert SVG.
     * 2. Adds a listener to the search field's focus property:
     *    - Hides the popup window when the search field loses focus.
     */
    private void searchFieldListener() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                searchResponseHolder.setItems(dataHandler.getResultData(newValue));
                if (!searchResponseHolder.getItems().isEmpty()) {
                    configurePopUpWindow();
                } else {
                    popupWindow.hide();
                }
            } else {
                loadRevertSVG();
                searchField.clear();
                popupWindow.hide();
            }

        });
        searchField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                popupWindow.hide();
            }
        });
    }
    /**
     * Adds a selection listener to the search response holder to handle selection of search results.
     * Adds a listener to the search response holder's selected item property
     */
    private void addSelectionListener() throws RateException  {
        searchResponseHolder.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    if (newValue instanceof Employee) {
                        dataHandler.performSelectSearchOperation(((Employee) newValue).getId());
                    }
                    if(newValue instanceof Team){
                        dataHandler.performSelectSearchOperation(((Team) newValue).getId());
                    }
                    if(newValue instanceof Region){
                        dataHandler.performSelectSearchOperation(((Region) newValue).getId());
                    }
                    if(newValue instanceof Country){
                        dataHandler.performSelectSearchOperation(((Country) newValue).getId());
                    }

                } catch (RateException e) {
                    ExceptionHandler.errorAlertMessage(ErrorCode.INVALID_INPUT.getValue());
                }

                Platform.runLater(() -> {
                    if (!searchResponseHolder.getItems().isEmpty()) {
                        searchResponseHolder.getSelectionModel().clearSelection();
                    }
                });
                loadRevertSVG();
                popupWindow.hide();
            }
        });
    }
    /**
     * Adds a mouse click event handler to a button to perform the undo search operation
     */
    private void goBack() {

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, event->{
            try {
                dataHandler.undoSearchOperation();
                searchField.clear();
                Platform.runLater(this::loadSearchSVG);
            } catch (RateException e) {
                ExceptionHandler.errorAlertMessage(ErrorCode.UNDO_FAILED.getValue());
            }
        });
    }



    private void loadSearchSVG() {
        String defaultSVGPath = "M269.46,1163.45 C263.17,1163.45 258.071,1158.44 258.071,1152.25 C258.071,1146.06 263.17,1141.04 269.46,1141.04 C275.75,1141.04 280.85,1146.06 280.85,1152.25 C280.85,1158.44 275.75,1163.45 269.46,1163.45 L269.46,1163.45 Z M287.688,1169.25 L279.429,1161.12 C281.591,1158.77 282.92,1155.67 282.92,1152.25 C282.92,1144.93 276.894,1139 269.46,1139 C262.026,1139 256,1144.93 256,1152.25 C256,1159.56 262.026,1165.49 269.46,1165.49 C272.672,1165.49 275.618,1164.38 277.932,1162.53 L286.224,1170.69 C286.629,1171.09 287.284,1171.09 287.688,1170.69 C288.093,1170.3 288.093,1169.65 287.688,1169.25 L287.688,1169.25 Z";
        searchSVG.getStyleClass().clear();
        searchSVG.getStyleClass().add("searchIcon");
        searchSVG.setContent(defaultSVGPath);
    }

    // Method to load the second SVG path
    private void loadRevertSVG() {
        String alternativeSVGPath = "M 15 3 L 10 7 L 15 11 L 15 8 C 18.877838 8 22 11.12216 22 15 C 22 18.87784 18.877838 22 15 22 C 11.122162 22 8 18.87784 8 15 C 8 13.485854 8.4798822 12.090114 9.2910156 10.947266 L 7.8730469 9.5292969 C 6.7042423 11.047902 6 12.942076 6 15 C 6 19.95872 10.041282 24 15 24 C 19.958718 24 24 19.95872 24 15 C 24 10.04128 19.958718 6 15 6 L 15 3 z ";
        searchSVG.getStyleClass().clear();
        searchSVG.getStyleClass().add("revertIcon");
        searchSVG.setContent(alternativeSVGPath);
    }


    //change the style of the search button to match the search text field
    private void addFocusListener(TextField filterInput, HBox sibling) {
        filterInput.focusWithinProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                sibling.getStyleClass().add("searchFocused");
            } else {
                sibling.getStyleClass().remove("searchFocused");
            }
        });
    }
}
