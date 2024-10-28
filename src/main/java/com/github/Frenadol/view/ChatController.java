package com.github.Frenadol.view;

import com.github.Frenadol.App;
import com.github.Frenadol.model.User;
import com.github.Frenadol.model.Message;
import com.github.Frenadol.utils.XmlReader;
import com.github.Frenadol.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatController implements Initializable {
    @FXML
    private DialogPane chatDialogPane;
    @FXML
    private ListView<String> messageList;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private Button BackButton;

    private User currentUser;
    private User selectedUser;

    private static final String filePathChat = "ChatData.xml";
    private static final String filePathChatTxt = "ChatData.txt"; // Ruta del archivo de texto

    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    SessionManager sessionManager = SessionManager.getInstance();

    public void goBack() throws IOException {
        App.setRoot("MainMenu");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = sessionManager.getCurrentUser();
        selectedUser = sessionManager.getSelectedUser();

        logger.log(Level.INFO, "Current User: {0}", currentUser);
        logger.log(Level.INFO, "Selected User: {0}", selectedUser);
        messageList.setCellFactory(listView -> new MessageListCell());
        if (selectedUser != null) {
            displayMessages();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se ha seleccionado un usuario");
            alert.showAndWait();
        }
        sendButton.setOnAction(event -> {
            if (selectedUser != null) {
                sendMessage();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se ha seleccionado un usuario");
                alert.showAndWait();
            }
        });

        // Add event handler for Enter key press
        messageField.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER:
                    sendMessage();
                    break;
                default:
                    break;
            }
        });
    }

    @FXML
    private void displayMessages() {
        try {
            List<Message> messages = XmlReader.getMessagesFromXML(filePathChat);

            if (messages.isEmpty()) {
                System.out.println("No messages found in XML.");
            } else {
                System.out.println("Messages loaded: " + messages.size());
            }

            for (Message message : messages) {
                if (message.getSender().equals(currentUser) && message.getReceiver().equals(selectedUser)) {
                    messageList.getItems().add("To " + selectedUser.getName() + ": " + message.getContent());
                } else if ((message.getSender().equals(selectedUser) && message.getReceiver().equals(currentUser))) {
                    messageList.getItems().add("From " + selectedUser.getName() + ": " + message.getContent());
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading messages", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al cargar mensajes");
            alert.setHeaderText("No se pudieron cargar los mensajes");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void addMessageToXML(Message newMessage, File xmlFile)
            throws ParserConfigurationException, TransformerException, IOException, org.xml.sax.SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc;

        if (xmlFile.exists()) {
            doc = docBuilder.parse(xmlFile);
        } else {
            doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("messages");
            doc.appendChild(rootElement);
        }

        Element rootElement = doc.getDocumentElement();

        // Create the new <message> element
        Element messageElement = doc.createElement("message");
        rootElement.appendChild(messageElement);

        // Add the <sender>
        Element senderElement = doc.createElement("sender");
        senderElement.appendChild(doc.createTextNode(newMessage.getSender().getName()));
        messageElement.appendChild(senderElement);

        // Add the <receiver>
        Element receiverElement = doc.createElement("receiver");
        receiverElement.appendChild(doc.createTextNode(newMessage.getReceiver().getName()));
        messageElement.appendChild(receiverElement);

        // Add the <content>
        Element contentElement = doc.createElement("content");
        contentElement.appendChild(doc.createTextNode(newMessage.getContent()));
        messageElement.appendChild(contentElement);

        // Add the <timestamp>
        Element timestampElement = doc.createElement("timestamp");
        timestampElement.appendChild(doc.createTextNode(newMessage.getTimestamp().toString()));
        messageElement.appendChild(timestampElement);

        // Save the document to the XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(xmlFile);

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
    }

    // Nuevo método para agregar el mensaje al archivo de texto
    private void addMessageToTxt(Message newMessage, File txtFile) throws IOException {
        try (FileWriter writer = new FileWriter(txtFile, true)) {
            writer.write("[" + newMessage.getTimestamp() + "] " + newMessage.getSender().getName() +
                    " to " + newMessage.getReceiver().getName() + ": " + newMessage.getContent() + "\n");
        }
    }

    @FXML
    private void sendMessage() {
        String content = messageField.getText();

        if (content != null && !content.isEmpty() && selectedUser != null) {
            Message newMessage = new Message(currentUser, selectedUser, content, LocalDateTime.now());

            try {
                // Add the new message to the XML file
                addMessageToXML(newMessage, new File(filePathChat));

                // Add the new message to the text file
                addMessageToTxt(newMessage, new File(filePathChatTxt));

                // Append the new message to the ListView
                messageList.getItems().add("To " + selectedUser.getName() + ": " + content);
                messageField.clear();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error sending message", e);
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al enviar mensaje");
                alert.setHeaderText("No se pudo enviar el mensaje");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText("No hay usuario seleccionado");
            alert.setContentText("Por favor, seleccione un usuario antes de enviar un mensaje.");
            alert.showAndWait();
        }
    }
        @FXML
        private void exportConversationToTXT() {
            if (selectedUser == null) {
                showAlert("Error", "No se ha seleccionado un usuario", "Por favor, selecciona un usuario para exportar la conversación.");
                return;
            }

            try {
                // Obtener todos los mensajes desde el archivo XML
                List<Message> allMessages = XmlReader.getMessagesFromXML(filePathChat);
                List<Message> conversation = new ArrayList<>();

                // Filtrar los mensajes entre el usuario actual y el usuario seleccionado
                for (Message message : allMessages) {
                    if ((message.getSender().equals(currentUser) && message.getReceiver().equals(selectedUser)) ||
                            (message.getSender().equals(selectedUser) && message.getReceiver().equals(currentUser))) {
                        conversation.add(message);
                    }
                }

                // Configurar el selector de archivos para guardar la conversación en un archivo de texto
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Guardar Conversación");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Texto (*.txt)", "*.txt"));
                File file = fileChooser.showSaveDialog((Stage) messageList.getScene().getWindow());

                if (file != null) {
                    try (FileOutputStream fos = new FileOutputStream(file);
                         OutputStreamWriter osw = new OutputStreamWriter(fos);
                         PrintWriter writer = new PrintWriter(osw)) {

                        // Escribir cada mensaje en el archivo de texto
                        for (Message message : conversation) {
                            writer.println("[" + message.getTimestamp() + "] " +
                                    message.getSender().getName() + " a " +
                                    message.getReceiver().getName() + ": " +
                                    message.getContent());
                        }
                    }

                    showAlert("Exportación Exitosa", "Conversación Exportada",
                            "La conversación ha sido exportada exitosamente a " + file.getAbsolutePath());
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error al exportar la conversación", e);
                showAlert("Error al Exportar", "No se pudo exportar la conversación", e.getMessage());
            }
        }

        private void showAlert (String title, String header, String content){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.showAndWait();
        }

    }