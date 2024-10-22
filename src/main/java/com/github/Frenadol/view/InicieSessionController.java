package com.github.Frenadol.view;

import com.github.Frenadol.App;
import com.github.Frenadol.model.User;
import com.github.Frenadol.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InicieSessionController {
    @FXML
    private TextField textUsername;
    @FXML
    private PasswordField textPassword;
    @FXML
    private Button UsersSession;

    private static final String USERS_FILE = "UsersData.xml";

    public void inicieUser() {
        String username = textUsername.getText();
        String pass = textPassword.getText();

        if (username.isEmpty() || pass.isEmpty()) {
            String message = "Por favor, complete todos los campos.";
            showAlert(message);
            return;
        }

        try {
            File xmlFile = new File(USERS_FILE);

            if (isUserExists(username, pass, xmlFile)) {
                String message = "Usuario Correcto.";
                showAlert(message);
                App.setRoot("MainMenu");
            } else {
                String message = "Usuario Incorrecto.";
                showAlert(message);
            }

        } catch (Exception e) {
            showAlert("Error al iniciar con el usuario: " + e.getMessage());
        }
    }

    private boolean isUserExists(String username, String password, File xmlFile) throws Exception {
        if (!xmlFile.exists()) return false;

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("user");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String existingUsername = element.getElementsByTagName("username").item(0).getTextContent();
                String existingPasswordHash = element.getElementsByTagName("password").item(0).getTextContent();

                if (existingUsername.equals(username) && checkPassword(password, existingPasswordHash)) {
                    SessionManager sessionManager = SessionManager.getInstance();
                    User userLogin= new User(username, existingPasswordHash.getBytes());
                    sessionManager.setCurrentUser(userLogin);
                    System.out.println(userLogin);

                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkPassword(String password, String storedHash) throws NoSuchAlgorithmException {
        String hashedPassword = hashPassword(password);
        return hashedPassword.equals(storedHash);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA3-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

}
