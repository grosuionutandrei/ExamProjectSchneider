package easv.ui.components.searchComponent;

import easv.be.Employee;
import easv.exception.RateException;
import easv.ui.pages.modelFactory.IModel;
import javafx.collections.ObservableList;

public class EmployeeSearchHandler implements DataHandler<Employee> {
    private IModel model;

    public EmployeeSearchHandler(IModel model) {
        this.model = model;
    }

    @Override
    public ObservableList<Employee> getResultData(String filter) {
        return model.getSearchResult(filter);
    }

    @Override
    public void performSelectSearchOperation(int entityId) throws RateException {
        model.performSelectUserSearchOperation(model.getEmployeeById(entityId));
    }

    @Override
    public void undoSearchOperation() throws RateException {
        model.performEmployeeSearchUndoOperation();
    }
}
