package com.grandefirano.cleanenger.single_items;

import java.util.HashMap;
import java.util.Map;

public class SingleMainScreenMessage {

    private String chatId;
    private long date;
    private Map<String, String> dateLastMessage;

    public SingleMainScreenMessage(String chatId, long date) {

        this.chatId = chatId;
        this.date = date;
    }

    public SingleMainScreenMessage(String chatId, Map<String,String> dateLastMessage) {
        this.chatId = chatId;
        this.dateLastMessage=dateLastMessage;
    }

    public SingleMainScreenMessage() {

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("chatId", chatId);
        result.put("date", dateLastMessage);


        return result;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
