package commons;

import Serializers.CollectionDeserializer;
import Serializers.CollectionSerializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@JsonDeserialize(using = CollectionDeserializer.class)
@JsonSerialize(using = CollectionSerializer.class)
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name"})})
public class Collection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @NotNull
    @NotEmpty
    public String name;

    @NotNull
    @NotEmpty
    private String server;

    @JsonIgnore
    @Transient
    public boolean serialized = false;


    @OneToMany(mappedBy = "collection", cascade = {CascadeType.PERSIST,
            CascadeType.REMOVE, CascadeType.MERGE, CascadeType.REFRESH}, orphanRemoval = true)
    public List<Note> notes;

    /**
     * (Empty constructor) Creates a new empty collection with a generic name
     */
    public Collection() {
        this.name = "New Collection";
        notes = new ArrayList<>();
    }

    /**
     * Adjusts the name after the ID is available
     */
    @PrePersist
    public void updateName() {
        if (name.equals("New Collection")) {
            name = (id == 1 ? "Default Collection" : "New Collection (" + id + ")");
        }
    }

    /**
     * (Arguments constructor) Creates a new empty collection with a given name
     *
     * @param name the given name
     */
    public Collection(String name) {
        this.name = name;
        notes = new ArrayList<>();
    }

    /**
     * (Arguments constructor) Creates a new collection with a given name and a given set of notes
     *
     * @param name  the given name
     * @param notes the given set of notes
     */
    public Collection(String name, List<Note> notes) {
        this.name = name;
        this.notes = notes;
    }

    /**
     * (Arguments constructor) Creates a new collection with a given id and a given name
     *
     * @param id   the given id
     * @param name the given name
     */
    public Collection(long id, String name) {
        this.id = id;
        this.name = name;
        notes = new ArrayList<>();
    }

    /**
     * (Arguments constructor) Creates a new collection with all arguments provided as parameters
     *
     * @param id    the given id
     * @param name  the given name
     * @param notes the given set of notes
     */
    public Collection(long id, String name, List<Note> notes) {
        this.id = id;
        this.name = name;
        this.notes = notes;
    }

    /**
     * Adds a note to the collection
     *
     * @param note the note to be added
     */
    public void addNoteToCollection(Note note) {
        notes.add(note);
        // note.collection = this;
    }

    /**
     * Returns the String url of the server
     *
     * @return the url of the server
     */
    public @NotNull @NotEmpty String getServer() {
        return server;
    }

    /**
     * Used by the server when sent to the api to save the url of the api
     *
     * @param server the url of the api
     */
    public void setServer(@NotNull @NotEmpty String server) {
        this.server = server;
    }

    /**
     * Checks whether a provided object is the same as this collection
     *
     * @param o the provided object
     * @return true if the provided object and this collection are the same or false otherwise
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    /**
     * Creates the hashcode of this collection
     *
     * @return the hashcode of this collection
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Creates a string version of this collection
     *
     * @return the string version
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    /**
     * Returns only the notes inside the collection that contain a given input
     *
     * @param input the input given
     * @return the filtered list of notes
     */
    public List<Note> filterNotesInCollection(String input) {
        return notes
                .stream()
                .filter(n -> (n.title.contains(input) || n.content.contains(input)))
                .toList();
    }
}
