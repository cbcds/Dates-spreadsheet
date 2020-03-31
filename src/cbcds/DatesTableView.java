package cbcds;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

public class DatesTableView extends TableView<ObservableMap<Character, Object>> {
    PropertyChangeListener cellTextChangeListener;

    public DatesTableView() {
        getSelectionModel().setCellSelectionEnabled(true);
        createRowsHeaders();
        createColumns();
        createRows();
    }

    /* TODO move style properties to .css */
    private void createRowsHeaders() {
        TableColumn<ObservableMap<Character, Object>, Integer> rowHeaders = new TableColumn<>();
        rowHeaders.setPrefWidth(85);
        rowHeaders.setEditable(false);
        rowHeaders.setStyle("-fx-background-color: #1eb980; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-font-size: 14px;");
        rowHeaders.setCellValueFactory(t -> new SimpleObjectProperty<>((Integer)t.getValue().get('0')));
        getColumns().add(rowHeaders);
    }

    private void createColumns() {
        for (char i = 0; i < 'Z' - 'A'; ++i) {
            TableColumn<ObservableMap<Character, Object>, String> col = new TableColumn<>(String.valueOf((char)('A' + i)));
            col.setPrefWidth(100);
            char colIndex = i;
            col.setCellValueFactory(t -> {
                Date date = (Date)t.getValue().get(colIndex);
                if (date == null) {
                    return new SimpleStringProperty(null);
                }
                return new SimpleStringProperty(DateUtils.toString(date));
            });
            col.setCellFactory(t -> new EditCell());
            setOnEditHandlers(col);
            getColumns().add(col);
        }
    }

    private void createRows() {
        for (int i = 1; i <= 40; ++i) {
            ObservableMap<Character, Object> row = FXCollections.observableHashMap();
            row.put('0', i);
            getItems().add(row);
        }
    }

    private void setOnEditHandlers(TableColumn<ObservableMap<Character, Object>, String> col) {
        col.setOnEditStart(t -> {
            char column = (char)(t.getTablePosition().getColumn() - 1 + 'A');
            ObservableMap<Character, Object> row = t.getTableView().getItems().get(t.getTablePosition().getRow());
            Date oldDate = (Date)row.get(column);
            if (oldDate != null) {
                cellTextChangeListener.propertyChange(
                        new PropertyChangeEvent(this, null, null, DateUtils.toString(oldDate)));
            }
        });
        col.setOnEditCancel(t -> {
            cellTextChangeListener.propertyChange(
                    new PropertyChangeEvent(this, null, null, null));
        });
    }

    Date getValue(int row, char col) throws IndexOutOfBoundsException {
        if (row <= 0 || row > 40 || col < 'A' || col > 'Z') {
            throw new IndexOutOfBoundsException();
        }
        return (Date)getItems().get(row - 1).get(col);
    }

    void setCellTextChangeListener(PropertyChangeListener listener) {
        cellTextChangeListener = listener;
    }
}
