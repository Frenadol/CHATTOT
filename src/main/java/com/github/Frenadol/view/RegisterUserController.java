package com.github.Frenadol.view;

import com.github.Frenadol.utils.Security;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class RegisterUserController {
    @FXML
    private TextField textUsername;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private PasswordField textPassword;
    @FXML
    private Button usersRegister;

    private static final String USERS_FILE = "UsersData.xml";

    /**
     * This method is used to register a new user and save it to an XML file.
     */
    public void registerUser() {
        String username = textUsername.getText();
        String pass = textPassword.getText();

        if (username.isEmpty() || pass.isEmpty()) {
            String message = "Por favor, complete todos los campos.";
            showAlert(message);
            return;
        }

        try {
            File xmlFile = new File(USERS_FILE);

            // Check if user already exists in the XML file
            if (isUserExists(username, xmlFile)) {
                String message = "El nombre de usuario ya está en uso. Por favor, elija otro.";
                showAlert(message);
                return;
            }

            // Hash the password (you can keep this part)
            String hashedPassword;
            try {
                hashedPassword = Security.hashPassword(pass);
            } catch (NoSuchAlgorithmException e) {
                showAlert("Error al hashear la contraseña.");
                return;
            }

            // Create new User element and save to XML
            addUserToXML(username, hashedPassword, xmlFile);

            String message = "Usuario registrado con éxito!";
            showAlert(message);
        } catch (Exception e) {
            showAlert("Error al registrar el usuario: " + e.getMessage());
        }
    }

    /**
     * Check if a user already exists in the XML file.
     */
    private boolean isUserExists(String username, File xmlFile) throws Exception {
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

                if (existingUsername.equals(username)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Add a new user to the XML file.
     */
    private void addUserToXML(String username, String password, File xmlFile) throws ParserConfigurationException, TransformerException, IOException, org.xml.sax.SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc;

        // Check if the file already exists
        if (xmlFile.exists()) {
            doc = docBuilder.parse(xmlFile);
        } else {
            // Create new XML structure if file does not exist
            doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            doc.appendChild(rootElement);
        }

        // Root element
        Element rootElement = doc.getDocumentElement();

        // User element
        Element userElement = doc.createElement("user");
        rootElement.appendChild(userElement);

        // Username element
        Element usernameElement = doc.createElement("username");
        usernameElement.appendChild(doc.createTextNode(username));
        userElement.appendChild(usernameElement);

        // Password element
        Element passwordElement = doc.createElement("password");
        passwordElement.appendChild(doc.createTextNode(password));
        userElement.appendChild(passwordElement);

        // Write the content into XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

    /**
     * This method is used to display an alert dialog with a specified message.
     * It creates a new Alert object, sets the provided message as its content, and then displays the alert.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    @FXML private void onClose() {
        // Close event logic here (if needed)
    }
}