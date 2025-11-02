package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NoteTest {

    @Test
    void testEmptyConstructor() {
        Note n = new Note();
        assertAll(
                () -> assertEquals("New Note", n.title),
                () -> assertEquals("", n.content)
        );
    }

    @Test
    void test2ArgumentConstructor() {
        Note n = new Note("Title", "Content");
        assertAll(
                () -> assertEquals("Title", n.title),
                () -> assertEquals("Content", n.content)
        );
    }

    @Test
    void test3ArgumentConstructor() {
        Note n = new Note(0, "Title", "Content");
        assertAll(
                () -> assertEquals(0, n.id),
                () -> assertEquals("Title", n.title),
                () -> assertEquals("Content", n.content)
        );
    }

    @Test
    void testEqualsSame() {
        Note n = new Note();
        assertEquals(n, n);
    }

    @Test
    void testEqualsEquals() {
        Note n1 = new Note();
        Note n2 = new Note();
        assertEquals(n1, n2);
    }

    @Test
    void testNotEqualTitleEquals() {
        Note n1 = new Note("Title1", "Same content");
        Note n2 = new Note("Title2", "Same content");
        assertNotEquals(n1, n2);
    }

    @Test
    void testNotEqualContentEquals() {
        Note n1 = new Note("Same title", "Content1");
        Note n2 = new Note("Same title", "Content2");
        assertNotEquals(n1, n2);
    }

    @Test
    void testHashCodeEqual() {
        Note n1 = new Note();
        Note n2 = new Note();
        assertEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    void testHashCodeNotEqual() {
        Note n1 = new Note("Same title", "Content1");
        Note n2 = new Note("Same title", "Content2");
        assertNotEquals(n1.hashCode(), n2.hashCode());
    }

    @Test
    void testUpdateTitle() {
        Note n1 = new Note(1, "New Note", "Content");
        n1.updateTitle();

        Note n2 = new Note(2, "New Note", "Content");
        n2.updateTitle();

        Note n3 = new Note(6, "New Note", "Content");
        n3.updateTitle();

        assertAll(
                () -> assertEquals("New Note", n1.title),
                () -> assertEquals("New Note (2)", n2.title),
                () -> assertEquals("New Note (6)", n3.title)
        );
    }

    @Test
    void testSetCollection() {
        Collection c = new Collection();

        Note n = new Note(1, "New Note", "Content");
        n.setCollectionId(0);

        assertAll(
                () -> assertEquals(0, n.collection.id),
                () -> assertEquals(c, n.collection)
        );
    }

    @Test
    void testGetCollectionId() {
        Note n1 = new Note(1, "Note1", "Content");
        n1.collection = null;

        Note n2 = new Note(1, "Note2", "Content");
        n2.collection = new Collection(2, "New Collection");

        assertAll(
                () -> assertEquals(0, n1.getCollectionId()),
                () -> assertEquals(2, n2.getCollectionId())
        );
    }
}