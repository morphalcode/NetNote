package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class NotificationCtrl {

    @FXML
    private Label notificationLabel;

    @FXML
    private StackPane notificationStackPane;

    /**
     * Sets the message of the notification
     *
     * @param message the message
     */
    public void setLabelMessage(String message) {
        notificationLabel.setText(message);
    }

    /**
     * Sets the background colour of the notification
     * @param color the background colour
     */
    public void setBackgroundColor(String color) {
        if (color.equals("red")) {
            notificationStackPane.setStyle("-fx-background-color: #EBA0A0;");
        } else if (color.equals("green")) {
            notificationStackPane.setStyle("-fx-background-color: #A6EFA4;");
        }
    }
}
