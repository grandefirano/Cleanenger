package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grandefirano.cleanenger.Notifications.APIService;
import com.grandefirano.cleanenger.Notifications.Client;
import com.grandefirano.cleanenger.Notifications.Data;
import com.grandefirano.cleanenger.Notifications.MyResponse;
import com.grandefirano.cleanenger.Notifications.Sender;
import com.grandefirano.cleanenger.Notifications.Token;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static com.grandefirano.cleanenger.Utilities.getFileExtension;


public class SendPhotoActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private static final int LIST_TO_SEND_CODE=1005;

    Uri mImageUri;
    Bitmap mSelectedImage;

    DatabaseReference mDatabaseReference;
    String myId;

    String myName;


    ImageView mPhotoImageView;
    ImageView mCancelButton;
    ImageView mSendButton;
    ImageView mRotateRightButton;
    ImageView mRotateLeftButton;
    ConstraintLayout mConstraintLayout;

    APIService mAPIService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_photo);


        mPhotoImageView=findViewById(R.id.photoToSendImageView);
        mCancelButton=findViewById(R.id.sendPhotoCancelButton);
        mSendButton=findViewById(R.id.sendPhotoSendButton);
        mRotateLeftButton=findViewById(R.id.sendPhotoRotateLeftButton);
        mRotateRightButton=findViewById(R.id.sendPhotoRotateRightButton);
        mConstraintLayout=findViewById(R.id.sendConstraintLayout);

        mConstraintLayout.setVisibility(View.INVISIBLE);

        mRotateLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotatePhoto(270);
            }
        });
        mRotateRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotatePhoto(90);
            }
        });

        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference=FirebaseDatabase.getInstance().getReference();

        if(firebaseUser==null){
            finish();
        }else {
            myId = firebaseUser.getUid();

            mAPIService = Client.getRetrofit("https://fcm.googleapis.com/").create(APIService.class);

            SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
            myName = sharedPreferences.getString(MainActivity.MY_NAME, myId);
        }



        if(Build.VERSION.SDK_INT>=23){
            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                //PERMISSION NOT ENABLED

                String[] permission={Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //SHOW REQUEST
                requestPermissions(permission,PERMISSION_CODE);
            }else{
                //PERMISSION ALREADY GRANTED
                openCamera();
            }
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

    public void openList(View view){

        if(mSelectedImage!=null){

            Intent intent=new Intent(this,ChooseSendListActivity.class);
            startActivityForResult(intent,LIST_TO_SEND_CODE);
        }else{
            finish();
        }

    }
    public void cancelAction(View view){
        finish();
    }

    public void rotatePhoto(int rotation){

        mSelectedImage=Utilities.rotateBitmap(mSelectedImage,rotation);
        mPhotoImageView.setImageBitmap(mSelectedImage);
    }

    //HANDLING PERMISSIONS RESULT
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         if(requestCode==PERMISSION_CODE){
             if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                 //PERMISSION GRANTED
                 openCamera();
             }else{
                 //PERMISSION DENIED
                 Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
             }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LIST_TO_SEND_CODE){
            if(resultCode==RESULT_OK && data!=null){

                final ArrayList<String> listToSend=data.getStringArrayListExtra("listToSend");

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                mSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] bytesData = byteArrayOutputStream.toByteArray();

                final String random = String.valueOf(UUID.randomUUID());

                final StorageReference snapPhotoReference = FirebaseStorage.getInstance()
                        .getReference().child("snaps")
                        .child(myId).child(random + "."+getFileExtension(mImageUri,getContentResolver()));


                snapPhotoReference.putBytes(bytesData)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                snapPhotoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        sendNotification(listToSend);

                                        for (int i = 0; i < listToSend.size(); i++) {

                                                mDatabaseReference.child("users").child(listToSend.get(i)).child("snaps").child(myId).child(random).setValue(uri.toString());

                                                Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_SHORT).show();

                                                mDatabaseReference.child("snaps").child(random).child(listToSend.get(i)).setValue(false);

                                        }


                                    }
                                });


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                finish();
            }
        }
        if(requestCode==IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {

                mConstraintLayout.setVisibility(View.VISIBLE);


                try {
                    mSelectedImage = Utilities.convertToBitmapFromUri(this, mImageUri, Utilities.TYPE_SNAP_PHOTO);

                    mSelectedImage = Utilities.rotateImageBasedOnExif(mSelectedImage, String.valueOf(mImageUri));

                } catch (IOException e) {
                    e.printStackTrace();
                }


                mPhotoImageView.setImageBitmap(mSelectedImage);


            } else {
                finish();
            }
        }
    }
    private void sendNotification(final ArrayList<String> idsOfSendPerson) {
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("tokens");

        for(final String idOfSendPerson:idsOfSendPerson) {
            allTokens.child(idOfSendPerson).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Token token = dataSnapshot.getValue(Token.class);

                    Data data = new Data(myId, "[New snap]", null, myName, idOfSendPerson, R.drawable.ic_notification);
                    if(token!=null) {

                        Sender sender = new Sender(data, token.getToken());

                        mAPIService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                Log.d("Response", response.toString());
                            }
                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) { }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

}
