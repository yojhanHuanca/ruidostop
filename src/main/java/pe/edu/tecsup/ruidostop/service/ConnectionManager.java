package pe.edu.tecsup.ruidostop.service;

import javafx.application.Platform;
import pe.edu.tecsup.ruidostop.bluetooth.BluetoothSerialDataSource;
import pe.edu.tecsup.ruidostop.bluetooth.NoiseDataSource;
import pe.edu.tecsup.ruidostop.bluetooth.SimulatedNoiseDataSource;

public final class ConnectionManager {
    private static final ConnectionManager INSTANCE = new ConnectionManager();

    private final AppState state = AppState.getInstance();
    private NoiseDataSource currentSource;

    private ConnectionManager() {}

    public static ConnectionManager getInstance() { return INSTANCE; }

    public void startSimulation() throws Exception {
        stop();
        state.setSimulationMode(true);
        currentSource = new SimulatedNoiseDataSource(state.getThresholdConfig());
        currentSource.start(reading -> Platform.runLater(() -> state.addReading(reading)));
    }

    public void startBluetooth(String port) throws Exception {
        stop();
        state.setSimulationMode(false);
        state.setSelectedPort(port);
        currentSource = new BluetoothSerialDataSource(port, state.getThresholdConfig());
        currentSource.start(reading -> Platform.runLater(() -> state.addReading(reading)));
    }

    public void stop() {
        if (currentSource != null) currentSource.stop();
        currentSource = null;
    }

    public boolean isRunning() {
        return currentSource != null && currentSource.isRunning();
    }

    public String getSourceName() {
        return currentSource == null ? "Sin conexion" : currentSource.getName();
    }
}
