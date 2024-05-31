package easv.ui.components.distributionPage.teamsFilters;

import easv.be.Country;
import easv.be.Region;
import easv.ui.components.common.regionFilter.FilterHandler;
import easv.ui.pages.distribution.DistributionControllerInterface;
import easv.ui.pages.modelFactory.IModel;
import javafx.collections.ObservableList;

public class DistributeFromFilter implements FilterHandler<Region, Country> {


    private IModel model;
    private DistributionControllerInterface distributionControllerInterface;


    public DistributeFromFilter(IModel model, DistributionControllerInterface distributionControllerInterface) {
        this.model = model;
        this.distributionControllerInterface = distributionControllerInterface;
    }


    @Override
    public ObservableList<Region> getRegionsData() {
        return model.getOperationalRegions();
    }

    @Override
    public ObservableList<Country> getCountryData() {
        return model.getOperationalCountries();
    }

    @Override
    public ObservableList<Country> filterCountriesByRegion(Region region) {
        return model.getRegionCountries(region);
    }


    /**
     * display the teams resulted from the country filter operation
     */
    @Override
    public void displaySelectedCountryTeams(Country country) {
        Country filteredCountry = model.getCountryById(country.getId());
        distributionControllerInterface.displayDistributeFromTeamsInContainer(filteredCountry.getTeams());
    }

    @Override
    public void displaySelectedRegionTeams(Region region) {
        distributionControllerInterface.displayDistributeFromTeamsInContainer(model.getRegionTeams(region));
    }

    @Override
    public void displayAllTeamsInTheSystem() {
     distributionControllerInterface.undoSearchOperationFrom();
    }
}
