package com.swagger.blackloveconnect.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.adapters.FragmentAdapter;
import com.swagger.blackloveconnect.fragments.CallsFragment;
import com.swagger.blackloveconnect.fragments.ChatsFragment;
import com.swagger.blackloveconnect.models.CallModel;
import com.swagger.blackloveconnect.opentok.VideoChatActivity;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import static com.swagger.blackloveconnect.utils.Constants.admin;
import static com.swagger.blackloveconnect.utils.Constants.callModelList;
import static com.swagger.blackloveconnect.utils.Constants.usersModelList;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    BottomNavigationView navigation;
    List<String> callsKeys;
    private static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 110;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference callsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = findViewById(R.id.navigation);
        callsKeys = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        callsRef = firebaseDatabase.getReference("Calls");
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = findViewById(R.id.viewpager);
        setupFm(getSupportFragmentManager(), viewPager);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(new PageChange());
        requestAppPermissions();
        getAllCalls();
    }

    public static void setupFm(FragmentManager fragmentManager, ViewPager viewPager){
        FragmentAdapter adapter = new FragmentAdapter(fragmentManager);
        adapter.add(new CallsFragment(), "Calls");
        adapter.add(new ChatsFragment(), "Chats");
        viewPager.setAdapter(adapter);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.radio:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.chats:
                    viewPager.setCurrentItem(1);
                    return true;
            }
            return false;
        }
    };


    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    navigation.setSelectedItemId(R.id.radio);
                    break;
                case 1:
                    navigation.setSelectedItemId(R.id.chats);
                    break;
            }
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions() && hasCameraPermissions() && hasAudioPermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.RECORD_AUDIO
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasCameraPermissions(){
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasAudioPermissions(){
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
    }

    private void getAllCalls(){
        callModelList = new ArrayList<>();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final CallModel callModel = snapshot.getValue(CallModel.class);
                callModelList.add(callModel);
                callsKeys.add(snapshot.getKey());
                if (mAuth.getCurrentUser()!=null){
//                    if (admin!=null){
//                        if (mAuth.getCurrentUser().getUid().equals(admin.getUid())){
                            if (callModel.getReceiverUID().equals(mAuth.getCurrentUser().getUid())&&callModel.getStatus().equals("Ringing")){

                                Intent intent = new Intent(MainActivity.this, IncomingCallActivity.class);
                                intent.putExtra("callModel", callModel);
                                startActivity(intent);

//                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                                builder.setTitle("Incoming call");
//                                for (int i = 0; i<usersModelList.size(); i++){
//                                    if (usersModelList.get(i).getUid().equals(callModel.getCallerUID())){
//                                        builder.setMessage(usersModelList.get(i).getUserName()+" is calling");
//                                    }
//                                }
//                                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent intent = new Intent(MainActivity.this, VideoChatActivity.class);
//                                        if (callModel.getCallType().equals("Video")){
//                                            intent.putExtra("VideoEnabled", true);
//                                        }else {
//                                            intent.putExtra("VideoEnabled", false);
//                                        }
//                                        startActivity(intent);
//                                        dialog.dismiss();
//                                        }
//                                    });
//                                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        if (callModel!=null){
//                                            Map<String, Object> map = new HashMap<>();
//                                            map.put("status", "Disconnected");
//                                            if (callModel.getCallID()!=null){
//                                                callsRef.child(callModel.getCallID()).updateChildren(map);
//                                            }
//                                        }
//                                        dialog.dismiss();
//                                    }
//                                });
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);
//                                alertDialog.setCancelable(false);
//                                alertDialog.setCanceledOnTouchOutside(false);
//                                alertDialog.show();
                            }
//                        }
//                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CallModel callModel = snapshot.getValue(CallModel.class);
                int index = callsKeys.indexOf(snapshot.getKey());

                if (mAuth.getCurrentUser()!=null){
//                    if (admin!=null){
//                        if (mAuth.getCurrentUser().getUid().equals(admin.getUid())){
                            if (callModel.getReceiverUID().equals(mAuth.getCurrentUser().getUid())&&callModel.getStatus().equals("Ringing")){
                                Intent intent = new Intent(MainActivity.this, IncomingCallActivity.class);
                                intent.putExtra("callModel", callModel);
                                startActivity(intent);
//                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                                builder.setTitle("Incoming call");
//                                for (int i = 0; i<usersModelList.size(); i++){
//                                    if (usersModelList.get(i).getUid().equals(callModel.getCallerUID())){
//                                        builder.setMessage(usersModelList.get(i).getUserName()+" is calling");
//                                    }
//                                }
//                                builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                });
//                                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//
//                                    }
//                                });
//                                AlertDialog alertDialog = builder.create();
//                                alertDialog.setCancelable(false);
//                                alertDialog.setCanceledOnTouchOutside(false);
//                                alertDialog.show();
                            }
//                        }
//                    }

                }

                try{
                    callModelList.set(index, callModel);
                }catch (Exception e) {
                    getAllCalls();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                int index = callsKeys.indexOf(key);
                try{
                    callModelList.remove(index);
                }catch (Exception e){
                    getAllCalls();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        callsRef.addChildEventListener(childEventListener);
    }
}
