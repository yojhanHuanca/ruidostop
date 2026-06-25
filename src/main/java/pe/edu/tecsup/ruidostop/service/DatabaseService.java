package pe.edu.tecsup.ruidostop.service;

import pe.edu.tecsup.ruidostop.model.NoiseLevel;
import pe.edu.tecsup.ruidostop.model.NoiseReading;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseService {
    private static final DatabaseService INSTANCE = new DatabaseService();
    private final Path databasePath;
    private final String jdbcUrl;

    private DatabaseService() {
        try {
            Path dataDir = Path.of(System.getProperty("user.dir"), "data");
            Files.createDirectories(dataDir);
            databasePath = dataDir.resolve("ruido_stop.db");
            jdbcUrl = "jdbc:sqlite:" + databasePath.toAbsolutePath();
            initialize();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo preparar la base de datos SQLite", e);
        }
    }

    public static DatabaseService getInstance() {
        return INSTANCE;
    }

    public void initialize() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS noise_readings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    timestamp TEXT NOT NULL,
                    raw_value INTEGER NOT NULL,
                    percentage INTEGER NOT NULL,
                    level TEXT NOT NULL,
                    source TEXT NOT NULL
                )
                """;
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    public void saveReading(NoiseReading reading) {
        String sql = """
                INSERT INTO noise_readings(timestamp, raw_value, percentage, level, source)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, reading.getTimestamp().toString());
            statement.setInt(2, reading.getRawValue());
            statement.setInt(3, reading.getPercentage());
            statement.setString(4, reading.getLevel().name());
            statement.setString(5, reading.getSource());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("No se pudo guardar lectura en SQLite: " + e.getMessage());
        }
    }

    public List<NoiseReading> loadRecentReadings(int limit) {
        String sql = """
                SELECT timestamp, raw_value, percentage, level, source
                FROM noise_readings
                ORDER BY id DESC
                LIMIT ?
                """;
        List<NoiseReading> readings = new ArrayList<>();
        try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    readings.add(0, new NoiseReading(
                            LocalDateTime.parse(rs.getString("timestamp")),
                            rs.getInt("raw_value"),
                            rs.getInt("percentage"),
                            NoiseLevel.valueOf(rs.getString("level")),
                            rs.getString("source")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("No se pudo leer historial SQLite: " + e.getMessage());
        }
        return readings;
    }

    public void clearReadings() {
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM noise_readings");
        } catch (SQLException e) {
            System.err.println("No se pudo limpiar historial SQLite: " + e.getMessage());
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }
}


