package com.grandefirano.cleanenger.SingleItems;

import java.util.Map;


    public class LastMessage extends SingleMessage{
        private boolean isifRead;

        public LastMessage(String uId, String message, Map<String,String> dateCreated, boolean isRead) {
            super(uId, message,dateCreated);
            this.isifRead = isRead;
        }
        public Map<String, Object> toMap() {
            Map<String,Object> result=super.toMap();
            result.put("ifRead",isifRead);
            return result;
        }

        @SuppressWarnings("unused")
        public LastMessage() { }

        public boolean isifRead() {
            return isifRead;
        }
    }

