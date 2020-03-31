package cbcds;

import javafx.fxml.FXML;

public class Controller {
    @FXML
    private FormulaField tfFormula;

    @FXML
    private DatesTableView table;

    @FXML
    public void initialize() {
        table.setCellTextChangeListener(tfFormula);
    }
}
