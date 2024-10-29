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
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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
    @FXML
    private Button changeNameButton;
    @FXML
    private Button changePhotoButton;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    String filePath = "UsersData.xml";

    /**
     * Navigates back to the primary view.
     *
     * @throws IOException if there is an issue changing the view.
     */
    public void goBack() throws IOException {
        App.setRoot("Primary");
    }

    /**
     * Initializes the controller by loading the user list and the current user image.
     * Sets up cell value factories for user tables and loads contacts for the current user.
     *
     * @param url the location used to resolve relative paths for the root object,
     *            or null if the location is not known.
     * @param resources the resources used to localize the root object, or null if the
     *                  root object was not localized.
     */
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

        changePhotoButton.setOnAction(event -> changeUserPhoto());
    }

    /**
     * Loads users from the XML file and populates the users table,
     * filtering out the current user and their contacts.
     */
    private void ListUsers() {
        List<User> users = XmlReader.getUsersFromXML(filePath);
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (users.isEmpty()) {
            System.out.println("No users found in XML.");
        } else {
            System.out.println("Users loaded: " + users.size());
        }

        if (currentUser != null) {
            List<User> contacts = currentUser.getContacts();
            users.removeIf(user -> user.getName().equals(currentUser.getName()) || contacts.stream().anyMatch(contact -> contact.getName().equals(user.getName())));
        }

        this.userList = FXCollections.observableArrayList(users);
        usersTable.setItems(userList);
    }

    /**
     * Confirms and adds the selected contact to the current user's contact list.
     * Displays a confirmation dialog before adding the contact.
     */
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
            alert.setTitle("Confirmación para Agregar");
            alert.setHeaderText("Estás a punto de agregar a " + selectedContact.getName() + " como contacto");
            alert.setContentText("¿Estás seguro de que deseas agregar a este usuario a tus contactos?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (currentUser != null) {
                    // Agrega el contacto a la lista del usuario actual
                    currentUser.addContactToList(selectedContact);
                    List<User> users = XmlReader.getUsersFromXML(filePath);
                    if (users != null) {
                        for (User user : users) {
                            if (user.getName().equals(currentUser.getName())) {
                                List<User> existingContacts = user.getContacts();
                                if (!existingContacts.contains(selectedContact)) {
                                    existingContacts.add(selectedContact);
                                    user.setContacts(existingContacts);
                                }
                                break;
                            }
                        }

                        XmlReader.saveUsersToXML(users, filePath);

                        showAlert("Contacto agregado exitosamente.");

                        // Remueve el contacto de la lista de usuarios disponibles
                        userList.remove(selectedContact);
                        usersTable.setItems(userList);

                        // Carga los contactos actualizados del usuario actual
                        loadContactsForCurrentUser();
                    }
                }
            }
        } else {
            showAlert("Por favor selecciona un usuario para agregar como contacto.");
        }
    }

    /**
     * Loads the contacts for the current user and populates the contacts table.
     */
    private void loadContactsForCurrentUser() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<User> users = XmlReader.getUsersFromXML(filePath);
            for (User user : users) {
                if (user.getName().equals(currentUser.getName())) {
                    contactList.clear();
                    contactList.addAll(user.getContacts());
                    contactsTable.setItems(contactList);
                    break;
                }
            }
        }
    }

    /**
     * Initiates a chat with the selected contact if they are mutually in each other's contact lists.
     */
    @FXML
    private void chattedWithContact() {
        try {
            User selectedContact = contactsTable.getSelectionModel().getSelectedItem();
            User currentUser = SessionManager.getInstance().getCurrentUser();

            if (selectedContact != null && currentUser != null) {
                List<User> allUsers = XmlReader.getUsersFromXML(filePath);

                User updatedSelectedContact = allUsers.stream().filter(user -> user.getName().equals(selectedContact.getName())).findFirst().orElse(null);
                User updatedCurrentUser = allUsers.stream().filter(user -> user.getName().equals(currentUser.getName())).findFirst().orElse(null);

                if (updatedSelectedContact != null && updatedCurrentUser != null) {
                    boolean currentUserHasContact = updatedCurrentUser.getContacts().contains(updatedSelectedContact);
                    boolean contactHasCurrentUser = updatedSelectedContact.getContacts().contains(updatedCurrentUser);

                    if (currentUserHasContact && contactHasCurrentUser) {
                        SessionManager.getInstance().setSelectedUser(updatedSelectedContact);
                        showAlert("Chat iniciado con " + updatedSelectedContact.getName());
                        App.setRoot("Chat");
                    } else {
                        showAlert("No puedes iniciar un chat con " + updatedSelectedContact.getName() + " porque no es tu contacto.");
                    }
                }
            } else {
                showAlert("Por favor selecciona un contacto para chatear.");
            }
        } catch (IOException e) {
            showAlert("Ocurrió un error al iniciar el chat: " + e.getMessage());
        }
    }

    /**
     * Loads the current user's profile image if available.
     */
    private void loadCurrentUserImage() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            byte[] visualData = currentUser.getProfileImage();
            if (visualData != null && visualData.length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(visualData);
                Image image = new Image(bis);
                userImage.setImage(image);
                userImage.setFitWidth(100);
                userImage.setFitHeight(100);
                applyCircularClip(userImage);
            }
        }
    }

    /**
     * Changes the current user's profile photo after confirmation.
     */
    @FXML
    private void changeUserPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto de Perfil");
        File file = fileChooser.showOpenDialog(anchorPane.getScene().getWindow());

        if (file != null) {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] imageData = new byte[(int) file.length()];
                    fis.read(imageData);
                    currentUser.setProfileImage(imageData);
                    showAlert("Foto de perfil cambiada exitosamente.");
                    loadCurrentUserImage();

                    List<User> users = XmlReader.getUsersFromXML(filePath);
                    for (User user : users) {
                        if (user.getName().equals(currentUser.getName())) {
                            user.setProfileImage(imageData);
                            break;
                        }
                    }
                    XmlReader.saveUsersToXML(users, filePath);

                    refreshContactsTable();
                } catch (IOException e) {
                    showAlert("Ocurrió un error al cargar la foto: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Refreshes the contacts table by reloading the contacts for the current user.
     */
    private void refreshContactsTable() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            List<User> users = XmlReader.getUsersFromXML(filePath);
            for (User user : users) {
                if (user.getName().equals(currentUser.getName())) {
                    contactList.clear();
                    contactList.addAll(user.getContacts());
                    contactsTable.setItems(contactList);
                    break;
                }
            }
        }
    }

    /**
     * Applies a circular clip to an ImageView.
     *
     * @param imageView the ImageView to clip.
     */
    private void applyCircularClip(ImageView imageView) {
        Circle clip = new Circle(50, 50, 50);
        imageView.setClip(clip);
    }

    /**
     * Shows an alert dialog with the given message.
     *
     * @param message the message to display in the alert.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}