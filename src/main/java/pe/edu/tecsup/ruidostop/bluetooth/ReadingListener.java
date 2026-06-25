package pe.edu.tecsup.ruidostop.bluetooth;

import pe.edu.tecsup.ruidostop.model.NoiseReading;

@FunctionalInterface
public interface ReadingListener {
    void onReading(NoiseReading reading);
}
