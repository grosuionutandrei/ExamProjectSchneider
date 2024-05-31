package easv.ui.components.homePage.openPageObserver;

import java.util.ArrayList;
import java.util.List;


/**it is observing changes in the home page navigation, in order to notify the subjects if they need to render a new view
 *provides methods for subjects to subscribe or unsubscribe , and a method to notify them about the changes.*/
public class Observer implements Observable {
    private List<Subject> subjects;

    public Observer() {
    subjects= new ArrayList<>();
    }

    public void addSubject(Subject subject){
        subjects.add(subject);
    }

    public void removeSubject(Subject subject){
        subjects.remove(subject);
    }

    //notify the necessary subject about the changes in the program
    public void modifyDisplay(Subject subject){
        for(Subject item : subjects ){
            item.modifyDisplay(item.equals(subject));
        }
    }


}
