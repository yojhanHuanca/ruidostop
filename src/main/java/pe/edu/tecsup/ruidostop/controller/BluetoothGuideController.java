package pe.edu.tecsup.ruidostop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import pe.edu.tecsup.ruidostop.bluetooth.BluetoothSerialDataSource;

import java.util.List;

public class BluetoothGuideController {
    @FXML private Label portSummaryLabel;
    @FXML private Label diagnosticLabel;

    @FXML
    public void initialize() {
        refreshDiagnosis();
    }

    @FXML
    public void refreshDiagnosis() {
        List<String> ports = BluetoothSerialDataSource.listPorts();
        if (ports.isEmpty()) {
            portSummaryLabel.setText("No se detectaron puertos COM Bluetooth.");
            diagnosticLabel.setText("Primero empareja el HC-05 desde Windows. Luego vuelve a esta pantalla y presiona Revisar puertos.");
            return;
        }
        if (ports.getFirst().startsWith("ERROR -")) {
            portSummaryLabel.setText("No se pudo leer la lista de puertos.");
            diagnosticLabel.setText(ports.getFirst());
            return;
        }
        portSummaryLabel.setText("Puertos detectados: " + String.join(" | ", ports));
        diagnosticLabel.setText("Si aparecen dos COM del HC-05, prueba uno en Conexion Bluetooth. Si no conecta, desconecta y prueba el otro.");
    }
}
