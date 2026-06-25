package pe.edu.tecsup.ruidostop.controller;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pe.edu.tecsup.ruidostop.model.NoiseLevel;
import pe.edu.tecsup.ruidostop.model.NoiseReading;
import pe.edu.tecsup.ruidostop.service.AppState;

public class HistoryController {
    @FXML private TableView<NoiseReading> readingTable;
    @FXML private TableColumn<NoiseReading, String> timeColumn;
    @FXML private TableColumn<NoiseReading, Integer> rawColumn;
    @FXML private TableColumn<NoiseReading, Integer> percentColumn;
    @FXML private TableColumn<NoiseReading, String> levelColumn;
    @FXML private LineChart<Number, Number> trendChart;
    @FXML private PieChart distributionChart;
    @FXML private Label summaryLabel;

    private final AppState state = AppState.getInstance();
    private final XYChart.Series<Number, Number> trendSeries = new XYChart.Series<>();

    @FXML
    public void initialize() {
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeText"));
        rawColumn.setCellValueFactory(new PropertyValueFactory<>("rawValue"));
        percentColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("levelText"));
        readingTable.setItems(state.getReadings());

        trendChart.setAnimated(false);
        trendSeries.setName("Ruido %");
        trendChart.getData().add(trendSeries);
        NumberAxis yAxis = (NumberAxis) trendChart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(20);

        state.getReadings().addListener((ListChangeListener<NoiseReading>) change -> refreshCharts());
        refreshCharts();
    }

    @FXML
    public void clearHistory() {
        state.clearReadings();
        refreshCharts();
    }

    private void refreshCharts() {
        trendSeries.getData().clear();
        int start = Math.max(0, state.getReadings().size() - 60);
        for (int i = start; i < state.getReadings().size(); i++) {
            trendSeries.getData().add(new XYChart.Data<>(i - start + 1, state.getReadings().get(i).getPercentage()));
        }

        long low = state.getReadings().stream().filter(r -> r.getLevel() == NoiseLevel.LOW).count();
        long medium = state.getReadings().stream().filter(r -> r.getLevel() == NoiseLevel.MEDIUM).count();
        long high = state.getReadings().stream().filter(r -> r.getLevel() == NoiseLevel.HIGH).count();
        distributionChart.getData().setAll(
                new PieChart.Data("Bajo", low),
                new PieChart.Data("Medio", medium),
                new PieChart.Data("Alto", high)
        );

        int total = state.getReadings().size();
        double average = state.getReadings().stream().mapToInt(NoiseReading::getPercentage).average().orElse(0);
        summaryLabel.setText("Lecturas: " + total + "  |  Promedio: " + Math.round(average) + "%  |  Bajo: " + low + "  Medio: " + medium + "  Alto: " + high);
    }
}
