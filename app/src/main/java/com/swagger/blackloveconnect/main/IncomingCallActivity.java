package com.swagger.blackloveconnect.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.models.CallModel;
import com.swagger.blackloveconnect.opentok.VideoChatActivity;

import java.util.HashMap;
import java.util.Map;

import static com.swagger.blackloveconnect.utils.Constants.callModelList;
import static com.swagger.blackloveconnect.utils.Constants.usersModelList;

public class IncomingCallActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvUserName;
    TextView tvCallType;
    Button btnDecline, btnAccept;
    CallModel callModel;
    FirebaseAuth mAuth;
    DatabaseReference callsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        mAuth = FirebaseAuth.getInstance();
        callsRef = FirebaseDatabase.getInstance().getReference("Calls");
        tvUserName = findViewById(R.id.userName);
        tvCallType = findViewById(R.id.callType);
        btnDecline = findViewById(R.id.btnDecline);
        btnAccept = findViewById(R.id.btnAccept);
        btnDecline.setOnClickListener(this);
        btnAccept.setOnClickListener(this);

        if (getIntent().hasExtra("callModel")){

            callModel = getIntent().getParcelableExtra("callModel");

            for (int i = 0; i<usersModelList.size(); i++){
                if (usersModelList.get(i).getUid().equals(callModel.getCallerUID())){
                    tvUserName.setText(usersModelList.get(i).getUserName());
                }
            }
            tvCallType.setText(callModel.getCallType()+" Call");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDecline:
                if (callModel!=null){
                    Map<String, Object> map = new HashMap<>();
                    map.put("status", "Disconnected");
                    if (callModel.getCallID()!=null){
                        callsRef.child(callModel.getCallID()).updateChildren(map);
                    }
                }
                finish();
                break;

            case R.id.btnAccept:
                Intent intent = new Intent(IncomingCallActivity.this, VideoChatActivity.class);
                if (callModel.getCallType().equals("Video")){
                    intent.putExtra("VideoEnabled", true);
                }else {
                    intent.putExtra("VideoEnabled", false);
                }
                startActivity(intent);
                finish();
                break;
        }
    }
}
