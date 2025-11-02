package client.services;

import com.google.inject.Singleton;
import commons.Note;

import java.util.List;

@Singleton
public class TitleListService {

    /**
     * @param currentNotes notes to be filtered
     * @param tags filters
     * @param isPartialMatch is partial match enabled
     * @return ist of filtered notes depending on tags and partial match
     */
    public List<Note> prepareFilteredNotes(List<Note> currentNotes, List<String> tags,
                                           boolean isPartialMatch){
        if(isPartialMatch){
            return currentNotes.stream().filter(x->containsAny(x.tags,tags)).toList();
        }else{
            return currentNotes.stream().filter(x->x.tags.containsAll(tags)).toList();
        }
    }

    /**
     * Checks if the first array contains any elements from the second
     * @param noteTags - List<String> Array to be checked
     * @param filtered - List<String> Array to check with
     * @return - boolean, whether the first array contains any elements from the second
     */
    public boolean containsAny(List<String> noteTags, List<String> filtered){
        if(noteTags == null || filtered == null){
            return false;
        }
        if(filtered.isEmpty()){
            return true;
        }else{
            if(noteTags.isEmpty()){
                return false;
            }
            for (String s : filtered) {
                if (noteTags.contains(s)) {
                    return true;
                }
            }
            return false;
        }
    }
}
