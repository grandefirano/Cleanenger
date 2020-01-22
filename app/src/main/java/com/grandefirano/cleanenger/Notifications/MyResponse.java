package com.grandefirano.cleanenger.Notifications;

public class MyResponse {
    private String success;

    public MyResponse(String success) {
        this.success = success;
    }
    @SuppressWarnings("unused")
    public String getSuccess() {
        return success;
    }
    @SuppressWarnings("unused")
    public void setSuccess(String success) {
        this.success = success;
    }
}
