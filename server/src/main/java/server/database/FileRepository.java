package server.database;

import commons.FileEntity;
import commons.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findByIdAndNote(long id, Note note);

    Optional<FileEntity> findByFileNameAndNote(String fileName, Note note);

    void deleteAllByNote(Note note);

    List<FileEntity> findAllByNote(Note note);
}
