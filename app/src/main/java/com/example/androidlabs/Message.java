package com.example.androidlabs;

public class Message {
    private long id;
    private String messageText;
    private boolean isSend;

    public Message() {
        this.messageText = "";
        this.isSend = false;
        this.id = 0;
    }

    public Message(long id, String messageText, boolean isSend) {
        this.id = id;
        this.messageText = messageText;
        this.isSend = isSend;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public boolean isSend() {
        return isSend;
    }

    public void setSend(boolean send) {
        isSend = send;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
