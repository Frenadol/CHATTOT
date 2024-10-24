package com.github.Frenadol.view;

import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MessageListCell extends ListCell<String> {
    private HBox content;
    private Label messageLabel;

    public MessageListCell() {
        super();
        messageLabel = new Label();
        content = new HBox(messageLabel);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            if (item.startsWith("To ")) {
                content.setAlignment(Pos.CENTER_RIGHT);
                messageLabel.setStyle("-fx-background-color: lightblue; -fx-padding: 5px;");
            } else if (item.startsWith("From ")) {
                content.setAlignment(Pos.CENTER_LEFT);
                messageLabel.setStyle("-fx-background-color: lightgreen; -fx-padding: 5px;");
            }
            messageLabel.setText(item);
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }
}
