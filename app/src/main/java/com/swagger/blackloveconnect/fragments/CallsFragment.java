package com.swagger.blackloveconnect.fragments;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.opentok.VideoChatActivity;

import static com.swagger.blackloveconnect.utils.Constants.admin;

public class CallsFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageButton btnAudioCall, btnVideoCall;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersReference;
    boolean incomingCall;
    String incomingRoomID;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CallsFragment() { }

    public static CallsFragment newInstance(String param1, String param2) {
        CallsFragment fragment = new CallsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        if (getActivity().getIntent().hasExtra("IncomingCall")){
            incomingCall = true;
            incomingRoomID = getActivity().getIntent().getStringExtra("roomID");
        }

        View view = inflater.inflate(R.layout.fragment_calls, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersReference = firebaseDatabase.getReference("Users");
        btnAudioCall = view.findViewById(R.id.btnAudio);
        btnVideoCall = view.findViewById(R.id.btnVideo);
        btnAudioCall.setOnClickListener(this);
        btnVideoCall.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAudio:
                if (admin!=null) {
                    if (!mAuth.getCurrentUser().getUid().equals(admin.getUid())) {
                        Intent intent1 = new Intent(getActivity(), VideoChatActivity.class);
                        intent1.putExtra("VideoEnabled", false);
                        intent1.putExtra("toAdmin", true);
                        startActivity(intent1);
                    }
                }

                break;
            case R.id.btnVideo:
                if (admin!=null){
                    if (!mAuth.getCurrentUser().getUid().equals(admin.getUid())){
                        Intent intent = new Intent(getActivity(), VideoChatActivity.class);
                        intent.putExtra("VideoEnabled", true);
                        intent.putExtra("toAdmin", true);
                        startActivity(intent);
                    }

                }
                break;
        }
    }
}
