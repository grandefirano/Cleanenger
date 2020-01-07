package com.grandefirano.cleanenger;

public class SinglePersonSearchItem {

    private String mImageResource;
    private String mPersonText;
    //private boolean mIfYourFriend;

    public SinglePersonSearchItem(String imageResource, String person){
        mImageResource=imageResource;
        mPersonText=person;
       // mIfYourFriend=ifYourFriend;

    }

    public String getImageResource() {
        return mImageResource;
    }

    public String getPersonText() {
        return mPersonText;
    }


//    public boolean isIfYourFriend() {
//        return mIfYourFriend;
//    }
}
