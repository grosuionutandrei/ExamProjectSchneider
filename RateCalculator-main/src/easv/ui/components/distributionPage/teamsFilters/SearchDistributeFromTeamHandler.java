package easv.ui.components.distributionPage.teamsFilters;

import easv.be.Team;
import easv.exception.RateException;
import easv.ui.components.searchComponent.DataHandler;
import easv.ui.pages.distribution.DistributionController;
import easv.ui.pages.distribution.DistributionControllerInterface;
import javafx.collections.ObservableList;

public class SearchDistributeFromTeamHandler implements DataHandler<Team> {
    private DistributionControllerInterface dataHandler;


    public SearchDistributeFromTeamHandler(DistributionController dataHandler) {
        this.dataHandler = dataHandler;
    }

    @Override
    public ObservableList<Team> getResultData(String filter) {
        return dataHandler.getResultData(filter);
    }

    @Override
    public void performSelectSearchOperation(int entityId) throws RateException {
          dataHandler.performSelectSearchOperationFrom(entityId);
    }

    @Override
    public void undoSearchOperation() throws RateException {
        dataHandler.undoSearchOperationFrom();
    }
}
