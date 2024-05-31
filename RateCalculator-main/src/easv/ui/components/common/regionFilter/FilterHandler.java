package easv.ui.components.common.regionFilter;

import javafx.collections.ObservableList;

public interface FilterHandler<T,V>{


    /**get regions value for the region filter*/
 ObservableList<T> getRegionsData();


 /**get countries values for the countries filter*/
 ObservableList<V> getCountryData();


 /**perform the region filter selection */
 ObservableList<V> filterCountriesByRegion(T region);


 /**perform country filter operation*/
 void displaySelectedCountryTeams(V country);

 /**display selected region teams*/
 void displaySelectedRegionTeams(T region);

 /**display all teams in the system when the undo button is pressed*/
 void displayAllTeamsInTheSystem();

}
