package cbcds;

import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

public class EditCell extends TableCell<ObservableMap<Character, Object>, String> implements PropertyChangeListener {
    private TextField textField;
    private TablePosition<ObservableMap<Character, Object>, ?> tablePos;
    private boolean escPressed;

    @Override
    public void startEdit() {
        super.startEdit();
        if (isEditing()) {
            if (textField == null) {
                createTextField();
            }
            escPressed = false;
            tablePos = getTableView().getEditingCell();
            updateItem(null, false);
            DatesTableView table = (DatesTableView) getTableView();
            ((FormulaField)table.cellTextChangeListener).setTextChangeListener(this);
            textField.selectAll();
            textField.requestFocus();
        }
    }

    @Override
    public void commitEdit(String newValue) {
        if (!isEditing()) {
            return;
        }
        TableColumn.CellEditEvent editEvent =
                new CellEditEvent(getTableView(), tablePos, TableColumn.editCommitEvent(), newValue);
        TableColumn.CellEditEvent.fireEvent(getTableColumn(), editEvent);

        if (newValue == null) {
            super.cancelEdit();
            updateItem(getValue(), false);
            return;
        }

        /* TODO add an exception handler (date stays the same) + wrong value is saved after calculation*/
        try {
            Date date = Calculator.calculate((DatesTableView) getTableView(), newValue);
            newValue = DateUtils.toString(date);
            getRow().put(getColumn(), date);
            super.cancelEdit();
            updateItem(newValue, false);
        } catch (IllegalArgumentException e) {
            super.cancelEdit();
            updateItem(getValue(), false);
        }
    }

    @Override
    public void cancelEdit() {
        if (escPressed) {
            super.cancelEdit();
            setText(getValue());
        } else {
            commitEdit(textField.getText());
        }
    }

    @Override
    protected void updateItem(String date, boolean isEmpty) {
        super.updateItem(date, isEmpty);
        if (isEmpty) {
            setText(null);
            setGraphic(null);
        } else if (isEditing()) {
            if (textField != null) {
                textField.setText(getValue());
            }
            setText(null);
            setGraphic(textField);
        } else {
            setText(getItem());
            setGraphic(null);
        }
    }

    private void createTextField() {
        textField = new TextField(getItem());
        textField.setOnAction(e -> {
            commitEdit(textField.getText());
            e.consume();
        });
        textField.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                escPressed = true;
            } else {
                DatesTableView table = (DatesTableView) getTableView();
                table.cellTextChangeListener.propertyChange(
                        new PropertyChangeEvent(table, null, null, textField.getText()));
            }
        });
    }

    private String getValue() {
        char column = getColumn();
        ObservableMap<Character, Object> row = getRow();
        Date date = (Date)row.get(column);
        if (date == null) {
            return null;
        }
        return DateUtils.toString(date);
    }

    private char getColumn() {
        return (char)(tablePos.getColumn() - 1 + 'A');
    }

    private ObservableMap<Character, Object> getRow() {
        return getTableView().getItems().get(tablePos.getRow());
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        String text = (String)propertyChangeEvent.getNewValue();
        commitEdit(text);
    }
}
