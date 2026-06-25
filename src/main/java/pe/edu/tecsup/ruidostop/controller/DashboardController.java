package pe.edu.tecsup.ruidostop.controller;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import pe.edu.tecsup.ruidostop.model.NoiseReading;
import pe.edu.tecsup.ruidostop.service.AppState;

public class DashboardController {
    @FXML private Label stateLabel;
    @FXML private Label messageLabel;
    @FXML private Label rawValueLabel;
    @FXML private Label percentLabel;
    @FXML private Label sourceLabel;
    @FXML private Label lastUpdateLabel;
    @FXML private Label recommendationLabel;
    @FXML private ProgressBar noiseBar;
    @FXML private StackPane trafficLight;
    @FXML private LineChart<Number, Number> liveChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final AppState state = AppState.getInstance();
    private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
    private int pointIndex = 0;

    @FXML
    public void initialize() {
        series.setName("Intensidad %");
        liveChart.getData().add(series);
        liveChart.setAnimated(false);
        xAxis.setLabel("Lecturas recientes");
        yAxis.setLabel("% ruido");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(20);
        state.getReadings().addListener((ListChangeListener<NoiseReading>) change -> {
            while (change.next()) if (change.wasAdded()) change.getAddedSubList().forEach(this::showReading);
        });
        if (!state.getReadings().isEmpty()) showReading(state.getReadings().getLast());
    }

    private void showReading(NoiseReading reading) {
        Platform.runLater(() -> {
            stateLabel.setText(reading.getLevel().getLabel().toUpperCase());
            messageLabel.setText(reading.getLevel().getMessage());
            rawValueLabel.setText(String.valueOf(reading.getRawValue()));
            percentLabel.setText(reading.getPercentage() + "%");
            sourceLabel.setText(reading.getSource());
            lastUpdateLabel.setText(reading.getTimeText());
            recommendationLabel.setText(recommendation(reading));
            noiseBar.setProgress(reading.getPercentage() / 100.0);
            trafficLight.setStyle("-fx-background-color: " + reading.getLevel().getColor() + ";");
            series.getData().add(new XYChart.Data<>(pointIndex++, reading.getPercentage()));
            if (series.getData().size() > 35) series.getData().remove(0);
        });
    }

    private String recommendation(NoiseReading reading) {
        return switch (reading.getLevel()) {
            case LOW -> "El ambiente esta estable. Mantener el volumen de conversacion.";
            case MEDIUM -> "Atencion: conviene bajar la voz antes de llegar al estado rojo.";
            case HIGH -> "Ruido excesivo. Activar pausa breve y pedir autorregulacion del grupo.";
        };
    }
}
