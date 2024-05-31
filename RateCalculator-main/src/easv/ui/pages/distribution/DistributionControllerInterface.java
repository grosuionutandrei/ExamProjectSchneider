package easv.ui.pages.distribution;

import easv.be.Team;
import easv.exception.RateException;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributeFromController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;

import java.util.List;

public interface DistributionControllerInterface {

    void updateTotalOverheadValue();

    boolean removeTeamFromDistributionView(List<Parent> teamsAfterRemoveOperation);

    /**add team to distribute from*/
    void addTeamToDistributeFrom(Team team);

    /**add distribute to team in the distribute to list */
    void addDistributeToTeam(Team teamToDisplay);



    /**display all the teams in the distribute from container*/
     void displayDistributeFromTeamsInContainer(List<Team>  teams);


     /**display all the teams in the distribute to list */
     void displayDistributeToTeamsInContainer(List<Team> teams);


     /**undo search operation from distribute from search field*/
    void undoSearchOperationFrom();

    /**undo search operation from the distribute to field*/
     void undoSearchOperationTo();

    /**show the selected team from the distribute from search field */
     void performSelectSearchOperationFrom(int entityId);

    /**show the selected team  from the distribute  to field */
  void performSelectSearchOperationTo(int entityId);

    /**get the results from the search operation*/
    ObservableList<Team> getResultData(String filter);

}
