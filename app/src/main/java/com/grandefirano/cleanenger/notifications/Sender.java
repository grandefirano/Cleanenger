package com.grandefirano.cleanenger.notifications;

public class Sender {
    private Data data;
    private String to;

    public Sender(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @SuppressWarnings("unused")
    public String getTo() {
        return to;
    }
    @SuppressWarnings("unused")
    public void setTo(String to) {
        this.to = to;
    }
}
