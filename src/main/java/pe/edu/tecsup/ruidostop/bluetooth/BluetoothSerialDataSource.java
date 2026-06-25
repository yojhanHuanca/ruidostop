package pe.edu.tecsup.ruidostop.bluetooth;

import com.fazecast.jSerialComm.SerialPort;
import pe.edu.tecsup.ruidostop.model.ThresholdConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BluetoothSerialDataSource implements NoiseDataSource {
    private final String portName;
    private final ReadingParser parser;
    private volatile boolean running;
    private Thread worker;
    private SerialPort serialPort;

    public BluetoothSerialDataSource(String portName, ThresholdConfig config) {
        this.portName = portName;
        this.parser = new ReadingParser(config);
    }

    public static List<String> listPorts() {
        List<String> names = new ArrayList<>();
        try {
            for (SerialPort port : SerialPort.getCommPorts()) {
                String label = port.getSystemPortName();
                String description = port.getDescriptivePortName();
                names.add(description == null || description.isBlank() ? label : label + " - " + description);
            }
        } catch (Throwable error) {
            names.add("ERROR - No se pudo cargar jSerialComm: " + friendlyNativeError(error));
        }
        return names;
    }

    public static String extractSystemPort(String selected) {
        if (selected == null || selected.isBlank() || selected.startsWith("ERROR -")) return "";
        return selected.split(" - ")[0].trim();
    }

    @Override
    public void start(ReadingListener listener) throws Exception {
        if (isRunning()) return;
        String systemPort = extractSystemPort(portName);
        if (systemPort.isBlank()) throw new IllegalArgumentException("Seleccione un puerto COM valido. Si aparece ERROR, reinicie IntelliJ y ejecute App otra vez.");

        try {
            serialPort = SerialPort.getCommPort(systemPort);
            serialPort.setBaudRate(9600);
            serialPort.setNumDataBits(8);
            serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            serialPort.setParity(SerialPort.NO_PARITY);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);
        } catch (Throwable error) {
            throw new IllegalStateException("No se pudo cargar el soporte Bluetooth serial: " + friendlyNativeError(error));
        }

        if (!serialPort.openPort()) throw new IllegalStateException("No se pudo abrir " + systemPort + ". Verifique que el HC-05 este emparejado y libre.");
        running = true;
        worker = new Thread(() -> readLoop(listener), "ruido-stop-bluetooth-reader");
        worker.setDaemon(true);
        worker.start();
    }

    private void readLoop(ReadingListener listener) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream(), StandardCharsets.UTF_8))) {
            while (running) {
                String line = reader.readLine();
                parser.parse(line, getName()).ifPresent(listener::onReading);
            }
        } catch (Exception ignored) {
            running = false;
        } finally {
            stop();
        }
    }

    @Override
    public void stop() {
        running = false;
        if (serialPort != null && serialPort.isOpen()) serialPort.closePort();
        serialPort = null;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public String getName() {
        return extractSystemPort(portName).isBlank() ? "Bluetooth HC-05" : extractSystemPort(portName);
    }

    private static String friendlyNativeError(Throwable error) {
        String message = error.getMessage();
        if (message == null && error.getCause() != null) message = error.getCause().getMessage();
        if (message == null) message = error.getClass().getSimpleName();
        if (message.contains("ARM 64-bit") || message.contains("aarch64")) {
            return "hay una DLL incorrecta en cache. Cierre IntelliJ y limpie la cache temporal de jSerialComm si sigue pasando.";
        }
        if (message.contains("Acceso denegado")) {
            return "Windows bloqueo la carpeta Temp. La app ahora usa target/native-cache; reinicie la ejecucion.";
        }
        return message;
    }
}


