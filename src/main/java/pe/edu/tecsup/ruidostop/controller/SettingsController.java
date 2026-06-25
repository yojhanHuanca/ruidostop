package pe.edu.tecsup.ruidostop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import pe.edu.tecsup.ruidostop.model.ThresholdConfig;
import pe.edu.tecsup.ruidostop.service.AppState;

public class SettingsController {
    @FXML private Slider mediumSlider;
    @FXML private Slider highSlider;
    @FXML private Spinner<Integer> sensorMaxSpinner;
    @FXML private Label mediumLabel;
    @FXML private Label highLabel;
    @FXML private Label savedLabel;

    private final ThresholdConfig config = AppState.getInstance().getThresholdConfig();

    @FXML
    public void initialize() {
        mediumSlider.setValue(config.getMediumFrom());
        highSlider.setValue(config.getHighFrom());
        sensorMaxSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(100, 4095, config.getSensorMax(), 1));
        mediumSlider.valueProperty().addListener((obs, oldValue, newValue) -> updateLabels());
        highSlider.valueProperty().addListener((obs, oldValue, newValue) -> updateLabels());
        updateLabels();
    }

    @FXML
    public void saveSettings() {
        int medium = (int) Math.round(mediumSlider.getValue());
        int high = (int) Math.round(highSlider.getValue());
        if (medium >= high) {
            savedLabel.setText("El umbral medio debe ser menor que el umbral alto.");
            return;
        }
        config.setMediumFrom(medium);
        config.setHighFrom(high);
        config.setSensorMax(sensorMaxSpinner.getValue());
        savedLabel.setText("Configuracion guardada: medio desde " + medium + "%, alto desde " + high + "%.");
    }

    private void updateLabels() {
        mediumLabel.setText(Math.round(mediumSlider.getValue()) + "%");
        highLabel.setText(Math.round(highSlider.getValue()) + "%");
    }
}


