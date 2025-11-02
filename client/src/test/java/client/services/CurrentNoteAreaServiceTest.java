package client.services;

import client.Config;
import client.Exceptions.PuttedNoteNotValidException;
import client.utils.ServerUtils;
import client.webSocket.WebSocketManager;
import commons.Collection;
import commons.Note;
import org.apache.commons.lang3.StringEscapeUtils;
import org.commonmark.testutil.TestResources;
import org.commonmark.testutil.example.Example;
import org.commonmark.testutil.example.ExampleReader;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class CurrentNoteAreaServiceTest {

    @Test
    void isCurrentNoteChangedTest() {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        Note n = new Note("Note","Content");

        assertAll(
                () -> assertFalse(service.isCurrentNoteChanged("Note","Content",n)),
                () -> assertTrue(service.isCurrentNoteChanged("Note2","Content",n)),
                () -> assertTrue(service.isCurrentNoteChanged("Note","Content2",n))
        );
    }

    @Test
    void isTitleChangedTest() {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        Note n = new Note("Note","Content");

        assertAll(
                () -> assertFalse(service.isTitleChanged("Note",n)),
                () -> assertTrue(service.isTitleChanged("Note2",n))
        );
    }

    @Test
    void isContentChangedTest() {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        Note n = new Note("Note","Content");

        assertAll(
                () -> assertFalse(service.isContentChanged("Content",n)),
                () -> assertTrue(service.isContentChanged("Content2",n))
        );
    }

    @Test
    void prepareCollectionListTest() {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        Collection c1 = new Collection("Collection1");
        Collection c2 = new Collection("Collection2");
        Collection c3 = new Collection("Collection3");
        Note n = new Note(1,"Note","Content");
        c2.addNoteToCollection(n);
        n.collection = c2;
        assertAll(
                () -> assertEquals(List.of(c2,c1,c3),service.prepareCollectionList(List.of(c2,c1,c3),n)),
                () -> assertEquals(List.of(c2,c1,c3),service.prepareCollectionList(List.of(c2,c1,c3),n))
        );
    }

    @Test
    void prepareFilteredNotesTest() {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        Note n1 = new Note(1,"Note","#1 #2");
        Note n2 = new Note(1,"Note","#1");
        Note n3 = new Note(1,"Note","#2");
        n1.tags = List.of("1","2");
        n2.tags = List.of("1");
        n3.tags = List.of("2");
        List<Note> notes = List.of(n1,n2,n3);
        assertAll(
                () -> assertEquals(List.of(n1,n2),service.prepareFilteredNotes(notes,List.of("1"),false)),
                () -> assertEquals(List.of(n1,n3),service.prepareFilteredNotes(notes,List.of("2"),false)),
                () -> assertEquals(List.of(n1),service.prepareFilteredNotes(notes,List.of("1","2"),false)),
                () -> assertEquals(List.of(n1,n2,n3),service.prepareFilteredNotes(notes,List.of("1","2"),true))
        );

    }

    @Test
    void convertToHTMLTest() {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        List<Example> examples = ExampleReader.readExamples(TestResources.getSpec());

        for (Example example : examples) {
            String renderedHtml = service.convertToHTML(example.getSource(), List.of());
            String actual = sanitizeActual(renderedHtml);

            String exampleHtml = example.getHtml();
            String expected = sanitizeExpected(exampleHtml);

            assertEquals(expected, actual, "HTML output mismatch for example: " + example.getSource());

        }
    }

    //Sanitization functions below are used to normalize the raw HTML, without creating any functional differences.

    public String sanitizeActual(String unsanitized) {
        String output = unsanitized.replace("%20"," ");
        output = StringEscapeUtils.unescapeHtml4(output)
                .replace("\\","\\\\");
        return output;
    }

    public String sanitizeExpected(String unsanitized) {
        String output = unsanitized.replace("→","\t");
        output = Pattern.compile("href=\"(.*)\"").matcher(output)
                .replaceAll(mr -> "href=\"" +
                        URLDecoder.decode(mr.group(1).replace("+","%2B"), StandardCharsets.UTF_8)
                                .replace("\\","\\\\") +
                        "\"");
        output = StringEscapeUtils.unescapeHtml4(output).replace("\\","\\\\");
        return output;
    }

    @Test
    void renderReferencesTest() {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        VarService varService = new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config());

        Note note1 = new Note("Note1", "[[Note2]]");
        Note note2 = new Note("Note2", "[[Note3]]");
        Note note3 = new Note("Note3", "123 [ [test]] [[[[] test");
        Collection c = new Collection("Collection");
        c.addNoteToCollection(note1);
        c.addNoteToCollection(note2);
        c.addNoteToCollection(note3);
        varService.setCurrentCollection(c);

        String expected1 = "<a href=\"#\">Note1</a>";
        String actual1 = service.renderReferences("[[Note1]]", varService);

        String expected2 = "TEST<a href=\"#\">Note2</a>TEST";
        String actual2 = service.renderReferences("TEST[[Note2]]TEST", varService);

        String expected3 = "<span style='color:#FF0000;'> [[UNRECOGNISED]] </span>";
        String actual3 = service.renderReferences("[[Note4]]", varService);

        String expected4 = "TEST<span style='color:#FF0000;'> [[UNRECOGNISED]] </span>TEST";
        String actual4 = service.renderReferences("TEST[[Note4]]TEST", varService);

        String expected5 = "123 [ [test]] [[[[] test";
        String actual5 = service.renderReferences("123 [ [test]] [[[[] test", varService);

        assertAll(
                () -> assertEquals(expected1, actual1),
                () -> assertEquals(expected2, actual2),
                () -> assertEquals(expected3, actual3),
                () -> assertEquals(expected4, actual4),
                () -> assertEquals(expected5, actual5)
        );
    }

    @Test
    void updateReferencesTest() throws PuttedNoteNotValidException {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        Note note1 = new Note("Note1", "[[Note2]]");
        Note note2 = new Note("Note2", "[[Note3]] [[Note2]] [[Note2]]");
        Note note3 = new Note("Note3", "123 [ [test]] [[[[] test");
        int updates = service.updateReferences("Note2","Note2Changed",List.of(note1,note2,note3),false);

        String expected1 = "[[Note2Changed]]";
        String actual1 = note1.content;

        String expected2 = "[[Note3]] [[Note2Changed]] [[Note2Changed]]";
        String actual2 = note2.content;

        String expected3 = "123 [ [test]] [[[[] test";
        String actual3 = note3.content;

        assertAll(
                () -> assertEquals(expected1, actual1),
                () -> assertEquals(expected2, actual2),
                () -> assertEquals(expected3, actual3),
                () -> assertEquals(3, updates)
        );
    }

    @Test
    void stylizeHTMLTest() throws URISyntaxException, IOException {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));
        String start = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n" +
                "</head>\n" +
                "<body>\n";
        String end = "\n</body>\n" +
                "</html>";
        String toStylize = "<a>Stylize this</a>";
        String expected = start + toStylize + end;
        String actual = service.stylizeHTML(toStylize);

        assertEquals(expected, actual);
    }

    @Test
    void renderTagsTest() throws URISyntaxException, IOException {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));

        String expected1 = "<a href=\"#\" role='button' class='btn btn-sm btn-primary'>#Note1</a>";
        String actual1 = service.renderTags("#Note1");

        String expected2 = "TEST<a href=\"#\" role='button' class='btn btn-sm btn-primary'>#Note1TEST</a>";
        String actual2 = service.renderTags("TEST#Note1TEST");

        String expected3 = "TEST<a href=\"#\" role='button' class='btn btn-sm btn-primary'>#Note1</a> TEST";
        String actual3 = service.renderTags("TEST#Note1 TEST");

        String expected4 = "#";
        String actual4 = service.renderTags("#");

        String expected5 = "# test";
        String actual5 = service.renderTags("# test");

        assertAll(
                () -> assertEquals(expected1, actual1),
                () -> assertEquals(expected2, actual2),
                () -> assertEquals(expected3, actual3),
                () -> assertEquals(expected4, actual4),
                () -> assertEquals(expected5, actual5)
        );
    }


    @Test
    void isNotesCurrentNoteValidTest() {
        CurrentNoteAreaService service = new CurrentNoteAreaService(new ServerUtils(new Config(), new WebSocketManager()));

        Note n1 = new Note("Note1", "Content1");
        Note n2 = new Note("Note2", "Content1");
        Note n3 = new Note("Note1", "Content2");

        assertAll(
                () -> assertTrue(service.isNotesCurrentNoteValid(n1,n1)),
                () -> assertFalse(service.isNotesCurrentNoteValid(n1,n2)),
                () -> assertFalse(service.isNotesCurrentNoteValid(n1,n3)),
                () -> assertFalse(service.isNotesCurrentNoteValid(null,n1)),
                () -> assertTrue(service.isNotesCurrentNoteValid(n1,null))
        );
    }
}