package com.grandefirano.cleanenger.SingleItems;

import com.grandefirano.cleanenger.Adapter.ChatListAdapter;

public class ChatData {
    private int color;
    private int textSize;

    public ChatData(int color, int textSize) {
        this.color = color;
        this.textSize = textSize;
    }

    public ChatData() {
        color= ChatListAdapter.DEFAULT_CHAT_COLOR;
        textSize=16;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
