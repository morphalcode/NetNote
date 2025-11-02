package server.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import commons.Note;
import server.services.NoteService;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    /**
     * Constructor using dependency injection
     *
     * @param noteService instance of the NoteService class
     */
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }


    /**
     * Handles "get" requests to /
     *
     * @return List<Note> of all notes in the repository.
     */
    @GetMapping("/")
    public List<Note> getNotes() {
        return noteService.get();
    }

    /**
     * Handles "post" requests to /.
     * Adds the note sent in the body of the request to the repository.
     *
     * @param note Note sent in the request body
     * @return ResponseEntity with status and the note which is added to the repository
     */
    @PostMapping("/")
    public ResponseEntity<Note> addNote(@RequestBody @Valid Note note) {
        if (note == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(noteService.post(note));
    }

    /**
     * Handles "delete" requests to /. Deletes all notes from the repository.
     */
    @DeleteMapping("/")
    public void deleteNotes() {
        noteService.delete();
    }


    //This is potentially a temporary method due to it being O(n).
    // Furthermore there is not planned use for it currently. Use patch instead of possible.

    /**
     * Handles "put" requests to /.
     * Replaces all notes in the repository with the notes sent in the request body.
     * Notes in the repository which are not sent in the body will be deleted.
     *
     * @param notes Iterable<Note>.
     *              After the method is called the repository should be equal to this.
     * @return ResponseEntity with response status and List<Note> of the repository in the body.
     */
    @PutMapping("/")
    public ResponseEntity<List<Note>> putNotes(@RequestBody Iterable<@Valid Note> notes) {
        if (notes == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(noteService.put(notes));
    }

    /**
     * Handles "patch" requests to /.
     * Updates all notes in the repository which are sent in the request body.
     * If the notes are not already in the repository, it adds them instead.
     * Notes in the repository which are not sent in the body will be not be affected.
     *
     * @param notes All the notes that should be added/updated in the repository
     * @return ResponseEntity with response status and list (List<Note>)
     * of all notes affected in the body.
     */
    @PatchMapping("/")
    public ResponseEntity<List<Note>> patchNotes(@RequestBody Iterable<@Valid Note> notes) {
        if (notes == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(noteService.patch(notes));
    }

    /**
     * Handles "get" requests to /{id}.
     *
     * @param id The id of the note that should be returned
     * @return ResponseEntity with the response status and Note matching the {id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNote(@PathVariable("id") long id) {
        return noteService.get(id);
    }
//
//    @GetMapping("/{title}")
//    public ResponseEntity<Note> getNoteByTitle(@PathVariable("title") String title) {
//        return db.findNoteByTitle(title)
//        .map(x -> ResponseEntity.ok(x)).orElse(ResponseEntity.notFound().build());
//    }


    /**
     * Handles "delete" requests to /{id}.
     * Deletes the note of the {id} requested (or returns 404 if not found)
     *
     * @param id The id of the note that should be deleted
     * @return 200 if note found and deleted or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteNote(@PathVariable("id") long id) {
        return noteService.delete(id);
    }


    /**
     * Handles "put" request to /{id}.
     *
     * @param id   id of note that should be updated
     * @param note Note with the content and title that the updated note should be set to
     * @return ResponseEntity with status and note after update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Note> putNote(@PathVariable("id") long id,
                                        @RequestBody @Valid Note note) {
        if (note == null) {
            return ResponseEntity.badRequest().body(null);
        } else if (!noteService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try{
            Note resNote = noteService.put(id, note);
            return ResponseEntity.ok(resNote);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

}
