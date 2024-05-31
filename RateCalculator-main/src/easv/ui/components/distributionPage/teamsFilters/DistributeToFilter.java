package easv.ui.components.distributionPage.teamsFilters;

import easv.be.Region;
import  easv.be.Country;
import easv.ui.components.common.regionFilter.FilterHandler;
import easv.ui.pages.distribution.DistributionControllerInterface;
import easv.ui.pages.modelFactory.IModel;
import javafx.collections.ObservableList;

public class DistributeToFilter implements FilterHandler<Region,Country> {

    private IModel model;
    private DistributionControllerInterface distributionControllerInterface;


    public DistributeToFilter(IModel model, DistributionControllerInterface distributionControllerInterface) {
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

    @Override
    public void displaySelectedCountryTeams(Country country) {
        Country filteredCountry = model.getCountryById(country.getId());
        distributionControllerInterface.displayDistributeToTeamsInContainer(filteredCountry.getTeams());
    }

    @Override
    public void displaySelectedRegionTeams(Region region) {
        distributionControllerInterface.displayDistributeToTeamsInContainer(model.getRegionTeams(region));
    }

    @Override
    public void displayAllTeamsInTheSystem() {
       distributionControllerInterface.undoSearchOperationTo();
    }
}
