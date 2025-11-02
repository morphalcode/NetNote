package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Note;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteController noteController;

    @Autowired
    private ObjectMapper objectMapper;

    private Note note1;

    private Note note2;

    private Note note3;

    private Note note4;

    private List<Note> notes;

    @BeforeEach
    public void seUp(){

        note1 = new Note("Web","javascript",1);
        note2 = new Note("Data","postgres",1);
        note3 = new Note("Data","postgres",1);
        note4 = new Note("IP","weblab",2);
    }

        @Test
        public void AddNoteTest() throws Exception {
            given(noteController.addNote(ArgumentMatchers.any())).willAnswer((invocation -> ResponseEntity.status(HttpStatus.CREATED).body(invocation.getArgument(0))));

            ResultActions response = mockMvc.perform(post("/api/notes/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(note1)));
            response.andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(note1.title)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(note1.content)));
        }

        @Test
        public void getNotesTest() throws Exception{
             List<Note> notes = new ArrayList<>();
             notes.add(note1);
             notes.add(note2);
            when(noteController.getNotes()).thenReturn(notes) ;

            ResultActions response = mockMvc.perform(get("/api/notes/")
                    .contentType(MediaType.APPLICATION_JSON));
            response.andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(notes.size())));
        }

        @Test
    public void getNoteTest() throws Exception{
        ResponseEntity<Note> note = ResponseEntity.ok(note1);
        when(noteController.getNote(1)).thenReturn(note) ;
            ResultActions response = mockMvc.perform(get("/api/notes/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(note)));
            response.andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(note1.title)))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(note1.content)));

    }

    @Test
    public void putNote() throws Exception{
        ResponseEntity<Note> note = ResponseEntity.ok(note1);
        when(noteController.putNote(1, note2)).thenReturn(note) ;
        ResultActions response = mockMvc.perform(put("/api/notes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(note1)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void putNotes() throws Exception{
        notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        ResponseEntity<List<Note>> note = ResponseEntity.ok(notes);
        when(noteController.putNotes(notes)).thenReturn(note) ;
        ResultActions response = mockMvc.perform(put("/api/notes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notes)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(notes.size())));
    }

    @Test
    public void deleteNote() throws Exception{
        ResponseEntity<Boolean> deleted = ResponseEntity.ok(true);
        when(noteController.deleteNote(1)).thenReturn(deleted) ;
        ResultActions response = mockMvc.perform(delete("/api/notes/1")
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteNotes() throws Exception{
        doNothing().when(noteController).deleteNotes();
        ResultActions response = mockMvc.perform(delete("/api/notes/")
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void patchNotes() throws Exception{
        notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        notes.add(note3);
        notes.add(note4);
        ResponseEntity<List<Note>> notesRE = ResponseEntity.ok(notes);
        when(noteController.patchNotes(notes)).thenReturn(notesRE) ;
        ResultActions response = mockMvc.perform(patch("/api/notes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notes)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(notes.size())));
    }

}