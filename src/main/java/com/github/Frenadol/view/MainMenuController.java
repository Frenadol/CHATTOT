package com.github.Frenadol.view;

import com.github.Frenadol.model.User;
import com.github.Frenadol.utils.SessionManager;
import com.github.Frenadol.utils.XmlReader;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.ByteArrayInputStream;
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
    private ObservableList<User> userList = FXCollections.observableArrayList();
    @FXML
    private Button addContactButton;

    String filePath = "UsersData.xml";

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        ListUsers();
        nameUserColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        imageProfileColumn.setCellValueFactory(cellData -> {
            byte[] visualData = cellData.getValue().getProfileImagen();
            if (visualData != null && visualData.length > 0) {
                ByteArrayInputStream bis = new ByteArrayInputStream(visualData);
                Image image = new Image(bis);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(120);
                imageView.setFitHeight(100);
                return new SimpleObjectProperty<>(imageView);
            } else {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(120);
                imageView.setFitHeight(100);
                return new SimpleObjectProperty<>(imageView);
            }
        });
    }

    public void ListUsers() {
        List<User> users = XmlReader.getUsersFromXML(filePath);
        if (users.isEmpty()) {
            System.out.println("No users found in XML.");
        } else {
            System.out.println("Users loaded: " + users.size());
        }
        this.userList = FXCollections.observableArrayList(users);
        usersTable.setItems(userList);
    }

    @FXML
    public void confirmAndAddContact() {
        User selectedContact = usersTable.getSelectionModel().getSelectedItem();

        if (selectedContact != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmación de adición");
            alert.setHeaderText("Estás a punto de agregar a " + selectedContact.getName() + " como contacto");
            alert.setContentText("¿Estás seguro de que quieres agregar a este usuario a tus contactos?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Obtener el usuario en sesión
                User currentUser = SessionManager.getInstance().getCurrentUser();

                // Llamar al método para agregar contacto
                addContactToCurrentUser(currentUser, selectedContact);
            }
        } else {
            showAlert("Por favor, selecciona un usuario para agregar como contacto.");
        }
    }

    private void addContactToCurrentUser(User currentUser, User selectedContact) {
        // Verificar si el contacto ya existe
        if (!currentUser.getContacts().contains(selectedContact)) {
            currentUser.addContactToList(selectedContact);
            showAlert("Contacto agregado exitosamente.");

            UsersDAO usersDAO = new UsersDAO();
            List<User> users = usersDAO.loadUsers();

            if (users != null) {
                for (User user : users) {
                    if (user.getName().equals(currentUser.getName())) {
                        user.setContacts(currentUser.getContacts());
                        break;
                    }
                }
                // Guardar los cambios en el archivo XML
                usersDAO.saveUsers(users);
            }
        } else {
            showAlert("Este contacto ya está en tu lista.");
        }
    }
}
