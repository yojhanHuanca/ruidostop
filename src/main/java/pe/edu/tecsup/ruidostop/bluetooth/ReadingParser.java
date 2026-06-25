package pe.edu.tecsup.ruidostop.bluetooth;

import pe.edu.tecsup.ruidostop.model.NoiseLevel;
import pe.edu.tecsup.ruidostop.model.NoiseReading;
import pe.edu.tecsup.ruidostop.model.ThresholdConfig;

import java.time.LocalDateTime;
import java.util.Optional;

public class ReadingParser {
    private final ThresholdConfig config;

    public ReadingParser(ThresholdConfig config) {
        this.config = config;
    }

    public Optional<NoiseReading> parse(String line, String source) {
        if (line == null || line.isBlank()) return Optional.empty();
        String clean = line.trim().replace(";", ",");
        String[] parts = clean.split(",");
        try {
            if (parts.length == 1) {
                int raw = Integer.parseInt(parts[0].trim());
                return Optional.of(fromRaw(raw, source));
            }
            if (parts.length >= 2) {
                int raw = Integer.parseInt(parts[1].trim());
                int percentage = config.toPercentage(raw);
                NoiseLevel level = levelFromText(parts[0].trim(), percentage);
                return Optional.of(new NoiseReading(LocalDateTime.now(), raw, percentage, level, source));
            }
        } catch (NumberFormatException ignored) {
            return Optional.empty();
        }
        return Optional.empty();
    }

    public NoiseReading fromRaw(int raw, String source) {
        int percentage = config.toPercentage(raw);
        return new NoiseReading(LocalDateTime.now(), raw, percentage, config.classifyPercentage(percentage), source);
    }

    private NoiseLevel levelFromText(String text, int percentage) {
        String upper = text.toUpperCase();
        if (upper.contains("ROJO") || upper.contains("ALTO")) return NoiseLevel.HIGH;
        if (upper.contains("AMARILLO") || upper.contains("MEDIO")) return NoiseLevel.MEDIUM;
        if (upper.contains("VERDE") || upper.contains("BAJO")) return NoiseLevel.LOW;
        return config.classifyPercentage(percentage);
    }
}
