package com.github.Frenadol.view;

import com.github.Frenadol.App;
import com.github.Frenadol.utils.Security;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RegisterUserController {
    @FXML
    private TextField textUsername;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private PasswordField textPassword;
    @FXML
    private Button usersRegister;
    @FXML
    private Button BackButton;
    @FXML
    private ImageView imageView;

    private File imageFile;

    private static final String USERS_FILE = "UsersData.xml";

    /**
     * This method is used to register a new user and save it to an XML file.
     */
    public void registerUser() {
        String username = textUsername.getText();
        String pass = textPassword.getText();

        // Check if all fields are filled
        if (username.isEmpty() || pass.isEmpty()) {
            showAlert("Por favor, complete todos los campos.");
            return;
        }

        // Check if image is chosen
        if (imageFile == null) {
            showAlert("Por favor, elija una foto de perfil.");
            return;
        }

        // Check if password is secure
        if (!isPasswordSecure(pass)) {
            showAlert("La contraseña debe tener al menos 8 caracteres, una letra mayúscula y un número.");
            return;
        }

        try {
            File xmlFile = new File(USERS_FILE);

            // Check if user already exists in the XML file
            if (isUserExists(username, xmlFile)) {
                showAlert("El nombre de usuario ya está en uso. Por favor, elija otro.");
                return;
            }

            String hashedPassword;
            try {
                hashedPassword = Security.hashPassword(pass);
            } catch (NoSuchAlgorithmException e) {
                showAlert("Error al hashear la contraseña.");
                return;
            }

            // Read image data
            byte[] imageData = new byte[(int) imageFile.length()];
            try (FileInputStream fis = new FileInputStream(imageFile)) {
                fis.read(imageData);
            }

            addUserToXML(username, hashedPassword, imageData, xmlFile);

            showAlert("Usuario registrado con éxito!");
            App.setRoot("primary");
        } catch (Exception e) {
            showAlert("Error al registrar el usuario: " + e.getMessage());
        }
    }

    /**
     * Checks if a password is secure, meaning it contains at least one uppercase letter, one digit, and is at least 8 characters long.
     */
    private boolean isPasswordSecure(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }

        return hasUppercase && hasDigit;
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
     * Add a new user to the XML file with a default image.
     */
    private void addUserToXML(String username, String password, byte[] imageData, File xmlFile)
            throws ParserConfigurationException, TransformerException, IOException, org.xml.sax.SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc;

        if (xmlFile.exists()) {
            doc = docBuilder.parse(xmlFile);
        } else {
            doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("users");
            doc.appendChild(rootElement);
        }

        Element rootElement = doc.getDocumentElement();

        // Crear el nuevo elemento <user>
        Element userElement = doc.createElement("user");
        rootElement.appendChild(userElement);

        // Añadir el <username>
        Element usernameElement = doc.createElement("username");
        usernameElement.appendChild(doc.createTextNode(username));
        userElement.appendChild(usernameElement);

        // Añadir el <password>
        Element passwordElement = doc.createElement("password");
        passwordElement.appendChild(doc.createTextNode(password));
        userElement.appendChild(passwordElement);

        // Añadir la <image>
        Element imageElement = doc.createElement("image");
        imageElement.appendChild(doc.createTextNode(Base64.getEncoder().encodeToString(imageData)));
        userElement.appendChild(imageElement);

        // Guardar el documento en el archivo XML
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

        alert.getDialogPane().setPrefSize(400, 200); // Ajusta el ancho y alto según tus necesidades

        alert.getDialogPane().setStyle("-fx-font-size: 14px;"); // Ajusta el tamaño de la fuente si deseas hacerlo más grande

        alert.show();
    }


    @FXML
    private void onClose() {
        // Método para cerrar la ventana si es necesario
    }

    @FXML
    private void loadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        Stage stage = (Stage) imageView.getScene().getWindow();
        imageFile = fileChooser.showOpenDialog(stage);
        if (imageFile != null) {
            try {
                InputStream is = new FileInputStream(imageFile);
                Image image = new Image(is);
                applyCircularClip(imageView);
                imageView.setImage(image); // Mostrar la imagen en el ImageView
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void goBack() throws IOException {
        App.setRoot("Primary");
    }

    private void applyCircularClip(ImageView imageView) {
        Circle clip = new Circle(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2);
        imageView.setClip(clip);
    }
}
