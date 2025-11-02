package server.api;

import commons.FileEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.FileService;
import server.services.NoteService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;
    private final NoteService noteService;

    /**
     * Constructor using dependency injection
     *
     * @param fileService instance of the CollectionService class
     * @param noteService instance of the NoteService class
     */
    public FileController(FileService fileService, NoteService noteService) {
        this.fileService = fileService;
        this.noteService = noteService;
    }

    @PostMapping("/notes/{id}/files/")
    public ResponseEntity<FileEntity> uploadFile(@PathVariable("id") long noteId,
                                                 @RequestBody FileEntity file) {
        if (file == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        try {
            return ResponseEntity.ok(fileService.post(noteId, file));
        } catch (Exception e) {
            System.out.println("Could not upload file");
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/notes/{id}/files/")
    public List<FileEntity> getAllFilesInNote(@PathVariable("id") long noteId) {
        return fileService.findAllByNote(noteService.findById(noteId));
    }

    @DeleteMapping("/notes/{id}/files/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable("id") long noteId,
                                             @PathVariable("fileId") long fileId) {
        if (fileService.findByIdAndNote(fileId, noteService.findById(noteId)).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
        try {
            fileService.deleteById(fileId);
            return ResponseEntity.ok("Deleted " + fileId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not delete " + fileId);
        }
    }

    @DeleteMapping("/notes/{id}/files/")
    public ResponseEntity<String> deleteAllFilesInNote(@PathVariable("id") long noteId) {
        try {
            fileService.deleteAllByNote(noteService.findById(noteId));
            return ResponseEntity.ok("Deleted all files");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Could not delete files");
        }
    }

    @PutMapping("/notes/{id}/files/{fileId}")
    public ResponseEntity<FileEntity> updateFileName(@PathVariable("id") long noteId,
                                                     @PathVariable("fileId") long fileId,
                                                     @RequestBody FileEntity file) {
        if (file == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            Optional<FileEntity> optionalFileEntity =
                    fileService.findByIdAndNote(fileId, noteService.findById(noteId));
            if (optionalFileEntity.isEmpty())
                return ResponseEntity.notFound().build();
            try {
                return ResponseEntity.ok(fileService.put(optionalFileEntity.get(), file));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
    }

    @GetMapping("/notes/{id}/files/{filename}")
    public ResponseEntity<byte[]> getFileDataByName(@PathVariable("id") long noteId,
                                                    @PathVariable("filename") String filename) {
        if (fileService.getByName(filename, noteService.findById(noteId)).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        FileEntity file = fileService.getByName(filename, noteService.findById(noteId)).get();
        return ResponseEntity.ok(file.data);
    }
}

