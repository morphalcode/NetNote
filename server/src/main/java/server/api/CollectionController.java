package server.api;

import commons.Collection;
import commons.Note;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.services.CollectionService;
import server.services.NoteService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {

    private final CollectionService collectionService;
    private final NoteService noteService;

    /**
     * Constructor using dependency injection
     *
     * @param collectionService instance of the CollectionService class
     * @param noteService instance of the NoteService class
     */
    public CollectionController(CollectionService collectionService, NoteService noteService) {
        this.collectionService = collectionService;
        this.noteService = noteService;
    }

    /**
     * Handles "get" requests to /
     *
     * @return List<Collection> of all collections in the repository.
     */
    @GetMapping("/")
    @Transactional
    public List<Collection> getCollections() {
        return collectionService.get();
    }

    /**
     * Handles "post" requests to /.
     * Adds the collection sent in the body of the request to the repository.
     *
     * @param collection Collection sent in the request body
     * @param request    request
     * @return ResponseEntity with status and the collection which is added to the repository
     */
    @PostMapping("/")
    public ResponseEntity<Collection> addCollection(@RequestBody @Valid Collection collection,
                                                    HttpServletRequest request) {
        if (collection == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok(collectionService.post(collection, request));
    }

    /**
     * Handles "get" requests to /{id}.
     * Returns the collection with the given id.
     *
     * @param id the given id
     * @return the collection with the given id.
     */
    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<Collection> getCollection(@PathVariable("id") Long id) {
        return collectionService.get(id);
    }

    /**
     * Handles "put" request to /{id}.
     *
     * @param id         Collection ID that should be updated
     * @param collection Collection with the name that the updated collection should be set to
     * @return ResponseEntity with status and collection after update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Collection> putCollection(@PathVariable("id") Long id,
                                                    @RequestBody @Valid Collection collection) {
        if (collection == null) {
            return ResponseEntity.badRequest().body(null);
        } else if (!collectionService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(collectionService.put(id, collection));
    }


    /**
     * @param id id of collection that will be made default
     * @return boolean in response entity, true if made default successfully
     * @deprecated
     */
    @PostMapping("/{id}/makeDefault")
    public ResponseEntity<Boolean> makeDefault(@PathVariable("id") Long id) {
        Collection oldDefault = collectionService.findById(1L);
        Collection newDefault = collectionService.findById(id);
        if (oldDefault == null || newDefault == null) {
            return ResponseEntity.badRequest().body(false);
        }
        List<Note> oldIds = new ArrayList<>(oldDefault.notes);
        List<Note> newIds = new ArrayList<>(newDefault.notes);
        for (Note note : oldIds) {
            note.setCollectionId(id);
            noteService.save(note);
            System.out.println(note);
        }
        for (Note note : newIds) {
            note.setCollectionId(1L);
            noteService.save(note);
            System.out.println(note);
        }
        String oldDefaultName = oldDefault.name;
        String newDefaultName = newDefault.name;
        oldDefault.name = "New Collection";
        collectionService.save(oldDefault);
        newDefault.name = oldDefaultName;
        collectionService.save(newDefault);
        oldDefault.name = newDefaultName;
        collectionService.save(oldDefault);
        return ResponseEntity.ok(true);
    }

}
