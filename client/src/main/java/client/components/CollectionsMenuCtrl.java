package client.components;

import client.Main;
import client.scenes.CollectionInterfaceCtrl;
import client.scenes.InterfaceCtrl;
import client.services.CollectionsMenuService;
import client.services.VarService;
import com.google.inject.Inject;
import commons.Collection;
import jakarta.annotation.PostConstruct;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CollectionsMenuCtrl {

    private final InterfaceCtrl interfaceCtrl;
    private final VarService varService;
    private final CollectionsMenuService service;

    private Stage collectionsInterface;

    @FXML
    public SplitMenuButton collectionsMenu;

    @FXML
    private Button editCollectionsButton;

    /**
     * Constructor using dependency injection. Used automatically by framework.
     *
     * @param interfaceCtrl          controller for main interface
     * @param varService             variables service
     * @param collectionsMenuService this service
     */
    @Inject
    public CollectionsMenuCtrl(InterfaceCtrl interfaceCtrl, VarService varService, CollectionsMenuService collectionsMenuService) {
        this.interfaceCtrl = interfaceCtrl;
        this.varService = varService;
        this.service = collectionsMenuService;
    }

    /**
     * Initializes the collections menu by:
     * - using the setKeyboardShortcuts method
     */
    public void initialize() {
        setKeyboardShortcuts();
    }

    /**
     * Creates menu items and adds them to the collections menu based on the
     * provided list of collections
     */
    public void refresh() {
        collectionsMenu.setText(varService.getCurrentCollection() != null ?
                varService.getCurrentCollection().name : "");
        collectionsMenu.getItems().clear();
        for (Collection collection : service.prepareCollectionsList(varService.getCollections(),
                varService.getAllNotes())) {
            MenuItem menuItem = new MenuItem(collection.name);
            menuItem.setOnAction(_ -> setCurrentCollection(collection));
            collectionsMenu.getItems().add(menuItem);
        }
    }

    /**
     * Sets the provided collection as the current collection
     *
     * @param collection the provided collection
     */
    public void setCurrentCollection(Collection collection) {
        collectionsMenu.setText(collection.name);
        varService.setCurrentCollection(collection);
        interfaceCtrl.refresh();
    }

    /**
     * Sets an event filter so that when the keyboard shortcuts containing
     * ESCAPE and CONTROL are used the collections menu behaves as intended
     * Otherwise it does not lose focus and the shortcuts do not work
     */
    public void setKeyboardShortcuts() {
        collectionsMenu.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
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
     * Moves the focus on the collections menu
     */
    public void focusOnCollectionsMenu() {
        collectionsMenu.requestFocus();
    }


    /**
     * This method is called when the edit collections button is pressed.
     */
    @PostConstruct
    public void editCollectionsButtonClick() {
        if (collectionsInterface != null && collectionsInterface.isShowing()) {
            // Bring the existing stage to the front
            collectionsInterface.toFront();
            return;
        }
        try {
            Parent root = Main.FXML.load(CollectionInterfaceCtrl.class, interfaceCtrl.myGetBundle(), "client", "scenes", "CollectionInterface.fxml").getValue();
            collectionsInterface = new Stage();
            collectionsInterface.setTitle(interfaceCtrl.myGetBundle().getString("editCollectionsMenuTitle"));
            Scene scene = new Scene(root);
            collectionsInterface.setScene(scene);
            collectionsInterface.initModality(Modality.WINDOW_MODAL);
            collectionsInterface.show();
            collectionsInterface.setOnCloseRequest((event) -> {
                interfaceCtrl.refresh();
            });
            collectionsInterface.focusedProperty().addListener(
                    (observable, wasFocused, isFocused) -> {
                        if (wasFocused && !isFocused) {
                            interfaceCtrl.refresh();
                        }
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Refreshes the UI elements so that they show the newly selected language
     */
    public void refreshUI() {
        editCollectionsButton.setText(interfaceCtrl.myGetBundle().getString("editCollections"));
    }
}
