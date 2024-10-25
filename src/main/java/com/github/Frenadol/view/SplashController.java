// Modificar el SplashController para cambiar a la escena Primary.fxml
package com.github.Frenadol.view;

import com.github.Frenadol.App;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SplashController implements Initializable {
    @FXML
    private AnchorPane splashPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(600);

                Platform.runLater(() -> {
                    try {
                        App.setRoot("Primary"); // Cambiar a Primary.fxml
                    } catch (IOException ex) {
                        Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } catch (InterruptedException ex) {
                Logger.getLogger(SplashController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}