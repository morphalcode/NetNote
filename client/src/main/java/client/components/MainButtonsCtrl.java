package client.components;

import client.services.MainButtonsService;
import client.services.VarService;
import client.Main;
import client.scenes.DeletionCheckInterfaceCtrl;
import client.scenes.InterfaceCtrl;
import com.google.inject.Inject;
import commons.Note;
import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;


public class MainButtonsCtrl {

    private final InterfaceCtrl interfaceCtrl;
    private final VarService varService;
    private final MainButtonsService service;

    @FXML
    private Button addNoteButton;

    @FXML
    private Button deleteNoteButton;

    @FXML
    private Button refreshButton;

    /**
     * Constructor using dependency injection
     *
     * @param interfaceCtrl the main InterfaceController
     * @param service a
     * @param varService s
     *
     */
    @Inject
    public MainButtonsCtrl(InterfaceCtrl interfaceCtrl,
                           VarService varService, MainButtonsService service) {
        this.interfaceCtrl = interfaceCtrl;
        this.varService = varService;
        this.service = service;
    }

    /**
     * Method called when the "Add a new note" button is pressed.
     * Creates an empty note & sends api request through server
     *
     * @return Note, new Note
     */
    public Note addButtonClick() {
        try{
            Note note = service.addButtonClick();
            interfaceCtrl.clearSelectedNote();
            interfaceCtrl.refresh();
            interfaceCtrl.showNotification(interfaceCtrl.myGetBundle()
                    .getString("addNoteNotification") + note.title + '"', 2, "green");
            return note;
        } catch (Exception e) {
            interfaceCtrl.showNotification(interfaceCtrl.myGetBundle()
                    .getString("noCollectionsConfiguredNotification"), 3, "red");
            interfaceCtrl.refresh();
            return null;
        }
    }

    /**
     * This method is called when the note remove button is pressed.
     */
    @PostConstruct
    public void removeNoteButtonClick() {
        if (varService.getCurrentNote() != null) {
            Pair<DeletionCheckInterfaceCtrl, Parent> pair = Main.FXML
                    .load(DeletionCheckInterfaceCtrl.class, interfaceCtrl.myGetBundle(),
                            "client", "scenes", "DeletionCheckInterface.fxml");
            Parent root = pair.getValue();
            DeletionCheckInterfaceCtrl controller = pair.getKey();
            Stage stage = new Stage();
            controller.initialize(stage);
            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.setTitle("DeletionCheckInterface");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        }
    }

    /**
     * This method is called when the refresh button is pressed.
     */
    public void refreshButton() {
        interfaceCtrl.saveAndRefresh();
        interfaceCtrl.showNotification(interfaceCtrl
                .myGetBundle()
                .getString("refreshNotification"), 2, "green");
    }


}
