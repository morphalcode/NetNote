package client.components;

import client.scenes.InterfaceCtrl;
import client.services.FilesSectionService;
import com.google.inject.Inject;
import commons.FileEntity;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.*;

public class FilesSectionCtrl {

    private final InterfaceCtrl interfaceCtrl;
    private final FilesSectionService service;


    @FXML
    private Button addFileButton;

    @FXML
    private ListView<FileEntity> fileList;

    @FXML
    private Text text;

    /**
     * Constructor with dependency injection
     * @param service the controller of the main interface
     * @param interfaceCtrl
     */
    @Inject
    public FilesSectionCtrl(InterfaceCtrl interfaceCtrl,FilesSectionService service) {
        this.interfaceCtrl = interfaceCtrl;
        this.service = service;
    }

    /**
     * Makes it so that when the user clicks the add button he can select a file from
     * their computer and upload it to the server
     * @return the uploaded file
     */
    public FileEntity addButtonClick() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(addFileButton.getScene().getWindow());
        if(file == null){
            System.out.println("File is null");
            return null;
        }
        try{
            FileEntity uploadedFile = service.uploadFileToNote(file);
            fileList.getItems().add(uploadedFile);
            return uploadedFile;
        }catch (IOException e){
            interfaceCtrl.showNotification(interfaceCtrl
                    .myGetBundle()
                    .getString("couldNotAddFileNotification"), 2, "red");
            e.printStackTrace();
            return null;
        }
    }

    public void initialize() {
        fileList.setCellFactory(lc -> new ListCell<>() {
            @Override
            protected void updateItem(FileEntity file, boolean empty) {
                super.updateItem(file, empty);
                if(empty || file == null){
                    setGraphic(null);
                }
                else{
                    Hyperlink fileNameLink = new Hyperlink(file.fileName);
                    fileNameLink.setMaxWidth(95);
                    fileNameLink.setOnMouseClicked((event) -> {
                        try{
                            service.downloadFile(file, fileNameLink);
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            interfaceCtrl.showNotification(interfaceCtrl
                                    .myGetBundle()
                                    .getString("couldNotDownloadFileNotification"), 2, "red");
                        }});
                    TextField fileTextField = new TextField(file.fileName);
                    fileTextField.setMaxWidth(95);
                    fileTextField.setVisible(false);
                    Button editButton = new Button();
                    editButton.setText("\uD83D\uDD8A\uFE0F");
                    editButton.setOnMouseClicked(event -> {
                        fileNameLink.setVisible(false);
                        fileTextField.setVisible(true);
                        fileTextField.requestFocus();
                    });
                    fileTextField.setOnAction(event -> {
                        String newFileName = fileTextField.getText();
                        file.fileName = editFileName(file, newFileName);

                        fileNameLink.setText(file.fileName);
                        fileTextField.setVisible(false);
                        fileNameLink.setVisible(true);
                    });
                    Button deleteButton = new Button();
                    deleteButton.setText("❌");
                    deleteButton.setOnMouseClicked(event -> deleteFile(file));
                    AnchorPane anchorPane = new AnchorPane(fileNameLink, fileTextField,editButton, deleteButton);
                    AnchorPane.setLeftAnchor(editButton, 100.0);
                    AnchorPane.setLeftAnchor(deleteButton, 140.0);
                    setGraphic(anchorPane);
                    if(lc.isPressed())
                        pickFile();
                }
            }
        });
    }

    public void refresh(){
        fileList.setItems(FXCollections.observableArrayList(service.getCurrentFiles()));
    }

    public String editFileName(FileEntity file, String newFileName){
        return service.editFileName(file, newFileName);
    }

    public void deleteFile(FileEntity file){
        service.deleteFile(file);
        fileList.getItems().remove(file);
    }

    public void refreshUI() {
        text.setText(interfaceCtrl.myGetBundle().getString("addedFiles"));
    }

    public void pickFile(){
        FileEntity currentFile = fileList.getSelectionModel().getSelectedItem();
        if(currentFile != null){
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(service.filePath(currentFile));
            clipboard.setContent(content);
        }
    }
}
