package easv.ui.pages.geographyManagementPage.geographyMainPage;

import easv.be.Country;
import easv.be.Region;
import easv.be.Team;
import javafx.collections.ObservableList;

import java.util.List;

public interface GeographyInterface {
    //REGION RELATED LOGIC
    /**get regions from the selection filter*/
    ObservableList<Region> getRegionsForSearch(String filter);

    /**undo search operation for region*/
    void undoSearchOperationRegion();

    /**show the selected team from the region search */
    void performSelectSearchOperationRegion(int entityId);


    //COUNTRY RELATED LOGIC

    /**get the countries from the search operation*/
    ObservableList<Country> getCountriesForSearch(String filter);

    /**show the selected country  from search field*/
    void performSelectSearchOperationCountry(int entityId);

    /**undo search operation for region*/
    void undoSearchOperationCountry();

    /**show the selected country from the search results */
    void performSelectSearchOperationTo(int entityId);




}
