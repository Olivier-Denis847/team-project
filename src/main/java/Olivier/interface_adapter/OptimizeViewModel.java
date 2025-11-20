package Olivier.interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class OptimizeViewModel {
    private final String viewName;
    private OptimizeState state;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public OptimizeViewModel() {
        this.viewName = "optimize expenses";
        OptimizeState s = new OptimizeState(null);
        s.setLabels(new String[]{"A", "B", "C", "D", "E", "F", "G", "H"});
        setState(s);
    }

    public String getViewName() {return viewName;}

    public void setState(OptimizeState state) {
        OptimizeState oldState = this.state;
        this.state = state;
        support.firePropertyChange("state", oldState, state);
    }

    public OptimizeState getState() {return this.state;}

    public void updateTime(int newTime) {
        OptimizeState newState = new OptimizeState(this.state);
        newState.setTime(newTime);
        setState(newState);
    }

    public void updatePriorities(String[] newPriorities) {
        OptimizeState newState = new OptimizeState(this.state);
        newState.setPriorities(newPriorities);
        setState(newState);
    }

    /**
     * Fires a property changed event
     * @param propertyName the name of the property that has changed
     */
    public void firePropertyChange(String propertyName) {
        this.support.firePropertyChange(propertyName, null, this.state);
    }

    /**
     * Adds a PropertyChangeListener to this ViewModel.
     * @param listener The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }
}
