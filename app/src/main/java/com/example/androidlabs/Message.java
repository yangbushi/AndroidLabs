package com.example.androidlabs;

public class Message {
    private String messageText;
    private boolean isSend;

    public Message() {
        this.messageText = "";
        this.isSend = false;
    }

    public Message(String messageText, boolean isSend) {
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
}
