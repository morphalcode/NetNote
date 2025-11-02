package client;

import commons.CollectionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ConfigTest {
    Config config;

    @BeforeEach
    void setUp() {
        config = new Config();
        config.addCollection(new CollectionInfo(1, "server1"));
        config.addCollection(new CollectionInfo(2, "server1"));
        config.addCollection(new CollectionInfo(3, "server1"));
        config.addCollection(new CollectionInfo(1, "server2"));
        config.setDefaultCollection(new CollectionInfo(2, "server1"));
    }

    @Test
    void getCollections() {
        assertNotNull(config.getCollections());
        HashSet<CollectionInfo> set = new HashSet<>();
        set.add(new CollectionInfo(1, "server1"));
        set.add(new CollectionInfo(2, "server1"));
        set.add(new CollectionInfo(3, "server1"));
        set.add(new CollectionInfo(1, "server2"));
        assertEquals(set, config.getCollections());
    }


    @Test
    void getDefaultCollection() {
        assertNotNull(config.getDefaultCollection());
        assertEquals(new CollectionInfo(2, "server1"), config.getDefaultCollection());
    }

    @Test
    void setDefaultCollection() {
        config.setDefaultCollection(new CollectionInfo(3, "server1"));
        assertEquals(new CollectionInfo(3, "server1"), config.getDefaultCollection());
        config.setDefaultCollection(new CollectionInfo(4, "server2"));
        assertNotEquals(new CollectionInfo(4, "server2"), config.getDefaultCollection());
        assertTrue(config.getCollections().contains(config.getDefaultCollection()));
    }

    @Test
    void addCollection() {
        int size = config.getCollections().size();
        config.addCollection(new CollectionInfo(1, "server1"));
        assertEquals(size, config.getCollections().size());
        config.addCollection(new CollectionInfo(5, "server1"));
        assertEquals(size + 1, config.getCollections().size());
        assertTrue(config.getCollections().contains(new CollectionInfo(3, "server1")));
    }

    @Test
    void removeCollection() {
        int size = config.getCollections().size();
        config.removeCollection(new CollectionInfo(5, "server1"));
        assertEquals(size, config.getCollections().size());
        config.removeCollection(new CollectionInfo(1, "server1"));
        assertEquals(size - 1, config.getCollections().size());
        config.removeCollection(config.getDefaultCollection());
        assertEquals(size - 2, config.getCollections().size());
        assertTrue(config.getCollections().contains(config.getDefaultCollection()));
    }

    @Test
    void isDefaultCollection() {
        assertTrue(config.isDefaultCollection(new CollectionInfo(2, "server1")));
    }

}