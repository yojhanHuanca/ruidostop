package pe.edu.tecsup.ruidostop.bluetooth;

import pe.edu.tecsup.ruidostop.model.ThresholdConfig;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SimulatedNoiseDataSource implements NoiseDataSource {
    private final ReadingParser parser;
    private final Random random = new Random();
    private ScheduledExecutorService executor;
    private int base = 300;

    public SimulatedNoiseDataSource(ThresholdConfig config) {
        this.parser = new ReadingParser(config);
    }

    @Override
    public void start(ReadingListener listener) {
        if (isRunning()) return;
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ruido-stop-simulator");
            t.setDaemon(true);
            return t;
        });
        executor.scheduleAtFixedRate(() -> {
            base += random.nextInt(161) - 80;
            if (random.nextDouble() < 0.12) base += random.nextInt(420) - 120;
            base = Math.max(40, Math.min(1000, base));
            listener.onReading(parser.fromRaw(base, getName()));
        }, 0, 700, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    @Override
    public boolean isRunning() {
        return executor != null && !executor.isShutdown();
    }

    @Override
    public String getName() {
        return "Simulacion";
    }
}
