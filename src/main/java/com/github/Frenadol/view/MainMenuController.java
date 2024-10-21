package com.github.Frenadol.view;

import com.github.Frenadol.model.User;
import com.github.Frenadol.utils.XmlReader;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.xml.sax.XMLReader;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainMenuController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ImageView userImage;
    @FXML
    private TableView usersTable;
    @FXML
    private TableColumn<User,String> nameUserColumn;
    @FXML
    private TableColumn<User, ImageView> imageProfileColumn;
    private ObservableList<User> userList = FXCollections.observableArrayList();


    public void initialize(URL location, ResourceBundle resources) {
        if (usersTable.getItems().isEmpty()) {
            nameUserColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            imageProfileColumn.setCellValueFactory(new PropertyValueFactory<>("imageView"));
            this.userList = FXCollections.observableArrayList(usersTable.getItems());
            String filePath = "UsersData.xml";
            List<User> users = XmlReader.getUsersFromXML(filePath);

            usersTable.getItems().addAll(users);
        }
    }

}
