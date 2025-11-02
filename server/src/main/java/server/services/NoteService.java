package server.services;

import commons.Note;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository db;

    /**
     * Constructor using dependency injection
     *
     * @param db the note database
     */
    public NoteService(NoteRepository db) {
        this.db = db;
    }

    /**
     * Saves the provided note in the database
     *
     * @param note the provided note
     */
    public void save(Note note) {
        db.save(note);
    }

    /**
     * Searches if a note with the provided id exists in the database
     *
     * @param id the provided id
     * @return true if it does, false otherwise
     */
    public boolean existsById(long id) {
        return db.existsById(id);
    }

    /**
     * Tries to find a note with the provided id in the database
     *
     * @param id the provided id
     * @return the note if it does find it, null otherwise
     */
    public Note findById(Long id) {
        return db.findById(id).orElse(null);
    }

    /**
     * Gets all notes from the database
     *
     * @return all notes from the database
     */
    public List<Note> get() {
        return db.findAll();
    }

    /**
     * Posts/Saves in the database the provided note
     *
     * @param note the provided note
     * @return the posted/saved note
     */
    public Note post(@Valid Note note) {
        var n = new Note();
        n.title = note.title;
        n.content = note.content;
        n.setCollectionId(note.getCollectionId());
        db.save(n);
        return n;
    }

    /**
     * Deletes all notes from the database
     */
    public void delete() {
        db.deleteAll();
    }

    /**
     * Puts the provided notes in the database
     *
     * @param notes the provided notes
     * @return the updated notes
     */
    public List<Note> put(Iterable<Note> notes) {
        List<Note> updatedNotes = db.saveAll(notes);
        List<Long> noteIds = updatedNotes.stream().map(x -> x.id).toList();
        db.findAll().forEach((Note note) -> {
            if (!noteIds.contains(note.id)) {
                db.delete(note);
            }
        });
        return updatedNotes;
    }

    /**
     * Patches the provided notes
     *
     * @param notes the provided notes
     * @return the saved notes
     */
    public List<Note> patch(Iterable<Note> notes) {
        return db.saveAll(notes);
    }

    /**
     * Gets the note with the provided id from the database
     *
     * @param id the provided id
     * @return response entity ok if it exists, not found if it does not
     */
    public ResponseEntity<Note> get(long id) {
        return db.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes the note with the provided id from the database
     *
     * @param id the provided id
     * @return response entity ok if it exists, not found if it does not
     */
    public ResponseEntity<Boolean> delete(long id) {
        if (db.existsById(id)) {
            db.deleteById(id);
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Puts the provided note in the database at the provided id
     *
     * @param id   the provided id
     * @param note the provided note
     * @return the saved note
     */
    public Note put(long id, @Valid Note note) {
        Note n = db.findById(id).get();
        n.title = note.title;
        n.content = note.content;
        n.collection = note.collection;
        db.save(n);
        return n;
    }
}
