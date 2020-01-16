package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grandefirano.cleanenger.FriendsController;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.Utilities;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static com.grandefirano.cleanenger.Activities.PhotoHelper.getFileExtension;

public class SendPhotoActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    Uri mImageUri;
    Bitmap mSelectedImage;


    ImageView mPhotoImageView;
    ImageView mCancelButton;
    ImageView mSendButton;
    ImageView mRotateButton;
    ConstraintLayout mConstraintLayout;

    FirebaseAuth mAuth;
    DatabaseReference mDatabaseReference;
    DatabaseReference mUsersDatabase;
    DatabaseReference mFriendsReference;
    String mCurrentUserId;


    ArrayList<String> mFriendsIdList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);

        mAuth=FirebaseAuth.getInstance();
        mCurrentUserId= mAuth.getCurrentUser().getUid();

        mDatabaseReference=FirebaseDatabase.getInstance().getReference();

        mFriendsReference=mDatabaseReference.child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("friends");


        FriendsController.downloadFriends(mFriendsReference,mFriendsIdList);


        mPhotoImageView=findViewById(R.id.photoToSendImageView);
        mCancelButton=findViewById(R.id.sendPhotoCancelButton);
        mSendButton=findViewById(R.id.sendPhotoSendButton);
        mRotateButton=findViewById(R.id.sendPhotoRotateButton);
        mConstraintLayout=findViewById(R.id.sendConstraintLayout);

        mConstraintLayout.setVisibility(View.INVISIBLE);


        mUsersDatabase= mDatabaseReference
                .child("users");



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
        values.put(MediaStore.Images.Media.TITLE,"Picture to send");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the Camera");
        mImageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //Camera intent
        Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,mImageUri);
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE);

    }

    public void sendToAll(View view){

        if(mSelectedImage!=null){


            final String random=String.valueOf(UUID.randomUUID());

            final StorageReference profilePhotoReference= FirebaseStorage.getInstance()
                    .getReference().child("snaps")
                    .child(mAuth.getCurrentUser().getUid()).child(random +"."+getFileExtension(mImageUri,getContentResolver()));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            profilePhotoReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            profilePhotoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    for(String friendId:mFriendsIdList) {
                                        Log.d("ddddddID",friendId);
                                        mUsersDatabase.child(friendId).child("snaps").child(mCurrentUserId).child(random).setValue(uri.toString());

                                        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();

                                        mDatabaseReference.child("snaps").child(random).child(friendId).setValue(false);
                                    }

                                    finish();
                                }
                            });



                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            Toast.makeText(this,"No file was selected",Toast.LENGTH_SHORT).show();

        }

    }
    public void cancelAction(View view){
        finish();
    }

    public void rotatePhoto(View view){
        mSelectedImage=Utilities.rotateBitmap(mSelectedImage,270);
        mPhotoImageView.setImageBitmap(mSelectedImage);
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

            mConstraintLayout.setVisibility(View.VISIBLE);



            try {
                mSelectedImage = Utilities.convertToBitmapFromUri(this,mImageUri,Utilities.TYPE_SNAP_PHOTO);

                mSelectedImage= Utilities.rotateImageBasedOnExif(mSelectedImage, String.valueOf(mImageUri));

            } catch (IOException e) { e.printStackTrace(); }



            mPhotoImageView.setImageBitmap(mSelectedImage);


        }
        else{
            finish();
        }
    }
}
