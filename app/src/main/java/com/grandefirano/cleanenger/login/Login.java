package com.grandefirano.cleanenger.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.grandefirano.cleanenger.MainActivity;
import com.grandefirano.cleanenger.R;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;
    String TAG="CHECK_LOG_LOGIN";

    TextView loginTextView;
    TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();

        loginTextView = findViewById(R.id.loginTextView);
        passwordTextView = findViewById(R.id.passwordTextView);

    }

    public void login(View view) {

       //TODO:
        String email=loginTextView.getText().toString();
        //String username = loginTextView.getText().toString();
        String password = passwordTextView.getText().toString();

        //TODO: zrobić logowanie do firebase

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            gotoMain();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            //TODO: EXCEPTIONS
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });



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
        if(currentUser!=null) {
            gotoMain();
        }else{
            //TODO: IF USER IS NULL


        }


    }
}