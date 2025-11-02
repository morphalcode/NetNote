package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollectionInfoTest {

    @Test
    void collectionInfoConstructorTest1() {
        CollectionInfo c = new CollectionInfo();
        assertAll(
                () -> assertNotNull(c),
                () -> assertEquals(-1, c.getCollectionId()),
                () -> assertEquals("test", c.getServer())
        );
    }

    @Test
    void collectionInfoConstructorTest2() {
        CollectionInfo c = new CollectionInfo(2,"myserver");
        assertAll(
                () -> assertNotNull(c),
                () -> assertEquals(2,c.getCollectionId()),
                () -> assertEquals("myserver", c.getServer())
        );
    }

    @Test
    void getCollectionId() {
        CollectionInfo c = new CollectionInfo(2,"myserver");
        assertEquals(2,c.getCollectionId());
    }

    @Test
    void getServer() {
        CollectionInfo c = new CollectionInfo(2,"myserver");
        assertEquals("myserver", c.getServer());
    }

    @Test
    void testEquals() {
        CollectionInfo c1 = new CollectionInfo();
        assertEquals(c1,c1);
        CollectionInfo c2 = new CollectionInfo();
        assertEquals(c1,c2);
        c1 = new CollectionInfo(2,"myserver");
        c2 = new CollectionInfo(2,"myserver");
        assertEquals(c1,c2);
    }

    @Test
    void testNotEquals() {
        CollectionInfo c1= new CollectionInfo(2,"myserver");
        CollectionInfo c2 = new CollectionInfo(1,"myserver");
        assertNotEquals(c1,c2);
        c1 = new CollectionInfo(1,"myotherserver");
        assertNotEquals(c1,c2);
    }

    @Test
    void testHashCode() {
        CollectionInfo c1 = new CollectionInfo(1,"myserver");
        CollectionInfo c2 = new CollectionInfo(1,"myserver");
        CollectionInfo c3 = new CollectionInfo(2,"myserver");
        CollectionInfo c4 = new CollectionInfo(1,"myotherserver");
        CollectionInfo c5 = new CollectionInfo(2,"myotherserver");
        assertAll(
                () -> assertEquals(c1, c2),
                () -> assertNotEquals(c1, c3),
                () -> assertNotEquals(c1, c4),
                () -> assertNotEquals(c1, c5)
        );

    }
}