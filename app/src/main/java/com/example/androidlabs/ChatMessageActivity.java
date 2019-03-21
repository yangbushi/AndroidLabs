package com.example.androidlabs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ChatMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from ChatRoomActivity

        //This is copied directly from FragmentExample.java lines 47-54
        DetailFragment dFragment = new DetailFragment();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        dFragment.setTablet(false); //tell the Fragment that it's on a phone.
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lab8FragLocation, dFragment)
                .addToBackStack("AnyName")
                .commit();
    }
}
