package client.scenes;

import client.services.VarService;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Note;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DeletionCheckInterfaceCtrl {

    private final InterfaceCtrl interfaceCtrl;
    private final ServerUtils server;
    private final VarService varService;

    private Note note;
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
     * Constructor using DI
     * @param interfaceCtrl
     * @param varService
     * @param server
     */
    @Inject
    public DeletionCheckInterfaceCtrl(InterfaceCtrl interfaceCtrl, ServerUtils server,
                                      VarService varService) {
        this.server = server;
        this.interfaceCtrl = interfaceCtrl;
        this.varService = varService;
        this.note = null;
        this.stage = null;
    }

    /**
     * Initializes the deletion check interface by:
     * - setting the interfaceCtrl, server, stage and note to the appropriate values
     * - setting the appropriate text to the label
     *
     * @param stage         the stage
     */
    public void initialize(Stage stage) {
        this.stage = stage;
        this.note = varService.getCurrentNote();
        promptPane.setText(interfaceCtrl.myGetBundle().getString("noteDeletionAlert")
                + note.title + "\"?");
    }

    /**
     * Sets this note to the specified note
     *
     * @param note the specified note
     */
    public void noteSetter(Note note) {
        this.note = note;
    }

    /**
     * Sets this stage to the specified stage
     *
     * @param stage the specified stage
     */
    public void stageSetter(Stage stage) {
        this.stage = stage;
    }

    /**
     * Closes the stage
     */
    public void clickNo() {
        stage.close();
    }

    /**
     * Deletes the note, goes to the last note then closes the stage
     */
    public void clickYes() {
        //remove note
        String message = interfaceCtrl.myGetBundle().getString("noteRemovedNotification1")
                + note.title
                + interfaceCtrl.myGetBundle().getString("noteRemovedNotification2");
        server.deleteNote(note);
        interfaceCtrl.refresh();
        varService.setCurrentNoteToLast();
        stage.close();
        interfaceCtrl.showNotification(message, 2, "green");
    }

}
