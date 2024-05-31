package easv.ui.components.searchComponent;
import easv.exception.RateException;
import javafx.collections.ObservableList;

public interface DataHandler<T> {
    ObservableList<T> getResultData(String filter);
    void performSelectSearchOperation(int entityId) throws RateException;

    void undoSearchOperation() throws RateException;
}
