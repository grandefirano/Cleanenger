package com.grandefirano.cleanenger.singleItems;


import java.util.HashMap;
import java.util.Map;

public class SingleMessage{
        private String uId;
        private String message;
        private long date;

        private Map<String, String> dateCreated;


        public SingleMessage(String uId, String message, Map<String,String> dateCreated) {
            this.uId = uId;
            this.message = message;
            this.dateCreated = dateCreated;
        }
    public SingleMessage(String uId, String message, long date) {
        this.uId = uId;
        this.message = message;
        this.date=date;
    }
        public SingleMessage() { }




    public String getuId() { return uId; }
        public String getMessage() { return message; }

    public long getDate() {
        return date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uId", uId);
        result.put("message", message);
        result.put("date", dateCreated);


        return result;
    }
    }



