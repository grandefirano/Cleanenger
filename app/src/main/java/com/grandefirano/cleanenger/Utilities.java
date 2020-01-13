package com.grandefirano.cleanenger;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;

import java.io.File;
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
            formatter=new SimpleDateFormat("dd MMM.");
        }
        else{
            formatter= new SimpleDateFormat("dd MMM. yyyy");
        }

    return formatter.format(new Date(date));
    }

    //PHOTO
    public static Bitmap convertToProfileBitmapFromStream(ContentResolver contentResolver,Uri imageUri) throws FileNotFoundException {
        InputStream imageStream = contentResolver.openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        bitmap=getResizedBitmap(bitmap,1200);
        bitmap=cropToSquare(bitmap);
        return bitmap;
    }
    public static Bitmap convertToStoryBitmapFromSteam(Context context,Uri imageUri) throws IOException {
        InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        rotateImage(bitmap, String.valueOf(imageUri));
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
    public static Bitmap rotateImage(Bitmap bitmap,String imageFileLocation) throws IOException {


        ExifInterface exifInterface = new ExifInterface(imageFileLocation);
        int orientation;

            orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90);

        Matrix matrix= new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
            default:
        }

        Bitmap rotatedBitmap= Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        return rotatedBitmap;
    }

    public static Bitmap rotateBitmap(Context context, Uri photoUri, Bitmap bitmap) {
        int orientation = getOrientation(context, photoUri);
        if (orientation <= 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return bitmap;
    }
    private static int getOrientation(Context context, Uri photoUri) {



        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);

        if (cursor.getCount() != 1) {
            cursor.close();
            return -1;
        }

        cursor.moveToFirst();
        int orientation = cursor.getInt(0);
        cursor.close();
        cursor = null;
        Log.d("ddddorient", String.valueOf(orientation));
        return orientation;
    }







}
