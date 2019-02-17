package com.oude.dndclub.message;

public class EventMessage {
    public final static int Exitapp = 0x11;
    public final static int CheckApp = 0x12;
    private int MessageType;
    private boolean DownLoading = false;

    public EventMessage(int messageType, boolean downLoading) {
        MessageType = messageType;
        DownLoading = downLoading;
    }

    public EventMessage(int messageType) {
        MessageType = messageType;
    }

    public int getMessageType() {
        return MessageType;
    }

    public void setMessageType(int messageType) {
        MessageType = messageType;
    }

    public boolean isDownLoading() {
        return DownLoading;
    }

    public void setDownLoading(boolean downLoading) {
        DownLoading = downLoading;
    }
}
