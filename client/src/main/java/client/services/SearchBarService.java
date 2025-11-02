package client.services;

import com.google.inject.Singleton;
import commons.Note;

@Singleton
public class SearchBarService {

    /**
     * Checks whether a string is in a note's title or content
     * @param note the note
     * @param search the string
     * @return boolean result of check
     */
    public boolean isMatch(Note note, String search) {
        if (search == null ||search.isBlank() || search.isEmpty()) {
            return true;//true if there is nothing in the search bar
        }
        String keyWords = search.toLowerCase();
        if (note.title.toLowerCase().contains(keyWords)) {
            return true;//true if the keywords are part of the title
        } else if (note.content.toLowerCase().contains(keyWords)) {
            return true;//true if the keywords are part of the content
        }
        return false;
    }
}
