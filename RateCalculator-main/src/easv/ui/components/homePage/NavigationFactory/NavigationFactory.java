package easv.ui.components.homePage.NavigationFactory;
import easv.be.Navigation;
import easv.ui.components.common.PageManager;
import easv.ui.components.homePage.callBackFactory.CallBackFactory;
import easv.ui.components.homePage.controllerFactory.*;
import easv.ui.components.homePage.navigation.HomePageNavigationController;
import easv.ui.components.homePage.navigation.NavigationController;
import easv.ui.pages.modelFactory.IModel;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import java.util.HashMap;

import static javafx.scene.shape.FillRule.EVEN_ODD;

/**
 * The NavigationFactory class is responsible for creating and configuring navigation components for the home page.
 * It dynamically generates navigation items for different pages, each populated with the appropriate SVG icon,
 * text, and functionality.
 */

public class NavigationFactory {
    private final static HashMap<Navigation, SVGPath> icons = new HashMap<>();
    private final static HashMap<Navigation, NavigationController> navigationControllers = new HashMap<>();
    private static StackPane modalLayout, secondLayout;
    private static IModel model;
    private static PageManager pageManager;


    public static void setModalWindow(StackPane stackPane) {
        modalLayout = stackPane;
    }

    public static void setSecondLayout(StackPane stackPane) {
        secondLayout = stackPane;
    }

    public static void setModel(IModel iModel) {
        model = iModel;
    }

    ;

    public static HBox getNavigationComponent(Navigation navigation) {
        return createAndConfigureNavigation(navigation);
    }

    private static HBox createAndConfigureNavigation(Navigation navigation) {
        NavigationController navigationController = navigationControllers.get(navigation);
        if (navigationController != null) {
            return navigationController.getNavComponent();
        }

        switch (navigation) {
            case CREATE ->
                    navigationController = new NavigationController(icons.get(navigation), CallBackFactory.createCallBack(Navigation.CREATE, new CreateControllerFactory(model, modalLayout), pageManager), IconsText.CREATE.value);

            case DISTRIBUTION ->
                    navigationController = new NavigationController(icons.get(navigation), CallBackFactory.createCallBack(Navigation.DISTRIBUTION, new CreateDistributionFactory(model, modalLayout), pageManager), IconsText.DISTRIBUTION.value);

            case EMPLOYEES ->
                    navigationController = new NavigationController(icons.get(navigation), CallBackFactory.createCallBack(Navigation.EMPLOYEES, new EmployeePageControllerFactory(model, modalLayout), pageManager), IconsText.EMPLOYEES.value);

            case MODELING ->
                    navigationController = new NavigationController(icons.get(navigation), CallBackFactory.createCallBack(Navigation.MODELING, new TeamPageControllerFactory(model, modalLayout), pageManager), IconsText.MODELING.value);
            case GEOGRAPHY ->
                    navigationController = new NavigationController(icons.get(navigation), CallBackFactory.createCallBack(Navigation.GEOGRAPHY, new GeographyControllerFactory(model, modalLayout, secondLayout), pageManager), IconsText.GEOGRAPHY.value);

        }
        navigationControllers.put(navigation, navigationController);
        return navigationController.getNavComponent();
    }

    public static ImageView getHomePageNavigation() {
        HomePageNavigationController homePageNavigationController = new HomePageNavigationController(CallBackFactory.createCallBack(Navigation.HOME, new CreateMapFactory(model, modalLayout), pageManager));
        return homePageNavigationController.getHomePageLogo();
    }

    public static void initializeHomePage() {
        CallBackFactory.createCallBack(Navigation.HOME, new CreateMapFactory(model, modalLayout), pageManager).call();
    }


    static {
        SVGPath distribution = new SVGPath();
        distribution.setContent("M11 20C11 15.8313 14.1886 12.4073 18.2599 12.0338L18.74 13.68C19.4867 16.24 21.8334 18 24.5 18H39.5C42.1667 18 44.5134 16.24 45.26 13.68L45.7402 12.0338C49.8115 12.4073 53 15.8313 53 20V32.5C53 33.6046 53.8954 34.5 55 34.5C56.1046 34.5 57 33.6046 57 32.5V20C57 13.939 52.5065 8.92747 46.6691 8.11515C46.7384 4.86198 44.1199 2 40.6667 2H23.3334C19.8801 2 17.2616 4.86197 17.3309 8.11515C11.4935 8.92745 7 13.939 7 20V50C7 56.6274 12.3726 62 19 62H37C38.1046 62 39 61.1046 39 60C39 58.8954 38.1046 58 37 58H19C14.5817 58 11 54.4183 11 50V20ZM23.3334 6C22 6 21.04 7.28 21.4134 8.56L22.58 12.56C22.8289 13.4133 23.6111 14 24.5 14H39.5C40.3889 14 41.1711 13.4133 41.42 12.56L42.5867 8.56C42.96 7.28 42 6 40.6667 6H23.3334ZM20 26C18.8954 26 18 26.8954 18 28C18 29.1046 18.8954 30 20 30H44C45.1046 30 46 29.1046 46 28C46 26.8954 45.1046 26 44 26H20ZM20 36C18.8954 36 18 36.8954 18 38C18 39.1046 18.8954 40 20 40H31.5C32.6046 40 33.5 39.1046 33.5 38C33.5 36.8954 32.6046 36 31.5 36H20ZM20 46C18.8954 46 18 46.8954 18 48C18 49.1046 18.8954 50 20 50H31.5C32.6046 50 33.5 49.1046 33.5 48C33.5 46.8954 32.6046 46 31.5 46H20ZM51 38C51 36.8954 50.1046 36 49 36C47.8954 36 47 36.8954 47 38V38.5968C43.7075 38.7658 41 41.4239 41 44.7941C41 48.2774 43.8922 51 47.3333 51H50.6667C52.012 51 53 52.0435 53 53.2059C53 54.3683 52.012 55.4118 50.6667 55.4118H43C41.8954 55.4118 41 56.3072 41 57.4118C41 58.5163 41.8954 59.4118 43 59.4118H47V60C47 61.1046 47.8954 62 49 62C50.1046 62 51 61.1046 51 60V59.4032C54.2925 59.2342 57 56.5761 57 53.2059C57 49.7226 54.1078 47 50.6667 47H47.3333C45.988 47 45 45.9565 45 44.7941C45 43.6317 45.988 42.5882 47.3333 42.5882H55C56.1046 42.5882 57 41.6928 57 40.5882C57 39.4837 56.1046 38.5882 55 38.5882H51V38Z");
        distribution.setFillRule(EVEN_ODD);
        distribution.setScaleX(0.5);
        distribution.setScaleY(0.5);
        icons.put(Navigation.DISTRIBUTION, distribution);
        //add the create svg
        SVGPath create = new SVGPath();
        create.setContent("M4,29c0.55,0,1-0.45,1-1c0-3.86,3.14-7,7-7h9c0.55,0,1-0.45,1-1s-0.45-1-1-1h-9c-4.96,0-9,4.04-9,9C3,28.55,3.45,29,4,29zM16,17c3.86,0,7-3.14,7-7s-3.14-7-7-7s-7,3.14-7,7S12.14,17,16,17z M16,5c2.76,0,5,2.24,5,5s-2.24,5-5,5s-5-2.24-5-5  S13.24,5,16,5z M28,24h-2v-2c0-0.55-0.45-1-1-1s-1,0.45-1,1v2h-2c-0.55,0-1,0.45-1,1s0.45,1,1,1h2v2c0,0.55,0.45,1,1,1s1-0.45,1-1v-2h2  c0.55,0,1-0.45,1-1S28.55,24,28,24z");
        create.setFillRule(EVEN_ODD);
        icons.put(Navigation.CREATE, create);
        // add all employees svg to the map
        SVGPath employees = new SVGPath();
        employees.setContent("M 40.5,14.5 C 60.9814,12.8142 70.1481,22.1475 68,42.5C 66.6043,46.7924 64.1043,50.2924 60.5,53C 71.8752,58.5746 77.5419,67.7413 77.5,80.5C 75.1667,80.5 72.8333,80.5 70.5,80.5C 70.2572,69.9238 65.2572,62.5905 55.5,58.5C 53.0163,60.483 50.6829,62.6496 48.5,65C 50.4591,69.71 52.1258,74.5433 53.5,79.5C 50.6614,87.5361 46.828,88.2028 42,81.5C 42.4609,75.7743 43.9609,70.2743 46.5,65C 44.3171,62.6496 41.9837,60.483 39.5,58.5C 29.7428,62.5905 24.7428,69.9238 24.5,80.5C 22.1667,80.5 19.8333,80.5 17.5,80.5C 17.4581,67.7413 23.1248,58.5746 34.5,53C 25.6263,44.0024 24.1263,33.8357 30,22.5C 32.94,18.9632 36.44,16.2965 40.5,14.5 Z M 42.5,21.5 C 59.2861,21.0726 64.7861,28.7392 59,44.5C 52.8025,50.1041 45.9692,50.9374 38.5,47C 32.8959,40.8025 32.0626,33.9692 36,26.5C 37.9651,24.4497 40.1318,22.783 42.5,21.5 ZM 19.5,23.5 C 20.8333,23.5 22.1667,23.5 23.5,23.5C 23.8076,25.924 23.4742,28.2573 22.5,30.5C 15.6443,33.634 13.811,38.634 17,45.5C 18.4133,47.4136 20.2466,48.7469 22.5,49.5C 23.8333,52.1667 23.8333,54.8333 22.5,57.5C 14.0597,60.0549 9.72635,65.7216 9.5,74.5C 7.16667,74.5 4.83333,74.5 2.5,74.5C 2.42017,65.0745 6.42017,57.9079 14.5,53C 12.094,51.378 10.2606,49.2114 9,46.5C 6.26862,35.8216 9.76862,28.155 19.5,23.5 ZM 71.5,23.5 C 84.1387,26.0118 88.9721,33.6785 86,46.5C 84.7394,49.2114 82.906,51.378 80.5,53C 88.5798,57.9079 92.5798,65.0745 92.5,74.5C 90.1667,74.5 87.8333,74.5 85.5,74.5C 85.2737,65.7216 80.9403,60.0549 72.5,57.5C 71.1667,54.8333 71.1667,52.1667 72.5,49.5C 79.3557,46.366 81.189,41.366 78,34.5C 76.5867,32.5864 74.7534,31.2531 72.5,30.5C 71.5258,28.2573 71.1924,25.924 71.5,23.5 Z");
        employees.setFillRule(EVEN_ODD);
        employees.setScaleX(0.3);
        employees.setScaleY(0.3);
        icons.put(Navigation.EMPLOYEES, employees);
        // add modeling svg
        SVGPath modeling = new SVGPath();
        modeling.setContent("M224,151L224,177C224,177.552 224.448,178 225,178L251,178C251.552,178 252,177.552 252,177C252,176.448 251.552,176 251,176L226,176C226,176 226,151 226,151C226,150.448 225.552,150 225,150C224.448,150 224,150.448 224,151ZM246.586,155L244.293,157.293C244.14,157.446 244.041,157.644 244.01,157.859L243.16,163.812C243.16,163.812 239.196,163.019 239.196,163.019C238.782,162.937 238.36,163.123 238.143,163.486L235.657,167.628C235.657,167.628 231.514,165.143 231.514,165.143C231.26,164.99 230.951,164.958 230.67,165.056C230.39,165.154 230.168,165.371 230.064,165.649L227.064,173.649C226.87,174.166 227.132,174.743 227.649,174.936C228.166,175.13 228.743,174.868 228.936,174.351L231.515,167.475C231.515,167.475 235.486,169.857 235.486,169.857C235.959,170.142 236.573,169.988 236.857,169.514L239.495,165.119C239.495,165.119 243.804,165.981 243.804,165.981C244.073,166.034 244.353,165.975 244.578,165.816C244.802,165.658 244.951,165.414 244.99,165.141L245.943,158.471L248,156.414L248,158C248,158.552 248.448,159 249,159C249.552,159 250,158.552 250,158L250,154C250,153.829 249.957,153.669 249.882,153.528C249.846,153.461 249.801,153.396 249.748,153.337L249.73,153.316L249.716,153.302L249.698,153.284L249.679,153.266L249.663,153.252C249.604,153.199 249.539,153.154 249.472,153.118C249.331,153.043 249.171,153 249,153L245,153C244.448,153 244,153.448 244,154C244,154.552 244.448,155 245,155L246.586,155Z");
        modeling.setFillRule(EVEN_ODD);
        icons.put(Navigation.MODELING, modeling);

        //add geography icon
        SVGPath geography = new SVGPath();
        geography.setContent("M427.968,349.536c-5.3-5.7-14.2-6.2-20-1c-35.2,31.6-80.5,49-127.7,49c-105.4,0-191.2-85.8-191.2-191.2\n" +
                "\t\t\tc0-79.1,49.8-151,124-179c7.3-2.7,11-10.9,8.2-18.2c-2.7-7.3-10.9-11-18.2-8.2c-85.1,32.1-142.2,114.6-142.2,205.4\n" +
                "\t\t\tc0,116.2,90.9,211.6,205.3,218.9v54.5h-69c-7.8,0-14.1,6.3-14.1,14.1s6.3,14.1,14.1,14.1h166.3c7.8,0,14.1-6.3,14.1-14.1\n" +
                "\t\t\tc0-7.8-6.3-14.1-14.1-14.1h-69v-54.6c49-3.2,95.6-22.6,132.4-55.7C432.668,364.236,433.168,355.336,427.968,349.536z\n" +
                "M280.268,39.536c-92,0-166.8,74.8-166.8,166.8s74.8,166.8,166.8,166.8s166.8-74.8,166.8-166.8\n" +
                "\t\t\tS372.268,39.536,280.268,39.536z M223.368,80.136c-5.8,9.7-10.9,20.9-15.2,33.4h-30.5\n" +
                "\t\t\tC190.468,99.436,205.968,88.036,223.368,80.136z M157.768,141.736h42.7c-3.3,15.7-5.5,32.7-6.3,50.5h-51.8\n" +
                "\t\t\tC144.268,174.136,149.668,157.136,157.768,141.736z M157.768,271.036c-8.1-15.4-13.5-32.4-15.4-50.5h51.8\n" +
                "\t\t\tc0.7,17.9,2.9,34.8,6.2,50.5H157.768z M177.768,299.236h30.5c4.2,12.4,9.3,23.6,15,33.3\n" +
                "\t\t\tC205.868,324.636,190.368,313.236,177.768,299.236z M222.468,220.536h43.6v50.5h-36.8\n" +
                "\t\t\tC225.768,255.936,223.268,238.936,222.468,220.536z M266.168,340.036c-10.4-6.9-20.3-21.1-28.1-40.8h28.1V340.036z\n" +
                "\t\t\t M266.168,192.236h-43.6c0.8-18.5,3.2-35.4,6.8-50.5h36.8V192.236z M266.168,113.436h-28.2c7.9-19.7,17.8-33.8,28.2-40.8V113.436z\n" +
                "\t\t\t M402.768,141.736c8.1,15.4,13.5,32.4,15.4,50.5h-51.8c-0.7-17.9-2.9-34.8-6.3-50.5H402.768z M382.868,113.536h-30.5\n" +
                "\t\t\tc-4.3-12.5-9.4-23.6-15.2-33.4C354.668,88.036,370.168,99.436,382.868,113.536z M294.368,72.636c10.4,6.9,20.4,21.1,28.2,40.9\n" +
                "\t\t\th-28.2V72.636z M294.368,141.736L294.368,141.736h36.9c3.5,15.1,6,32.1,6.8,50.5h-43.7V141.736z M294.368,340.036v-40.8h28.2\n" +
                "\t\t\tC314.768,319.036,304.768,333.136,294.368,340.036z M294.368,270.936v-50.5h43.7c-0.8,18.5-3.2,35.4-6.8,50.5H294.368z\n" +
                "\t\t\t M337.268,332.536c5.8-9.7,10.8-20.9,15-33.3h30.5C370.168,313.236,354.668,324.636,337.268,332.536z M402.768,271.036h-42.7\n" +
                "\t\t\tc3.3-15.7,5.5-32.7,6.2-50.5h51.8C416.268,238.536,410.868,255.636,402.768,271.036z");
        geography.setScaleX(0.07);
        geography.setScaleY(0.07);
        icons.put(Navigation.GEOGRAPHY, geography);
    }

    public static void setPageHolder(PageManager pageHolder) {
        pageManager = pageHolder;
    }

    private enum IconsText {
        DISTRIBUTION("Distribution"), CREATE("Create"), EMPLOYEES("Employees"), MODELING("Teams"), GEOGRAPHY("Geography");
        private final String value;

        IconsText(String value) {
            this.value = value;
        }
    }

}
