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
package client;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import client.Exceptions.ConfigFileCorruptedException;
import client.webSocket.WebSocketManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.CollectionInfo;
import org.apache.commons.io.FileUtils;

@Singleton
public class Config {

    private final WebSocketManager webSocketManager;

    private HashSet<CollectionInfo> collections;
    private CollectionInfo defaultCollection;

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final static String FILEPATH = "client/src/main/java/client/Config.json";

    /**
     * Empty constructor.
     */
    public Config() {
        this.collections = new HashSet<CollectionInfo>();
        this.defaultCollection = null;
        this.webSocketManager = null;
    }

    /**
     * Constructor using DI. Used automatically by framework
     * @param webSocketManager manages web sockets to multiple servers
     */
    @Inject
    public Config(WebSocketManager webSocketManager) {
        this.collections = new HashSet<CollectionInfo>();
        this.defaultCollection = null;
        this.webSocketManager = webSocketManager;
    }

    /**
     * Initialize method. Loads config from file
     * and handles if config file does not exist or corrupted.
     * @throws ConfigFileCorruptedException throws exception so notification is sent.
     */
    public void initialize() throws ConfigFileCorruptedException {
        Config fileConfig = null;
        try{
            fileConfig = Config.loadFromFile();
        } catch (ConfigFileCorruptedException e){
            fileConfig = new Config();
            throw new ConfigFileCorruptedException();
        } finally {
            if(fileConfig == null){
                fileConfig = new Config();
            }
            this.collections = fileConfig.getCollections();
            this.defaultCollection = fileConfig.getDefaultCollection();
            if (this.webSocketManager != null && this.collections != null) {
                this.collections.stream().map(x -> x.getServer()).distinct().forEach(x -> {
                    this.webSocketManager.connect(x);
                });
            }
        }
    }

    /**
     * Getter
     * @return collections configured on this client
     */
    public HashSet<CollectionInfo> getCollections() {
        return new HashSet<CollectionInfo>(collections);
    }


    /**
     * Getter
     * @return default collection on this client
     */
    public CollectionInfo getDefaultCollection() {
        return defaultCollection;
    }

    /**
     * Setter
     * @param defaultCollection new default collection. Must be in collections.
     */
    public void setDefaultCollection(CollectionInfo defaultCollection) {
        this.defaultCollection = defaultCollection;
        if (!collections.contains(defaultCollection)) {
            this.defaultCollection = null;
            if (!collections.isEmpty()) {
                this.defaultCollection = collections.iterator().next();
            }
        }
    }

    /**
     * Adds a collection to this client
     * @param collection collection to be added
     */
    public void addCollection(CollectionInfo collection) {
        if (collections.stream().noneMatch(x -> x.getServer().
                equals(collection.getServer()))) {
            if (webSocketManager != null) {
                this.webSocketManager.connect(collection.getServer());
            } else {
                System.out.println("ERROR websocketmanager null");
            }
        }
        collections.add(collection);
        if (defaultCollection == null) {
            defaultCollection = collection;
        }

    }

    /**
     * removes a collection from this client
     * @param collection collection to be removed
     */
    public void removeCollection(CollectionInfo collection) {
        collections.remove(collection);
        if (isDefaultCollection(collection)) {
            defaultCollection = null;
            if (!collections.isEmpty()) {
                defaultCollection = collections.iterator().next();
            }
        }
        if (collections.stream().noneMatch(x -> x.getServer().
                equals(collection.getServer()))) {
            if (webSocketManager != null) {
                this.webSocketManager.disconnect(collection.getServer());
            } else {
                System.out.println("ERROR websocketmanager null");
            }
        }
    }

    /**
     * @param collection collectionInfo to be compared with default
     * @return whether the collection is the default collection
     */
    public boolean isDefaultCollection(CollectionInfo collection) {
        if (defaultCollection == null) {
            return false;
        }
        return defaultCollection.equals(collection);
    }


    /**
     * Writes the value of this config to the file config.json specified in FILEPATH
     * @throws IOException file error
     */
    public void saveToFile() throws IOException {
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        FileUtils.writeStringToFile(new File(FILEPATH), json, "UTF-8");
    }

    public static Config loadFromFile() throws ConfigFileCorruptedException {
        try {
            File file = new File(FILEPATH);
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    System.out.println("There was an error creating a config file");
                } else {
                    String json = objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(new Config());
                    FileUtils.writeStringToFile(new File(FILEPATH), json, "UTF-8");
                }
            } else {
                String json = FileUtils.readFileToString(new File(FILEPATH), "UTF-8");
                return objectMapper.readValue(json, Config.class);
            }
            return new Config();
        } catch (IOException e) {
            throw new ConfigFileCorruptedException();
        }
    }
}
