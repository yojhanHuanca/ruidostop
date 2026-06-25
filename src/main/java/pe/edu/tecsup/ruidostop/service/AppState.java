package pe.edu.tecsup.ruidostop.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pe.edu.tecsup.ruidostop.model.NoiseReading;
import pe.edu.tecsup.ruidostop.model.ThresholdConfig;

public final class AppState {
    private static final AppState INSTANCE = new AppState();

    private final ObservableList<NoiseReading> readings = FXCollections.observableArrayList();
    private final ThresholdConfig thresholdConfig = new ThresholdConfig();
    private final DatabaseService databaseService = DatabaseService.getInstance();
    private String selectedPort = "";
    private boolean simulationMode = true;

    private AppState() {
        readings.addAll(databaseService.loadRecentReadings(250));
    }

    public static AppState getInstance() { return INSTANCE; }
    public ObservableList<NoiseReading> getReadings() { return readings; }
    public ThresholdConfig getThresholdConfig() { return thresholdConfig; }
    public DatabaseService getDatabaseService() { return databaseService; }

    public String getSelectedPort() { return selectedPort; }
    public void setSelectedPort(String selectedPort) { this.selectedPort = selectedPort == null ? "" : selectedPort; }

    public boolean isSimulationMode() { return simulationMode; }
    public void setSimulationMode(boolean simulationMode) { this.simulationMode = simulationMode; }

    public void addReading(NoiseReading reading) {
        readings.add(reading);
        databaseService.saveReading(reading);
        if (readings.size() > 250) readings.remove(0);
    }

    public void clearReadings() {
        readings.clear();
        databaseService.clearReadings();
    }
}
