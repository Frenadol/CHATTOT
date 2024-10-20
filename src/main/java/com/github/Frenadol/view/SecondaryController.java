package com.github.Frenadol.view;

import java.io.IOException;

import com.github.Frenadol.App;
import javafx.fxml.FXML;

public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}