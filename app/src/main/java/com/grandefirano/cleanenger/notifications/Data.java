package com.grandefirano.cleanenger.notifications;

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

    @SuppressWarnings("unused")
    public String getChatId() {
        return chatId;
    }

    @SuppressWarnings("unused")
    public String getUser() {
        return user;
    }

    @SuppressWarnings("unused")
    public String getBody() {
        return body;
    }

    @SuppressWarnings("unused")
    public int getIcon() {
        return icon;
    }

    @SuppressWarnings("unused")
    public String getTitle() {
        return title;
    }

    @SuppressWarnings("unused")
    public String getSent() {
        return sent;
    }

}
