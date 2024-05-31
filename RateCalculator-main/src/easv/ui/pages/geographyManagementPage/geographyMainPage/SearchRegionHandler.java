package easv.ui.pages.geographyManagementPage.geographyMainPage;

import easv.be.Region;
import easv.exception.RateException;
import easv.ui.components.searchComponent.DataHandler;
import javafx.collections.ObservableList;

public class SearchRegionHandler implements DataHandler<Region> {
  GeographyInterface geographyInterface;


    public SearchRegionHandler(GeographyInterface geographyInterface) {
     this.geographyInterface=geographyInterface;
    }

    @Override
    public ObservableList<Region> getResultData(String filter) {
        return geographyInterface.getRegionsForSearch(filter);
    }

    @Override
    public void performSelectSearchOperation(int entityId) throws RateException {
       geographyInterface.performSelectSearchOperationRegion(entityId);
    }

    @Override
    public void undoSearchOperation() throws RateException {
       geographyInterface.undoSearchOperationRegion();
    }
}
