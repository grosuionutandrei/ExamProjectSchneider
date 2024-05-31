package easv.ui.components.common;

import easv.ui.components.homePage.NavigationFactory.NavigationFactory;
import easv.ui.components.homePage.openPageObserver.Subject;
import javafx.scene.Parent;

public interface PageManager {
    void changePage(Parent root,Subject subject);
}
