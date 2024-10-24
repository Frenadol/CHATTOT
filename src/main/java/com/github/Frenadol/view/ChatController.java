package com.github.Frenadol.view;

import com.github.Frenadol.model.User;
import com.github.Frenadol.model.Message;
import com.github.Frenadol.utils.XmlReader;
import com.github.Frenadol.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
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

    private User currentUser;
    private User selectedUser;

    private static final String filePathChat = "ChatData.xml";
    private static final String filePathChatTxt = "ChatData.txt"; // Ruta del archivo de texto

    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    SessionManager sessionManager = SessionManager.getInstance();

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
        }
    }
}
