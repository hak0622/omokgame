package me.byungjin.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;

import me.byungjin.manager.ENVIRONMENT;
import me.byungjin.manager.SystemManager;
import me.byungjin.network.event.ClientEvent;
import me.byungjin.network.event.DataComeInEvent;

public class Client extends Agent {
    private Socket sock;
    private PrintWriter sender;
    private BufferedReader reader;
    private ClientEvent clientExitEvent;
    private int identify = -1;
    
    public Client() throws Exception {
        sock = new Socket();
        sock.connect(new InetSocketAddress(ENVIRONMENT.SERVER_IP, ENVIRONMENT.SERVER_PORT.getValue()), 3000);
        init();
    }

    public Client(String ip) throws Exception {
        sock = new Socket(ip, ENVIRONMENT.PORT.getValue());
        init();
    }

    public Client(Socket sock, DataComeInEvent chatEvent, DataComeInEvent otherEvent,
            DataComeInEvent gameEvent, ClientEvent clientExitEvent, int identify) throws Exception {
        this.sock = sock;
        this.clientExitEvent = clientExitEvent;
        this.identify = identify;
        this.chatComeInEvent = chatEvent;
        this.otherComeInEvent = otherEvent;
        this.gameComeInEvent = gameEvent;
        init();
    }

    private void init() throws Exception {
        sender = new PrintWriter(sock.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    }

    public int getIdentify() {
        return identify;
    }

    public String getSocketIP() {
        return this.sock.getInetAddress().getHostAddress();
    }

    @Override
    public void run() {
        running = true;
        SystemManager.message(ENVIRONMENT.CLIENT, "connect : " + identify);
        String buffer;
        try {
            while (sock.isConnected()) {
                if ((buffer = reader.readLine()) != null) {
                    if (comeInRouter(identify, buffer))
                        close();
                }
            }
        } catch (Exception e) {
            close();
            SystemManager.catchException(ENVIRONMENT.CLIENT, e);
        }
        running = false;
        if (clientExitEvent != null)
            clientExitEvent.dispatch(this);
        SystemManager.message(ENVIRONMENT.CLIENT, "disconnect : " + identify);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Client) {
            if (((Client) obj).getIdentify() == identify)
                return true;
        }
        return false;
    }

    @Override
    public void chat(String str) {
        send(PROMISE.CHAT, str);
    }

    @Override
    public void send(PROMISE type, String str) {
        sendRAW(type + " " + str);
    }

    public void chatEmoji(String base64ImageData) {
        try {
            byte[] imageData = Base64.getDecoder().decode(base64ImageData);
            send(PROMISE.EMOJI, base64ImageData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendRAW(String str) {
        super.sendRAW(str);
        if (sock.isConnected())
            sender.println(str);
    }

    @Override
    public void open() {
        start();
    }

    @Override
    public void close() {
        try {
            send(PROMISE.CUT_COMMU, "");
            sender.close();
            reader.close();
            sock.close();
            interrupt();
        } catch (Exception e) {
            SystemManager.catchException(ENVIRONMENT.CLIENT, e);
        }
    }
    public void chatEmoji(byte[] imageData) {
        // 이미지 데이터를 처리하는 로직을 구현하세요.
        // 예: 이미지 데이터를 서버로 전송하거나 UI에 표시하는 등의 동작을 수행할 수 있습니다.
        // 이 예시에서는 이미지 데이터를 Base64로 인코딩하여 서버로 전송하는 예시를 보여줍니다.
        String base64ImageData = Base64.getEncoder().encodeToString(imageData);
        send(PROMISE.EMOJI, base64ImageData);
    }
    public void chatPhoto(byte[] imageData) {
        // 이미지 데이터를 처리하는 로직을 구현하세요.
        // 예: 이미지 데이터를 서버로 전송하거나 UI에 표시하는 등의 동작을 수행할 수 있습니다.
        // 이 예시에서는 이미지 데이터를 Base64로 인코딩하여 서버로 전송하는 예시를 보여줍니다.
        String base64ImageData = Base64.getEncoder().encodeToString(imageData);
        send(PROMISE.PHOTO, base64ImageData);
    }
}
