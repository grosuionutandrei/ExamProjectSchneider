package easv.ui.pages.geographyManagementPage.geographyMainPage;

import easv.be.Country;
import easv.be.Region;
import easv.exception.RateException;
import easv.ui.components.searchComponent.DataHandler;
import javafx.collections.ObservableList;

public class SearchCountryHandler implements DataHandler<Country> {

    GeographyInterface countryInterface;

    public SearchCountryHandler(GeographyInterface geographyInterface) {
        this.countryInterface = geographyInterface;
    }

    @Override
    public ObservableList<Country> getResultData(String filter) {
        return countryInterface.getCountriesForSearch(filter);
    }

    @Override
    public void performSelectSearchOperation(int entityId) throws RateException {
        countryInterface.performSelectSearchOperationCountry(entityId);
    }

    @Override
    public void undoSearchOperation() throws RateException {
        countryInterface.undoSearchOperationCountry();
    }
}
