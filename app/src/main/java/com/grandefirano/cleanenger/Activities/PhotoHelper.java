package com.grandefirano.cleanenger.Activities;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class PhotoHelper {

    public static String getFileExtension(Uri uri,ContentResolver cR){
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
