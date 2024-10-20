package com.github.Frenadol.view;

import java.io.IOException;

import com.github.Frenadol.App;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("RegisterUser");
    }
}
