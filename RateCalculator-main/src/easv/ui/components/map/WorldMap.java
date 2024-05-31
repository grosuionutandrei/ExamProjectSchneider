package easv.ui.components.map;
import easv.Utility.WindowsManagement;
import easv.be.Country;
import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.ui.components.map.popUpInfo.CountryInfoContainer;
import easv.ui.components.map.popUpInfo.notSupportedCountries.NotSupportedView;
import easv.ui.pages.modelFactory.IModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.WorldMapView;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class WorldMap implements Initializable {
    @FXML
    private StackPane mapContainer;
    @FXML
    private VBox unsuportedCountries;
    @FXML
    private WorldMapView worldMap;
    private StackPane firstLayout;
    private IModel model;
    private CountryInfoContainer countryInfoContainer;

    public WorldMap(StackPane firstLayout, IModel model) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewMap.fxml"));
        loader.setController(this);
        this.model = model;
        this.firstLayout = firstLayout;
        try {
            mapContainer = loader.load();
        } catch (IOException e) {
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
        }
    }

    public StackPane getRoot() {
        return mapContainer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addCountryClickHandler();
        changeColor(model.getCountries().values());
        Platform.runLater(() -> {
            List<String> countries = new ArrayList<>();
            for (WorldMapView.Country c : WorldMapView.Country.values()) {
                countries.add(c.getLocale().getDisplayCountry());
            }
            model.populateValidCountries(countries);
        });
        addUnsuportedCountriesHandler();
    }


    private void addCountryClickHandler() {
        worldMap.setOnMouseClicked(event -> {
            if (!worldMap.getSelectedCountries().isEmpty()) {
                WindowsManagement.showStackPane(firstLayout);
                String countrySelected = worldMap.getSelectedCountries().get(0).getLocale().getDisplayCountry();
                model.setSelectedCountry(countrySelected);
                if(model.getCountries().get(countrySelected)!=null){
                    countryInfoContainer = new CountryInfoContainer(model, firstLayout,false);
                    firstLayout.getChildren().add(countryInfoContainer.getRoot());
                }else{
                    countryInfoContainer = new CountryInfoContainer(model, firstLayout,true);
                    firstLayout.getChildren().add(countryInfoContainer.getRoot());
                }
            }
        });
    }

    private  void addUnsuportedCountriesHandler(){
        this.unsuportedCountries.addEventHandler(MouseEvent.MOUSE_CLICKED,event -> {
            NotSupportedView notSupportedView = new NotSupportedView(model,firstLayout);
            firstLayout.getChildren().add(notSupportedView.getRoot());
            WindowsManagement.showStackPane(firstLayout);
        });
    }


    /**
     * change the color of the countries that are operational
     */
    private void changeColor(Collection<Country> countries) {
        worldMap.setCountryViewFactory(param -> {
            WorldMapView.CountryView countryView = new WorldMapView.CountryView(param);
            boolean isOperational = countries.stream().anyMatch(e -> e.getCountryName().equals(param.getLocale().getDisplayCountry()));
            if (isOperational) {
                countryView.getStyleClass().add("country_operational");
            }
            return countryView;
        });
    }




}




