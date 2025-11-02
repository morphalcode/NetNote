/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import client.Exceptions.PuttedNoteNotValidException;
import client.webSocket.WebSocketManager;
import commons.*;
import client.Config;
import client.ServerStatus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.validator.routines.UrlValidator;

@SuppressWarnings("checkstyle:Indentation")
@Singleton
public class ServerUtils {

    //private final static String SERVER = "http://localhost:8080/";
    private final Config config;
    private final WebSocketManager manager;

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    @Inject
    public ServerUtils(Config config, WebSocketManager manager) {
        this.config = config;
        this.manager = manager;
    }

    public Config getConfig() {
        return config;
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public ServerStatus checkServerStatus(String serverUrl, String collectionName) {
        String[] schemes = {"http","https"};
        UrlValidator urlValidator = new UrlValidator(schemes, ALLOW_LOCAL_URLS);
        boolean isServerValid = urlValidator.isValid(serverUrl);
        if(!isServerValid) {
            return ServerStatus.SERVER_NOT_REACHABLE;
        }
        if (serverUrl == null) {
            return ServerStatus.SERVER_NOT_REACHABLE;
        }
        if (collectionName == null) {
            return ServerStatus.COLLECTION_NOT_VALID;
        }
        List<Collection> collections = new ArrayList<>();
        try (var client = ClientBuilder.newClient()) {
            collections = client
                    .target(serverUrl + "/api/collections/")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
        } catch (Exception e) {
            return ServerStatus.SERVER_NOT_REACHABLE;
        }
        if (collections == null) {
            return ServerStatus.SERVER_NOT_REACHABLE;
        }
        if (collections.stream().noneMatch(x -> Objects.equals(x.name, collectionName))) {
            ServerStatus status = ServerStatus.COLLECTION_WILL_BE_SAVED;
            status.setCollectionId(0);
            return status;
        } else {
            ServerStatus status = ServerStatus.NO_CHANGES_MADE;
            status.setCollectionId(collections.
                    stream().filter(x -> Objects.equals(x.name, collectionName)).
                    findFirst().get().id);
            return status;
        }
    }


    //NOTES

    /**
     * Gets all notes from the server (localhost...), and stores it in 'notes'.
     * // ClientBuilder etc. is from one of the self studies
     *
     * @return List of Notes from server
     */
    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        try (var client = ClientBuilder.newClient()) {
            notes = client
                    .target("http://localhost:8080/api/notes/")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
            return notes;
        } catch (Exception e) {                                         // and parses it into a List
            System.out.println("Client could not get notes from server.\n" + e.getMessage());
            return null;
        }
    }


    /**
     * COMPLETE THIS
     *
     * @return COMPLETE THIS
     */
    public Note addNote() {
        var note = new Note();
        note.setCollectionId(config.getDefaultCollection().getCollectionId());
        var requestBody = Entity.entity(note, MediaType.APPLICATION_JSON);
        try (var client = ClientBuilder.newClient()) {
            var response = client.target(config.getDefaultCollection().getServer() + "/api/notes/")
                    .request(MediaType.APPLICATION_JSON)
                    .post(requestBody);
            Note addedNote = response.readEntity(Note.class);
            if (addedNote.getCollectionId() != config.getDefaultCollection().getCollectionId()) {
                System.out.println("ERROR NOT COLLECTION ID");
            }
            manager.sendMessage(config.getDefaultCollection().getServer(),
                    new UpdateMessage(addedNote.getCollectionId(), addedNote.id, Action.CREATE));
            return addedNote;
        } catch (Exception e) {
            System.out.println("Client could not get notes from server.\n" + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public Note addNoteToCollection(CollectionInfo collection) {
        var note = new Note();
        note.setCollectionId(collection.getCollectionId());
        var requestBody = Entity.entity(note, MediaType.APPLICATION_JSON);
        try (var client = ClientBuilder.newClient()) {
            var response = client.target(collection.getServer() + "/api/notes/")
                    .request(MediaType.APPLICATION_JSON)
                    .post(requestBody);
            Note addedNote = response.readEntity(Note.class);
            if (addedNote.getCollectionId() != collection.getCollectionId()) {
                System.out.println("ERROR NOT COLLECTION ID");
            }
            manager.sendMessage(collection.getServer(),
                    new UpdateMessage(collection.getCollectionId(), addedNote.id, Action.CREATE));
            return addedNote;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * COMPLETE THIS
     *
     * @param note COMPLETE THIS
     */
    @SuppressWarnings("checkstyle:Indentation")
    public void deleteNote(Note note) {
        try (var client = ClientBuilder.newClient()) {
            client.target(note.collection.getServer() + "/api/notes/{id}")
                    .resolveTemplate("id", note.id)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();
            manager.sendMessage(note.collection.getServer(),
                    new UpdateMessage(note.collection.id, note.id, Action.DELETE));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * COMPLETE THIS
     *
     * @param id     COMPLETE THIS
     * @param server COMPLETE THIS
     * @return COMPLETE THIS
     */
    public Note getNote(Long id, String server) {
        Note note = null;
        try (var client = ClientBuilder.newClient()) {
            var response = client.target(server + "/api/notes/{id}")
                    .resolveTemplate("id", id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            if (response.getStatus() != 200)
                System.out.println(response.getStatus());
            else
                note = response.readEntity(Note.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Client could not get note from server.\n" + e.getMessage());
        }
        return note;
    }


    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public void putNote(Note note) throws PuttedNoteNotValidException{
        if(note == null){
            return;
        }
        try (Client client = ClientBuilder.newClient()) {
            //ObjectMapper om = new ObjectMapper();
            //String json = om.writeValueAsString(note);
            //System.out.println(json);
            var requestBody = Entity.entity(note, MediaType.APPLICATION_JSON);
            var response = client
                    .target(note.collection.getServer() + "/api/notes/{id}")
                    .resolveTemplate("id", note.id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(requestBody);
            Note puttedNote = response.readEntity(Note.class);
            if(response.getStatus() == 400 || puttedNote == null){
                throw new PuttedNoteNotValidException();
            }
            if (puttedNote.getCollectionId() != note.getCollectionId()) {
                System.out.println("ERROR NOT COLLECTION ID");
            }
            manager.sendMessage(note.collection.getServer(),
                    new UpdateMessage(puttedNote.getCollectionId(), puttedNote.id, Action.UPDATE));
            if (response.getStatus() != 200){
                System.out.println(response.getStatus());
            }
        } catch (PuttedNoteNotValidException e) {
            throw e;
        }
    }


    //COLLECTIONS

    /**
     * COMPLETE THIS
     *
     * @return COMPLETE THIS
     */
    public List<Collection> getCollections() {
        List<Collection> collections = new ArrayList<>();
        try (var client = ClientBuilder.newClient()) {
            collections = client
                    .target("http://localhost:8080/api/collections/")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });                       // Gets the requested body
        } catch (Exception e) {                                         // and parses it into a List
            System.out.println("Client could not get collections from server.\n" + e.getMessage());
        }
        return collections;
    }

    @SuppressWarnings({"checkstyle:LineLength", "checkstyle:MissingJavadocMethod"})
    public List<Collection> getConfigCollections() {
        List<Collection> collections = new ArrayList<>();
        HashSet<CollectionInfo> configCollections = config.getCollections();
        for (CollectionInfo collectionInfo : configCollections) {
            try (var client = ClientBuilder.newClient()) {
                Collection collection = client.target(collectionInfo.getServer() + "/api/collections/{id}")
                        .resolveTemplate("id", collectionInfo.getCollectionId())
                        .request(APPLICATION_JSON)
                        .get(new GenericType<>() {
                        });
                collections.add(collection);
            } catch (Exception e) {
                config.removeCollection(collectionInfo);
                System.out.println("A CONFIGURED COLLECTION HAS BEEN DELETED ON THE SERVER");
            }
        }
        return collections;
    }


    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    public Collection addCollection(String server) {
        var collection = new Collection();
        var requestBody = Entity.entity(collection, MediaType.APPLICATION_JSON);
        try (var client = ClientBuilder.newClient()) {
            var response = client.target(server + "/api/collections/")
                    .request(MediaType.APPLICATION_JSON)
                    .post(requestBody);
            Collection addedCollection = response.readEntity(Collection.class);
            if (!addedCollection.getServer().equals(server)) {
                System.out.println("ERROR SERVER NOT SET PROPERLY");
            }
            manager.sendMessage(server,
                    new UpdateMessage(addedCollection.id, Action.CREATE));
            return addedCollection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * COMPLETE THIS
     *
     * @param id     COMPLETE THIS
     * @param server COMPLETE THIS
     * @return COMPLETE THIS
     */
    public Collection getCollection(Long id, String server) {
        try {
            var response = ClientBuilder.newClient()
                    .target(server + "/api/collections/{id}")
                    .resolveTemplate("id", id)
                    .request(MediaType.APPLICATION_JSON)
                    .get();
            return response.readEntity(Collection.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * COMPLETE THIS
     *
     * @param collection COMPLETE THIS
     * @return COMPLETE THIS
     */
    public Collection putCollection(Collection collection) {
        try (Client client = ClientBuilder.newClient()) {
            var requestBody = Entity.entity(collection, MediaType.APPLICATION_JSON);
            Collection puttedCollection = client
                    .target(collection.getServer() + "/api/collections/{id}")
                    .resolveTemplate("id", collection.id)
                    .request(MediaType.APPLICATION_JSON)
                    .put(requestBody).readEntity(Collection.class);
            manager.sendMessage(collection.getServer(),
                    new UpdateMessage(puttedCollection.id, Action.UPDATE));
            return puttedCollection;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    //FILES

    public FileEntity uploadFileToNote(Note note, MultipartFile file){
        try{
            FileEntity fileEntity = new FileEntity();
            fileEntity.fileName = file.getOriginalFilename();
            fileEntity.type = file.getContentType();
            fileEntity.data = file.getBytes();
            fileEntity.setNoteId(note.id);
            var requestBody = Entity.entity(fileEntity, MediaType.APPLICATION_JSON);
            try(var client = ClientBuilder.newClient()){
                var response = client.target(note.collection.getServer() + "/api/notes/{id}/files/")
                        .resolveTemplate("id", note.id)
                        .request(MediaType.APPLICATION_JSON)
                        .post(requestBody);
                FileEntity addedFile = response.readEntity(FileEntity.class);
                return addedFile;
            }
        }catch(Exception e){
            System.out.println("Could not upload file");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<FileEntity> getFiles(Note note){
        List<FileEntity> files = new ArrayList<>();
        try(var client = ClientBuilder.newClient()){
            files = client.target(note.collection.getServer() + "/api/notes/{id}/files/")
                    .resolveTemplate("id", note.id)
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<>() {
                    });
            return files;
        } catch (Exception e){
            //System.out.println("Client could not get the file from server.\n" + e.getMessage());
            e.printStackTrace();
        }
        return files;
    }

    public void deleteFile(Note note, long fileId){
        try(var client = ClientBuilder.newClient()){
            client.target(note.collection.getServer() + "/api/notes/{id}/files/{fileId}")
                    .resolveTemplate("id", note.id)
                    .resolveTemplate("fileId", fileId)
                    .request(MediaType.APPLICATION_JSON)
                    .delete();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public FileEntity updateFileName(Note note, long fileId, FileEntity file){
        try(var client = ClientBuilder.newClient()){
            var requestBody = Entity.entity(file, APPLICATION_JSON);
            var response = client.target(note.collection.getServer()
                            + "/api/notes/{id}/files/{fileId}")
                    .resolveTemplate("id", note.id)
                    .resolveTemplate("fileId", fileId)
                    .request(MediaType.APPLICATION_JSON)
                    .put(requestBody);
            FileEntity updatedFile = response.readEntity(FileEntity.class);
            return updatedFile;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public byte[] getFileData(Note note, String fileName){
        byte[] data = null;
        try(var client = ClientBuilder.newClient()){
            var response = client.target(note.collection.getServer()
                            + "/api/notes/{id}/files/{filename}")
                    .resolveTemplate("id", note.id)
                    .resolveTemplate("filename", fileName)
                    .request(MediaType.APPLICATION_OCTET_STREAM)
                    .get();
            if(response.getStatus() != 200)
                System.out.println(response.getStatus());
            else
                data = response.readEntity(byte[].class);
        } catch (Exception e){
            System.out.println("Client could not get the file from server.\n" + e.getMessage());
        }
        return data;
    }
}