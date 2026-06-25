package pe.edu.tecsup.ruidostop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    public static void main(String[] args) {
        configureNativeLibraryCache();
        Application.launch(JavaFxApp.class, args);
    }

    private static void configureNativeLibraryCache() {
        try {
            Path cache = Path.of(System.getProperty("user.dir"), "target", "native-cache");
            Files.createDirectories(cache);
            System.setProperty("java.io.tmpdir", cache.toAbsolutePath().toString());
            System.setProperty("fazecast.jSerialComm.appid", "ruido-stop-java-bluetooth-x64");
            if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
                System.setProperty("os.arch", "amd64");
            }
        } catch (Exception ignored) {
            // If the local cache cannot be prepared, Java will fall back to the default temp folder.
        }
    }

    public static class JavaFxApp extends Application {
        @Override
        public void start(Stage stage) throws Exception {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/pe/edu/tecsup/ruidostop/view/MainLayout.fxml"));
            Scene scene = new Scene(loader.load(), 1180, 720);
            scene.getStylesheets().add(App.class.getResource("/pe/edu/tecsup/ruidostop/style/app.css").toExternalForm());
            stage.setTitle("RUIDO-STOP | Monitor Bluetooth HC-05");
            stage.setMinWidth(1040);
            stage.setMinHeight(650);
            stage.setScene(scene);
            stage.show();
        }
    }
}
