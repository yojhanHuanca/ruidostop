package pe.edu.tecsup.ruidostop.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoiseReading {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final LocalDateTime timestamp;
    private final int rawValue;
    private final int percentage;
    private final NoiseLevel level;
    private final String source;

    public NoiseReading(LocalDateTime timestamp, int rawValue, int percentage, NoiseLevel level, String source) {
        this.timestamp = timestamp;
        this.rawValue = rawValue;
        this.percentage = percentage;
        this.level = level;
        this.source = source;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public String getTimeText() { return timestamp.format(TIME); }
    public int getRawValue() { return rawValue; }
    public int getPercentage() { return percentage; }
    public NoiseLevel getLevel() { return level; }
    public String getLevelText() { return level.getLabel(); }
    public String getSource() { return source; }
}
