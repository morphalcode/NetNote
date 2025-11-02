package client.services;

import commons.Note;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchBarServiceTest {

    @Test
    void isMatchTest() {
        SearchBarService service = new SearchBarService();
        Note note1 = new Note("Title with test","Content");
        Note note2 = new Note("Title","Content with test");
        Note note3 = new Note("Title","Content with Test");
        Note note4 = new Note("Title","Content");
        assertAll(
                () -> assertTrue(service.isMatch(note4, "")),
                () -> assertTrue(service.isMatch(note1, "test")),
                () -> assertTrue(service.isMatch(note2, "test")),
                () -> assertTrue(service.isMatch(note3, "test")),
                () -> assertFalse(service.isMatch(note4, "test"))
        );
    }
}