package com.grandefirano.cleanenger.single_items;

public class ChatData {
    private int color;
    private int textSize;

    public ChatData(int color, int textSize) {
        this.color = color;
        this.textSize = textSize;
    }
    @SuppressWarnings("unused")
    public ChatData() {
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
