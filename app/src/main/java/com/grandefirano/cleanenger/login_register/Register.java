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

import com.google.firebase.database.ServerValue;
import com.grandefirano.cleanenger.activities.MainActivity;
import com.grandefirano.cleanenger.R;
import com.grandefirano.cleanenger.single_items.LastMessage;
import com.grandefirano.cleanenger.single_items.SingleMainScreenMessage;
import com.grandefirano.cleanenger.single_items.SingleMessage;
import com.grandefirano.cleanenger.single_items.UserData;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Register extends AppCompatActivity {

    //FIREBASE
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

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
        mDatabaseReference=FirebaseDatabase.getInstance().getReference();

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
                                        String linktoPhoto =getResources().getString(R.string.link_to_default_photo);

                                        UserData newUserData = new UserData(email, username, linktoPhoto);
                                        mDatabaseReference.child("users").child(uId)
                                                .child("data").setValue(newUserData);

                                        //SET WELCOMING MESSAGES
                                        setWelcomingMessages(uId);

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

    public void setWelcomingMessages(String userId) {

        String chatId= UUID.randomUUID().toString();
        String[] welcomingSnapsList=getResources().getStringArray(R.array.welcoming_snaps);
        String textOfWelcomingMessage=getResources().getString(R.string.welcoming_message);
        String idOfCleanTeam=getResources()
                .getString(R.string.id_of_cleanenger_profile);
        DatabaseReference userDatabaseReference=
                mDatabaseReference.child("users").child(userId);
        DatabaseReference chatDatabaseReference=
                mDatabaseReference.child("chats").child(chatId);

        //ADD SLIDES
        List<String> welcomingSnapsArrayList= Arrays.asList(welcomingSnapsList);
        userDatabaseReference.child("snaps").child(idOfCleanTeam)
                .setValue(welcomingSnapsArrayList);

        //ADD MESSAGE
        SingleMainScreenMessage singleMainMessage=
                new SingleMainScreenMessage(chatId, ServerValue.TIMESTAMP);
        SingleMessage singleMessage = new SingleMessage(
                userId, textOfWelcomingMessage, ServerValue.TIMESTAMP);
        LastMessage lastMessage = new LastMessage(
                singleMessage.getUId(), singleMessage.getMessage(),
                ServerValue.TIMESTAMP, false);

        userDatabaseReference.child("main_screen_messages")
                .child(idOfCleanTeam).setValue(singleMainMessage.toMap());
        chatDatabaseReference.child("messages").push().setValue(singleMessage.toMap());
        chatDatabaseReference.child("last_message").setValue(lastMessage.toMap());
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
