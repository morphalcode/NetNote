package client.scenes;

import client.services.CollectionInterfaceService;
import client.services.VarService;
import client.Main;
import client.ServerStatus;
import com.google.inject.Inject;
import commons.Collection;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

public class CollectionInterfaceCtrl {
    private final InterfaceCtrl interfaceCtrl;
    private final VarService varService;
    private final CollectionInterfaceService service;

    private ServerStatus status;
    private int selectedIndex;

    private Collection selectedCollection;

    @FXML
    private ListView<Collection> collectionListView;

    @FXML
    private Button addCollectionButton;

    @FXML
    private Button removeCollectionButton;

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField serverTextField;

    @FXML
    private Text actionText;

    @FXML
    private Button makeDefaultCollectionButton;

    @FXML
    private Button saveButton;

    @FXML
    private Text serverStatusText;

    @FXML
    private AnchorPane anchorPane;

    /**
     * Constructor using DI, used by framework automatically.
     *
     * @param interfaceCtrl singleton interfaceCtrl
     * @param varService    s
     * @param service       s
     */
    @Inject
    public CollectionInterfaceCtrl(InterfaceCtrl interfaceCtrl, VarService varService,
                                   CollectionInterfaceService service) {
        this.interfaceCtrl = interfaceCtrl;
        this.varService = varService;
        this.service = service;
    }

    /**
     * Getter for the selected collection
     *
     * @return - Collection, selected collection
     */
    public Collection getSelectedCollection() {
        return selectedCollection;
    }


    /**
     * Custom initialize method. Called automatically by javafx when object created.
     */
    public void initialize() {
        saveButton.setDisable(true);
        selectedIndex = collectionListView.getSelectionModel().getSelectedIndex();
        selectedCollection = null;
        collectionListView.getSelectionModel()
                .selectedItemProperty()
                .addListener(new ChangeListener<Collection>() {
                    @Override
                    public void changed(ObservableValue<? extends Collection> observableValue,
                                        Collection s, Collection t1) {

                        // Gets the index of the collection the user selected in the client
                        selectedCollection = collectionListView
                                .getSelectionModel()
                                .getSelectedItem();
                        if (selectedCollection != null) {
                            serverTextField.textProperty().set(selectedCollection.getServer());
                            actionText.textProperty()
                                    .set(interfaceCtrl.myGetBundle().getString("editing")
                                            + selectedCollection.name);
                            serverTextField.setDisable(true);
                            saveButton.setText(interfaceCtrl.myGetBundle().getString("saveButton"));
                            refreshCollectionInfo();
                        } else {
                            serverTextField.textProperty().set("");
                            serverTextField.setDisable(false);
                            actionText.textProperty()
                                    .set(interfaceCtrl.myGetBundle().getString("adding"));
                            saveButton.setText(interfaceCtrl.myGetBundle().getString("addButton"));
                            refreshCollectionInfo();
                        }
                    }
                });
        collectionListView.setOnMouseClicked(event -> {
            int newSelectedIndex = collectionListView.getSelectionModel().getSelectedIndex();
            if (newSelectedIndex == -1 || newSelectedIndex == selectedIndex) {
                selectedCollection = null;
                selectedIndex = -1;
                collectionListView.getSelectionModel().clearSelection();
            } else {
                selectedIndex = newSelectedIndex;
            }
        });
        serverTextField.textProperty().addListener((ob, ol, nv) -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Thread thread = new Thread(() -> {
                        updateStatus();
                    });
                    thread.start();
                }
            });
        });
        titleTextField.textProperty().addListener((ob, ol, nv) -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Thread thread = new Thread(() -> {
                        updateStatus();
                    });
                    thread.start();
                }
            });
        });
        Platform.runLater(() -> {
            Scene scene = addCollectionButton.getScene();
            setKeyboardShortcuts(scene);
            anchorPane.requestFocus();
        });
        refresh();
    }

    /**
     * Updates the status fields according to the server status of the currently typed server
     */
    public synchronized void updateStatus() {
        status = checkServerStatus();
        serverStatusText.setText(status.getMessage());
        saveButton.setDisable(status != ServerStatus.COLLECTION_WILL_BE_SAVED
                && status != ServerStatus.COLLECTION_WILL_BE_ADDED);
    }

    /**
     * Sets the keyboard shortcuts by binding the handleKeyPress method
     * to the action of pressing a key for the specified scene
     *
     * @param scene the specified scene
     */
    public void setKeyboardShortcuts(Scene scene) {
        scene.setOnKeyPressed(this::handelKeyPress);
    }

    /**
     * Checks which keys were pressed in the specified event and does the corresponding actions
     *
     * @param keyEvent the specified event
     */
    public void handelKeyPress(KeyEvent keyEvent) {
        if (keyEvent.isControlDown()) {
            if (keyEvent.getCode() == KeyCode.N) {
                anchorPane.requestFocus();
                createCollection();
            }
            if (keyEvent.getCode() == KeyCode.D) {
                anchorPane.requestFocus();
                removeCollection();
            }
            if (keyEvent.getCode() == KeyCode.M) {
                anchorPane.requestFocus();
                setDefaultCollectionId();
            }
            if (keyEvent.getCode() == KeyCode.Q) {
                anchorPane.requestFocus();
                titleTextField.requestFocus();
            }
            if (keyEvent.getCode() == KeyCode.W) {
                anchorPane.requestFocus();
                serverTextField.requestFocus();
            }
        }
        if (keyEvent.isAltDown() && keyEvent.getCode() == KeyCode.L) {
            collectionListView.requestFocus();
        }
        if (keyEvent.getCode() == KeyCode.ESCAPE) {
            Stage stage = (Stage) addCollectionButton.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * COMPLETE THIS
     */
    public void refreshCollectionInfo() {
        titleTextField.textProperty()
                .set((selectedCollection == null ? "" : selectedCollection.name));
        serverTextField.textProperty()
                .set((selectedCollection == null ? "" : selectedCollection.getServer()));
        titleTextField.textProperty()
                .set((selectedCollection == null ? "" : selectedCollection.name));

    }

    /**
     * COMPLETE THIS
     */
    public void refreshCollectionList() {
        collectionListView
                .setItems(FXCollections.observableArrayList(varService.getCollections()));
        collectionListView
                .setCellFactory(lc -> new ListCell<Collection>() {
                    @Override
                    protected void updateItem(Collection collection, boolean empty) {
                        super.updateItem(collection, empty);
                        if (empty || collection == null) {
                            setText("");
                        } else {
                            setText(collection.name);
                        }
                    }
                });
    }

    /**
     * COMPLETE THIS
     */
    public void refresh() {
        interfaceCtrl.refresh();
        refreshCollectionInfo();
        refreshCollectionList();
    }

    /**
     * COMPLETE THIS
     *
     * @return COMPLETE THIS
     */
    public ServerStatus checkServerStatus() {
        String serverUrl = serverTextField.textProperty().get();
        String collectionName = titleTextField.getText();
        return service.getServerStatus(serverUrl, collectionName, selectedCollection);
    }

    /**
     * Creates a new collection
     */
    public void createCollection() {
        String server = serverTextField.getText();
        if (service.createCollection(status, server)) {
            interfaceCtrl.showNotification(
                    interfaceCtrl
                            .myGetBundle()
                            .getString("newCollectionCreatedNotification1") +
                            server +
                            interfaceCtrl
                                    .myGetBundle()
                                    .getString("newCollectionCreatedNotification2"),
                    3, "green");
            refresh();
        } else {
            interfaceCtrl.showNotification(interfaceCtrl
                    .myGetBundle()
                    .getString("serverNotReachableNotification"), 2, "red");
        }
    }

    /**
     * Removes the currently selected collection
     */
    public void removeCollection() {
        if (selectedCollection == null) {
            interfaceCtrl.showNotification(interfaceCtrl
                    .myGetBundle()
                    .getString("noCollectionSelectedNotification"), 2, "red");
            return;
        }
        if (service.isDefaultCollection(selectedCollection)) {
            interfaceCtrl.showNotification(interfaceCtrl
                    .myGetBundle()
                    .getString("cannotRemoveDefaultNotification"), 3, "red");
            return;
        }
        Pair<CollectionDeletionInterfaceCtrl, Parent> pair = Main.FXML.
                load(CollectionDeletionInterfaceCtrl.class, interfaceCtrl.myGetBundle(),
                        "client", "scenes", "CollectionDeletionInterface.fxml");
        Parent root = pair.getValue();
        CollectionDeletionInterfaceCtrl controller = pair.getKey();
        Stage stage = new Stage();
        controller.initialize(interfaceCtrl, this, stage);
        Scene scene = new Scene(root);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.setTitle(interfaceCtrl.myGetBundle().getString("deleteCollectionTitle"));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    /**
     * COMPLETE THIS
     */
    public void editCollection() {
        String server = serverTextField.getText();
        if (status == ServerStatus.COLLECTION_WILL_BE_ADDED) {
            service.addCollection(status, server, selectedCollection);
        } else if (status == ServerStatus.COLLECTION_WILL_BE_SAVED && selectedCollection != null) {
            String newName = titleTextField.textProperty().get();
            if (newName.isEmpty() || newName.isBlank()) {
                interfaceCtrl.showNotification(interfaceCtrl.myGetBundle().getString("nameNotEmptyNotification"), 2, "red");
                return;
            }
            selectedCollection.name = newName;
            if (service.putCollection(selectedCollection)) {
                interfaceCtrl.showNotification(interfaceCtrl
                        .myGetBundle()
                        .getString("successfullySavedChangesNotification"), 2, "green");
            } else {
                interfaceCtrl.showNotification(interfaceCtrl
                        .myGetBundle()
                        .getString("couldNotSaveChangesNotification"), 2, "red");
            }
        }
        refresh();
    }

    /**
     * Sets the selected collection as the default collection in the config singleton
     */
    public void setDefaultCollectionId() {
        if (service.isDefaultCollection(selectedCollection)) {
            interfaceCtrl.showNotification(interfaceCtrl
                    .myGetBundle()
                    .getString("alreadyDefaultNotification"), 2, "red");
            return;
        }
        service.setDefaultCollection(selectedCollection);
        interfaceCtrl.showNotification("\""
                + selectedCollection.name
                + interfaceCtrl.myGetBundle().getString("isNowDefaultNotification"), 3, "green");

    }
}

