package com.grandefirano.cleanenger;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utilities {

    //DATA
    public static String getProperDateFormat(long date){
        SimpleDateFormat formatter;
        Calendar calendarToday=Calendar.getInstance();
        Calendar calendarMessageDay=Calendar.getInstance();
        calendarToday.setTime(new Date(System.currentTimeMillis()));
        calendarMessageDay.setTime(new Date(date));

        boolean sameDayofYear = calendarToday.get(Calendar.DAY_OF_YEAR) ==
                calendarMessageDay.get(Calendar.DAY_OF_YEAR);
        boolean sameYear=calendarToday.get(Calendar.YEAR) == calendarMessageDay.get(Calendar.YEAR);
        if(sameDayofYear&&sameYear) {
            formatter= new SimpleDateFormat("HH:mm");
        }else if(sameYear){
            formatter=new SimpleDateFormat("dd MMM");
        }
        else{
            formatter= new SimpleDateFormat("dd MMM yyyy");
        }

    return formatter.format(new Date(date));
    }

    //PHOTO
    public static Bitmap convertToProfileBitmapFromUri(Context context,Uri imageUri) throws IOException {
        Bitmap bitmap=convertToBitmapFromUri(context,imageUri);
        bitmap=getResizedBitmap(bitmap,1200);
        bitmap=cropToSquare(bitmap);
        return bitmap;
    }
    public static Bitmap convertToStoryBitmapFromUri(Context context,Uri imageUri) throws IOException {
        Bitmap bitmap=convertToBitmapFromUri(context,imageUri);
        bitmap=getResizedBitmap(bitmap,1920);

        return bitmap;
    }


    public static Bitmap convertToBitmapFromUri(Context context,Uri imageUri) throws IOException {
        InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        return bitmap;

    }

    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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
    public static Bitmap cropToSquare(Bitmap bitmap){
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);

        return cropImg;
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
        Bitmap rotatedBitmap= Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        Log.d("dddd","adfter rotation");

        return rotatedBitmap;
    }






}
