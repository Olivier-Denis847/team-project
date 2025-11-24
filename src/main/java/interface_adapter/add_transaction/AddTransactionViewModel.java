package interface_adapter.add_transaction;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AddTransactionViewModel {
    private String message = "";
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {

        String old = this.message;
        this.message = message;

        support.firePropertyChange("message", old, this.message);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
