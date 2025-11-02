package client.services;

import client.Config;
import client.utils.ServerUtils;
import client.webSocket.WebSocketManager;
import commons.Collection;
import commons.Note;
import commons.UpdateMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VarServiceTest {

    //Couldn't test methods using varService.collections or varService.notes variables
    //because they are private and to set them I need a server

    @Test
    public void ConstructorTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        assertNotNull(varService);
    }

    @Test
    void tagUpdaterTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        List<Note> notes = new ArrayList<>();
        List<String> tags = varService.tagUpdater(notes);
        assertTrue(tags.isEmpty());
        notes.add(new Note("Note1","#tag1 #tag2"));
        notes.add(new Note("Note2","#tag2 #tag1"));
        notes.add(new Note("Note3","#tag3 # test"));
        tags = varService.tagUpdater(notes);
        List<String> expectedTags = List.of("tag1","tag2","tag2","tag1","tag3");
        assertEquals(expectedTags, tags);

    }

    @Test
    void getAndSetCurrentCollectionsTest(){
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        assertNull(varService.getCurrentCollection());
        Collection c = new Collection();
        varService.setCurrentCollection(c);
        assertEquals(c,varService.getCurrentCollection());

    }

    @Test
    void getAndSetCurrentNoteTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        assertNull(varService.getCurrentNote());
        Note n = new Note();
        varService.setCurrentNote(n);
        assertEquals(n,varService.getCurrentNote());
    }

    @Test
    void getCurrentCollectionNotesTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        assertTrue(varService.getCurrentCollectionNotes().isEmpty());
        Collection c = new Collection();
        Note n1 = new Note("Note1","");
        Note n2 = new Note("Note2","");
        c.addNoteToCollection(n1);
        c.addNoteToCollection(n2);
        varService.setCurrentCollection(c);
        assertEquals(List.of(n1,n2),varService.getCurrentCollectionNotes());
    }

    @Test
    void getAndSetAllTagsTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        assertNull(varService.getAllTags());
        List<String> tags = List.of("tag1","tag2","tag3");
        varService.setAllTags(tags);
        assertEquals(tags,varService.getAllTags());
    }

    @Test
    void getNoteInCurrentCollectionByTitleTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        Collection c = new Collection();
        Note n1 = new Note("Note1","");
        Note n2 = new Note("Note2","");
        c.addNoteToCollection(n1);
        c.addNoteToCollection(n2);
        varService.setCurrentCollection(c);
        assertAll(
                () -> assertEquals(n1,varService.getNoteInCurrentCollectionByTitle("Note1")),
                () -> assertEquals(n2,varService.getNoteInCurrentCollectionByTitle("Note2")),
                () -> assertNull(varService.getNoteInCurrentCollectionByTitle("Note3s"))
        );
    }

    @Test
    void isCurrentNoteTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        Note n1 = new Note("Note1","");
        Note n2 = new Note("Note2","");
        assertFalse(varService.isCurrentNote(n1));
        assertFalse(varService.isCurrentNote(n2));
        varService.setCurrentNote(n1);
        assertFalse(varService.isCurrentNote(n2));
        assertTrue(varService.isCurrentNote(n1));
        varService.setCurrentNote(n2);
        assertFalse(varService.isCurrentNote(n1));
        assertTrue(varService.isCurrentNote(n2));
    }

    @Test
    void isNoteChangedByAnotherClientTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        Note n1 = new Note(1,"Note1","");
        Note n2 = new Note(2,"Note2","");
        n1.collection.setServer("server");
        n2.collection.setServer("anotherserver");
        UpdateMessage m1 = new UpdateMessage();
        UpdateMessage m2 = new UpdateMessage();
        m1.setNoteId(1);
        m2.setNoteId(2);
        varService.setCurrentNote(n1);
        assertAll(
                () -> assertTrue(varService.isNoteChangedByAnotherClient(m1,"server")),
                () -> assertFalse(varService.isNoteChangedByAnotherClient(m1,"anotherserver")),
                () -> assertFalse(varService.isNoteChangedByAnotherClient(m2,"server")),
                () -> assertFalse(varService.isNoteChangedByAnotherClient(m2,"anotherserver"))
        );
        varService.setCurrentNote(n2);
        assertTrue(varService.isNoteChangedByAnotherClient(m2,"anotherserver"));
    }

    @Test
    void isCollectionChangedByAnotherClientTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        Collection c1 = new Collection(1,"Collection1");
        Collection c2 = new Collection(2,"Collection2");
        c1.setServer("server");
        c2.setServer("anotherserver");
        UpdateMessage m1 = new UpdateMessage();
        UpdateMessage m2 = new UpdateMessage();
        m1.setCollectionId(1);
        m2.setCollectionId(2);
        varService.setCurrentCollection(c1);
        assertAll(
                () -> assertTrue(varService.isCollectionChangedByAnotherClient(m1,"server")),
                () -> assertFalse(varService.isCollectionChangedByAnotherClient(m1,"anotherserver")),
                () -> assertFalse(varService.isCollectionChangedByAnotherClient(m2,"server")),
                () -> assertFalse(varService.isCollectionChangedByAnotherClient(m2,"anotherserver"))
        );
        varService.setCurrentCollection(c2);
        assertTrue(varService.isCollectionChangedByAnotherClient(m2,"anotherserver"));
    }
}