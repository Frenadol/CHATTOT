package com.github.Frenadol.view;

import java.io.IOException;

import com.github.Frenadol.App;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PrimaryController {
    @FXML
    private Button registerButtom;
    @FXML
    private Button inicieButtom;

    @FXML
    private void switchToRegister() throws IOException {
        App.setRoot("RegisterUser");
    }
    @FXML
    private void switchToInicie() throws IOException {
        App.setRoot("InicieSession");
    }
}
