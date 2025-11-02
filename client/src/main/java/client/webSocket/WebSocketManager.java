package client.webSocket;

import client.scenes.InterfaceCtrl;
import client.services.VarService;
import com.google.inject.Singleton;
import commons.UpdateMessage;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class WebSocketManager {


    private final Map<String, WebSocketClient> clients = new HashMap<>();
    private InterfaceCtrl interfaceCtrl;
    private VarService varService;


    public void connect(String serverUri) {
        if (clients.containsKey(serverUri)) {
            System.out.println("Already connected to: " + serverUri);
            return;
        }

        WebSocketClient client = new WebSocketClient(this.interfaceCtrl, this.varService);
        client.connect(serverUri);
        clients.put(serverUri, client);
    }

    public void disconnect(String serverUri) {
        WebSocketClient client = clients.remove(serverUri);
        if (client != null) {
            client.disconnect();
        }
    }

    public void disconnectAll() {
        clients.values().forEach(WebSocketClient::disconnect);
        clients.clear();
    }


    public void sendMessage(String serverUri, UpdateMessage message) {
        WebSocketClient client = clients.get(serverUri);
        if (client != null) {
            client.sendMessage(message);
        }
    }

    public InterfaceCtrl getInterfaceCtrl() {
        return interfaceCtrl;
    }

    public void setInterfaceCtrl(InterfaceCtrl interfaceCtrl) {
        this.interfaceCtrl = interfaceCtrl;
    }
    public VarService getVarService() {
        return varService;
    }

    public void setVarService(VarService varService) {
        this.varService = varService;
    }

}
