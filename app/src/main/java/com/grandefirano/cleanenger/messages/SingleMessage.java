package com.grandefirano.cleanenger.messages;



    public class SingleMessage{
        private String uId;
        private String message;

        public SingleMessage(String uId, String message) {
            this.uId = uId;
            this.message = message; }
        public SingleMessage() { }

        public String getuId() { return uId; }
        public String getMessage() { return message; }
    }

