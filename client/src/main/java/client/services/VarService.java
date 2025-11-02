package client.services;

import client.Config;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public class VarService {
    private final ServerUtils server;
    private final Config config;


    private Note currentNote;
    private List<Note> notes;
    private List<Collection> collections;
    private Collection currentCollection;
    private List<String> allTags;

    @Inject
    public VarService(ServerUtils server, Config config) {
        this.server = server;
        this.config = config;
    }


    public void refresh() {
        collections = server.getConfigCollections();
        notes = new ArrayList<>();
        for (Collection collection : collections) {
            notes.addAll(collection.notes);
        }
        allTags = tagUpdater(notes);
        if (currentCollection != null && currentCollection.id == 0 && currentCollection.name.equals("All Notes")) {
            currentCollection.notes = notes;
        } else if (currentCollection == null ||
                collections.stream().noneMatch(x -> x.id == currentCollection.id &&
                        x.getServer().equals(currentCollection.getServer()))) {
            currentCollection = getDefaultCollection();
        } else {
            currentCollection = collections.stream()
                    .filter(x -> x.id == currentCollection.id && x.getServer().equals(currentCollection.getServer()))
                    .findFirst()
                    .orElse(null);
        }
        if (currentCollection != null) {
            if (currentNote == null ||
                    notes.stream().noneMatch(x -> x.id == currentNote.id
                            && x.collection.getServer().equals(currentCollection.getServer()))) {
                currentNote = (!currentCollection.notes.isEmpty() ?
                        currentCollection.notes.getFirst() : null);
            } else {
                currentNote = notes.stream()
                        .filter(x -> x.id == currentNote.id &&
                                x.collection.getServer().equals(currentCollection.getServer()))
                        .findFirst()
                        .orElse(null);
            }
        }
    }

    /**
     * Updates the tags in the tag repository according to the current tags in the note
     *
     * @param notes notes
     * @return tags
     */
    public List<String> tagUpdater(List<Note> notes) {
        List<String> allTags = new ArrayList<>();
        for (Note note : notes) {
            String content = note.content;
            String[] words = content.split(" |\n");
            List<String> currentTags = new ArrayList<>();
            for (String word : words) {
                if (word.startsWith("#") && word.length()>1) {
                    String tag = word.substring(1);
                    if(!tag.contains("#")){
                        currentTags.add(tag);
                        allTags.add(tag);
                    }
                }
            }
            note.tags = currentTags;
        }
        return allTags;
    }

    /**
     * gets the default collection from server
     * @return default collection
     */
    public Collection getDefaultCollection() {
        CollectionInfo defaultCollectionInfo = config.getDefaultCollection();
        if (defaultCollectionInfo != null && collections != null) {
            Collection defaultCollection =
                    collections.stream().filter(x -> x.getServer()
                                    .equals(defaultCollectionInfo.getServer())
                                    && x.id == defaultCollectionInfo.getCollectionId())
                            .findFirst().orElse(null);
            if (defaultCollection != null) {
                return defaultCollection;
            }
        }
        return collections != null && !collections.isEmpty() ? collections.getFirst() : null;
    }

    /**
     * gets current note in client
     * @return current note
     */
    public Note getCurrentNote() {
        return currentNote;
    }

    /**
     * sets client current note
     * @param currentNote to be set
     */
    public void setCurrentNote(Note currentNote) {
        this.currentNote = currentNote;
    }

    /**
     * gets all notes in stored in client
     * @return all notes
     */
    public List<Note> getAllNotes() {
        return notes;
    }

    /**
     * gets all notes in client current collection
     * @return all notes in current collection
     */
    public List<Note> getCurrentCollectionNotes() {
        return (currentCollection != null ? currentCollection.notes : new ArrayList<Note>());
    }

    /**
     * gets all client collections
     * @return all collections
     */
    public List<Collection> getCollections() {
        if(collections == null) {
            return new ArrayList<>();
        }
        return collections;
    }

    /**
     * gets client current collection
     * @return current collection
     */
    public Collection getCurrentCollection() {
        return currentCollection;
    }

    /**
     * sets client current collection
     * @param collection to be set
     */
    public void setCurrentCollection(Collection collection) {
        this.currentCollection = collection;
        this.currentNote = null;
    }

    /**
     * gets all client-stored tags
     * @return all tags
     */
    public List<String> getAllTags() {
        return allTags;
    }

    /**
     * sets client-stored tags
     * @param allTags to be set
     */
    public void setAllTags(List<String> allTags) {
        this.allTags = allTags;
    }

    /**
     * gets note on a specific server by id
     * @param id
     * @param server
     * @return resulting note else null
     */
    public Note getNoteByIdAndServer(long id, String server) {
        return notes.stream()
                .filter(x -> x.id == id && x.collection.getServer().equals(server))
                .findFirst()
                .orElse(null);
    }

    /**
     * gets note on a specific server by title
     * @param title
     * @param server
     * @return resulting note else null
     */
    public Note getNoteByTitleAndServer(String title, String server) {
        return notes.stream()
                .filter(x -> x.title.equals(title) && x.collection.getServer().equals(server))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the note in the current collection with the specified title
     *
     * @param title the specified title
     * @return the first note in the current collection with that title (title is unique) if it exists, null otherwise
     */
    public Note getNoteInCurrentCollectionByTitle(String title) {
        return currentCollection.notes.stream()
                .filter(x -> x.title.equals(title))
                .findFirst()
                .orElse(null);
    }

    /**
     * gets collection on a specific server by title
     * @param id
     * @param server
     * @return resulting collection else null
     */
    public Collection getCollectionByIdAndServer(long id, String server) {
        return collections.stream()
                .filter(x -> x.id == id && x.getServer().equals(server))
                .findFirst().orElse(null);
    }

    /**
     * Sets the last note to be the current note
     */
    public void setCurrentNoteToLast() {
        setCurrentNote((notes.isEmpty() ? null : notes.getLast()));
    }

    /**
     * Checks if the specified note is the current note
     *
     * @param note the specified note
     * @return true if the specified note is the current note, false otherwise
     */
    public boolean isCurrentNote(Note note) {
        return Objects.equals(currentNote, note);
    }

    /**
     * Update note based on an UpdateMessage's instructions
     * @param message the message
     * @param server the server to perform on
     * @return true if successful
     */
    public boolean webSocketUpdateNote(UpdateMessage message, String server) {
        System.out.println(1);
        long id = message.getNoteId();
        Action action = message.getAction();
        long collectionId = message.getCollectionId();
        Collection existingCollection = getCollectionByIdAndServer(collectionId, server);
        List<Note> collectionNotes = null;
        if (existingCollection != null) {
            collectionNotes = existingCollection.notes;
        }

        switch (action) {
            case CREATE:
                if (existingCollection != null) {
                    if(notes == null){
                        notes = new ArrayList<>();
                    }
                    Note newNote = this.server.getNote(id, server);
                    notes.add(newNote);
                    collectionNotes.add(newNote);
                }
                break;
            case DELETE:
                if(notes == null || collections == null){
                    return false;
                }
                if (existingCollection != null) {
                    Note removedNote = notes.stream()
                            .filter(x -> x.id == id && x.collection.getServer().equals(server))
                            .findFirst()
                            .orElse(null);
                    if (removedNote == null) {
                        System.out.println("ERROR NOTE SHOULD EXIST BUT DOESNT EXIST");
                        refresh();
                        return true;
                    }
                    notes.remove(removedNote);
                    collectionNotes.remove(removedNote);
                }
                break;
            case UPDATE:
                if(notes == null) {
                    return false;
                }
                Note oldNote = notes.stream()
                        .filter(x -> x.id == id && x.collection.getServer().equals(server))
                        .findFirst()
                        .orElse(null);
                if (oldNote == null) {
                    System.out.println("ERROR NOTE SHOULD EXIST BUT DOESNT EXIST");
                    refresh();
                    return true;
                }
                int index = notes.indexOf(oldNote);
                Note updatedNote = this.server.getNote(id, server);

                if (updatedNote == null || oldNote.title.equals(updatedNote.title)
                        //If update does not change anything, discard
                        && oldNote.content.equals(updatedNote.content)
                        && oldNote.getCollectionId() == updatedNote.getCollectionId()) {
                    return false;
                }
                if (existingCollection == null) {
                    refresh();
                } else {
                    if (updatedNote.getCollectionId() != oldNote.getCollectionId()) {
                        collectionNotes = getCollectionByIdAndServer(updatedNote
                                .getCollectionId(), updatedNote.collection.getServer()).notes;
                    }
                    Note oldCollectionNote = collectionNotes.stream()
                            .filter(x -> x.id == id && x.collection.getServer().equals(server))
                            .findFirst()
                            .orElse(null);
                    int collectionNoteIndex = collectionNotes.indexOf(oldCollectionNote);
                    if (index != -1 && collectionNoteIndex != -1) {
                        collectionNotes.set(collectionNoteIndex, updatedNote);
                        notes.set(index, updatedNote);
                    } else {
                        System.out.println("REFRESH ALL");
                        refresh();
                    }
                    break;
                }
        }
        return true;
    }

    /**
     * Update collection based on an UpdateMessage's instructions
     * @param message the message
     * @param server the server to perform on
     * @return true if successful
     */
    public boolean webSocketUpdateCollection(UpdateMessage message, String server) {
        long id = message.getCollectionId();
        switch (message.getAction()) {
            case DELETE:
                if (collections != null)
                    collections = collections.stream()
                            .filter(x -> !(x.id == id && x.getServer().equals(server))).toList();
                if (notes != null)
                    notes = notes.stream()
                            .filter(x -> !(x.collection.id == id
                                    && x.collection.getServer().equals(server)))
                            .toList();
                break;
            case UPDATE:
                Collection updatedCollection = this.server.getCollection(id, server);
                if (updatedCollection == null ||
                        !updatedCollection.getServer().equals(server)) {
                    System.out.println("ERROR COLLECTION SERVER INCONSISTENT");
                    refresh();
                    return true;
                }
                if (collections == null || notes == null)
                    return false;
                Collection oldCollection = collections.stream().filter(x -> x.id == id
                        && x.getServer().equals(server)).findFirst().orElse(null);
                if (oldCollection == null) {
                    System.out.println("ERROR COLLECTION SHOULD EXIST BUT DOESNT EXIST");
                    refresh();
                    return true;
                }
                if (oldCollection.name.equals(updatedCollection.name)){
                        //If update does not change anything, discard
                    return false;
                }
                int index = collections.indexOf(oldCollection);
                notes.removeAll(oldCollection.notes);
                collections.set(index, updatedCollection);
                notes.addAll(updatedCollection.notes);
                break;
        }
        return true;
    }

    /**
     * returns boolean representing whether note changed by another client, based on an UpdateMessage
     * @param message the UpdateMessage
     * @param server the server to perform on
     * @return the boolean
     */
    public boolean isNoteChangedByAnotherClient(UpdateMessage message, String server) {
        return currentNote != null
                && currentNote.collection.getServer().equals(server)
                && currentNote.id == message.getNoteId();
    }

    /**
     * returns boolean representing whether collection changed by another client, based on an UpdateMessage
     * @param message the UpdateMessage
     * @param server the server to perform on
     * @return the boolean
     */
    public boolean isCollectionChangedByAnotherClient(UpdateMessage message, String server) {
        return currentCollection != null
                && Objects.equals(currentCollection.getServer(), server)
                && currentCollection.id == message.getCollectionId();
    }

    /**
     * gets note from server that matches client current note
     * @return the result else null
     */
    public Note getNotesCurrentNote() {
        if (notes == null || currentNote == null)
            return null;
        return notes.stream()
                .filter(x -> x.id == currentNote.id
                        && x.collection.getServer().equals(
                        currentNote.collection.getServer()))
                .findFirst().orElse(null);
    }

    /**
     * returns boolean representing whether any of the client variables are empty
     * @return the boolean
     */
    public boolean isVarsEmpty() {
        return collections == null ||
                collections.isEmpty() ||
                currentNote == null;
    }

    /**
     * gets all collections on a specific server
     * @param server the server
     * @return the resulting collections
     */
    public List<Collection> getCollectionsByServer(String server) {
        if (collections == null)
            return null;
        return new ArrayList<>(collections.stream()
                .filter(x -> x.getServer().
                        equals(server)).toList());
    }

}
