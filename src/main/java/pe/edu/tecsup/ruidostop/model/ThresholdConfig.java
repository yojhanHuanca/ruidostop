package pe.edu.tecsup.ruidostop.model;

public class ThresholdConfig {
    private int mediumFrom = 35;
    private int highFrom = 70;
    private int sensorMax = 1023;

    public int getMediumFrom() { return mediumFrom; }
    public void setMediumFrom(int mediumFrom) { this.mediumFrom = clamp(mediumFrom); }

    public int getHighFrom() { return highFrom; }
    public void setHighFrom(int highFrom) { this.highFrom = clamp(highFrom); }

    public int getSensorMax() { return sensorMax; }
    public void setSensorMax(int sensorMax) { this.sensorMax = Math.max(1, sensorMax); }

    public int toPercentage(int rawValue) {
        int safe = Math.max(0, Math.min(rawValue, sensorMax));
        return Math.round((safe * 100f) / sensorMax);
    }

    public NoiseLevel classifyPercentage(int percentage) {
        if (percentage >= highFrom) return NoiseLevel.HIGH;
        if (percentage >= mediumFrom) return NoiseLevel.MEDIUM;
        return NoiseLevel.LOW;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
