package client.scenes;

import client.Exceptions.ConfigFileCorruptedException;
import client.Main;
import client.services.InterfaceService;
import client.services.VarService;
import client.webSocket.WebSocketManager;
import client.components.*;
import client.utils.LanguageUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.Collection;
import commons.Note;
import commons.UpdateMessage;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;


@Singleton
public class InterfaceCtrl {

    private final InterfaceService interfaceService;
    private final VarService varService;
    private final WebSocketManager webSocketManager;

    private ResourceBundle bundle;
    private Locale oldLocale;

    @FXML
    private CollectionsMenuCtrl collectionsMenuController;

    @FXML
    private CurrentNoteAreaCtrl currentNoteAreaController;

    @FXML
    private SearchBarCtrl searchBarController;

    @FXML
    private TitleListCtrl titleListController;

    @FXML
    private MainButtonsCtrl mainButtonsController;

    @FXML
    private FilesSectionCtrl filesSectionController;

    @FXML
    private Label netNoteLogo;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private SplitMenuButton languageMenu;

    @FXML
    private MenuButton filterButton;

    @FXML
    private ToggleButton tagsButton;

    @FXML
    private Button clearAllButton;

    @FXML
    private ToggleButton matchButton;

    @FXML
    private Label tagsDisplayLabel;


    @Inject
    public InterfaceCtrl(InterfaceService interfaceService, VarService varService,
                         WebSocketManager webSocketManager) {
        this.varService = varService;
        this.interfaceService = interfaceService;
        this.webSocketManager = webSocketManager;
    }

    public TitleListCtrl getTitleListController() {
        return titleListController;
    }

    /**
     * Returns the resource bundle
     *
     * @return the resource bundle
     */
    public ResourceBundle myGetBundle() {

        if(bundle == null) {
            Locale newLocale = Locale.forLanguageTag(LanguageUtils.getSavedLanguage()
                    .replace('_', '-'));
            return ResourceBundle.getBundle("client.properties.text", newLocale);
        }
        return bundle;
    }

    public void tagsReveal() {
        currentNoteAreaController.tagsReveal();
        titleListController.tagsReveal();
        clearAllButton.setVisible(true);
        matchButton.setVisible(true);
        filterButton.setVisible(true);
        tagsDisplayLabel.setVisible(true);
    }

    public void tagsHide() {
        currentNoteAreaController.tagsHide();
        titleListController.tagsHide();
        clearAllButton.setVisible(false);
        matchButton.setVisible(false);
        filterButton.setVisible(false);
        tagsDisplayLabel.setVisible(false);
    }

    /**
     * Initialize method
     */
    public void initialize() {
        try{
            webSocketManager.setInterfaceCtrl(this);
            webSocketManager.setVarService(varService);
            interfaceService.initialize();
        } catch (ConfigFileCorruptedException e){
            Platform.runLater(() -> {
                showNotification(bundle.getString("configCorruptedNewCreatedNotification"), 2, "red");
            });
        }
        varService.refresh();
        Locale newLocale = Locale.forLanguageTag(LanguageUtils.getSavedLanguage()
                .replace('_', '-'));
        bundle = ResourceBundle.getBundle("client.properties.text", newLocale);
        setUpLanguageMenu();
        //Components are refreshed after all variables have been refreshed
        tagsHide();
        matchButton.setText(bundle.getString("matchButtonComplete"));
        matchButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                matchButton.setText(bundle.getString("matchButtonPartial"));
            } else {
                matchButton.setText(bundle.getString("matchButtonComplete"));
            }
        });
        tagsButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                tagsReveal();
            } else {
                tagsHide();
            }
        });
        Platform.runLater(() -> {
            Scene scene = mainAnchorPane.getScene();
            setKeyboardShortcuts(scene);
            mainAnchorPane.requestFocus();
            refresh();
        });
    }

    /**
     * Sets up the language menu
     */
    public void setUpLanguageMenu() {
        MenuItem englishItem = new MenuItem("",
                (createFlagImageView("/client/images/flags/US.png")));
        MenuItem romanianItem = new MenuItem("",
                (createFlagImageView("/client/images/flags/RO.png")));
        MenuItem dutchItem = new MenuItem("",
                (createFlagImageView("/client/images/flags/NL.png")));
        MenuItem italianItem = new MenuItem("",
                (createFlagImageView("/client/images/flags/IT.png")));
        MenuItem spanishItem = new MenuItem("",
                (createFlagImageView("/client/images/flags/ES.png")));
        MenuItem germanItem = new MenuItem("",
                (createFlagImageView("/client/images/flags/DE.png")));
        MenuItem portugueseItem = new MenuItem("",
                (createFlagImageView("/client/images/flags/PT.png")));
        MenuItem frenchItem = new MenuItem("",
                (createFlagImageView("/client/images/flags/FR.png")));

        englishItem.setOnAction(e -> {
            changeLanguage("en_US");
        });
        romanianItem.setOnAction(e -> {
            changeLanguage("ro_RO");
        });
        dutchItem.setOnAction(e -> {
            changeLanguage("nl_NL");
        });
        italianItem.setOnAction(e -> {
            changeLanguage("it_IT");
        });
        spanishItem.setOnAction(e -> {
            changeLanguage("es_ES");
        });
        germanItem.setOnAction(e -> {
            changeLanguage("de_DE");
        });
        portugueseItem.setOnAction(e -> {
            changeLanguage("pt_PT");
        });
        frenchItem.setOnAction(e -> {
            changeLanguage("fr_FR");
        });

        languageMenu.getItems().addAll(englishItem, romanianItem, dutchItem, italianItem,
                spanishItem, germanItem, portugueseItem, frenchItem);

        switch (LanguageUtils.getSavedLanguage()) {
            case "en_US" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/US.png"));
            case "ro_RO" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/RO.png"));
            case "nl_NL" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/NL.png"));
            case "it_IT" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/IT.png"));
            case "es_ES" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/ES.png"));
            case "de_DE" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/DE.png"));
            case "pt_PT" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/PT.png"));
            case "fr_FR" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/FR.png"));
        }
    }

    /**
     * Creates an image view with the desired properties from the given image
     *
     * @param imagePath path to the image
     * @return the image view created
     */
    public ImageView createFlagImageView(String imagePath) {
        ImageView imageView = new ImageView(new Image(imagePath));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * Changes the language of the app
     *
     * @param languageCode the code of the new language
     */
    public void changeLanguage(String languageCode) {
        LanguageUtils.saveLanguage(languageCode);
        Locale newLocale = Locale.forLanguageTag(languageCode.replace('_', '-'));
        bundle = ResourceBundle.getBundle("client.properties.text", newLocale);
        switch (languageCode) {
            case "en_US" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/US.png"));
            case "ro_RO" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/RO.png"));
            case "nl_NL" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/NL.png"));
            case "it_IT" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/IT.png"));
            case "es_ES" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/ES.png"));
            case "de_DE" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/DE.png"));
            case "pt_PT" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/PT.png"));
            case "fr_FR" -> languageMenu
                    .setGraphic(createFlagImageView("/client/images/flags/FR.png"));
        }
        refreshUI();
    }

    /**
     * Refreshes the UI elements so that they show the newly selected language
     */
    public void refreshUI() {
        try{
            collectionsMenuController.refreshUI();
            searchBarController.refreshUI();
            filesSectionController.refreshUI();
            tagsButton.setText(bundle.getString("tagsButton"));
            clearAllButton.setText(bundle.getString("clearAllButton"));
            filterButton.setText(bundle.getString("filterButton"));
            if (matchButton.getText().equals(
                    ResourceBundle.getBundle("client.properties.text", oldLocale)
                            .getString("matchButtonComplete"))) {
                matchButton.setText(bundle.getString("matchButtonComplete"));
            } else if (matchButton.getText().equals(
                    ResourceBundle.getBundle("client.properties.text", oldLocale)
                            .getString("matchButtonPartial"))) {
                matchButton.setText(bundle.getString("matchButtonPartial"));
            }
        } catch (Exception e){

        }
    }

    /**
     * Saves current note and refreshes
     */
    public void saveAndRefresh() {
        saveCurrentNote();
        refresh();
    }

    /**
     * Refreshes the interface of both the notes search bar and the notes "sidebar"
     * to match the state of the database when this method is called.
     */
    public void refresh() {
        varService.refresh();
        interfaceService.refresh();
        refreshComponents();
    }

    /**
     * Refreshes the interface components
     */
    public void refreshComponents() {
        //The order of refreshes matters! DO NOT CHANGE WITH NO REASON & EXTENSIVE TESTING
        currentNoteAreaController.refresh();
        titleListController.refresh();
        collectionsMenuController.refresh();
        collectionsMenuController.refresh();
        searchBarController.refresh();
        titleListController.refresh();
        if (filesSectionController != null){
            filesSectionController.refresh();
        }
        refreshTagSelector();
        filterButton.onMousePressedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                varService.tagUpdater(varService.getAllNotes());
                refreshTagSelector();
            }
        });
    }

    /**
     * Refreshes the tag filet button
     */
    public void refreshTagSelector(){
        filterMenuUpdate();
        for(MenuItem item:filterButton.getItems()){
            if(!item.getText().equals("Clear All") && !item.getText().equals("Partial Match")){
                item.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent actionEvent) {
                        varService.tagUpdater(varService.getAllNotes());
                        titleListController.refresh();
                    }
                });
            }
        }
    }

    /**
     * Checks if partial match is enabled in the filter menu
     *
     * @return - boolean, whether partial match is enabled
     */
    public boolean partialMatch() {
        if (filterButton.getItems().isEmpty()) {
            return false;
        }
        return matchButton.isSelected();
    }

    /**
     * Returns a list of all currently applied filters
     *
     * @return - List<String>, list of currently applied filters
     */
    public List<String> getFilters() {
        return interfaceService.getFilters(filterButton.getItems());
    }


    /**
     * Updates the list of tags seen in the filter menu
     */
    public void filterMenuUpdate() {
        filterButton.getItems().setAll(interfaceService.generateFilterMenuItems
                (titleListController.remainingTags(), filterButton.getItems()));
    }

    /**
     * Deselects all currently selected tags in the filter menu
     */
    public void deselectAllTags() {
        for (MenuItem item : filterButton.getItems()) {
            ((CheckMenuItem) item).setSelected(false);
        }
        refreshComponents();
    }

    public void setTitleList(List<Note> titleList) {
        titleListController.setTitleList(titleList);
    }


    public void selectNote(Note note) {
        titleListController.selectNote(note);
    }


    public void saveAndSetCurrentNote(Note note) {
        //saveCurrentNote();
        setCurrentNote(note);
    }

    public void saveCurrentNote() {
        currentNoteAreaController.putNote();
    }

    public void setCurrentNote(Note note) {
        if (note != null) {
            boolean shouldSwitch = interfaceService.
                    isNoteAndCurrentAll(note, varService.getCurrentCollection());
            if (shouldSwitch) {
                Collection newCurrentCollection = varService.getCollectionByIdAndServer
                        (note.getCollectionId(), note.collection.getServer());
                collectionsMenuController.setCurrentCollection(newCurrentCollection);
            }
            varService.setCurrentNote(note);
            if (filesSectionController != null){
                filesSectionController.refresh();
            }
            currentNoteAreaController.refresh();
        }
    }

    /**
     * Shows the user a notification
     *
     * @param message  the message of the notification
     * @param duration the duration of the notification
     * @param colour   the background colour of the notification
     */
    public void showNotification(String message, int duration, String colour) {
        Pair<NotificationCtrl, Parent> pair = Main.FXML.load(NotificationCtrl.class,
                "client", "scenes", "Notification.fxml");
        Parent root = pair.getValue();
        NotificationCtrl notificationCtrl = pair.getKey();
        Stage notificationStage = new Stage();
        Scene scene = new Scene(root);
        notificationStage.setScene(scene);
        notificationStage.initStyle(StageStyle.UNDECORATED);
        notificationStage.setResizable(false);


        double x = 0;
        double y = 0;

        if (mainAnchorPane != null && mainAnchorPane.getScene() != null) {
            Stage primaryStage = (Stage) mainAnchorPane.getScene().getWindow();
            double windowX = primaryStage.getX();
            double windowY = primaryStage.getY();
            double windowWidth = primaryStage.getWidth();
            double windowHeight = primaryStage.getHeight();

            x = windowX + windowWidth - 385;
            y = windowY + windowHeight - 130;

        }
        notificationStage.setX(x);
        notificationStage.setY(y);
        notificationStage.show();
        notificationCtrl.setLabelMessage(message);
        notificationCtrl.setBackgroundColor(colour);
        PauseTransition delay = new PauseTransition(Duration.seconds(duration));
        delay.setOnFinished(event -> notificationStage.close());
        delay.play();
    }

    public void webSocketUpdateNote(UpdateMessage message, String server) {
        Platform.runLater(() -> {
            if (varService.isNoteChangedByAnotherClient(message, server)) {
                showNotification(bundle
                        .getString("noteChangedByAnotherClientNotification"), 2, "green");
            } else if (varService.isCollectionChangedByAnotherClient(message, server)) {
                showNotification(bundle
                        .getString("collectionChangedByAnotherClientNotification"), 2, "green");
            }
            //refresh components
            refreshComponents();
        });
    }

    public void webSocketUpdateCollection(UpdateMessage message, String server) {
        Platform.runLater(() -> {
            if (varService.isCollectionChangedByAnotherClient(message, server)) {
                showNotification(bundle
                        .getString("collectionChangedByAnotherClientNotification"), 2, "green");
            }
            //Refresh interface ot include updated data
            refreshComponents();
        });
    }

    /**
     * Sets the keyboard shortcuts by binding the handleKeyPress method
     * to the action of pressing a key for the specified scene
     *
     * @param scene the specified scene
     */
    public void setKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Checks which keys were pressed in the specified event and does the corresponding actions
     *
     * @param event the specified event
     */
    public void handleKeyPress(KeyEvent event) {
        if (event.isControlDown()) {
            if (event.getCode() == KeyCode.N) {
                mainAnchorPane.requestFocus();
                useAddButton();
            }
            if (event.getCode() == KeyCode.D) {
                mainAnchorPane.requestFocus();
                useRemoveButton();
            }
            if (event.getCode() == KeyCode.R) {
                mainAnchorPane.requestFocus();
                useRefreshButton();
            }
            if (event.getCode() == KeyCode.T) {
                mainAnchorPane.requestFocus();
                focusOnTitleBar();
            }
            if (event.getCode() == KeyCode.B) {
                mainAnchorPane.requestFocus();
                focusOnContentArea();
            }
            if (event.getCode() == KeyCode.E) {
                mainAnchorPane.requestFocus();
                useEditCollectionsButton();
            }
        }
        if (event.isAltDown()) {
            if (event.getCode() == KeyCode.L) {
                focusOnTitleList();
            }
            if (event.getCode() == KeyCode.Z) {
                focusOnUpperCollectionsMenu();
            }
            if (event.getCode() == KeyCode.X) {
                focusOnLowerCollectionsMenu();
            }
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            mainAnchorPane.requestFocus();
            focusOnSearchBar();
        }
    }


    /**
     * Moves the focus to the collections menu
     */
    public void focusOnUpperCollectionsMenu() {
        collectionsMenuController.focusOnCollectionsMenu();
    }

    /**
     * Moves the focus to the collections menu
     */
    public void focusOnLowerCollectionsMenu() {
        currentNoteAreaController.focusOnCollectionSplitMenuButton();
    }

    /**
     * Moves the focus to the collections menu
     */
    public void focusOnContentArea() {
        currentNoteAreaController.focusOnContentArea();
    }

    /**
     * Moves the focus to the collections menu
     */
    public void focusOnTitleBar() {
        currentNoteAreaController.focusOnTitleBar();
    }

    /**
     * Moves the focus to the search bar
     */
    public void focusOnSearchBar() {
        searchBarController.focusOnSearchBar();
    }

    /**
     * Moves the focus to the search bar list view
     */
    public void focusOnSearchBarListView() {
        searchBarController.focusOnSearchBarListView();
    }

    /**
     * Moves the focus to the title list
     */
    private void focusOnTitleList() {
        titleListController.focusOnTitleList();
    }

    /**
     * Moves the focus to the main anchor pane
     */
    public void focusOnMainAnchorPane() {
        mainAnchorPane.requestFocus();
    }

    /**
     * Uses the Add button
     */
    public void useAddButton() {
        mainButtonsController.addButtonClick();
    }

    /**
     * Uses the Remove button
     */
    public void useRemoveButton() {
        mainButtonsController.removeNoteButtonClick();
    }

    /**
     * Uses the refresh button
     */
    public void useRefreshButton() {
        mainButtonsController.refreshButton();
    }

    /**
     * Uses the edit collections button
     */
    public void useEditCollectionsButton() {
        collectionsMenuController.editCollectionsButtonClick();
    }

    public void clearSelectedNote() {
        titleListController.clearSelectedNote();
    }
}
