package commons;

import Serializers.NoteDeserializer;
import Serializers.NoteSerializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@JsonDeserialize(using = NoteDeserializer.class)
@JsonSerialize(using = NoteSerializer.class)
@Entity
@Embeddable
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"title"}), @UniqueConstraint(columnNames = {"collection"})})
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @NotNull
    @NotEmpty
    public String title;

    @NotNull
    @Column(length = 10000)
    public String content;

    @JsonIgnore
    @Transient
    public boolean serialized = false;


    @ManyToOne
    @JoinColumn(name = "collectionId", nullable = false)
    public Collection collection;

    @Transient
    @JsonIgnore
    public List<String> tags;

    @JsonManagedReference
    @OneToMany(mappedBy = "note", cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    public List<FileEntity> files;

    /**
     * (Empty constructor) Creates a new note with the default values
     */
    public Note() {
        this.title = "New Note";
        this.content = "";
        setCollectionId(1);
    }

    /**
     * Adjust the title before saving to the database but after the ID is available.
     */
    @PrePersist
    public void updateTitle() {
        if (title.equals("New Note")) {
            title = "New Note" + (id == 1 ? "" : " (" + id + ")");
        }
    }

    /**
     * (Arguments constructor) Creates a new note with the provided arguments
     *
     * @param title   the title of the note
     * @param content the content of the note
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        setCollectionId(1);
    }

    /**
     * (Arguments constructor) Creates a new note with the provided arguments
     *
     * @param title        the title of the note
     * @param content      the content of the note
     * @param collectionID the id of the collection the note will belong to
     */
    public Note(String title, String content, long collectionID) {
        this.title = title;
        this.content = content;
        setCollectionId(collectionID);
    }

    /**
     * (Arguments constructor) Creates a new note with the provided arguments
     *
     * @param id      The id of the note.
     * @param title   the title of the note.
     * @param content the content of the note.
     */
    public Note(long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
        setCollectionId(1);
    }

    /**
     * (Arguments constructor) Creates a new note with the provided arguments
     *
     * @param id           The id of the note.
     * @param title        the title of the note.
     * @param content      the content of the note.
     * @param collectionId the id of the collection the note will belong to
     */
    public Note(long id, String title, String content, long collectionId) {
        this.id = id;
        this.title = title;
        this.content = content;
        setCollectionId(collectionId);
    }

    /**
     * COMPLETE THIS
     *
     * @param collectionId COMPLETE THIS
     */
    public void setCollectionId(long collectionId) {
        Collection setCollection = new Collection();
        setCollection.id = collectionId;
        this.collection = setCollection;
    }

    /**
     * returns the collection id in a safe way
     *
     * @return collection id
     */
    public long getCollectionId() {
        return collection != null ? collection.id : 0;
    }


    //Reflection builder cannot handle derived fields properly,
    // therefore, custom equals & hashcode methods

    /**
     * Compares between the two objects to see if they are equal
     *
     * @param obj object compared to
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Note other = (Note) obj;

        return id == other.id &&
                title.equals(other.title) &&
                content.equals(other.content) &&
                getCollectionId() == other.getCollectionId();
    }

    /**
     * Hashcode
     *
     * @return a unique hashcode
     */
    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 7 * result + title.hashCode();
        result = 17 * result + content.hashCode();
        result = 23 * result + Long.hashCode(getCollectionId());
        return result;
    }


    /**
     * Creates a string representation of this object
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }


}
