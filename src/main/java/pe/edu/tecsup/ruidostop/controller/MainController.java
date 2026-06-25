package pe.edu.tecsup.ruidostop.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    @FXML private StackPane contentPane;
    @FXML private Button dashboardButton;
    @FXML private Button connectionButton;
    @FXML private Button guideButton;
    @FXML private Button historyButton;
    @FXML private Button settingsButton;

    @FXML
    public void initialize() {
        showDashboard();
    }

    @FXML public void showDashboard() { load("DashboardView.fxml", dashboardButton); }
    @FXML public void showConnection() { load("ConnectionView.fxml", connectionButton); }
    @FXML public void showGuide() { load("BluetoothGuideView.fxml", guideButton); }
    @FXML public void showHistory() { load("HistoryView.fxml", historyButton); }
    @FXML public void showSettings() { load("SettingsView.fxml", settingsButton); }

    private void load(String fxml, Button active) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/pe/edu/tecsup/ruidostop/view/" + fxml));
            contentPane.getChildren().setAll(view);
            for (Button button : new Button[]{dashboardButton, connectionButton, guideButton, historyButton, settingsButton}) {
                button.getStyleClass().remove("nav-active");
            }
            active.getStyleClass().add("nav-active");
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar " + fxml, e);
        }
    }
}

