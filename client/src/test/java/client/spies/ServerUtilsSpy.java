package client.spies;

import client.Config;
import client.utils.ServerUtils;
import client.webSocket.WebSocketManager;
import commons.CollectionInfo;
import commons.Note;

public class ServerUtilsSpy extends ServerUtils {

    private boolean addNoteToCollectionCalled = false;

    public ServerUtilsSpy(Config config, WebSocketManager webSocketManager) {
        super(config, webSocketManager);
    }

    @Override
    public Note addNoteToCollection(CollectionInfo collection) {
        addNoteToCollectionCalled = true;
        return new Note();
    }

    public boolean isAddNoteToCollectionCalled() {
        return addNoteToCollectionCalled;
    }
}
