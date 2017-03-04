import java.beans.*;
import java.io.Serializable;

/**
 * Created by angela on 05.12.16.
 */
public class NumberField implements Serializable {
    private int number, x, y, area;
    private boolean initial, numberSet;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private VetoableChangeSupport vcs = new VetoableChangeSupport(this);

    public NumberField() {

    }

    public NumberField(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener lst) {
        pcs.addPropertyChangeListener(lst);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener lst) {
        pcs.removePropertyChangeListener(lst);
    }

    public synchronized void addVetoableChangeListener(VetoableChangeListener lst) {
        vcs.addVetoableChangeListener(lst);
    }

    public synchronized void removeVetoableChangeListener(VetoableChangeListener lst) {
        vcs.removeVetoableChangeListener(lst);
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) throws PropertyVetoException {
        int oldNumber = this.number;
        vcs.fireVetoableChange("numberchange", oldNumber, number);
        this.number = number;
        this.numberSet = true;
        pcs.firePropertyChange("numberchange", oldNumber, number);
    }

    public boolean isInitial() {
        return initial;
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    public boolean isNumberSet() {
        return numberSet;
    }

    public void emptyNumber() {
        int oldNumber = this.number;
        this.number = 0;
        pcs.firePropertyChange("number_" + x + "_" + y, oldNumber, number);
        this.numberSet = false;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }
}
