package com.github.Frenadol.view;

import com.github.Frenadol.model.User;
import com.github.Frenadol.model.Message;
import com.github.Frenadol.utils.XmlReader;
import com.github.Frenadol.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

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

    private static final String filePathUser = "UsersData.xml";
    private static final String filePathChat = "ChatData.xml";

    SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = sessionManager.getCurrentUser();
        sendButton.setOnAction(event -> sendMessage());
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void setSelectedUser(User user) {
        this.selectedUser = user;
        displayMessages();
    }

    private void displayMessages() {
        messageList.getItems().clear();
        List<Message> messages = XmlReader.getMessagesFromXML(filePathChat);
        for (Message message : messages) {
            if ((message.getSender().equals(currentUser) && message.getReceiver().equals(selectedUser)) ||
                    (message.getSender().equals(selectedUser) && message.getReceiver().equals(currentUser))) {
                messageList.getItems().add(message.getContent());
            }
        }
    }

    @FXML
    private void sendMessage() {
        String content = messageField.getText();
        if (content != null && !content.isEmpty() && selectedUser != null) {
            // Create a new message
            Message newMessage = new Message(currentUser, selectedUser, content, LocalDateTime.now());

            // Save the message to the XML file
            List<Message> messages = XmlReader.getMessagesFromXML(filePathChat);
            messages.add(newMessage);
            XmlReader.saveMessagesToXML(messages, filePathChat);

            // Update the ListView
            messageList.getItems().add("To " + selectedUser.getName() + ": " + content);
            messageField.clear();
        }
    }
}