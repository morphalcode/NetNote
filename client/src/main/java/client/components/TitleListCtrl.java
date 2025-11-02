package client.components;

import client.scenes.InterfaceCtrl;
import client.services.TitleListService;
import client.services.VarService;
import com.google.inject.Inject;
import commons.Note;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;

public class TitleListCtrl {

    private final InterfaceCtrl interfaceCtrl;
    private final VarService varService;
    private final TitleListService service;


    @FXML
    private ListView<Note> noteTitlesListView;

    /**
     * Constructor using dependency injection
     *
     * @param interfaceCtrl the main InterfaceController
     * @param varService  vars
     * @param service service
     */
    @Inject
    public TitleListCtrl(InterfaceCtrl interfaceCtrl, VarService varService,
                         TitleListService service) {
        this.interfaceCtrl = interfaceCtrl;
        this.varService = varService;
        this.service = service;
    }

   public List<String> remainingTags(){
        return varService.tagUpdater(noteTitlesListView.getItems().stream().toList());
   }

    /**
     * Initializes the title list by:
     * - using the setKeyboardShortcuts method
     */
    public void initialize() {
        setKeyboardShortcuts();
        noteTitlesListView.getSelectionModel()
                .selectedItemProperty()
                .addListener(new ChangeListener<Note>() {
                    @Override
                    public void changed(ObservableValue<? extends Note> observableValue,
                                        Note s, Note t1) {
                        Note selectedNote = noteTitlesListView  // Gets the index of the note the
                                .getSelectionModel()            // user selected in the client
                                .getSelectedItem();
                        if (selectedNote != null) {
                            interfaceCtrl.saveAndSetCurrentNote(selectedNote);
                        }
                    }
                });
        noteTitlesListView.setCellFactory(lc -> new ListCell<Note>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText("");
                } else {
                    setText(note.title);
                }
                if (note != null && varService.isCurrentNote(note)) {
                    //noteTitlesListView.getSelectionModel().select(note);
                }
            }
        });

    }

    public void refresh() {
        List<Note> currentNotes = varService.getCurrentCollectionNotes();
        List<String> tags = interfaceCtrl.getFilters();
        boolean isPartialMatch = interfaceCtrl.partialMatch();
        List<Note> filteredNotes = service.prepareFilteredNotes(currentNotes, tags, isPartialMatch);
        noteTitlesListView.setItems(FXCollections.observableArrayList(filteredNotes));
    }


    public void selectNote(Note note) {
        noteTitlesListView.getSelectionModel().select(note);
    }

    public void setTitleList(List<Note> titleList) {
        noteTitlesListView.setItems(FXCollections.observableArrayList(titleList));
    }

    /**
     * Sets an event filter so that when the keyboard shortcuts containing
     * ESCAPE and CONTROL are used the title list behaves as intended
     * Otherwise it does not lose focus and the shortcuts do not work
     */
    public void setKeyboardShortcuts() {
        noteTitlesListView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                event.consume();
                interfaceCtrl.focusOnMainAnchorPane();
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                interfaceCtrl.focusOnSearchBar();
            }
        });
    }

    /**
     * Moves the focus on the title list
     */
    public void focusOnTitleList() {
        noteTitlesListView.requestFocus();
    }

    public void tagsReveal() {
        noteTitlesListView.setPrefHeight(615);
        noteTitlesListView.setLayoutY(164);
    }

    public void tagsHide() {
        noteTitlesListView.setPrefHeight(665);
        noteTitlesListView.setLayoutY(114);
    }

    public void clearSelectedNote() {
        noteTitlesListView.getSelectionModel().clearSelection();
    }
}
