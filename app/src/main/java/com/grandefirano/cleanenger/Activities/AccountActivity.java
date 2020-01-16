package com.grandefirano.cleanenger.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.Utilities;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AccountActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST=1;

    FirebaseAuth mAuth;
    DatabaseReference mUserDataDatabase;
    FirebaseUser user;

    EditText emailEditText;
    EditText passwordEditText;
    EditText usernameEditText;
    LinearLayout saveAccountLinearLayout;
    ImageView mProfilePhotoImageView;

    String usernameBefore;
    String emailBefore;

    String photoBefore;
    Bitmap newPhotoBitmap;

    private View alertView;
    private Uri mImageUri;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mAuth=FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mUserDataDatabase=FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(mAuth.getCurrentUser().getUid())
                .child("data");

        emailEditText=findViewById(R.id.emailAccountEditText);
        passwordEditText=findViewById(R.id.passwordAccountEditText);
        usernameEditText=findViewById(R.id.usernameAccountEditText);
        saveAccountLinearLayout=findViewById(R.id.saveAccountLinearLayout);
        mProfilePhotoImageView=findViewById(R.id.profilePhotoImageView);


        updatePools();







    }

    public void showDialog(){
        LayoutInflater inflater = this.getLayoutInflater();
        alertView=inflater.inflate(R.layout.dialog_reauth,null);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Reauth")
                .setMessage("Are u?")
                .setView(alertView)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        EditText passEditText=alertView.findViewById(R.id.passwordWhenChangeProfile);
                        String passwordWritten=passEditText.getText().toString();
                        Log.d("dddd",emailBefore);
                        if(passwordWritten!=null && !passwordWritten.equals("")){
                            //TODO: ogarnac co gdy źle podane
                        reauthenticate(passwordWritten);
                        saveData(null);
                        }else{
                            showDialog();
                        }
                    }
                }).create()
                .show();
    }

    public void saveData(View view){

        final String temporaryEmail=emailEditText.getText().toString();
        final String temporaryPassword=passwordEditText.getText().toString();
        String temporaryUsername=usernameEditText.getText().toString();

        if(!temporaryEmail.equals(emailBefore)){
            mUserDataDatabase.child("email").setValue(temporaryEmail);
            changeEmailInAuth(temporaryEmail);
        }
        if(!temporaryUsername.equals(usernameBefore)){
            mUserDataDatabase.child("username").setValue(temporaryUsername);
        }
        if(!temporaryPassword.equals("") && !(temporaryPassword==null)){
            changePasswordInAuth(temporaryPassword);
        }
        if(newPhotoBitmap!=null) {
            uploadFile();
        }

        finish();

    }
    public void closeAccountActivity(View view){
        finish();
    }


    private AuthCredential getCredentials(String passwordWritten){

       AuthCredential credential = EmailAuthProvider
                .getCredential(emailBefore, passwordWritten);
        return credential;
    }
    private void reauthenticate(String passwordWritten){
        user.reauthenticate(getCredentials(passwordWritten))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

    }
    private void changeEmailInAuth(final String newEmail){

            user.updateEmail(newEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("ddddd", "User email address updated.");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if(e.getClass().equals(FirebaseAuthRecentLoginRequiredException.class)){

                        //REAUTHORIZATION
                        showDialog();

                    }
                }
            });


    }
    private void changePasswordInAuth(final String newPassword){
        user.updatePassword(newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("dddd", "User password updated.");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e.getClass().equals(FirebaseAuthRecentLoginRequiredException.class)){

                    //REAUTH
                    showDialog();
                }

            }
        });

    }
    public void changePhoto(View view){
        openPhotoChooser();
    }


    private void uploadFile(){

            final StorageReference profilePhotoReference= FirebaseStorage.getInstance()
                    .getReference().child("profile_photos")
                    .child(mAuth.getCurrentUser().getUid()+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        newPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();


        profilePhotoReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                           profilePhotoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mUserDataDatabase.child("profilePhoto").setValue(uri.toString());
                                    Toast.makeText(getApplicationContext(),"Upload successful",Toast.LENGTH_SHORT).show();

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


    }

    private void openPhotoChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST&&resultCode==RESULT_OK
        &&data!=null &&data.getData()!=null){

            Uri imageUri=data.getData();

            try {

                Bitmap selectedImage=Utilities.convertToBitmapFromUri(getApplicationContext(),imageUri,Utilities.TYPE_PROFILE_PHOTO);
                mProfilePhotoImageView.setImageBitmap(selectedImage);
                newPhotoBitmap=selectedImage;

            } catch (IOException e) { e.printStackTrace(); Toast.makeText(this,"Problem with compression",Toast.LENGTH_SHORT).show(); }

        }
    }

    private void updatePools(){

        Log.i("dddd",mAuth.getCurrentUser().getUid());

        mUserDataDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    emailBefore=String.valueOf(dataSnapshot.child("email").getValue());
                    emailEditText.setText(emailBefore);

                    usernameBefore=String.valueOf(dataSnapshot.child("username").getValue());
                    usernameEditText.setText(usernameBefore);

                    photoBefore=String.valueOf(dataSnapshot.child("profilePhoto").getValue());
                    Picasso.with(getApplicationContext()).load(photoBefore)
                        .fit()
                        .centerCrop()
                        .into(mProfilePhotoImageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
