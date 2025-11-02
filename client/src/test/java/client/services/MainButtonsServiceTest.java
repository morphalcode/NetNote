package client.services;

import client.Config;
import client.Exceptions.NoCollectionsConfiguredException;
import client.spies.ServerUtilsSpy;
import client.utils.ServerUtils;
import client.webSocket.WebSocketManager;
import commons.Collection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainButtonsServiceTest {

    @Test
    public void ConstructorTest() {
        MainButtonsService mainButtonsService = new MainButtonsService(
                new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config()),
                new ServerUtils(new Config(), new WebSocketManager()),
                new Config());
        assertNotNull(mainButtonsService);
    }

    @Test
    public void isAllNotesSuccessTest() {
        MainButtonsService mainButtonsService = new MainButtonsService(
                new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config()),
                new ServerUtils(new Config(), new WebSocketManager()),
                new Config());
        Collection collection = new Collection(0L, "All Notes");
        assertTrue(mainButtonsService.isAllNotes(collection));
    }

    @Test
    public void isAllNotesWrongIdTest() {
        MainButtonsService mainButtonsService = new MainButtonsService(
                new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config()),
                new ServerUtils(new Config(), new WebSocketManager()),
                new Config());
        Collection collection = new Collection(1L, "All Notes");
        assertFalse(mainButtonsService.isAllNotes(collection));
    }

    @Test
    public void isAllNotesWrongNameTest() {
        MainButtonsService mainButtonsService = new MainButtonsService(
                new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config()),
                new ServerUtils(new Config(), new WebSocketManager()),
                new Config());
        Collection collection = new Collection(0L, "Wrong Name");
        assertFalse(mainButtonsService.isAllNotes(collection));
    }

    @Test
    public void isAllNotesWrongBothTest() {
        MainButtonsService mainButtonsService = new MainButtonsService(
                new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config()),
                new ServerUtils(new Config(), new WebSocketManager()),
                new Config());
        Collection collection = new Collection(1L, "Wrong Name");
        assertFalse(mainButtonsService.isAllNotes(collection));
    }

    @Test
    public void addButtonClickNullTest() {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        MainButtonsService mainButtonsService = new MainButtonsService(
                varService,
                new ServerUtils(new Config(), new WebSocketManager()),
                new Config());
        varService.setCurrentCollection(null);
        assertThrows(NoCollectionsConfiguredException.class, mainButtonsService::addButtonClick);
    }

    @Test
    public void addButtonClickAllNotesTest() throws NoCollectionsConfiguredException {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        ServerUtilsSpy serverUtilsSpy = new ServerUtilsSpy(new Config(), new WebSocketManager());
        MainButtonsService mainButtonsService = new MainButtonsService(
                varService,
                serverUtilsSpy,
                new Config());
        varService.setCurrentCollection(new Collection(0L, "All Notes"));
        mainButtonsService.addButtonClick();
        assertTrue(serverUtilsSpy.isAddNoteToCollectionCalled());
    }

    @Test
    public void addButtonClickOtherwiseTest() throws NoCollectionsConfiguredException {
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());
        ServerUtilsSpy serverUtilsSpy = new ServerUtilsSpy(new Config(), new WebSocketManager());
        MainButtonsService mainButtonsService = new MainButtonsService(
                varService,
                serverUtilsSpy,
                new Config());
        varService.setCurrentCollection(new Collection(1L, "Test Collection"));
        mainButtonsService.addButtonClick();
        assertTrue(serverUtilsSpy.isAddNoteToCollectionCalled());
    }
}
