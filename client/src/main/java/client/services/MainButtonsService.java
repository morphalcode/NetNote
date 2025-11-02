package client.services;

import client.Config;
import client.Exceptions.NoCollectionsConfiguredException;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.Collection;
import commons.CollectionInfo;
import commons.Note;

@Singleton
public class MainButtonsService {

    private final VarService varService;
    private final ServerUtils serverUtils;
    private final Config config;

    /**
     * Constructor using dependency injection
     * @param varService instance of VarService class
     * @param serverUtils instance of ServerUtils class
     * @param config instance of Config class
     */
    @Inject
    public MainButtonsService(VarService varService, ServerUtils serverUtils, Config config) {
        this.varService = varService;
        this.serverUtils = serverUtils;
        this.config = config;
    }

    /**
     * Checks if the specified collection is the "All Notes" Collection
     * @param collection the specified collection
     * @return true if it is, false otherwise
     */
    public boolean isAllNotes(Collection collection) {
        return collection.name.equals("All Notes")
                && collection.id == 0;
    }

    /**
     * Function for the add note button (adds a note)
     * @return the added note
     * @throws NoCollectionsConfiguredException if there are no collections configured
     */
    public Note addButtonClick() throws NoCollectionsConfiguredException {
        Note note;
        if (varService.getCurrentCollection() == null) {
            throw new NoCollectionsConfiguredException();
        } else if (isAllNotes(varService.getCurrentCollection())) {
            note = serverUtils.addNoteToCollection(config.getDefaultCollection());
        } else {
            Collection currentCollection = varService.getCurrentCollection();
            note = serverUtils.addNoteToCollection(
                    new CollectionInfo(currentCollection.id, currentCollection.getServer()));
        }
        return note;
    }
}
