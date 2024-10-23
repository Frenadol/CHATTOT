package com.github.Frenadol.view;

import com.github.Frenadol.model.User;
import com.github.Frenadol.model.Message;
import com.github.Frenadol.utils.XmlReader;
import com.github.Frenadol.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    private DialogPane chatDialogPane;  // No se usa activamente, pero podría ser útil para otras cosas
    @FXML
    private ListView<String> messageList;  // Aquí se muestran los mensajes
    @FXML
    private TextField messageField;  // Campo de texto para escribir mensajes
    @FXML
    private Button sendButton;  // Botón de envío de mensajes

    private User currentUser;
    private User selectedUser;

    private static final String usersPathChat = "UsersData.xml";  // Archivo XML para los chats
    private static final String filePathChat = "ChatData.xml";  // Archivo XML para los chats


    // Manejador de sesiones para obtener el usuario actual
    SessionManager sessionManager = SessionManager.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = sessionManager.getCurrentUser();
        selectedUser= sessionManager.getSelectedUser();

        if(selectedUser!=null){
            displayMessages();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se ha seleccionado un usuario");
            alert.showAndWait();
        }
        sendButton.setOnAction(event -> sendMessage());
    }




    @FXML
    private void displayMessages() {
        messageList.getItems().clear();  // Limpiar la lista de mensajes

        try {
            List<User> users = XmlReader.getUsersFromXML("UsersData.xml"); // Cambia esto a tu ruta real
            List<Message> messages = XmlReader.getMessagesFromXML(usersPathChat);

            // Mostrar solo los mensajes entre el usuario actual y el seleccionado
            for (Message message : messages) {
                if ((message.getSender().equals(currentUser) && message.getReceiver().equals(selectedUser)) ||
                        (message.getSender().equals(selectedUser) && message.getReceiver().equals(currentUser))) {
                    messageList.getItems().add(message.getContent());  // Agregar el mensaje a la lista
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al cargar mensajes");
            alert.setHeaderText("No se pudieron cargar los mensajes");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    // Método para enviar un mensaje
    @FXML
    private void sendMessage() {
        String content = messageField.getText();  // Obtener el contenido del mensaje

        if (content != null && !content.isEmpty() && selectedUser != null) {
            // Crear un nuevo objeto de tipo Message
            Message newMessage = new Message(currentUser, selectedUser, content, LocalDateTime.now());

            List<Message> messages = XmlReader.getMessagesFromXML(filePathChat);  // Asegúrate de que filePathChat esté definido correctamente

            // Agregar el nuevo mensaje a la lista
            messages.add(newMessage);

            // Guardar los mensajes actualizados en el archivo XML
            XmlReader.saveMessagesToXML(messages, filePathChat);

            // Mostrar el mensaje enviado en la lista de mensajes
            messageList.getItems().add("To " + selectedUser.getName() + ": " + content);

            // Limpiar el campo de texto para nuevos mensajes
            messageField.clear();
        } else {
            // Mostrar un mensaje de advertencia si no se puede enviar el mensaje
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("Mensaje no enviado");
            alert.setContentText("Por favor, escribe un mensaje y selecciona un usuario.");
            alert.showAndWait();
        }
    }


}
