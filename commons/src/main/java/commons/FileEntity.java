package commons;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String fileName;

    public String type;

    @Lob
    public byte[] data;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "noteId", nullable = false)
    public Note note;

    public FileEntity() {
        this.fileName = "New File";
    }

    public FileEntity(String fileName, String type, byte[] data, long noteId) {
        this.fileName = fileName;
        this.type = type;
        this.data = data;
        setNoteId(noteId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity file = (FileEntity) o;
        return Objects.equals(id, file.id) && Objects.equals(fileName, file.fileName)
                && Objects.equals(type, file.type) && Objects.deepEquals(data, file.data)
                && note.id == file.note.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, type, Arrays.hashCode(data), note.id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    public void setNoteId(long noteId) {
        Note setNote = new Note();
        setNote.id = noteId;
        this.note = setNote;
    }

    public long getNoteId() {
        return note != null ? note.id : 0;
    }
}
