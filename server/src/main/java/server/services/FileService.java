package server.services;

import commons.FileEntity;
import commons.Note;
import org.springframework.stereotype.Service;
import server.database.FileRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private final FileRepository db;

    /**
     * Constructor using dependency injection
     *
     * @param db the file database
     */
    public FileService(FileRepository db) {
        this.db = db;
    }

    /**
     * Posts/Saves in the database the provided file
     *
     * @param noteId the id of the note
     * @param file   the provided file
     * @return the file posted/saved
     */
    public FileEntity post(long noteId, FileEntity file) {
        FileEntity f = new FileEntity();
        f.fileName = file.fileName;
        f.type = file.type;
        f.data = file.data;
        f.setNoteId(file.getNoteId());
        db.save(f);
        return f;
    }

    public List<FileEntity> findAllByNote(Note note) {
        return db.findAllByNote(note);
    }

    public Optional<FileEntity> findByIdAndNote(long fileId, Note note) {
        return db.findByIdAndNote(fileId, note);
    }

    public void deleteById(long fileId) {
        db.deleteById(fileId);
    }

    public void deleteAllByNote(Note note) {
        db.deleteAllByNote(note);
    }

    public FileEntity put(FileEntity fileToBeUpdated, FileEntity file) {
        fileToBeUpdated.fileName = file.fileName;
        db.save(fileToBeUpdated);
        return fileToBeUpdated;
    }

    public Optional<FileEntity> getByName(String filename, Note note) {
        return db.findByFileNameAndNote(filename, note);
    }
}
