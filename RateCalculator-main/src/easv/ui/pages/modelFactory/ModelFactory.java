package easv.ui.pages.modelFactory;
import easv.exception.RateException;

import java.util.HashMap;


public class ModelFactory {
    final private static HashMap<ModelType, IModel> models = new HashMap<>();

    public enum ModelType {
        NORMAL_MODEL,
    }


    public static IModel createModel(ModelType modelType) throws RateException {
        if (models.containsKey(modelType)) {
            return models.get(modelType);
        }
        IModel model = new Model();
        models.put(modelType, model);
        return model;
    }




}