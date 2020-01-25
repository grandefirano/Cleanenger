package com.grandefirano.cleanenger.login_register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.grandefirano.cleanenger.activities.MainActivity;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.single_items.UserData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Register extends AppCompatActivity {

    //FIREBASE
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    //VIEWS
    private TextView loginTextView;
    private TextView passwordTextView;
    private TextView confirmPasswordTextView;
    private TextView emailTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //FIREBASE
        mAuth=FirebaseAuth.getInstance();

        //VIEWS
        loginTextView=findViewById(R.id.loginTextView);
        passwordTextView=findViewById(R.id.passwordTextView);
        confirmPasswordTextView=findViewById(R.id.confirmPasswordTextView);
        emailTextView=findViewById(R.id.emailTextView);
    }

    public void register(View view){

        final String username=loginTextView.getText().toString();
        String password=passwordTextView.getText().toString();
        final String email=emailTextView.getText().toString();

        if(username.length()>0 && password.length()>0
                && email.length()>0) {
            if (checkPassword(password)) {

                //CREATE ACCOUNT
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if(user!=null) {
                                        String uId = user.getUid();

                                        myRef = FirebaseDatabase.getInstance()
                                                .getReference().child("users").child(uId);

                                        String linktoPhoto = "https://firebasestorage.googleapis.com/v0/b/cleanenger.appspot.com/o/profile_photos%2Fdefault_profile_photo.jpg?alt=media&token=5f8f3295-d9d1-4a70-bc41-b344cf07fd5d";

                                        UserData newUserData = new UserData(email, username, linktoPhoto);
                                        myRef.child("data").setValue(newUserData);

                                        //SET WELCOMING MESSAGES
                                        setWelcomingMessages(myRef);

                                        goToMain();
                                    }

                                } else if(task.getException()!=null) {
                                        try {
                                            throw task.getException();

                                        } catch (FirebaseAuthUserCollisionException existEmail) {
                                            Toast.makeText(Register.this, "This email already exists",
                                                    Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Toast.makeText(Register.this, "There is some problem with signing up",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                }
                            }
                        });
            }
        }else{
            Toast.makeText(Register.this, "Fill all the blank fields",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void setWelcomingMessages(DatabaseReference referenceOfUser) {

        //ADD SLIDES
        String[] welcomingSnapsList=getResources().getStringArray(R.array.welcoming_snaps);

        List<String> welcomingSnapsArrayList= Arrays.asList(welcomingSnapsList);
        referenceOfUser.child("snaps").child("7EFVvXXy7uZwsWC036PQftd4OWG3").setValue(welcomingSnapsArrayList);



    }

    private boolean checkPassword(String password){
        String confirmPassword=confirmPasswordTextView.getText().toString();
        //CHECK PASSWORD and make toast
        if(password.equals(confirmPassword) && password.length()>=5){
            return true;
        }else if(password.equals(confirmPassword)){
            Toast.makeText(this,"Passwords are not the same",Toast.LENGTH_SHORT).show();

            return false;
        }else if(password.length()<5){
            Toast.makeText(this,"Password is too short",Toast.LENGTH_SHORT).show();
            return false;
        }else return false;

    }

    public void goToLogin(View view){

        Intent intent=new Intent(this, Login.class);
        startActivity(intent);
    }
    public void goToMain(){
        Intent intent=new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
