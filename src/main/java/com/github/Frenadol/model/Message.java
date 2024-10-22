package com.github.Frenadol.model;



public class Message {
    private String text;
    private String sender;
    private String receiver;

    public Message() {
    }

    public Message(String text, String send, String receiver) {
        this.text = text;
        this.sender = send;
        this.receiver = receiver;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", send='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}
