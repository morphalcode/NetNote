package client.scenes;

import commons.CollectionInfo;
import client.Config;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CollectionDeletionInterfaceCtrl {
    private final Config config;

    private InterfaceCtrl interfaceCtrl;
    private CollectionInterfaceCtrl controller;
    private Stage stage;

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;

    @FXML
    private AnchorPane popUpPane;

    @FXML
    private Label promptPane;

    /**
     * @param config config
     */
    @Inject
    public CollectionDeletionInterfaceCtrl(Config config) {
        this.config = config;
    }

    /**
     * Initializes the CollectionDeletionInterfaceCtrl
     * by setting the stage and giving the interfaceCtrl and
     * CollectionInterfaceCtrl an instance of the class
     *
     * @param interfaceCtrl - InterfaceCtrl,
     *                      instance of the InterfaceCtrl to use for deleting the collection
     * @param controller    - CollectionInterfaceCtrl,
     *                      instance of the CollectionInterfaceCtrl for accessing
     *                      the selected collection
     * @param stage         - Stage, CollectionDeletionInterface.fxml stage
     */
    public void initialize(InterfaceCtrl interfaceCtrl,
                           CollectionInterfaceCtrl controller, Stage stage) {
        this.interfaceCtrl = interfaceCtrl;
        this.controller = controller;
        this.stage = stage;
        promptPane.setText(interfaceCtrl.myGetBundle().getString("collectionDeletionAlert1") +
                controller.getSelectedCollection().name +
                interfaceCtrl.myGetBundle().getString("collectionDeletionAlert2"));
    }

    /**
     * Closes the stage if the deletion is rejected
     * by pressing the no button
     */
    public void clickNo() {
        stage.close();
    }

    /**
     * Deletes the selected collection and closes the
     * stage if the yes button is pressed
     */
    public void clickYes() {
        String message = interfaceCtrl.myGetBundle().getString("collectionRemovedNotification1")
                + controller.getSelectedCollection().name
                + interfaceCtrl.myGetBundle().getString("collectionRemovedNotification2");
        config.removeCollection(new CollectionInfo(controller.getSelectedCollection().id,
                controller.getSelectedCollection().getServer()));
        controller.refresh();
        interfaceCtrl.showNotification(message, 2, "green");
        stage.close();
    }

}
