package client.services;

import client.Config;
import client.ServerStatus;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.Collection;
import commons.CollectionInfo;

@Singleton
public class CollectionInterfaceService {

    private final ServerUtils serverUtils;
    private final VarService varService;
    private final Config config;

    @Inject
    public CollectionInterfaceService(ServerUtils serverUtils, VarService varService, Config config) {
        this.serverUtils = serverUtils;
        this.varService = varService;
        this.config = config;
    }


    public ServerStatus getServerStatus(String serverUrl, String collectionName,
                                        Collection selectedCollection) {
        ServerStatus status = serverUtils.checkServerStatus(serverUrl, collectionName);
        //when adding new container;
        if (selectedCollection == null) {
            if (varService.getCollections().
                    stream().
                    anyMatch(x -> x.getServer().equals(serverUrl) &&
                            x.name.equals(collectionName))) {
                status = ServerStatus.COLLECTION_ALREADY_ON_CLIENT;
            } else if (status == ServerStatus.NO_CHANGES_MADE) {
                long collectionId = status.getCollectionId();
                status = ServerStatus.COLLECTION_WILL_BE_ADDED;
                status.setCollectionId(collectionId);
            } else if (status == ServerStatus.COLLECTION_WILL_BE_SAVED) {
                status = ServerStatus.COLLECTION_DOES_NOT_EXIST;
            }
        }
        return status;
    }

    public boolean isStatusValidForCreate(ServerStatus status) {
        return status == ServerStatus.COLLECTION_WILL_BE_ADDED
                || status == ServerStatus.NO_CHANGES_MADE
                || status == ServerStatus.COLLECTION_WILL_BE_SAVED
                || status == ServerStatus.COLLECTION_DOES_NOT_EXIST;
    }

    public boolean createCollection(ServerStatus status,String server) {
        if(isStatusValidForCreate(status)) {
            Collection collection = serverUtils.addCollection(server);
            if(collection == null) {
                return false;
            }
            config.addCollection(new CollectionInfo(collection.id, collection.getServer()));
            if (!collection.getServer().equals(server)) {
                System.out.println("ERROR");
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isDefaultCollection(Collection selectedCollection) {
        return config.isDefaultCollection(new CollectionInfo(selectedCollection.id,
                selectedCollection.getServer()));
    }

    public boolean putCollection(Collection selectedCollection) {
        return serverUtils.putCollection(selectedCollection) != null;
    }

    public void addCollection(ServerStatus status, String server, Collection selectedCollection) {
        config.addCollection(new CollectionInfo(status.getCollectionId(), server));
    }

    public void setDefaultCollection(Collection selectedCollection) {
        config.setDefaultCollection(new CollectionInfo(selectedCollection.id,
                selectedCollection.getServer()));
    }
}
