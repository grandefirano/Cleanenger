package com.grandefirano.cleanenger.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.grandefirano.cleanenger.MainActivity;
import com.grandefirano.cleanenger.R;

import org.w3c.dom.Text;

public class Register extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference myRef;
    String TAG="CHECK_LOG_REGISTER";

    TextView loginTextView;
    TextView passwordTextView;
    TextView confirmPasswordTextView;
    TextView emailTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();

        loginTextView=findViewById(R.id.loginTextView);
        passwordTextView=findViewById(R.id.passwordTextView);
        confirmPasswordTextView=findViewById(R.id.confirmPasswordTextView);
        emailTextView=findViewById(R.id.emailTextView);

    }

    public void register(View view){

        final String username=loginTextView.getText().toString();
        String password=passwordTextView.getText().toString();
        final String email=emailTextView.getText().toString();
        if(checkPassword(password) && !username.equals("") && username!=null){

            //TODO: UTWORZ KONTO

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                //TODO: Create in Database
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uId=user.getUid();

                                myRef=FirebaseDatabase.getInstance()
                                        .getReference().child("users").child(uId);

                                //TODO:nie masz pozwolenia
                                //default photo
                                String linktoPhoto="";

                                UserData newUserData= new UserData(username,email,linktoPhoto);
                                myRef.child("data").setValue(newUserData);


                                goToMain();

                            } else {

                                try {
                                    throw task.getException();
                                }catch (FirebaseAuthUserCollisionException existEmail) {
                                    Toast.makeText(Register.this, "This email already exists",
                                            Toast.LENGTH_SHORT).show();
                                //TODO: NIEUDANA REJESTRACJA
                                } catch (Exception e) {
                                    Toast.makeText(Register.this, "There is some problem with Signing Up",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }

                            // ...
                        }
                    });


        }
    }
    private boolean checkPassword(String password){
        String confirmPassword=confirmPasswordTextView.getText().toString();
        //CHECK password and make toast
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
        startActivity(intent);
    }



    static public class UserData{
        public String username;
        public String email;
        public String profile_photo;

        public UserData() {
        }
        public UserData(String username, String email,String profile_photo) {
            this.username = username;
            this.email = email;
            this.profile_photo=profile_photo;
        }
        public String getUsername() {
            return username;
        }
        public String getEmail() {
            return email;
        }

        public String getProfile_photo() {
            return profile_photo;
        }
    }

}
