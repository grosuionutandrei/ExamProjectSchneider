package easv.ui.components.distributionPage.distributeToTeamInfo;

import javafx.scene.Parent;

public interface DistributeToInterface {
    
    void changeStyleToError();

    void setDayRate(String s);

    void setHourlyRate(String s);

    Parent getRoot();
}
