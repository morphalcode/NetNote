package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CollectionTest {

    @Test
    void testEmptyConstructor(){
        Collection collection = new Collection();
        assertAll(
                () -> assertEquals("New Collection", collection.name),
                () -> assertEquals(new ArrayList<>(), collection.notes)
        );
    }

    @Test
    void testNameConstructor(){
        Collection collection = new Collection("name");
        assertEquals("name", collection.name);
    }

    @Test
    void testNameAndNotesConstructor(){
        List<Note> list = new ArrayList<>(Arrays.asList(new Note()));
        Collection collection = new Collection("name", list);
        assertAll(
                () -> assertEquals("name", collection.name),
                () -> assertEquals(list, collection.notes)
        );
    }

    @Test
    void testIdAndNameConstructor(){
        Collection collection = new Collection(1,"name");
        assertAll(
                () -> assertEquals(1, collection.id),
                () -> assertEquals("name", collection.name)
        );
    }

    @Test
    void testAllAttributesConstructor(){
        long id = 0;
        String name = "name";
        List<Note> list = new ArrayList<>(Arrays.asList(new Note()));
        Collection collection = new Collection(id, name, list);
        assertAll(
                () -> assertEquals(id, collection.id),
                () -> assertEquals(name, collection.name),
                () -> assertEquals(list, collection.notes)
        );
    }

    @Test
    void updateName() {
        Collection collection = new Collection();
        collection.updateName();
        assertEquals("New Collection (0)", collection.name);
    }

    @Test
    void addNoteToCollection() {
        Collection collection = new Collection();
        Note note = new Note();
        collection.addNoteToCollection(note);
        assertEquals(collection.notes.get(0), note);
        //assertEquals(note.collection, collection);
    }

    @Test
    void filterNotesInCollection() {
        Collection collection = new Collection();
        Note note1 = new Note("note1", "ab");
        Note note2 = new Note("note2", "cd");
        Note note3 = new Note("note3", "b");
        collection.addNoteToCollection(note1);
        collection.addNoteToCollection(note2);
        collection.addNoteToCollection(note3);
        List<Note> filterByTitleList = collection.filterNotesInCollection("note1");
        List<Note> filterByContentList = collection.filterNotesInCollection("b");
        assertAll(
                () -> assertEquals(filterByTitleList, Arrays.asList(note1)),
                () -> assertEquals(filterByContentList, Arrays.asList(note1, note3))
        );
    }

    @Test
    void testEqualsTrue() {
        Collection c1 = new Collection();
        Collection c2 = new Collection();
        assertEquals(c1, c2);
        Collection collection1 = new Collection("Collection 1");
        Collection collection2 = new Collection("Collection 1");
        assertEquals(collection1, collection2);
        Note note = new Note();
        c1.addNoteToCollection(note);
        c2.addNoteToCollection(note);
        assertEquals(c1, c2);
        collection1.addNoteToCollection(note);
        collection2.addNoteToCollection(note);
        assertEquals(collection1, collection2);
    }

    @Test
    void testEqualsFalse() {
        Collection collection1 = new Collection("Collection 1");
        Collection collection2 = new Collection("Collection 2");
        Collection collection3 = new Collection("Collection 1");
        assertNotEquals(collection1, collection2);
        Note note = new Note();
        collection1.addNoteToCollection(note);
        assertNotEquals(collection1, collection3);
    }

    @Test
    void testHashCode() {
        Collection c1 = new Collection();
        Collection c2 = new Collection();
        assertEquals(c1.hashCode(), c2.hashCode());
        Collection collection1 = new Collection("Collection 1");
        Collection collection2 = new Collection("Collection 1");
        assertEquals(collection1.hashCode(), collection2.hashCode());
        Note note = new Note();
        c1.addNoteToCollection(note);
        c2.addNoteToCollection(note);
        assertEquals(c1.hashCode(), c2.hashCode());
        collection1.addNoteToCollection(note);
        collection2.addNoteToCollection(note);
        Collection c = new Collection("c");
        assertEquals(collection1.hashCode(), collection2.hashCode());
        assertNotEquals(c1.hashCode(), c.hashCode());
    }

    @Test
    void testSetAndGetServer() {
        Collection c1 = new Collection();
        c1.setServer("server1");
        String server1 = c1.getServer();


        Collection c2 = new Collection();
        c2.setServer("server2");
        String server2 = c2.getServer();

        assertAll(
                () -> assertEquals("server1", server1),
                () -> assertEquals("server2", server2)
        );
    }
}