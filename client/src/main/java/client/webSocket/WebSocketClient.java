package client.webSocket;

import Serializers.UpdateMessageDecoder;
import Serializers.UpdateMessageEncoder;
import client.scenes.InterfaceCtrl;
import client.services.VarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.UpdateMessage;
import jakarta.websocket.*;

import java.net.URI;


@ClientEndpoint(encoders = {UpdateMessageEncoder.class}, decoders = {UpdateMessageDecoder.class})
public class WebSocketClient {

    private final InterfaceCtrl interfaceCtrl;
    private final VarService varService;



    private final ObjectMapper objectMapper = new ObjectMapper();
    private Session session;
    private String url;
    //LEGACY. Now the server side filters request to the same client, saving bandwidth and resource
    private final double clientId = Math.random();

    public WebSocketClient(InterfaceCtrl interfaceCtrl, VarService varService) {
        this.interfaceCtrl = interfaceCtrl;
        this.varService = varService;
    }


    public void connect(String uri) {
        try {
            this.url = uri;
            String wsUri = uri.replaceFirst("^http", "ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(wsUri + "/update"));
        } catch (Exception e) {
            System.out.println("One or more of the servers is not online.");
            interfaceCtrl.showNotification(interfaceCtrl
                    .myGetBundle()
                    .getString("serverConfiguredNotOnlineNotification"), 5, "red");
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to WebSocket: " + url.replaceFirst("^http", "ws"));
    }

    @OnMessage
    public void onMessage(UpdateMessage message) {

        if (message.getSessionId() == clientId) {
            //"Discarding received update as it was sent by this client" LEGACY
            return;
        }
        //If the client does not have the thing being updated configured on their client, there is no reason to do anything
        //The client would not be connected to a server from which they have no collections.
        if (varService.getCollections().stream().noneMatch(x -> x.getServer().equals(url)
                && x.id == message.getCollectionId())
                && varService.getAllNotes().stream().noneMatch(x ->
                x.collection.getServer().equals(url) && x.id == message.getNoteId())) {
            //Discarding received update as updated item not configured on this client"
            return;
        }
        //If only a note is updated
        if (message.getNoteId() != 0) {
            if (varService.webSocketUpdateNote(message, url)) {
                interfaceCtrl.webSocketUpdateNote(message, url);
            }
        } else {
            if (varService.webSocketUpdateCollection(message, url)) {
                interfaceCtrl.webSocketUpdateCollection(message, url);
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("WebSocket connection to: " + url + " closed: " + reason);
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(UpdateMessage message) {
        try {
            if (session != null && message != null) {
                message.setSessionId(clientId);
                session.getBasicRemote().sendObject(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (session != null) {
                session.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}