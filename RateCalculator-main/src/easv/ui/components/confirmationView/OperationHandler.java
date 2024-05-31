package easv.ui.components.confirmationView;

import easv.exception.RateException;

public interface OperationHandler {
    /** Used to perform operations in confirmation popup*/
    void performOperation() throws RateException;
}
