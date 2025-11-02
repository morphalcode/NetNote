package client.services;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.FileEntity;
import javafx.scene.control.Hyperlink;
import javafx.stage.FileChooser;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class FilesSectionService {

    private final ServerUtils serverUtils;
    private final VarService varService;

    @Inject
    public FilesSectionService(ServerUtils serverUtils, VarService varService) {
        this.serverUtils = serverUtils;
        this.varService = varService;
    }

    public void downloadFile(FileEntity file, Hyperlink hyperlink) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(file.fileName);
        fileChooser.getExtensionFilters().addAll(new FileChooser
                .ExtensionFilter("All Files", "*.*"));
        File saveFile = fileChooser.showSaveDialog(hyperlink.getScene().getWindow());
        if(saveFile != null) {
            try (FileOutputStream fos = new FileOutputStream(saveFile)) {
                fos.write(file.data);
            } catch (IOException e) {
                throw new IOException();
            }
        }
    }

    public FileEntity uploadFileToNote(File file) throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(
                file.getName(), file.getName(), Files.probeContentType(file.toPath()),
                Files.readAllBytes(file.toPath())
        );
        return serverUtils.uploadFileToNote(varService.getCurrentNote(),
                multipartFile);
    }

    public List<FileEntity> getCurrentFiles(){
        if(varService.getCurrentNote() != null){
            return serverUtils.getFiles(varService.getCurrentNote());
        }
        else return new ArrayList<>();
    }

    public String editFileName(FileEntity file, String newFileName) {
        file.fileName = newFileName;
        FileEntity updatedFile = serverUtils.updateFileName(varService.getCurrentNote(),
                file.id, file);
        return updatedFile.fileName;
    }


    public void deleteFile(FileEntity file) {
        System.out.println("deleting " + file.fileName);
        serverUtils.deleteFile(varService.getCurrentNote(), file.id);
    }

    public String filePath(FileEntity file){
        return varService.getCurrentCollection().getServer() + "/api/notes/"
                + varService.getCurrentNote().id + "/files/" + file.fileName;
    }
}
