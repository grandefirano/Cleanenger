package com.grandefirano.cleanenger.singleItems;

public class SinglePersonSearchItem {

    private String mImageResource;
    private String mPersonText;
    private String mPersonId;

    public SinglePersonSearchItem(String imageResource, String person,String personId){
        mImageResource=imageResource;
        mPersonText=person;
        mPersonId=personId;


    }

    public String getImageResource() {
        return mImageResource;
    }

    public String getPersonText() {
        return mPersonText;
    }

    public String getPersonId() {
        return mPersonId;
    }

}
