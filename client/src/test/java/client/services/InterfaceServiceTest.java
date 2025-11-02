package client.services;

import client.Config;
import client.Exceptions.ConfigFileCorruptedException;
import client.spies.ConfigSpy;
import commons.Collection;
import commons.Note;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InterfaceServiceTest {

    @Test
    public void ConstructorTest() {
        InterfaceService interfaceService = new InterfaceService(new Config());
        assertNotNull(interfaceService);
    }

    @Test
    public void initializeTest() throws ConfigFileCorruptedException {
        ConfigSpy configSpy = new ConfigSpy();
        InterfaceService interfaceService = new InterfaceService(configSpy);
        interfaceService.initialize();
        assertTrue(configSpy.isInitializeCalled());
    }

    @Test
    public void refreshTest() {
        ConfigSpy configSpy = new ConfigSpy();
        InterfaceService interfaceService = new InterfaceService(configSpy);
        interfaceService.refresh();
        assertTrue(configSpy.isSaveToFileCalled());
    }

    @Test
    public void getFiltersTest() {
        InterfaceService interfaceService = new InterfaceService(new Config());
        List<MenuItem> items = new ArrayList<>();
        CheckMenuItem item1 = new CheckMenuItem("filter1");
        CheckMenuItem item2 = new CheckMenuItem("filter2");
        CheckMenuItem item3 = new CheckMenuItem("filter3");
        item1.setSelected(true);
        item3.setSelected(true);
        items.add(item1);
        items.add(item2);
        items.add(item3);
        List<String> resultFilters = new ArrayList<>();
        resultFilters.add("filter1");
        resultFilters.add("filter3");
        assertEquals(resultFilters, interfaceService.getFilters(items));
    }

    @Test
    public void generateFilterMenuItemsNullTest() {
        InterfaceService interfaceService = new InterfaceService(new Config());
        List<MenuItem> existingItemsTest = new ArrayList<>();
        CheckMenuItem item1 = new CheckMenuItem("filter1");
        CheckMenuItem item2 = new CheckMenuItem("filter2");
        CheckMenuItem item3 = new CheckMenuItem("filter3");
        item1.setSelected(true);
        item3.setSelected(true);
        existingItemsTest.add(item1);
        existingItemsTest.add(item2);
        existingItemsTest.add(item3);
        assertEquals(existingItemsTest, interfaceService.generateFilterMenuItems(null, existingItemsTest));
    }

    @Test
    public void generateFilterMenuItemsEmptyTest() {
        InterfaceService interfaceService = new InterfaceService(new Config());
        List<String> allTagsTest = new ArrayList<>();
        List<MenuItem> existingItemsTest = new ArrayList<>();
        CheckMenuItem item1 = new CheckMenuItem("filter1");
        CheckMenuItem item2 = new CheckMenuItem("filter2");
        CheckMenuItem item3 = new CheckMenuItem("filter3");
        item1.setSelected(true);
        item3.setSelected(true);
        existingItemsTest.add(item1);
        existingItemsTest.add(item2);
        existingItemsTest.add(item3);
        assertEquals(existingItemsTest, interfaceService.generateFilterMenuItems(allTagsTest, existingItemsTest));
    }

    @Test
    public void generateFilterMenuItemsTest() {
        InterfaceService interfaceService = new InterfaceService(new Config());
        List<String> allTagsTest = new ArrayList<>();
        allTagsTest.add("filter1");
        allTagsTest.add("filter2");
        allTagsTest.add("filter3");
        CheckMenuItem item1 = new CheckMenuItem("filter1");
        CheckMenuItem item2 = new CheckMenuItem("filter2");
        CheckMenuItem item3 = new CheckMenuItem("filter3");
        item1.setSelected(true);
        item3.setSelected(true);
        List<MenuItem> existingItemsTest = new ArrayList<>();
        existingItemsTest.add(item1);
        existingItemsTest.add(item2);
        existingItemsTest.add(item3);
        assertEquals(3, interfaceService.generateFilterMenuItems(allTagsTest, existingItemsTest).size());
    }

    @Test
    public void isNoteAndCurrentAllSuccessTest() {
        InterfaceService interfaceService = new InterfaceService(new Config());
        Collection collection = new Collection(1L, "TestCollection");
        Note note = new Note(1L, "Title", "Content", 2L);
        assertTrue(interfaceService.isNoteAndCurrentAll(note, collection));
    }

    @Test
    public void isNoteAndCurrentAllCollectionId0LTest() {
        InterfaceService interfaceService = new InterfaceService(new Config());
        Collection collection = new Collection(0L, "TestCollection");
        Note note = new Note(1L, "Title", "Content", 2L);
        assertFalse(interfaceService.isNoteAndCurrentAll(note, collection));
    }

    @Test
    public void isNoteAndCurrentAllNoteCollectionIdEqualTest() {
        InterfaceService interfaceService = new InterfaceService(new Config());
        Collection collection = new Collection(1L, "TestCollection");
        Note note = new Note(1L, "Title", "Content", 1L);
        assertFalse(interfaceService.isNoteAndCurrentAll(note, collection));
    }
}
