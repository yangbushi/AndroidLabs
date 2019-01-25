package com.example.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import static android.widget.TextView.BufferType.NORMAL;

public class MainActivity extends AppCompatActivity {
    public static final String PROFILEINTENT = "ProfileActivity";
    private EditText emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab3);

        emailEdit = (EditText) findViewById(R.id.emailAddress);
        SharedPreferences prefs = getSharedPreferences("shared.txt", Context.MODE_PRIVATE);
        String emailAddress = prefs.getString("emailAddress", "");
        emailEdit.setText(emailAddress.toCharArray(), 0, emailAddress.length());

        Button mButton = (Button) findViewById(R.id.lab3LoginButton);
        mButton.setOnClickListener(v->{
            Intent startProfileIntent = new Intent(this, ProfileActivity.class);
            startProfileIntent.putExtra("emailAddress", emailEdit.getText().toString());
            startActivity(startProfileIntent);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = getSharedPreferences("shared.txt", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("emailAddress", emailEdit.getText().toString());
        edit.commit();
    }
}
