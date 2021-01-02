package com.swagger.blackloveconnect.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.adapters.AdapterChat;
import com.swagger.blackloveconnect.models.MessageModel;
import com.swagger.blackloveconnect.models.UsersModel;
import com.swagger.blackloveconnect.opentok.VideoChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvUserName;
    ImageButton goBack, btnAudioCall, btnVideoCall;
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    FirebaseDatabase firebaseDatabase;
    EditText etTypeHere;
    ImageButton btnSend;
    UsersModel usersModel;
    String userId, receiverId;
    AdapterChat adapterChat;
    RecyclerView recyclerChat;
    List<MessageModel> messageModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        tvUserName = findViewById(R.id.tvUserName);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        messageModelList = new ArrayList<>();
        goBack = findViewById(R.id.goBack);
        btnAudioCall = findViewById(R.id.btnAudio);
        btnAudioCall.setOnClickListener(this);
        btnVideoCall = findViewById(R.id.btnVideo);
        btnVideoCall.setOnClickListener(this);
        etTypeHere = findViewById(R.id.etTypeHere);
        recyclerChat = findViewById(R.id.recycler_chat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setReverseLayout(false);
        recyclerChat.setLayoutManager(linearLayoutManager);
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        goBack.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent.hasExtra("user")){
            usersModel = intent.getParcelableExtra("user");
            userId = usersModel.getUid();
            receiverId = mAuth.getCurrentUser().getUid();
            tvUserName.setText(usersModel.getUserName());
        }
        getChat();
    }

    private void getChat(){
        messageModelList.clear();
        adapterChat = new AdapterChat(messageModelList, this);
        recyclerChat.setAdapter(adapterChat);
        rootRef.child("Message").child(receiverId).child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                MessageModel messagesModel = dataSnapshot.getValue(MessageModel.class);
                messageModelList.add(messagesModel);
                adapterChat.notifyDataSetChanged();
                recyclerChat.smoothScrollToPosition(recyclerChat.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.goBack:
                finish();
                break;

            case R.id.btnVideo:
                Intent intent = new Intent(ChatActivity.this, VideoChatActivity.class);
                intent.putExtra("VideoEnabled", true);
                intent.putExtra("userID", userId);
                startActivity(intent);
                break;

            case R.id.btnAudio:
                Intent intent1 = new Intent(ChatActivity.this, VideoChatActivity.class);
                intent1.putExtra("VideoEnabled", false);
                intent1.putExtra("userID", userId);
                startActivity(intent1);
                break;

            case R.id.btnSend:
                String message = etTypeHere.getText().toString();
                if (TextUtils.isEmpty(message)){
                    Toast.makeText(this, "Please write message first", Toast.LENGTH_SHORT).show();
                }else {
                    String messageReferenceReceiver = "Message/" + receiverId + "/" + userId;
                    String messageReferenceSender = "Message/" + userId + "/" + receiverId;

                    DatabaseReference userMessRefKey = rootRef.child("Messages").child(receiverId).child(userId).push();
                    String messagePushId = userMessRefKey.getKey();

                    Map<String, Object> messageBody = new HashMap();
                    messageBody.put("message", message);
                    messageBody.put("timeStamp", System.currentTimeMillis());
                    messageBody.put("from", receiverId);

                    Map<String, Object> messageBodyDetails = new HashMap();
                    messageBodyDetails.put(messageReferenceReceiver + "/" + messagePushId, messageBody);
                    messageBodyDetails.put(messageReferenceSender + "/" + messagePushId, messageBody);
                    rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            etTypeHere.setText("");
                        }
                    });
                }

                break;
        }
    }
}
