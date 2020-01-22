package com.grandefirano.cleanenger.Login;

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
import com.google.firebase.auth.FirebaseUser;
import com.grandefirano.cleanenger.Activities.MainActivity;
import com.grandefirano.cleanenger.R;

public class Login extends AppCompatActivity {

    FirebaseAuth mAuth;

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


        String email=loginTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        if(email.length()>0 && password.length()>0) {

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
}