package easv.ui.components.homePage.openPageObserver;

public interface Observable {
    void modifyDisplay(Subject subject);
    void addSubject(Subject subject);
    void removeSubject(Subject subject);
}
