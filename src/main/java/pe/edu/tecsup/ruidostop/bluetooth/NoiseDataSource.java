package pe.edu.tecsup.ruidostop.bluetooth;

public interface NoiseDataSource {
    void start(ReadingListener listener) throws Exception;
    void stop();
    boolean isRunning();
    String getName();
}
