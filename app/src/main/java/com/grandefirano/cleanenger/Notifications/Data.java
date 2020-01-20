package com.grandefirano.cleanenger.Notifications;

public class Data {

    private String user;
    private int icon;
    private String body;
    private String title;
    private String sent;
    private String chatId;

    public Data(String user, String body,String chatId, String title, String sent, int icon) {
        this.user = user;
        this.icon = icon;
        this.chatId=chatId;
        this.body = body;
        this.title = title;
        this.sent = sent;
    }

    public Data() {
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }
}
