import easv.exception.ErrorCode;
import easv.exception.ExceptionHandler;
import easv.exception.RateException;
import easv.ui.pages.homePage.HomePageController;
import easv.ui.pages.modelFactory.IModel;
import easv.ui.pages.modelFactory.ModelFactory;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String[] args) {
        Application.launch();

    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        IModel model = initializeModel(primaryStage);
        HomePageController homePageController = new HomePageController(model);
        Scene scene = new Scene(homePageController.getRoot());
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(700);
        primaryStage.setTitle("Overhead manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private static IModel initializeModel(Stage primaryStage) {
        IModel model = null;
        try{
            model =  ModelFactory.createModel(ModelFactory.ModelType.NORMAL_MODEL);
        }catch (RateException e){
            ExceptionHandler.errorAlertMessage(ErrorCode.LOADING_FXML_FAILED.getValue());
            primaryStage.close();
        }
        return model;
    }
}