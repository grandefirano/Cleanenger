package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.grandefirano.cleanenger.R;

public class SendPhotoActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Uri imageUri;
    ImageView mPhotoImageView;
    Button mCancelButton;
    Button mSendButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);

        mPhotoImageView=findViewById(R.id.photoToSendImageView);



        if(Build.VERSION.SDK_INT>=23){
            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                //pwemission not enabled,request it

                String[] permission={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //show popup request permission
                requestPermissions(permission,PERMISSION_CODE);
            }else{
                //permission already granted
                openCamera();
            }
        } else{
            //system os<marshmallow
        }
    }
    private void openCamera() {


        ContentValues values= new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Taken Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //Camera intent
        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);

    }

    //handling permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //permission from popup was granted
                    openCamera();
                }else{
                    //permission from popup was denied
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            mPhotoImageView.setImageURI(imageUri);
        }
    }
}
