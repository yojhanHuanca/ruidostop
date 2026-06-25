package pe.edu.tecsup.ruidostop.model;

public enum NoiseLevel {
    LOW("Bajo", "VERDE", "Ambiente adecuado", "#22c55e"),
    MEDIUM("Medio", "AMARILLO", "Precaucion: el ruido esta subiendo", "#fbbf24"),
    HIGH("Alto", "ROJO", "Ruido excesivo: bajar la voz", "#ef4444");

    private final String label;
    private final String code;
    private final String message;
    private final String color;

    NoiseLevel(String label, String code, String message, String color) {
        this.label = label;
        this.code = code;
        this.message = message;
        this.color = color;
    }

    public String getLabel() { return label; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getColor() { return color; }
}
