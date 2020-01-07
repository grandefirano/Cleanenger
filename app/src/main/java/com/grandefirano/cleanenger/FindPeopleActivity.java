package com.grandefirano.cleanenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class FindPeopleActivity extends AppCompatActivity {

    ImageButton mImageButton;
    EditText mSearchEditText;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);

        mImageButton=findViewById(R.id.searchPeopleImageButton);
        mSearchEditText=findViewById(R.id.searchPeopleEditText);
        mRecyclerView=findViewById(R.id.searchPeopleRecyclerView);

    }

    public void searchPeople(View view){
        String searchName=mSearchEditText.getText().toString();
    }

}
