package com.github.Frenadol.view;

import com.github.Frenadol.App;
import com.github.Frenadol.model.User;
import com.github.Frenadol.utils.SessionManager;
import com.github.Frenadol.utils.XmlReader;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainMenuController implements Initializable {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView userImage;
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, String> nameUserColumn;
    @FXML
    private TableColumn<User, ImageView> imageProfileColumn;
    @FXML
    private Label NameUser;
    @FXML
    private TableView<User> contactsTable;
    @FXML
    private TableColumn<User, String> contactNameColumn;
    @FXML
    private TableColumn<User, ImageView> contactProfileColumn;
    private ObservableList<User> contactList = FXCollections.observableArrayList();
    @FXML
    private Button addContactButton;
    @FXML
    private Button sendMessageButton;
    @FXML
    private Button BackButton;

    private ObservableList<User> userList = FXCollections.observableArrayList();

    String filePath = "UsersData.xml";

    public void goBack() throws IOException {
        App.setRoot("Primary");
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        ListUsers();
        loadCurrentUserImage();

        nameUserColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        imageProfileColumn.setCellValueFactory(cellData -> {
            byte[] visualData = cellData.getValue().getProfileImage();
            if (visualData != null && visualData.length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(visualData);
                Image image = new Image(bis);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(120);
                imageView.setFitHeight(100);
                applyCircularClip(imageView);
                return new SimpleObjectProperty<>(imageView);
            } else {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(120);
                imageView.setFitHeight(100);
                applyCircularClip(imageView);
                return new SimpleObjectProperty<>(imageView);
            }
        });
        contactNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        contactProfileColumn.setCellValueFactory(cellData -> {
            byte[] visualData = cellData.getValue().getProfileImage();
            if (visualData != null && visualData.length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(visualData);
                Image image = new Image(bis);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(120);
                imageView.setFitHeight(120);
                applyCircularClip(imageView);
                return new SimpleObjectProperty<>(imageView);
            } else {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(120);
                imageView.setFitHeight(100);
                applyCircularClip(imageView);
                return new SimpleObjectProperty<>(imageView);
            }
        });

        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            NameUser.setText(currentUser.getName());
            loadContactsForCurrentUser();
        }
    }

    private void ListUsers() {
        List<User> users = XmlReader.getUsersFromXML(filePath);
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (users.isEmpty()) {
            System.out.println("No users found in XML.");
        } else {
            System.out.println("Users loaded: " + users.size());
        }

        // Filter out the current user and their contacts
        if (currentUser != null) {
            List<User> contacts = currentUser.getContacts();
            users.removeIf(user -> user.getName().equals(currentUser.getName()) || contacts.stream().anyMatch(contact -> contact.getName().equals(user.getName())));
        }

        this.userList = FXCollections.observableArrayList(users);
        usersTable.setItems(userList);
    }

    @FXML
    private void confirmAndAddContact() {
        User selectedContact = usersTable.getSelectionModel().getSelectedItem();

        if (selectedContact != null) {
            User currentUser = SessionManager.getInstance().getCurrentUser();

            if (currentUser != null && currentUser.getContacts().contains(selectedContact)) {
                showAlert("Este contacto ya está en tu lista.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de adición");
            alert.setHeaderText("Estás a punto de agregar a " + selectedContact.getName() + " como contacto");
            alert.setContentText("¿Estás seguro de que quieres agregar a este usuario a tus contactos?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (currentUser != null && !currentUser.getContacts().contains(selectedContact)) {
                    currentUser.addContactToList(selectedContact);

                    List<User> users = XmlReader.getUsersFromXML(filePath);
                    if (users != null) {
                        // Buscar y actualizar la lista de contactos del usuario actual
                        for (User user : users) {
                            if (user.getName().equals(currentUser.getName())) {
                                user.setContacts(currentUser.getContacts());
                                break;
                            }
                        }

                        // Guardar los cambios en el XML con persistencia
                        XmlReader.saveUsersToXML(users, filePath);
                    }

                    // Mostrar alerta de éxito
                    showAlert("Contacto agregado exitosamente.");

                    // Cargar y mostrar los contactos actualizados
                    loadContactsForCurrentUser();

                    // Remover el usuario agregado de la lista de usuarios y actualizar la tabla
                    userList.remove(selectedContact);
                    usersTable.setItems(userList);
                } else {
                    showAlert("Este contacto ya está en tu lista.");
                }
            }
        } else {
            showAlert("Por favor, selecciona un usuario para agregar como contacto.");
        }
    }

    private void loadContactsForCurrentUser() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<User> users = XmlReader.getUsersFromXML(filePath);
            for (User user : users) {
                if (user.getName().equals(currentUser.getName())) {
                    // Clear the existing contact list and add all contacts
                    contactList.clear();
                    contactList.addAll(user.getContacts());
                    contactsTable.setItems(contactList);
                    break;
                }
            }
        }
    }

    @FXML
    private void chattedWithContact() {
        try {
            User selectedContact = contactsTable.getSelectionModel().getSelectedItem(); // El contacto seleccionado
            User currentUser = SessionManager.getInstance().getCurrentUser(); // El usuario actual

            if (selectedContact != null && currentUser != null) {
                // Cargar la lista de usuarios actualizada desde el XML
                List<User> allUsers = XmlReader.getUsersFromXML(filePath);

                // Obtener la versión actualizada del contacto seleccionado y del usuario actual desde el archivo XML
                User updatedSelectedContact = allUsers.stream()
                        .filter(user -> user.getName().equals(selectedContact.getName()))
                        .findFirst()
                        .orElse(null);

                User updatedCurrentUser = allUsers.stream()
                        .filter(user -> user.getName().equals(currentUser.getName()))
                        .findFirst()
                        .orElse(null);

                if (updatedSelectedContact != null && updatedCurrentUser != null) {
                    boolean currentUserHasContact = updatedCurrentUser.getContacts().contains(updatedSelectedContact);
                    boolean contactHasCurrentUser = updatedSelectedContact.getContacts().contains(updatedCurrentUser);

                    if (currentUserHasContact && contactHasCurrentUser) {
                        SessionManager.getInstance().setSelectedUser(updatedSelectedContact);
                        showAlert("Chat iniciado con " + updatedSelectedContact.getName());
                        App.setRoot("Chat");
                    } else {
                        showAlert("No puedes chatear con " + updatedSelectedContact.getName() + " porque no se tienen agregados mutuamente.");
                    }
                } else {
                    showAlert("Error: No se encontró el usuario o el contacto seleccionado.");
                }
            } else {
                showAlert("Por favor, selecciona un contacto para chatear.");
            }
        } catch (Exception e) {
            showAlert("Error al iniciar el chat: " + e.getMessage());
        }
    }

    private void loadCurrentUserImage() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<User> users = XmlReader.getUsersFromXML(filePath);
            for (User user : users) {
                if (user.getName().equals(currentUser.getName())) {
                    byte[] visualData = user.getProfileImage();
                    if (visualData != null && visualData.length > 0) {
                        ByteArrayInputStream bis = new ByteArrayInputStream(visualData);
                        Image image = new Image(bis);
                        userImage.setImage(image);
                        applyCircularClip(userImage);
                    }
                    break;
                }
            }
        }
    }

    private void applyCircularClip(ImageView imageView) {
        Circle clip = new Circle(imageView.getFitWidth() / 2, imageView.getFitHeight() / 2, Math.min(imageView.getFitWidth(), imageView.getFitHeight()) / 2);
        imageView.setClip(clip);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
}