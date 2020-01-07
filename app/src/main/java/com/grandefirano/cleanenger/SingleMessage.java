package com.grandefirano.cleanenger;

public class SingleMessage {

    private String author;
    private String message;

    public SingleMessage(String author, String message) {
        this.author = author;
        this.message = message;
    }

    public SingleMessage() {
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }
}
