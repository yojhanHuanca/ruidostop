package pe.edu.tecsup.ruidostop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import pe.edu.tecsup.ruidostop.bluetooth.BluetoothSerialDataSource;
import pe.edu.tecsup.ruidostop.service.ConnectionManager;

public class ConnectionController {
    @FXML private ComboBox<String> portCombo;
    @FXML private Label statusLabel;
    @FXML private Label helpLabel;

    private final ConnectionManager manager = ConnectionManager.getInstance();

    @FXML
    public void initialize() {
        refreshPorts();
        updateStatus();
    }

    @FXML
    public void refreshPorts() {
        portCombo.getItems().setAll(BluetoothSerialDataSource.listPorts());
        if (!portCombo.getItems().isEmpty()) portCombo.getSelectionModel().selectFirst();
        helpLabel.setText(portCombo.getItems().isEmpty()
                ? "No se detectaron puertos. Empareja el HC-05 en Windows y vuelve a actualizar."
                : "Selecciona el COM saliente del HC-05. Normalmente aparece como COM5, COM6 o similar.");
    }

    @FXML
    public void connectBluetooth() {
        try {
            String selected = portCombo.getSelectionModel().getSelectedItem();
            manager.startBluetooth(selected);
            statusLabel.setText("Conectado a " + manager.getSourceName());
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void startSimulation() {
        try {
            manager.startSimulation();
            statusLabel.setText("Modo simulacion activo");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    public void disconnect() {
        manager.stop();
        updateStatus();
    }

    private void updateStatus() {
        statusLabel.setText(manager.isRunning() ? "Activo: " + manager.getSourceName() : "Sin conexion activa");
    }
}
