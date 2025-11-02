package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import commons.Note;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query(
            nativeQuery = true,
            value
                    = "SELECT * FROM note n WHERE n.title = :title LIMIT 1")
    Optional<Note> findNoteByTitle(@Param("title") String title);
}
