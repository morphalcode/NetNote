package server.services;

import commons.Collection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.CollectionRepository;

import java.util.List;

@Service
public class CollectionService {

    private final CollectionRepository db;

    /**
     * Constructor using dependency injection
     *
     * @param db the collection database
     */
    public CollectionService(CollectionRepository db) {
        this.db = db;
    }

    /**
     * Saves the provided collection in the database
     *
     * @param collection the provided collection
     */
    public void save(Collection collection) {
        db.save(collection);
    }

    /**
     * Searches if a collection with the provided id exists in the database
     *
     * @param id the provided id
     * @return true if it does, false otherwise
     */
    public boolean existsById(long id) {
        return db.existsById(id);
    }

    /**
     * Tries to find a collection with the provided id in the database
     *
     * @param id the provided id
     * @return the collection if it does find it, null otherwise
     */
    public Collection findById(Long id) {
        return db.findById(id).orElse(null);
    }

    /**
     * Gets all collections from the database
     *
     * @return all collections from the database
     */
    public List<Collection> get() {
        List<Collection> collections = db.findAll();
        for (Collection collection : collections) {
            Hibernate.initialize(collection.notes);
        }
        return collections;
    }

    /**
     * Posts/Saves in the database the provided collection with the server in the provided request
     *
     * @param collection the provided collection
     * @param request    the provided request
     * @return the collection posted/saved
     */
    public Collection post(@Valid Collection collection, HttpServletRequest request) {
        String server = getServerFromRequest(request);
        var col = new Collection();
        col.setServer(server);
        col.name = collection.name;
        col.notes = collection.notes;
        db.save(col);
        return col;
    }

    /**
     * Gets the collection with the provided id from the database
     *
     * @param id the provided id
     * @return response entity ok if it exists, not found if it does not
     */
    public ResponseEntity<Collection> get(Long id) {
        var temp = db.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        Collection collection = temp.getBody();
        Hibernate.initialize(collection != null ? collection.notes : null);
        return temp;
    }

    /**
     * Puts the provided collection in the database at the provided id
     *
     * @param id         the provided id
     * @param collection the provided collection
     * @return the saved collection
     */
    public Collection put(Long id, @Valid Collection collection) {
        Collection col = db.findById(id).get();
        col.name = collection.name;
        db.save(col);
        return col;
    }

    /**
     * @param request http request
     * @return String url of this instance of the server
     */
    public String getServerFromRequest(HttpServletRequest request) {
        if (request == null) {
            return "a";
        }
        String scheme = request.getHeader("X-Forwarded-Proto") != null
                ? request.getHeader("X-Forwarded-Proto")
                : request.getScheme();
        String host = request.getHeader("X-Forwarded-Host") != null
                ? request.getHeader("X-Forwarded-Host")
                : request.getServerName();
        int port = request.getHeader("X-Forwarded-Port") != null
                ? Integer.parseInt(request.getHeader("X-Forwarded-Port"))
                : request.getServerPort();
        return scheme + "://" + host + (port != 80 && port != 443 ? ":" + port : "");
    }
}
