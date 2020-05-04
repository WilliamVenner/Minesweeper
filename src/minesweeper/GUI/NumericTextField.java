package minesweeper.GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

class NumericOnlyListener implements ChangeListener<String> {
    private final TextField textField;
    public NumericOnlyListener(TextField textField) {
        this.textField = textField;
    }

    @Override
    public void changed(ObservableValue<? extends String> __, String ___, String val) {
        if (!val.matches("^(?!0)\\d*")) {
            textField.setText(val.replaceAll("^0+|\\D+", ""));
        }
    }
}

public class NumericTextField extends TextField {
    public NumericTextField() {
        this.textProperty().addListener(new NumericOnlyListener(this));
    }
}
