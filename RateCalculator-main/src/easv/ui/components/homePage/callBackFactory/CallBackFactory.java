package easv.ui.components.homePage.callBackFactory;
import easv.be.Navigation;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.controllerFactory.PageControllerFactory;
import easv.ui.components.homePage.openPageObserver.Observable;
import easv.ui.components.homePage.openPageObserver.Observer;
import java.util.HashMap;

/**
 * The CallBackFactory class is responsible for creating and managing callbacks for different navigation actions within
 * the application. These callbacks are used by NavigationController instances to handle the navigation to various pages.
 * This factory ensures that callbacks are created, cached, and appropriately linked to the navigation actions.
 */

public class CallBackFactory {
    private static final HashMap<Navigation, CallBack> callBacks = new HashMap<>();
    private final static Observable observer = new Observer();
    public static CallBack createCallBack(Navigation pageTo, PageControllerFactory pageControllerFactory,PageManager pageManager) {
        return () -> getCallBack(pageTo,pageControllerFactory,pageManager).call();
    }

    private static CallBack getCallBack(Navigation pageTo, PageControllerFactory pageControllerFactory,PageManager  pageManager) {
        if (callBacks.containsKey(pageTo)) {
            return callBacks.get(pageTo);
        }
        NavigationOperation  navigationOperation = new NavigationOperation(pageManager,pageControllerFactory);
        callBacks.put(pageTo,navigationOperation);
        observer.addSubject(navigationOperation);
        return navigationOperation;
    }

    public static Observable getObserver() {
        return observer;
    }
}
