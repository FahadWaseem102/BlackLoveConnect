package com.swagger.blackloveconnect.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.swagger.blackloveconnect.R;

public class InboxActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fab;
    ImageButton goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        fab = findViewById(R.id.float_new_chat);
        fab.setOnClickListener(this);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.float_new_chat:
                startActivity(new Intent(InboxActivity.this, SearchUsersActivity.class));
                break;
            case R.id.goBack:
                finish();
                break;
        }
    }
}
