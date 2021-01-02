package com.swagger.blackloveconnect.opentok;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opentok.android.AudioDeviceManager;
import com.opentok.android.BaseAudioDevice;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.models.CallModel;

import java.util.HashMap;
import java.util.Map;

import static com.swagger.blackloveconnect.utils.Constants.admin;
import static com.swagger.blackloveconnect.utils.Constants.callModelList;
import static com.swagger.blackloveconnect.utils.Constants.usersModelList;

public class VideoChatActivity extends AppCompatActivity implements Session.SessionListener,
        PublisherKit.PublisherListener {

    FrameLayout subscriberContainer, publisherContainer;
    ImageButton btnDisconnect, btnCamera;
    boolean videoEnabled = false;
    private Session session;
    RelativeLayout relativeAudio;
    private Publisher publisher;
    private Subscriber subscriber;
    String callType;
    String userID;
    TextView tvUserName;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    boolean toAdmin = false;
    DatabaseReference callsRef;
    CallModel callModel;
    private static String API_KEY = "46980104";
    private static String SESSION_ID = "1_MX40Njk4MDEwNH5-MTYwNDgzMzk4MjM4MX5FN2d4MzVlT2QyN2pDdFB6WnQ5VC9leEh-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00Njk4MDEwNCZzaWc9NTVkYjZlMDg1NGJmNzIzMmZhNWRkMTZmODllYTc5MTE2MDg1YTQ1MTpzZXNzaW9uX2lkPTFfTVg0ME5qazRNREV3Tkg1LU1UWXdORGd6TXprNE1qTTRNWDVGTjJkNE16VmxUMlF5TjJwRGRGQjZXblE1VkM5bGVFaC1mZyZjcmVhdGVfdGltZT0xNjA0ODM0MDAzJm5vbmNlPTAuNjk3MTAwODUzOTg5MDA4NyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjA3NDI2MDAyJmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        callsRef = firebaseDatabase.getReference("Calls");
        tvUserName = findViewById(R.id.userName);
        btnCamera = findViewById(R.id.btnCamera);
        subscriberContainer = findViewById(R.id.subscriber_container);
        publisherContainer = findViewById(R.id.publisher_container);
        btnDisconnect = findViewById(R.id.btnDisconnect);
        relativeAudio = findViewById(R.id.relative_audio);
        Intent intent = getIntent();
        if (intent.hasExtra("VideoEnabled")){
            toAdmin = intent.getBooleanExtra("toAdmin", false);

            if (intent.hasExtra("userID")){
                userID = intent.getExtras().getString("userID");
            }else {
                userID=admin.getUid();
            }

            videoEnabled = intent.getBooleanExtra("VideoEnabled", false);
            if (videoEnabled){
                callType = "Video";
            }else {
                callType = "Audio";
                btnCamera.setVisibility(View.GONE);
                relativeAudio.setVisibility(View.VISIBLE);
                subscriberContainer.setVisibility(View.GONE);
                publisherContainer.setVisibility(View.GONE);
            }
        }
        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (session!=null){
                    session.disconnect();
                    if (publisher!=null){
                        publisher.destroy();
                    }
                    if (subscriber!=null){
                        subscriber.destroy();
                    }
                    VideoChatActivity.this.finish();
                }
            }
        });
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publisher.cycleCamera();
            }
        });

        callModel = new CallModel();
        if (!mAuth.getCurrentUser().getUid().equals(userID)){
            callModel.setCallerUID(mAuth.getCurrentUser().getUid());
            callModel.setStatus("Ringing");
            callModel.setStartTime(System.currentTimeMillis());
            callModel.setCallType(callType);
            if (toAdmin){
                callModel.setReceiverUID(admin.getUid());
                callModel.setRoomID(admin.getUid()+"-"+mAuth.getCurrentUser().getUid());
            }else {
                callModel.setReceiverUID(userID);
                callModel.setRoomID(userID+"-"+mAuth.getCurrentUser().getUid());
            }
            String key = callsRef.push().getKey();
            callModel.setCallID(key);
            callsRef.child(key).setValue(callModel);
            if (!videoEnabled){
                for (int i = 0; i<usersModelList.size(); i++){
                    if (mAuth.getCurrentUser().equals(admin.getUid())){
                        if (usersModelList.get(i).getUid().equals(callModel.getCallerUID())){
                            tvUserName.setText(usersModelList.get(i).getUserName());
                        }
                    }else {
                        tvUserName.setText(admin.getUserName());
                    }

                }
            }
        }

        session = new Session.Builder(this, API_KEY, SESSION_ID).build();
        session.setSessionListener(VideoChatActivity.this);
        session.connect(TOKEN);

        if (callModel.getCallID()!=null){
            callsRef.child(callModel.getCallID()).child("status").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.exists()){
                        String status = snapshot.getValue(String.class);
                        if (status.equals("Disconnected")){
                            if (session!=null){
                                session.disconnect();
                                if (publisher!=null){
                                    publisher.destroy();
                                }
                                if (subscriber!=null){
                                    subscriber.destroy();
                                }
                                VideoChatActivity.this.finish();
                            }
                        }
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onConnected(Session session) {
        if (videoEnabled){
            publisher = new Publisher.Builder(this).build();
            AudioDeviceManager.getAudioDevice().setOutputMode(
                    BaseAudioDevice.OutputMode.SpeakerPhone);
        }else {
            publisher = new Publisher.Builder(this).videoTrack(false).build();
            AudioDeviceManager.getAudioDevice().setOutputMode(
                    BaseAudioDevice.OutputMode.Handset);
        }
        publisher.setPublisherListener(this);
        publisherContainer.addView(publisher.getView());
        if (publisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) publisher.getView()).setZOrderOnTop(true);
        }
        session.publish(publisher);

        if (callModel!=null){
            Map<String, Object> map = new HashMap<>();
            map.put("status", "Connected");
            map.put("connectStartTime", System.currentTimeMillis());
            if (callModel.getCallID()!=null){
                callsRef.child(callModel.getCallID()).updateChildren(map);
            }
        }
    }

    @Override
    public void onDisconnected(Session session) {
        if (session!=null){
            session.disconnect();
            if (publisher!=null){
                publisher.destroy();
            }
            if (subscriber!=null){
                subscriber.destroy();
            }
            if (callModel!=null){
                Map<String, Object> map = new HashMap<>();
                map.put("status", "Ended");
                map.put("callEndTime", System.currentTimeMillis());
                if (callModel.getCallID()!=null){
                    callsRef.child(callModel.getCallID()).updateChildren(map);
                }
            }
            VideoChatActivity.this.finish();
        }
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        if (subscriber==null){

            subscriber = new Subscriber.Builder(this, stream).build();
            if (!videoEnabled){
                subscriber.setSubscribeToAudio(true); // video off
                subscriber.setSubscribeToVideo(false);
                AudioDeviceManager.getAudioDevice().setOutputMode(
                        BaseAudioDevice.OutputMode.Handset);
            }else {
                subscriber.setSubscribeToAudio(true);
                subscriber.setSubscribeToVideo(true);
                AudioDeviceManager.getAudioDevice().setOutputMode(
                        BaseAudioDevice.OutputMode.SpeakerPhone );
            }
            session.subscribe(subscriber);
            subscriberContainer.addView(subscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        if (subscriber!=null){
            subscriber=null;
            subscriberContainer.removeAllViews();
            if (session!=null){
                session.disconnect();
                if (publisher!=null){
                    publisher.destroy();
                }
                if (subscriber!=null){
                    subscriber.destroy();
                }
                VideoChatActivity.this.finish();
            }
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
