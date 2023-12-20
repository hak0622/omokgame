package me.byungjin.network;

import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import me.byungjin.manager.ENVIRONMENT;
import me.byungjin.manager.SystemManager;
import me.byungjin.network.event.DataComeInEvent;

public abstract class Agent extends Thread {
    protected boolean running;
    protected String nick;
    DataComeInEvent gameComeInEvent;
    DataComeInEvent chatComeInEvent;
    DataComeInEvent otherComeInEvent;
    protected boolean isAdminLoginSuccess = false;

    public Agent() {
        gameComeInEvent = null;
        chatComeInEvent = null;
        otherComeInEvent = null;
    }

    public abstract void chat(String str);

    public abstract void open();

    public abstract void send(PROMISE type, String str);

    public void sendRAW(String str) {
        SystemManager.message(ENVIRONMENT.CLIENT, str);
    }

    public abstract void close();

    public boolean isRunning() {
        return running;
    }

    public synchronized void addChatComeInEvent(DataComeInEvent event) {
        chatComeInEvent = event;
    }

    public synchronized void addOtherComeInEvent(DataComeInEvent event) {
        otherComeInEvent = event;
    }

    public synchronized void addGameComeInEvent(DataComeInEvent event) {
        gameComeInEvent = event;
    }

    public boolean isAdminLoginSuccess() {
        return isAdminLoginSuccess;
    }

    public abstract void chatEmoji(byte[] imageData);

    public abstract void chatPhoto(byte[] imageData);
    
    boolean comeInRouter(int identify, String data) {
        SystemManager.message(ENVIRONMENT.CLIENT, identify + " / " + data);
        StringTokenizer tokens = new StringTokenizer(data);

        String promise = tokens.nextToken();
        switch (PROMISE.valueOf(promise)) {
            case CHAT:
                if (chatComeInEvent != null)
                    chatComeInEvent.dispatch(this, data);
                return false;
            case GAME:
                if (gameComeInEvent != null)
                    gameComeInEvent.dispatch(this, data);
                return false;
            case CUT_COMMU:
                return true;
            case ADMIN_LOGIN_SUC:
                isAdminLoginSuccess = true;
                return false;
            case EMOJI:
                String base64ImageData = tokens.nextToken();
                if (chatComeInEvent != null)
                    chatComeInEvent.dispatch(this, PROMISE.EMOJI + " " + base64ImageData);
                return false;
            case PHOTO: 
                String base64PhotoData = tokens.nextToken();
                if (chatComeInEvent != null)
                    chatComeInEvent.dispatch(this, PROMISE.PHOTO + " " + base64PhotoData);
                return false;   
            default:
                if (otherComeInEvent != null)
                    otherComeInEvent.dispatch(this, data);
                return false;
        }
    }
}
