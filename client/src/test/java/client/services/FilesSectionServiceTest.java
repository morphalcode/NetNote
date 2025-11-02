package client.services;

import client.Config;
import client.utils.ServerUtils;
import client.webSocket.WebSocketManager;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class FilesSectionServiceTest {

    @Test
    void constructorTest(){
        FilesSectionService filesSectionService = new FilesSectionService(
                new ServerUtils(new Config(), new WebSocketManager()),
                new VarService(new ServerUtils(new Config(), new WebSocketManager()), new Config()));
        assertNotNull(filesSectionService);
    }

}