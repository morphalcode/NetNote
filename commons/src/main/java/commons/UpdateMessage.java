package commons;

import java.util.Objects;

public class UpdateMessage {
    private long collectionId;
    private long noteId;
    private double sessionId;
    private Action action;

    public UpdateMessage(long collectionId, long noteId, double sessionId, Action action) {
        this.collectionId = collectionId;
        this.noteId = noteId;
        this.sessionId = sessionId;
        this.action = action;
    }

    public UpdateMessage(long collectionId, long noteId, Action action) {
        this.collectionId = collectionId;
        this.noteId = noteId;
        sessionId = -1.0;
        this.action = action;
    }

    public UpdateMessage() {
        this.collectionId = 0;
        this.noteId = 0;
        sessionId = -1.0;
        action = null;
    }

    public UpdateMessage(long collectionId, Action action) {
        this.collectionId = collectionId;
        this.noteId = 0;
        sessionId = -1.0;
        this.action = action;
    }

    public long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(long collectionId) {
        this.collectionId = collectionId;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public double getSessionId() {
        return sessionId;
    }

    public void setSessionId(double sessionId) {
        this.sessionId = sessionId;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateMessage that = (UpdateMessage) o;
        return collectionId == that.collectionId && noteId == that.noteId && Double.compare(sessionId, that.sessionId) == 0 && action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectionId, noteId, sessionId, action);
    }

    @Override
    public String toString() {
        return "UpdateMessage{" +
                "collectionId=" + collectionId +
                ", noteId=" + noteId +
                ", sessionId=" + sessionId +
                ", action=" + action +
                '}';
    }
}


