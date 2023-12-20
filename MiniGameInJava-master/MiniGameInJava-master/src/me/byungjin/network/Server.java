package me.byungjin.network;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.Vector;

import me.byungjin.manager.ENVIRONMENT;
import me.byungjin.manager.SystemManager;
import me.byungjin.network.event.ClientEvent;
import me.byungjin.network.event.DataComeInEvent;

public class Server extends Agent {
    private ServerSocket sockServ;
    private int identify = 0;
    private Vector<Client> clients;
    private ClientEvent clientExitEvt;
    private ClientEvent clientEnterEvt;

    public Server(String nick) throws Exception {
        this.nick = nick;
        sockServ = new ServerSocket(nick == null ? ENVIRONMENT.SERVER_PORT.getValue() : ENVIRONMENT.PORT.getValue());
        clients = new Vector<Client>();
        identify = 0;
        clientExitEvt = new ClientEvent() {
            @Override
            public void dispatch(Client source) {
                clients.remove(source);
            }
        };
    }

    @Override
    public void run() {
        running = true;
        SystemManager.message(ENVIRONMENT.SERVER, "start running");
        try {
            while (running) {
                Socket sock = sockServ.accept();
                Client client = new Client(sock, chatComeInEvent, otherComeInEvent, gameComeInEvent, clientExitEvt,
                        identify);
                clients.add(client);
                client.open();
                if (clientEnterEvt != null)
                    clientEnterEvt.dispatch(client);
                SystemManager.message(ENVIRONMENT.SERVER, "Client connect : " + identify);
                identify++;
            }
        } catch (Exception e) {
            SystemManager.catchException(ENVIRONMENT.SERVER, e);
        } finally {
            close();
            SystemManager.message(ENVIRONMENT.SERVER, "close");
        }
    }

    public void removeClient(Client client) {
        clients.remove(client);
    }

    public void broadcast(String str) {
        for (Client c : clients) {
            c.sendRAW(str);
        }
    }

    public void sendRAWExceptionSpecial(Client c, String str) {
        for (Client cl : clients) {
            if (cl.equals(c))
                continue;
            cl.sendRAW(str);
        }
    }

    public int getNumberOfClients() {
        return clients.size();
    }

    @Override
    public void chat(String str) {
        send(PROMISE.CHAT, str);
    }

    @Override
    public void send(PROMISE type, String str) {
        for (Client c : clients) {
            c.send(type, str);
        }
    }

    public void chatEmoji(String base64ImageData) {
        try {
            byte[] imageData = Base64.getDecoder().decode(base64ImageData);
            for (Client client : clients) {
                client.chatEmoji(base64ImageData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRAW(String str) {
        for (Client c : clients) {
            c.sendRAW(str);
        }
    }

    @Override
    public void open() {
        start();
    }

    @Override
    public void close() {
        try {
            for (Client c : clients) {
                c.close();
            }
            if (!sockServ.isClosed())
                sockServ.close();
        } catch (Exception e) {
            SystemManager.catchException(ENVIRONMENT.SERVER, e);
        }
        interrupt();
        running = false;
    }

    @Override
    public synchronized void addChatComeInEvent(DataComeInEvent event) {
        super.addChatComeInEvent(event);
        for (Client c : clients) {
            c.addChatComeInEvent(event);
        }
    }

    @Override
    public synchronized void addOtherComeInEvent(DataComeInEvent event) {
        super.addOtherComeInEvent(event);
        for (Client c : clients) {
            c.addOtherComeInEvent(event);
        }
    }

    @Override
    public synchronized void addGameComeInEvent(DataComeInEvent event) {
        super.addGameComeInEvent(event);
        for (Client c : clients) {
            c.addGameComeInEvent(event);
        }
    } public void chatEmoji(byte[] imageData) {
        // 이미지 데이터를 처리하는 로직을 구현하세요.
        // 예: 이미지 데이터를 클라이언트로 전송하거나 UI에 표시하는 등의 동작을 수행할 수 있습니다.
        // 이 예시에서는 이미지 데이터를 Base64로 인코딩하여 클라이언트로 전송하는 예시를 보여줍니다.
        String base64ImageData = Base64.getEncoder().encodeToString(imageData);
        send(PROMISE.EMOJI, base64ImageData); //server
    }
    public void chatPhoto(byte[] imageData) {
        // 이미지 데이터를 처리하는 로직을 구현하세요.
        // 예: 이미지 데이터를 서버로 전송하거나 UI에 표시하는 등의 동작을 수행할 수 있습니다.
        // 이 예시에서는 이미지 데이터를 Base64로 인코딩하여 서버로 전송하는 예시를 보여줍니다.
        String base64ImageData = Base64.getEncoder().encodeToString(imageData);
        send(PROMISE.PHOTO, base64ImageData);
    }
}
