package com.grandefirano.cleanenger.single_items;

import java.util.HashMap;
import java.util.Map;

public class SingleMainScreenMessage {

    private String chatId;
    private long date;
    private Map<String, String> dateLastMessage;

    @SuppressWarnings("unused")
    public SingleMainScreenMessage(String chatId, long date) {
        this.chatId = chatId;
        this.date = date;
    }

    public SingleMainScreenMessage(String chatId, Map<String,String> dateLastMessage) {
        this.chatId = chatId;
        this.dateLastMessage=dateLastMessage;
    }

    @SuppressWarnings("unused")
    public SingleMainScreenMessage() {
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("chatId", chatId);
        result.put("date", dateLastMessage);

        return result;
    }
    @SuppressWarnings("unused")
    public long getDate() {
        return date;
    }

    @SuppressWarnings("unused")
    public void setDate(long date) {
        this.date = date;
    }

    public String getChatId() {
        return chatId;
    }

    @SuppressWarnings("unused")
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
