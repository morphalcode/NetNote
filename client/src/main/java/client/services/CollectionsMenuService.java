package client.services;

import com.google.inject.Singleton;
import commons.Collection;
import commons.Note;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class CollectionsMenuService {

    public List<Collection> prepareCollectionsList(List<Collection> collections, List<Note> notes){
        if(collections == null || notes == null){
            return new ArrayList<>();
        }
        collections = new ArrayList<>(collections);
        if(collections.isEmpty()){
            return new ArrayList<>();
        }
        Collection completeCollection = createCompleteCollection(notes);
        collections.add(completeCollection);
        return collections;
    }

    /**
     * Creates a collection with all the notes
     * @param allNotes all notes
     * @return the collection with all the notes
     */
    public Collection createCompleteCollection(List<Note> allNotes) {
        Collection completeCollection = new Collection();
        completeCollection.notes = allNotes;
        completeCollection.name = "All Notes";
        completeCollection.id = 0;
        return completeCollection;
    }
}
