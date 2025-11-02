package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateMessageTest {

    @Test
    void constructorTest1() {
        UpdateMessage m = new UpdateMessage();
        assertAll(
                () -> assertNotNull(m),
                () -> assertEquals(0, m.getCollectionId()),
                () -> assertEquals(0, m.getNoteId()),
                () -> assertEquals(-1, m.getSessionId()),
                () -> assertEquals(null, m.getAction())
        );
    }

    @Test
    void constructorTest2() {
        Action a = Action.CREATE;
        UpdateMessage m = new UpdateMessage(2,a);
        assertAll(
                () -> assertNotNull(m),
                () -> assertEquals(2, m.getCollectionId()),
                () -> assertEquals(0, m.getNoteId()),
                () -> assertEquals(-1, m.getSessionId()),
                () -> assertEquals(a, m.getAction())
        );
    }

    @Test
    void constructorTest3() {
        Action a = Action.CREATE;
        UpdateMessage m = new UpdateMessage(2,3,a);
        assertAll(
                () -> assertNotNull(m),
                () -> assertEquals(2, m.getCollectionId()),
                () -> assertEquals(3, m.getNoteId()),
                () -> assertEquals(-1, m.getSessionId()),
                () -> assertEquals(a, m.getAction())
        );
    }

    @Test
    void constructorTest4() {
        Action a = Action.CREATE;
        UpdateMessage m = new UpdateMessage(2,3,4,a);
        assertAll(
                () -> assertNotNull(m),
                () -> assertEquals(2, m.getCollectionId()),
                () -> assertEquals(3, m.getNoteId()),
                () -> assertEquals(4, m.getSessionId()),
                () -> assertEquals(a, m.getAction())
        );
    }

    @Test
    void getCollectionIdTest() {
        Action a = Action.CREATE;
        UpdateMessage m1 = new UpdateMessage(2,a);
        UpdateMessage m2 = new UpdateMessage(3,a);
        assertAll(
                () -> assertEquals(2,m1.getCollectionId()),
                () -> assertEquals(3,m2.getCollectionId())
        );
    }

    @Test
    void setCollectionIdTest() {
        Action a = Action.CREATE;
        UpdateMessage m = new UpdateMessage(2,a);
        m.setCollectionId(3);
        assertEquals(3,m.getCollectionId());
    }

    @Test
    void getNoteIdTest() {
        Action a = Action.CREATE;
        UpdateMessage m1 = new UpdateMessage(1,2,a);
        UpdateMessage m2 = new UpdateMessage(1,3,a);
        assertAll(
                () -> assertEquals(2,m1.getNoteId()),
                () -> assertEquals(3,m2.getNoteId())
        );
    }

    @Test
    void setNoteIdTest() {
        Action a = Action.CREATE;
        UpdateMessage m = new UpdateMessage(1,2,a);
        m.setNoteId(3);
        assertEquals(3,m.getNoteId());
    }

    @Test
    void getSessionIdTest() {
        Action a = Action.CREATE;
        UpdateMessage m1 = new UpdateMessage(1,1,2,a);
        UpdateMessage m2 = new UpdateMessage(1,1,3,a);
        assertAll(
                () -> assertEquals(2,m1.getSessionId()),
                () -> assertEquals(3,m2.getSessionId())
        );
    }

    @Test
    void setSessionIdTest() {
        Action a = Action.CREATE;
        UpdateMessage m = new UpdateMessage(1,2,a);
        m.setSessionId(3);
        assertEquals(3,m.getSessionId());
    }

    @Test
    void getActionTest() {
        Action a1 = Action.CREATE;
        Action a2 = Action.UPDATE;
        UpdateMessage m1 = new UpdateMessage(1,a1);
        UpdateMessage m2 = new UpdateMessage(1,a2);
        assertAll(
                () -> assertEquals(a1,m1.getAction()),
                () -> assertEquals(a2,m2.getAction())
        );
    }

    @Test
    void setActionTest() {
        Action a1 = Action.CREATE;
        Action a2 = Action.UPDATE;
        UpdateMessage m = new UpdateMessage(2,a1);
        m.setAction(a2);
        assertEquals(a2,m.getAction());
    }

    @Test
    void testEquals() {
        UpdateMessage m1 = new UpdateMessage();
        UpdateMessage m2 = new UpdateMessage();
        assertEquals(m1,m2);
        m1.setCollectionId(2);
        m2.setCollectionId(2);
        assertEquals(m1,m2);
        m1.setNoteId(2);
        m2.setNoteId(2);
        assertEquals(m1,m2);
        m1.setSessionId(2);
        m2.setSessionId(2);
        assertEquals(m1,m2);
        m1.setAction(Action.CREATE);
        m2.setAction(Action.CREATE);
        assertEquals(m1,m2);
    }

    @Test
    void testNotEquals() {
        UpdateMessage m1 = new UpdateMessage();
        UpdateMessage m2 = new UpdateMessage();
        m1.setCollectionId(1);
        m2.setCollectionId(2);
        assertNotEquals(m1,m2);
        m1.setCollectionId(2);
        m1.setNoteId(1);
        m2.setNoteId(2);
        assertNotEquals(m1,m2);
        m1.setNoteId(2);
        m1.setSessionId(1);
        m2.setSessionId(2);
        assertNotEquals(m1,m2);
        m1.setSessionId(2);
        m1.setAction(Action.UPDATE);
        m2.setAction(Action.CREATE);
        assertNotEquals(m1,m2);
    }

    @Test
    void testHashCode() {
        Action a1 = Action.CREATE;
        Action a2 = Action.UPDATE;
        UpdateMessage m1 = new UpdateMessage(1,1,1,a1);
        UpdateMessage m2 = new UpdateMessage(1,1,1,a1);
        UpdateMessage m3 = new UpdateMessage(1,1,2,a1);
        UpdateMessage m4 = new UpdateMessage(1,2,1,a1);
        UpdateMessage m5 = new UpdateMessage(2,1,1,a1);
        UpdateMessage m6 = new UpdateMessage(1,1,1,a2);
        assertAll(
                () -> assertEquals(m1.hashCode(), m2.hashCode()),
                () -> assertNotEquals(m1.hashCode(), m3.hashCode()),
                () -> assertNotEquals(m1.hashCode(), m4.hashCode()),
                () -> assertNotEquals(m1.hashCode(), m5.hashCode()),
                () -> assertNotEquals(m1.hashCode(), m6.hashCode())
        );
    }

    @Test
    void testToString() {
        Action a = Action.CREATE;
        UpdateMessage m = new UpdateMessage(1,1,1,a);
        String expected = "UpdateMessage{" +
                "collectionId=1" +
                ", noteId=1" +
                ", sessionId=1.0" +
                ", action=CREATE" +
                '}';
        assertEquals(expected,m.toString());
    }
}