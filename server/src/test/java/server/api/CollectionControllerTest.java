package server.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = CollectionController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CollectionControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CollectionController collectionController;

    @Autowired
    private ObjectMapper objectMapper;

    private Collection c1;

    private Collection c2;

    @BeforeEach
    public void setUp(){
        c1 = new Collection(1L,"university");
        c2 = new Collection(2L,"games");
    }

    @Test
    public void getCollections() throws Exception{
        List<Collection> collections = new ArrayList<>();
        collections.add(c1);
        collections.add(c2);
        when(collectionController.getCollections()).thenReturn(collections);
        ResultActions response = mockMvc.perform(get("/api/collections/")
                .contentType(MediaType.APPLICATION_JSON));
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(collections.size())));
    }

    @Test
    public void getCollection() throws Exception {
        ResponseEntity<Collection> collection = ResponseEntity.ok(c1);
        when(collectionController.getCollection(1L)).thenReturn(collection) ;
        ResultActions response = mockMvc.perform(get("/api/collections/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(collection)));
        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int)c1.id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(c1.name)));
    }

    @Test
    public void putCollectionTest() throws Exception {
        ResponseEntity<Collection> collection = ResponseEntity.ok(c2);
        when(collectionController.putCollection(1L, c2)).thenReturn(collection) ;
        ResultActions response = mockMvc.perform(put("/api/collections/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c1)));

        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addCollectionTest() throws Exception {
        BDDMockito.given(collectionController.addCollection(ArgumentMatchers.any(),ArgumentMatchers.any())).willAnswer((invocation -> ResponseEntity.status(HttpStatus.CREATED).body(invocation.getArgument(0))));

        ResultActions response = mockMvc.perform(post("/api/collections/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(c1)));
        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int)c1.id)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(c1.name)));
    }
}