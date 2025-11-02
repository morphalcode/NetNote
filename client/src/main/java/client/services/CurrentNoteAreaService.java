package client.services;

import client.Exceptions.PuttedNoteNotValidException;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.Collection;
import commons.Note;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.ext.image.attributes.ImageAttributesExtension;
import org.commonmark.ext.ins.InsExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

import static org.springframework.util.CollectionUtils.containsAny;

@Singleton
public class CurrentNoteAreaService {

    private final ServerUtils serverUtils;
    private final String cssPath = "/client/CSS/UserStyle.css";

    /**
     * constructor using DI. Used automatically by framework
     * @param serverUtils
     */
    @Inject
    public CurrentNoteAreaService(ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    /**
     * returns boolean representing whether the new title or new content is different from the current note
     * @param newTitle the new title
     * @param newContent the new content
     * @param currentNote the current note
     * @return the boolean
     */
    public boolean isCurrentNoteChanged(String newTitle, String newContent, Note currentNote) {
        if(currentNote == null) {
            return false;
        }
        String oldTitle = currentNote.title;
        String oldContent = currentNote.content;
        return !(Objects.equals(oldTitle, newTitle) &&
                Objects.equals(oldContent, newContent));
    }

    /**
     * returns boolean representing whether new title is different from current note
     * @param newTitle the new title
     * @param currentNote the current note
     * @return the boolean
     */
    public boolean isTitleChanged(String newTitle, Note currentNote) {
        if(currentNote == null) {
            return false;
        }
        String oldTitle = currentNote.title;
        return !Objects.equals(oldTitle, newTitle);
    }

    /**
     * returns boolean representing whether new content is different from current note
     * @param newContent the new content
     * @param currentNote the current note
     * @return the boolean
     */
    public boolean isContentChanged(String newContent, Note currentNote) {
        if(currentNote == null) {
            return false;
        }
        String oldContent = currentNote.content;
        return !Objects.equals(oldContent, newContent);
    }

    /**
     * updates note on server with new title and new content
     * @param newTitle the new title
     * @param newContent the new content
     * @param note the original note
     * @throws PuttedNoteNotValidException
     */
    public void putNote(String newTitle, String newContent, Note note)
            throws PuttedNoteNotValidException {
        note.title = newTitle;
        note.content = newContent;
        serverUtils.putNote(note);
    }

    /**
     * returns the list of current collections with the default collection in front
     * @param currentCollections
     * @param currentNote
     * @return the new list of collections
     */
    public List<Collection> prepareCollectionList(List<Collection> currentCollections,
                                                  Note currentNote){
        List<Collection> collections = new ArrayList<>(currentCollections);
        if(collections.isEmpty()) {
            return collections;
        }
        int index = collections
                .indexOf(collections
                        .stream()
                        .filter(x -> x.id == currentNote.collection.id)
                        .findFirst()
                        .orElse(collections.getFirst()));
        if(index == -1) {
            index = 0;
            System.out.println("ERROR, NOT FATAL, CURRENT NOTE not found");
        }
        Collections.swap(collections, 0, index);
        return collections;
    }

    /**
     * filters a list of notea by listed tags, with or without partial match
     * @param currentNotes
     * @param tags
     * @param isPartialMatch true if notes with any tag should be included, false if must be all tags.
     * @return the result of filtering
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
     * returns a list of built extensions for the markdown feature
     * @return the list
     */
    public List buildMarkdownExtensions() {
        return List.of(StrikethroughExtension.create(),
                TablesExtension.create(),
                InsExtension.create(),
                AutolinkExtension.create(),
                HeadingAnchorExtension.create(),
                ImageAttributesExtension.create());
    }

    /**
     * returns true if a non-empty css file exists at the cssPath else false
     * @return the boolean
     * @throws URISyntaxException
     * @throws IOException
     */
    public boolean cssExists() throws URISyntaxException, IOException {
        URL cssURL = getClass().getResource(cssPath);
        return cssURL != null &&
                !Files.readString(Path.of(cssURL.toURI())).isEmpty();
    }

    /**
     * Converts markdown to HTML
     *
     * @param markdown   markdown string
     * @param extensions the list of extensions
     * @return html string
     */
    public String convertToHTML(String markdown, List extensions) {
        //Parses markdown to renderable object
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);

        //Renders object to HTML
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        String html = renderer.render(document);

        return html;
    }

    /**
     * Uses bootstrap to style a raw html code block
     *
     * @param rawhtml COMPLETE THIS
     * @return Stylized html
     */
    public String stylizeHTML(String rawhtml) throws URISyntaxException, IOException {
        String start = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                (!cssExists() ? "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n" : "")+
                "</head>\n" +
                "<body>\n";
        String end = "\n</body>\n" +
                "</html>";

        return start + rawhtml + end;
    }

    /**
     * replaces references in markdown based on a title regex parameter and returns the result
     * @param markdown
     * @param titleRegex
     * @param replacer
     * @return the result
     */
    public String replaceReferences(String markdown, String titleRegex,
                                    Function<MatchResult, String> replacer){
        return Pattern.compile("\\[\\[" + titleRegex + "\\]\\]", Pattern.MULTILINE)
                .matcher(markdown)
                .replaceAll(replacer);
    }

    /**
     * returns number of references in markdown based on a title regex
     * @param markdown
     * @param titleRegex
     * @return the number of references
     */
    public int countReferences(String markdown, String titleRegex){
        return (int) Pattern.compile("\\[\\[" + titleRegex + "\\]\\]", Pattern.MULTILINE)
                .matcher(markdown)
                .results()
                .count();
    }

    /**
     * replaces tags in markdown based on a tag regex parameter and returns the result
     * @param markdown
     * @param tagRegex
     * @param replacer
     * @return the result
     */
    public String replaceTags(String markdown, String tagRegex,
                                    Function<MatchResult, String> replacer){
        return Pattern.compile("#" + tagRegex, Pattern.MULTILINE)
                .matcher(markdown)
                .replaceAll(replacer);
    }

    /**
     * updates references from old title to new title in markdown and returns number of changes
     * @param oldTitle
     * @param newTitle
     * @param notes
     * @param andSave generally true (for when changed notes should be saved), false for testing
     * @return returns number of changes
     * @throws PuttedNoteNotValidException
     */
    public int updateReferences(String oldTitle, String newTitle, List<Note> notes, boolean andSave) throws PuttedNoteNotValidException {
        int updates = 0;
        if(!oldTitle.equals(newTitle)){
            for (Note note : notes) {
                int referencesCount = countReferences(note.content, Pattern.quote(oldTitle));
                if(referencesCount > 0) {
                    note.content = replaceReferences(note.content,
                            Pattern.quote(oldTitle),
                            mr -> "\\[\\[" + newTitle + "\\]\\]");
                    if(andSave){
                        putNote(note.title, note.content, note);
                    }
                    updates += referencesCount;
                }
            }
        }
        return updates;
    }

    /**
     * renders references as links or errors in markdown based on whether they are valid, returns result
     * @param markdown
     * @param varService
     * @return result
     */
    public String renderReferences(String markdown, VarService varService){
        String error = "<span style='color:#FF0000;'> [[UNRECOGNISED]] </span>";
        return replaceReferences(markdown,
                "(.*?)",
                mr -> (varService.getNoteInCurrentCollectionByTitle(mr.group(1)) == null ? error : "<a href=\"#\">" +
                        mr.group(1) +
                        "</a>"));
    }

    /**
     * renders tags as filter links, returns result
     * @param markdown
     * @return result
     */
    public String renderTags(String markdown){
        return replaceTags(markdown,
                "(\\S+)\\b",
                mr -> "<a href=\"#\" role='button' class='btn btn-sm btn-primary'>#" +
                        mr.group(1) +
                        "</a>");
    }

    /**
     * returns boolean based on whether the server currentNote is equal to currentNote and both not null
     * (also false if server version null but client version not)
     * @param notesCurrentnote
     * @param currentNote
     * @return boolean
     */
    public boolean isNotesCurrentNoteValid(Note notesCurrentnote, Note currentNote) {
        if (notesCurrentnote == null && currentNote != null) {
            return false;
        }
        if (notesCurrentnote != null && currentNote != null &&
                (!notesCurrentnote.content.equals(currentNote.content)
                        || !notesCurrentnote.title.equals(currentNote.title))) {
            return false;
        }
        return true;
    }
}
