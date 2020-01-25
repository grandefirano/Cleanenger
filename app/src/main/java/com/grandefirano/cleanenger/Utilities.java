package com.grandefirano.cleanenger;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.webkit.MimeTypeMap;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.grandefirano.cleanenger.single_items.LastMessage;
import com.grandefirano.cleanenger.single_items.SingleMainScreenMessage;
import com.grandefirano.cleanenger.single_items.SingleMessage;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Utilities {


    public static final int TYPE_SNAP_PHOTO=0;
    public static final int TYPE_PROFILE_PHOTO=1;


    //DATA
    public static String getProperDateFormat(long date,boolean insideChat){
        SimpleDateFormat formatter;
        Calendar calendarToday=Calendar.getInstance();
        Calendar calendarMessageDay=Calendar.getInstance();
        calendarToday.setTime(new Date(System.currentTimeMillis()));
        calendarMessageDay.setTime(new Date(date));

        boolean sameDayofYear = calendarToday.get(Calendar.DAY_OF_YEAR) ==
                calendarMessageDay.get(Calendar.DAY_OF_YEAR);
        boolean sameYear=calendarToday.get(Calendar.YEAR) == calendarMessageDay.get(Calendar.YEAR);
        if(sameDayofYear&&sameYear) {
            formatter= new SimpleDateFormat("HH:mm", Locale.getDefault());
        }else if(sameYear){
            if(insideChat){
                formatter=new SimpleDateFormat("dd MMM 'AT' HH:mm",Locale.getDefault());
            }else {
                formatter = new SimpleDateFormat("dd MMM",Locale.getDefault());
            }
        }
        else{
            if (insideChat) {
                formatter = new SimpleDateFormat("dd MMM yyyy 'AT' HH:mm",Locale.getDefault());
            }else {
                formatter = new SimpleDateFormat("dd MMM yyyy",Locale.getDefault());
            }
        }

    return formatter.format(new Date(date));
    }

    //PHOTO
    public static String getFileExtension(Uri uri,ContentResolver cR){
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    public static Bitmap convertToBitmapFromUri(Context context,Uri imageUri,int type) throws IOException {
        InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

        if(type==TYPE_SNAP_PHOTO) {
            bitmap = getResizedBitmap(bitmap, 1920);
        }else if(type==TYPE_PROFILE_PHOTO){
            bitmap=getResizedBitmap(bitmap,1200);
            bitmap=cropToSquare(bitmap);
        }

        return bitmap;
    }
    private static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    private static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;

        return Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

    }
    public static Bitmap rotateImageBasedOnExif(Bitmap bitmap,String imageFileLocation) throws IOException {


        ExifInterface exifInterface = new ExifInterface(imageFileLocation);
        int orientation;

            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90);

        Bitmap rotatedBitmap;
        int rotation;

        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotation=90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotation=180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotation=270;
                break;
            default:
                rotation=0;
        }
        rotatedBitmap=rotateBitmap(bitmap,rotation);

        return rotatedBitmap;
    }
    public static Bitmap rotateBitmap(Bitmap bitmap, int rotation){
        Matrix matrix= new Matrix();
        matrix.setRotate(rotation);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),
                bitmap.getHeight(),matrix,true);
    }

    //WELCOMING MESSAGES AND SNAPS
    public static void setWelcomingMessages(Context context, String userId) {

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

        String chatId= UUID.randomUUID().toString();
        String[] welcomingSnapsList=context.getResources().getStringArray(R.array.welcoming_snaps);
        String textOfWelcomingMessage=context.getResources().getString(R.string.welcoming_message);
        String idOfCleanTeam=context.getResources()
                .getString(R.string.id_of_cleanenger_profile);
        DatabaseReference userDatabaseReference=
                databaseReference.child("users").child(userId);
        DatabaseReference chatDatabaseReference=
                databaseReference.child("chats").child(chatId);

        //ADD SLIDES
        List<String> welcomingSnapsArrayList= Arrays.asList(welcomingSnapsList);
        userDatabaseReference.child("snaps").child(idOfCleanTeam)
                .setValue(welcomingSnapsArrayList);

        //ADD MESSAGE
        SingleMainScreenMessage singleMainMessage=
                new SingleMainScreenMessage(chatId, ServerValue.TIMESTAMP);
        SingleMessage singleMessage = new SingleMessage(
                idOfCleanTeam, textOfWelcomingMessage, ServerValue.TIMESTAMP);
        LastMessage lastMessage = new LastMessage(
                singleMessage.getUId(), singleMessage.getMessage(),
                ServerValue.TIMESTAMP, false);

        userDatabaseReference.child("main_screen_messages")
                .child(idOfCleanTeam).setValue(singleMainMessage.toMap());
        chatDatabaseReference.child("messages").push().setValue(singleMessage.toMap());
        chatDatabaseReference.child("last_message").setValue(lastMessage.toMap());
    }
}
