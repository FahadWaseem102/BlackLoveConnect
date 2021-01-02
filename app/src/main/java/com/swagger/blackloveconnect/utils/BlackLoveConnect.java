package com.swagger.blackloveconnect.utils;

import android.app.Application;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swagger.blackloveconnect.models.CallModel;
import com.swagger.blackloveconnect.models.UsersModel;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import static com.swagger.blackloveconnect.utils.Constants.admin;
import static com.swagger.blackloveconnect.utils.Constants.callModelList;
import static com.swagger.blackloveconnect.utils.Constants.currentUser;
import static com.swagger.blackloveconnect.utils.Constants.usersModelList;

public class BlackLoveConnect extends Application {

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, callsRef;
    List<String> usersKey;
//    List<String> callsKeys;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        callsRef = firebaseDatabase.getReference("Calls");
        usersModelList = new ArrayList<>();
        usersKey = new ArrayList<>();
//        callsKeys = new ArrayList<>();
        getUsers();
    }

    private void getUsers(){
        usersModelList.clear();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final UsersModel usersModel = snapshot.getValue(UsersModel.class);
                if (usersModel.getUid()!=null){
                    usersModelList.add(usersModel);
                    String key = snapshot.getKey();
                    usersKey.add(key);
                    mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                        @Override
                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                            if (firebaseAuth.getCurrentUser()!=null){
                                if (usersModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                                    currentUser = usersModel;
                                }
                            }
                        }
                    });

                    if (usersModel.isAdmin()){
                        admin = usersModel;
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final UsersModel usersModel = snapshot.getValue(UsersModel.class);
                String key = snapshot.getKey();
                int index = usersKey.indexOf(key);
                mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser()!=null){
                            currentUser= usersModel;
                        }
                    }
                });
                if (usersModel.isAdmin()){
                    admin = usersModel;
                }
                usersModelList.set(index, usersModel);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                int index = usersKey.indexOf(key);
                usersModelList.remove(index);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.addChildEventListener(childEventListener);
    }

}
