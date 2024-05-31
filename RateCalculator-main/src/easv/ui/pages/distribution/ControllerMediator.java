package easv.ui.pages.distribution;

import easv.be.Team;
import easv.ui.components.distributionPage.distributeFromTeamInfo.DistributionFromComponentInterface;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToController;
import easv.ui.components.distributionPage.distributeToTeamInfo.DistributeToInterface;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerMediator {

    private DistributionControllerInterface distributionController;


    // distribute  from  team component, that was selected by the user
    private DistributionFromComponentInterface distributionFromTeamController;


    // selected team chosen to distribute from
    private DistributionFromComponentInterface selectedTeamToDistributeFromController;


    // holds all the controllers that was selected to distribute to by the user
    private final Map<Integer, DistributeToInterface> distributeToControllers;

    public ControllerMediator() {
        distributeToControllers = new HashMap<>();
    }

    public void registerDistributionController(DistributionController distributionController) {
        this.distributionController = distributionController;
    }

    public void setTheSelectedComponentToDistributeFrom(DistributionFromComponentInterface selectedTeamComponent) {
        if (this.distributionFromTeamController != null) {
            this.distributionFromTeamController.setTheStyleClassToDefault();
        }
        this.distributionFromTeamController = selectedTeamComponent;
    }


    public void addTeamToDistributeFrom(Team team) {
        distributionController.addTeamToDistributeFrom(team);
    }


    public void addSelectedTeamToDistributeFromController(DistributionFromComponentInterface selectedTeamToDistributeFromController){
        this.selectedTeamToDistributeFromController=selectedTeamToDistributeFromController;
    }

    /**
     * add the selected team to distribute to
     */
    public void addDistributeToTeam(Team teamToDisplay) {
        distributionController.addDistributeToTeam(teamToDisplay);
    }

    /**
     * save added controllers in order to change their style
     */
    public void addDistributeToController(DistributeToInterface selectedTeamToDistributeTo, int teamId) {
        distributeToControllers.put(teamId, selectedTeamToDistributeTo);
    }

    /**
     * clear the stored controllers when the operation is saved
     */
    public void clearSavedControllers() {
        this.distributeToControllers.clear();
    }


    /**
     * change the component style when the user entered invalid values
     */
    public void changeComponentStyleToError(Integer teamId) {
        DistributeToInterface distributeToController = distributeToControllers.get(teamId);
        if (distributeToController != null) {
            distributeToController.changeStyleToError();
        }
    }

//TODO if   used modify to use the mediator

    /**
     * update the  selected components to distribute overhead, after the save operation was performed
     */
    public void updateComponentOverheadValues(int teamId, double dayOverhead, double hourOverhead) {
        DistributeToInterface distributeToController = distributeToControllers.get(teamId);
        if (distributeToController != null) {
            distributeToController.setDayRate(dayOverhead + "");
            distributeToController.setHourlyRate(hourOverhead + "");
        }
    }

    /**
     * update the distribute from component with the distribution computation
     */

    public void updateDistributeFromComponent(double dayOverhead, double hourOverhead) {
        this.selectedTeamToDistributeFromController.setDayRate(dayOverhead + "");
        this.selectedTeamToDistributeFromController.setHourlyRate(hourOverhead + "");
    }


    //update the value of the total overhead, based on the inserted percerntage values;
    public void updateTotalOverheadValue() {
        distributionController.updateTotalOverheadValue();
    }


    /**
     * remove the team from the view
     */
    public boolean removeTeamFromDistributionView(int id) {
        List<Parent> removedTeamControllers = new ArrayList<>();
        for (Integer teamId : distributeToControllers.keySet()) {
            if (teamId != id) {
                removedTeamControllers.add(distributeToControllers.get(teamId).getRoot());
            }
        }
        boolean removedFromView = distributionController.removeTeamFromDistributionView(removedTeamControllers);
        if (removedFromView) {
            this.distributeToControllers.remove(id);
            if(this.distributeToControllers.isEmpty()){
                selectedTeamToDistributeFromController.setBackToOriginal();
            }
            return true;
        } else {
            return false;
        }
    }
}
