package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.FileEntity;
import commons.Note;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FileController fileController;

    @MockBean
    private NoteController noteController;

    private Note note;
    private FileEntity file1;
    private FileEntity file2;
    private List<FileEntity> files;

    @BeforeEach
    public void setup(){
        files = new ArrayList<>();
        note = new Note(1, "title", "content", 1);
        file1 = new FileEntity("f1", "t1", new byte[0], 1);
        file2 = new FileEntity("f2", "t2", new byte[0], 1);
        files.add(file1);
        files.add(file2);
    }

    @Test
    void uploadFile() throws Exception {
        given(fileController.uploadFile(1, file1))
                .willReturn(ResponseEntity.status(HttpStatus.OK).body(file1));
        ResultActions response = mockMvc.perform(post("/api/notes/1/files/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(file1)));
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fileName", CoreMatchers.is(file1.fileName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", CoreMatchers.is(file1.type)));
    }

    @Test
    void getAllFilesInNote() throws Exception {
        when(fileController.getAllFilesInNote(1)).thenReturn(files);
        ResultActions response = mockMvc.perform(get("/api/notes/1/files/")
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(files.size())));

    }

    @Test
    void deleteFile() throws Exception {
        given(fileController.deleteFile(1, 1))
                .willReturn(ResponseEntity.status(HttpStatus.OK).body("deleted " + 1));
        ResultActions response = mockMvc.perform(delete("/api/notes/1/files/1")
                .contentType(MediaType.TEXT_PLAIN)
                .content("deleted " + 1));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteAllFilesInNote() throws Exception {
        given(fileController.deleteAllFilesInNote(1))
                .willReturn(ResponseEntity.status(HttpStatus.OK).body("Deleted all files"));
        ResultActions response = mockMvc.perform(delete("/api/notes/1/files/")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Deleted all files"));
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateFileName() throws Exception {
        file1.fileName = "f11";
        given(fileController.updateFileName(1, 1, file1))
                .willReturn(ResponseEntity.status(HttpStatus.OK).body(file1));
        ResultActions response = mockMvc.perform(put("/api/notes/1/files/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(file1)));
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fileName", CoreMatchers.is(file1.fileName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", CoreMatchers.is(file1.type)));
    }

    @Test
    void getFileDataByName() throws Exception {
        when(fileController.getFileDataByName(1, file1.fileName)).thenReturn(ResponseEntity.ok(file1.data));
        ResultActions response = mockMvc.perform(get("/api/notes/1/files/" + file1.fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM));
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(MockMvcResultMatchers.content().bytes(file1.data));
    }
}