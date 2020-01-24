package com.grandefirano.cleanenger.single_items;

import java.util.Map;


    public class LastMessage extends SingleMessage{
        private boolean ifRead;

        public LastMessage(String uId, String message, Map<String,String> dateCreated, boolean ifRead) {
            super(uId, message,dateCreated);
            this.ifRead = ifRead;
        }
        //GETTING FROM FIREBASE
        @SuppressWarnings("unused")
        public LastMessage(String uId, String message, long date, boolean ifRead) {
            super(uId, message, date);
            this.ifRead =ifRead;
        }

        public Map<String, Object> toMap() {
            Map<String,Object> result=super.toMap();
            result.put("ifRead",ifRead);
            return result;
        }

        @SuppressWarnings("unused")
        public LastMessage() { }

        public boolean isIfRead() {
            return ifRead;
        }
    }

