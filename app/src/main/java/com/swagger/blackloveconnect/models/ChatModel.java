package com.swagger.blackloveconnect.models;

import java.util.List;

public class ChatModel {
    String userName;
    List<MessageModel> messages;

    public ChatModel(String userName, List<MessageModel> messages) {
        this.userName = userName;
        this.messages = messages;
    }

    public List<MessageModel> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageModel> messages) {
        this.messages = messages;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
