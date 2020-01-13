package com.grandefirano.cleanenger.login;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utilities {

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
    public static Bitmap convertToBitmapFromStream(InputStream imageStream){
        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
        bitmap=getResizedBitmap(bitmap,1200);
        bitmap=cropToSquare(bitmap);
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

}
