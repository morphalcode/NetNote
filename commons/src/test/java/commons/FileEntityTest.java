package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileEntityTest {

    @Test
    void testEmptyConstructor(){
        FileEntity file = new FileEntity();
        assertNotNull(file);
        assertEquals(file.fileName, "New File");
    }

    @Test
    void testArgumentsConstructor(){
        FileEntity file = new FileEntity("File", "text", new byte[0], 1);
        assertEquals("File", file.fileName);
        assertEquals("text", file.type);
        assertEquals(1, file.note.id);
    }

    @Test
    void testEquals() {
        FileEntity file1 = new FileEntity("File", "text", new byte[0], 1);
        FileEntity file2 = new FileEntity("File", "text", new byte[0], 1);
        FileEntity file3 = new FileEntity();
        assertEquals(file1, file2);
        assertNotEquals(file1, file3);
    }

    @Test
    void testSetNoteId() {
        FileEntity file = new FileEntity("File", "text", new byte[0], 1);
        file.setNoteId(2);
        assertEquals(2, file.note.id);
    }

    @Test
    void testGetNoteId() {
        FileEntity file = new FileEntity("File", "text", new byte[0], 1);
        assertEquals(1, file.getNoteId());
    }

    @Test
    void testHashCode() {
        FileEntity file1 = new FileEntity("File", "text", new byte[0], 1);
        FileEntity file2 = new FileEntity("File", "text", new byte[0], 1);
        FileEntity file3 = new FileEntity("f", "t", new byte[0], 2);
        assertEquals(file1.hashCode(), file2.hashCode());
        assertNotEquals(file1.hashCode(), file3.hashCode());
    }

}