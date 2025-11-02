package client.services;

import commons.Note;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TitleListServiceTest {


    @Test
    void prepareFilteredNotes() {
        List<Note> notes = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        Note note1 = new Note();
        Note note2 = new Note();
        Note note3 = new Note();
        note2.title = "New note (2)";
        note2.id = 2l;
        note3.title = "New note (3)";
        note3.id = 3l;
        note1.tags = new ArrayList<>(tags);
        tags.add("tag1");
        tags.add("tag2");
        note2.tags = new ArrayList<>(tags);
        tags.add("tag3");
        note3.tags = new ArrayList<>(tags);
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);

        TitleListService service = new TitleListService();

        List<Note> expectedResults = new ArrayList<>(notes);
        tags = new ArrayList<>();
        assertEquals(expectedResults, service.prepareFilteredNotes(notes, tags, false));
        assertEquals(expectedResults, service.prepareFilteredNotes(notes, tags, true));
        tags.add("tag1");
        expectedResults.remove(note1);
        assertEquals(expectedResults, service.prepareFilteredNotes(notes, tags, true));
        assertEquals(expectedResults, service.prepareFilteredNotes(notes, tags, true));
        tags.add("tag3");
        assertEquals(expectedResults, service.prepareFilteredNotes(notes, tags, true));
        expectedResults.remove(note2);
        assertEquals(expectedResults, service.prepareFilteredNotes(notes, tags, false));
    }

    @Test
    void containsAny() {
        TitleListService titleListService = new TitleListService();
        List<String> x = new ArrayList<>();
        List<String> y = new ArrayList<>();
        assertTrue(titleListService.containsAny(x,y));
        x.add("A");
        x.add("B");
        x.add("C");
        x.add("D");
        y = new ArrayList<>();
        assertTrue(titleListService.containsAny(x,y));
        assertFalse(titleListService.containsAny(y,x));
        y.add("A");
        assertTrue(titleListService.containsAny(y, x));
        assertTrue(titleListService.containsAny(x,y));
    }
}