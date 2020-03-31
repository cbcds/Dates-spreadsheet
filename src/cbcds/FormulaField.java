package cbcds;

import javafx.scene.control.TextField;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class FormulaField extends TextField implements PropertyChangeListener {
    private DatesTableView table;
    PropertyChangeListener textChangeListener;

    public FormulaField() {
        setOnAction(e -> {
            textChangeListener.propertyChange(
                    new PropertyChangeEvent(this, null, null, getText()));
            e.consume();
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String date = (String) propertyChangeEvent.getNewValue();
        setText(date);
    }

    void setTextChangeListener(PropertyChangeListener listener) {
        textChangeListener = listener;
    }
}
