package client.components;

import client.scenes.InterfaceCtrl;
import client.services.SearchBarService;
import client.services.VarService;
import com.google.inject.Inject;
import commons.Note;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Objects;

public class SearchBarCtrl {

    private final InterfaceCtrl interfaceCtrl;
    private final VarService varService;
    private final SearchBarService service;

    @FXML
    private TextField searchBar;

    @FXML
    private ListView<Note> searchBarListView;

    private int currentIndex;

    /**
     * Constructor using dependency injection
     *
     * @param interfaceCtrl the main InterfaceController
     * @param varService vars
     * @param service service
     */
    @Inject
    public SearchBarCtrl(InterfaceCtrl interfaceCtrl, VarService varService,
                         SearchBarService service) {
        this.interfaceCtrl = interfaceCtrl;
        this.varService = varService;
        this.service = service;
    }

    /**
     * COMPLETE THIS
     */
    public void initialize() {
        currentIndex = -1;
        setKeyboardShortcutsTextField();
        setKeyboardShortcuts();
        searchBarListView.setCellFactory(lc -> new ListCell<Note>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText("");
                    setStyle("");
                } else {
                    setText(note.title);
                    setOnMouseEntered(event -> setStyle("-fx-background-color: lightblue;"));
                    setOnMouseExited(event -> setStyle(""));
                    if(getIndex() == currentIndex){
                        setStyle("-fx-background-color: lightblue;");
                    }else{
                        setStyle("");
                    }
                }
            }
        });
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            searchBarAction();
            var searchResults = searchNotes();
            if (searchResults.isEmpty() || searchBar.getText().isEmpty()) {
                searchBarListView.setItems(FXCollections.observableArrayList(
                        new Note("no matching results", "no matching results")));
            } else {
                //putting the matching notes in the listview
                searchBarListView.setItems(searchResults);
                //opening the note selected after the search
            }
        });
        //Buttons functionality
        searchBarListView.getSelectionModel()
                .selectedItemProperty()
                .addListener((ob, ol, nv) -> {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Note note = searchBarListView
                                    .getSelectionModel()
                                    .getSelectedItem();
                            if (note != null) {
                                interfaceCtrl.saveAndSetCurrentNote(note);
                                searchBar.setText("");
                                deactivateSearchList();
                            }
                        }
                    });
                });
        //Keyboard press "Enter" functionality
        searchBar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                var searchedNotes = searchBarListView.getItems();
                Note selectedNote = null;
                if (!searchedNotes.isEmpty()) {
                    selectedNote = searchedNotes.getFirst();
                }
                if (selectedNote != null &&
                        !Objects.equals(selectedNote.title, "no matching results")) {
                    interfaceCtrl.setCurrentNote(selectedNote);
                    searchBar.setText("");
                    deactivateSearchList();
                }
            }
        });

        //Makes it so when you deselect the search bar it disappears
        searchBar.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                deactivateSearchList();
            } else {
                searchBarAction();
            }
        });
    }

    /**
     * Defines eventHandlers, values and content of the search bar acc
     */
    public void refresh() {
        deactivateSearchList();
    }


    /**
     * Searches for notes using the keywords in the search bar
     *
     * @return a list with all the matching notes
     */
    public FilteredList<Note> searchNotes() {
        ObservableList<Note> noteList = FXCollections
                .observableArrayList(varService.getCurrentCollectionNotes());
        // initialize the resulting list with all notes
        FilteredList<Note> filteredList = new FilteredList<>(noteList, b -> true);
        String newValue = searchBar.getText();
        filteredList.setPredicate(note -> service.isMatch(note, newValue));
        return filteredList;
    }

    /**
     * Activates the listview underneath the search bar
     */
    public void activateSearchList() {
        searchBarListView.setVisible(true);
        searchBarListView.setManaged(true);
    }

    /**
     * Deactivates the listview underneath the search bar
     */
    public void deactivateSearchList() {
        searchBarListView.setVisible(false);
        searchBarListView.setManaged(false);
    }

    /**
     * Activates the search bar list view if there is text
     * in the search bar or deactivates it otherwise
     */
    public void searchBarAction() {
        if (searchBar.getText().isEmpty())
            deactivateSearchList();
        else
            activateSearchList();
    }

    /**
     * Sets an event filter so that when the keyboard shortcuts containing
     * ESCAPE and CONTROL are used the search bar list behaves as intended
     * Otherwise it does not lose focus and the shortcuts do not work
     */
    public void setKeyboardShortcuts() {
        searchBarListView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.CONTROL) {
                event.consume();
                interfaceCtrl.focusOnMainAnchorPane();
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                interfaceCtrl.focusOnSearchBar();
            }
            if(event.getCode() == KeyCode.DOWN){
                event.consume();
                exploreHits(1);
            }
        });
    }

    /**
     * Handles UP DOWN and ENTER key presses when the text field is focused on to enable
     * scrolling through the options that are presented
     */
    public void setKeyboardShortcutsTextField(){
        searchBar.addEventFilter(KeyEvent.KEY_PRESSED, event->{
            if(event.getCode() == KeyCode.DOWN){
                event.consume();
                exploreHits(1);
            }
            if(event.getCode() == KeyCode.UP){
                event.consume();
                exploreHits(-1);
            }
            if(event.getCode() == KeyCode.ENTER){
                event.consume();
                searchBarListView.getSelectionModel().select(currentIndex);
                currentIndex = -1;
            }
        });
    }

    /**
     * Moves the currently selected option from the listview by displacement
     * @param displacement - int, by how much to move the currently selected listview item
     */
    public void exploreHits(int displacement){
        int index = currentIndex + displacement;
        if(index >= 0 && index < searchBarListView.getItems().size()){
            currentIndex = index;
            searchBarListView.refresh();
        }
    }

    /**
     * Moves the focus to the search bar
     */
    public void focusOnSearchBar() {
        currentIndex = -1;
        searchBar.requestFocus();
    }

    /**
     * Moves the focus to the search bar list view
     */
    public void focusOnSearchBarListView() {
        searchBarListView.requestFocus();
    }

    /**
     * Checks if the focus is on the search bar or not
     *
     * @return true if it is, false otherwise
     */
    public boolean searchBarHasFocus() {
        return searchBar.isFocusWithin();
    }

    /**
     * Set the text field of the search bar
     *
     * @param string String that search bar is set to
     */
    public void setSearchBar(String string) {
        this.searchBar.setText(string);
    }

    /**
     * THIS METHOD HAS NO USAGES
     *
     * @param searchBarListView the list view for the search bar
     */
    public void setSearchBarListView(ListView<Note> searchBarListView) {
        this.searchBarListView = searchBarListView;
    }

    /**
     * Refreshes the UI elements so that they show the newly selected language
     */
    public void refreshUI() {
        searchBar.setPromptText(interfaceCtrl.myGetBundle().getString("searchBar"));
    }
}
