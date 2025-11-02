package commons;

import java.util.Objects;

public class CollectionInfo {
    private final long collectionId;
    private final String server;

    /**
     * Constructor
     *
     * @param collectionId if of the collection
     * @param server       server of the collection
     */
    public CollectionInfo(long collectionId, String server) {
        this.collectionId = collectionId;
        this.server = server;
    }

    /**
     * Default constructor for json mapping
     */
    public CollectionInfo() {
        collectionId = -1;
        server = "test";
    }

    /**
     * getter
     *
     * @return id of collection
     */
    public long getCollectionId() {
        return collectionId;
    }

    /**
     * getter
     *
     * @return server of collection
     */
    public String getServer() {
        return server;
    }

    /**
     * equals
     *
     * @param o compared to
     * @return if fields equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionInfo that = (CollectionInfo) o;
        return collectionId == that.collectionId && Objects.equals(server, that.server);
    }

    /**
     * hashcode
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(collectionId, server);
    }
}