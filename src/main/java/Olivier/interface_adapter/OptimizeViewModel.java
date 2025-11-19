package Olivier.interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class OptimizeViewModel {
    private final String viewName;
    private OptimizeState state;

    public OptimizeViewModel() {
        this.viewName = "optimize expenses";
        OptimizeState s = new OptimizeState();
        s.setLabels(new String[]{"A", "B", "C", "D", "E", "F", "G", "H"});
        setState(s);
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public String getViewName() {return viewName;}

    public void setState(OptimizeState state) {this.state = state;}

    public OptimizeState getState() {return this.state;}

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
