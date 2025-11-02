package client.components;

import client.Exceptions.PuttedNoteNotValidException;
import client.scenes.InterfaceCtrl;
import client.services.CurrentNoteAreaService;
import client.services.VarService;
import com.google.inject.Inject;
import commons.Collection;
import commons.Note;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import org.codefx.libfx.control.webview.WebViewHyperlinkListener;
import org.codefx.libfx.control.webview.WebViews;
import org.commonmark.Extension;

import javax.swing.event.HyperlinkEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class CurrentNoteAreaCtrl {
    private final InterfaceCtrl interfaceCtrl;
    private final VarService varService;
    private final CurrentNoteAreaService service;

    private List<Extension> extensions;

    private Timeline timeline;

    @FXML
    private TextField titleBar;

    @FXML
    private TextArea noteContentTextArea;

    @FXML
    private WebView markdownRenderView;

    @FXML
    private ToggleButton markDownToggleButton;

    @FXML
    private SplitMenuButton collectionSplitMenuButton;


    /**
     * Constructor with DI. Used automatically by framework
     * @param interfaceCtrl  main interface
     * @param varService vars
     * @param service ser
     */
    @Inject
    public CurrentNoteAreaCtrl(InterfaceCtrl interfaceCtrl,
                               VarService varService, CurrentNoteAreaService service) {
        this.interfaceCtrl = interfaceCtrl;
        this.varService = varService;
        this.service = service;
    }

    /**
     * Custom initialize method. Used automatically by javafx.
     */
    public void initialize() {
        setKeyboardShortcuts();
        hidePreview();
        timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            String newTitle = titleBar.getText();
            String newContent = noteContentTextArea.getText();
            if (!service.isCurrentNoteChanged(newTitle, newContent, varService.getCurrentNote())) {
                timeline.stop();
            } else {
                putNote();
            }
            refreshPreview();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        titleBar.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!timeline.getStatus().equals(Timeline.Status.RUNNING)) {
                    timeline.play(); // Start the timeline if not running
                }
            }
        });
        titleBar.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused && wasFocused) {
                String newTitle = titleBar.getText();
                if (service.isTitleChanged(newTitle, varService.getCurrentNote())) {
                    putNote();
                }
                timeline.stop();
            }
            refreshPreview();
        });

        noteContentTextArea.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (!timeline.getStatus().equals(Timeline.Status.RUNNING)) {
                    timeline.play(); // Start the timeline if not running
                }
                refreshPreview();
            }
        });

        noteContentTextArea.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused && wasFocused) {
                String newContent = noteContentTextArea.getText();
                if (service.isContentChanged(newContent, varService.getCurrentNote())) {
                    putNote();
                }
                timeline.stop();
            }
        });
        markDownToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                showPreview(); //Show when checked
            } else {
                hidePreview(); //Hide when unchecked
            }
        });
        WebViewHyperlinkListener switchNoteListener = event -> {
            putNote();
            if (event.getDescription().startsWith("#")) {
                List<Note> currentNotes = varService.getCurrentCollectionNotes();
                List<String> tags = new ArrayList<>();
                tags.add(event.getDescription().substring(1, event.getDescription().length()));
                boolean isPartialMatch = false;
                List<Note> titleList = service.prepareFilteredNotes(currentNotes, tags, isPartialMatch);
                interfaceCtrl.setTitleList(titleList);
            } else {
                Note selectedNote = varService.getNoteInCurrentCollectionByTitle
                        (event.getDescription());
                if (selectedNote != null) {
                    interfaceCtrl.selectNote(selectedNote);
                    interfaceCtrl.refresh();
                }
            }
            return false;
        };
        WebViews.addHyperlinkListener(markdownRenderView,
                switchNoteListener, HyperlinkEvent.EventType.ACTIVATED);

        extensions = service.buildMarkdownExtensions();
    }

    /**
     * Sets an event filter so that when the keyboard shortcuts containing
     * ESCAPE and CONTROL are used the collection split menu button behaves as intended
     * Otherwise it does not lose focus and the shortcuts do not work
     */
    public void setKeyboardShortcuts() {
        collectionSplitMenuButton.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
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

    public void focusOnCollectionSplitMenuButton() {
        collectionSplitMenuButton.requestFocus();
    }

    public void putNote() {
        String oldTitle = "";
        try {
            Note currentNote = varService.getCurrentNote();
            if (currentNote != null) {
                oldTitle = currentNote.title;
                String newTitle = titleBar.getText();
                if (newTitle.isEmpty() || newTitle.isBlank()) {
                    return;
                }
                if (currentNote.collection.notes.stream().anyMatch(x -> x.title.equals(newTitle)
                        && x.id != currentNote.id)) {
                    handlePuttedNoteNotValid(oldTitle);
                    return;
                }
                String newContent = noteContentTextArea.getText();
                service.putNote(newTitle, newContent, currentNote);
                int updates = service.updateReferences(oldTitle, newTitle, varService.getCurrentCollectionNotes(),true);
                if (updates > 0) {
                    String updatesString = updates + "";
                    interfaceCtrl.showNotification(updatesString
                            + ' '
                            + interfaceCtrl
                            .myGetBundle()
                            .getString("referencesUpdatedNotification"), 2, "green");
                }
            }
        } catch (PuttedNoteNotValidException e) {
            handlePuttedNoteNotValid(oldTitle);
        }
    }

    public void handlePuttedNoteNotValid(String oldTitle) {
        interfaceCtrl.showNotification(interfaceCtrl
                .myGetBundle()
                .getString("noteTitleUniqueServerNotification"), 2, "red");
        varService.getCurrentNote().title = oldTitle;
        titleBar.setText(oldTitle);
        interfaceCtrl.refresh();
    }

    /**
     * Sets the text fields according to the content of the currentNote
     * field inside the interfaceCtrl
     */
    public void refresh() {
        if (!service.isNotesCurrentNoteValid(varService.getNotesCurrentNote(),
                varService.getCurrentNote())) {
//            interfaceCtrl.showNotification(interfaceCtrl
//                    .myGetBundle().getString("noteNotLoadedOnThisClient"), 5, "red");
            varService.setCurrentNote(varService.getNotesCurrentNote());
        }
        if (varService.getCurrentNote() != null) {
            titleBar.setText(varService.getCurrentNote().title);
            noteContentTextArea.setText(varService.getCurrentNote().content);
            refreshPreview();
        } else {
            titleBar.setText("");
            noteContentTextArea.setText("");
        }
        refreshPreview();
        refreshCollectionSplitMenuButton();
    }

    public void refreshCollectionSplitMenuButton() {
        collectionSplitMenuButton.setText("");
        if (varService.isVarsEmpty()) {
            return;
        }
        List<Collection> collections = new ArrayList<>();
        if(varService.getCurrentCollection().id == 0
                && varService.getCurrentCollection().name.equals("All Notes") ) {
            collections = service.prepareCollectionList(
                    varService.getCollectionsByServer(varService.getCurrentNote()
                            .collection.getServer()),
                    varService.getCurrentNote());
        } else {
            collections = service.prepareCollectionList(
                    varService.getCollectionsByServer(varService.getCurrentCollection().getServer()),
                    varService.getCurrentNote());
        }
        if(!collections.isEmpty()){
            collectionSplitMenuButton.setText(collections.getFirst().name);
            collectionSplitMenuButton.getItems().clear();
            for (Collection collection : collections) {
                if (collection != null) {
                    MenuItem menuItem = getMenuItem(collection);
                    collectionSplitMenuButton.getItems().add(menuItem);
                }
            }
        }
        collectionSplitMenuButton.setText(varService.getCurrentNote() != null ?
                varService.getCurrentNote().collection.name : "");
    }

    private MenuItem getMenuItem(Collection collection) {
        MenuItem menuItem = new MenuItem(collection.name);
        menuItem.setOnAction(event -> {
            if (!collection.getServer().
                    equals(varService.getCurrentNote().collection.getServer())) {
                System.out.println("ERROR, USER SHOULD NOT HAVE OPTION TO MOVE NOTE " +
                        "TO COLLECTION ON DIFFERENT SERVER");
                interfaceCtrl
                        .showNotification(interfaceCtrl.myGetBundle().getString(
                                        "cannotMoveNotesToDifferentServersNotification"),
                                2, "red");
                return;
            }
            varService.getCurrentNote().setCollectionId(collection.id);
            varService.getCurrentNote().collection = collection;
            putNote();
            varService.setCurrentCollection(collection);
            interfaceCtrl.refresh();
        });
        return menuItem;
    }


    /**
     * Displays HTML in the preview
     *
     * @param html html string
     */
    public void displayHTML(String html) throws URISyntaxException, IOException {
        if(service.cssExists()){
            markdownRenderView.getEngine().setUserStyleSheetLocation(getClass().getResource("/client/CSS/UserStyle.css").toString());
        }
        markdownRenderView.getEngine().loadContent(html);
    }

    /**
     * Renders current note content and displays it in preview (Slight delay to fix preview refreshing with references)
     */
    public void refreshPreview() {
        new Timeline(new KeyFrame(Duration.millis(10), e -> {
            try {
                displayHTML(service.stylizeHTML(service.convertToHTML(
                        service.renderReferences(service.renderTags(noteContentTextArea.getText()), varService),
                        extensions)));
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        })).play();
    }

    /**
     * COMPLETE THIS
     */
    public void showPreview() {
        markdownRenderView.setVisible(true);
        noteContentTextArea.setPrefWidth(429.0);
    }

    /**
     * COMPLETE THIS
     */
    public void hidePreview() {
        markdownRenderView.setVisible(false);
        noteContentTextArea.setPrefWidth(859.0);
    }


    /**
     * Moves the focus to the title bar
     */
    public void focusOnTitleBar() {
        titleBar.requestFocus();
    }

    /**
     * Moves the focus to the content area
     */
    public void focusOnContentArea() {
        noteContentTextArea.requestFocus();
    }


    public void tagsReveal() {
        noteContentTextArea.setPrefHeight(580);
        noteContentTextArea.setLayoutY(198);
        markdownRenderView.setPrefHeight(580);
        markdownRenderView.setLayoutY(198);
        titleBar.setLayoutY(164);
        collectionSplitMenuButton.setLayoutY(164);
        markDownToggleButton.setLayoutY(164);
    }

    public void tagsHide() {
        noteContentTextArea.setPrefHeight(630);
        noteContentTextArea.setLayoutY(148);
        markdownRenderView.setPrefHeight(630);
        markdownRenderView.setLayoutY(148);
        titleBar.setLayoutY(114);
        collectionSplitMenuButton.setLayoutY(114);
        markDownToggleButton.setLayoutY(114);
    }

}
