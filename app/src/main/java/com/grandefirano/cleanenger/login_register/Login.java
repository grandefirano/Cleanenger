package com.grandefirano.cleanenger.login_register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.grandefirano.cleanenger.Utilities;
import com.grandefirano.cleanenger.activities.MainActivity;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.single_items.UserData;

public class Login extends AppCompatActivity {

    //FIREBASE AND FB
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;

    //VIEWS
    private TextView loginTextView;
    private TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginTextView = findViewById(R.id.loginTextView);
        passwordTextView = findViewById(R.id.passwordTextView);

        //FIREBASE AND FB
        mAuth= FirebaseAuth.getInstance();

        mCallbackManager=CallbackManager.Factory.create();
        LoginButton loginButton=findViewById(R.id.loginWithFacebookButton);

        loginButton.setPermissions("email","public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() { }
            @Override
            public void onError(FacebookException error) { }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode,resultCode,data);
    }

    public void login(View view) {

        String email=loginTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        if(email.length()>0 && password.length()>0) {
            //SIGN IN NORMALLY
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                gotoMain();
                            } else {
                                Toast.makeText(Login.this, "Authentication failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(Login.this, "Fill all blank fields",
                    Toast.LENGTH_SHORT).show();
        }
    }
    public void goToRegister(View view){
        Intent intent= new Intent(this,Register.class);
        startActivity(intent);
    }
    public void gotoMain(){
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) gotoMain();
    }

    private void handleFacebookAccessToken(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user!=null) {
                                loadFacebookUserToDatabase(user);
                                gotoMain();
                            }
                        } else {
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadFacebookUserToDatabase(FirebaseUser user) {

        //VARIABLES
        Uri photoUri=user.getPhotoUrl();
        final String uId=user.getUid();
        String email=user.getEmail();
        String username=user.getDisplayName();
        String photo;
        if(photoUri!=null) {
            photo = photoUri.toString();
        }
        else{
            photo = "https://firebasestorage.googleapis.com/v0/b/cleanenger.appspot.com/o/profile_photos%2Fdefault_profile_photo.jpg?alt=media&token=5f8f3295-d9d1-4a70-bc41-b344cf07fd5d";
        }

        final UserData userData=new UserData(email,username,photo);

        //FIREBASE

        FirebaseDatabase.getInstance().getReference()
                .child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    //IF LOGGED FIRST TIME SEND WELCOMING MESSAGES
                    Utilities.setWelcomingMessages(getApplicationContext(),uId);
                }
                FirebaseDatabase.getInstance().getReference()
                        .child("users").child(uId).child("data").setValue(userData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}