package com.swagger.blackloveconnect.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chootdev.recycleclick.RecycleClick;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.adapters.AdapterChat;
import com.swagger.blackloveconnect.adapters.AdapterInbox;
import com.swagger.blackloveconnect.main.ChatActivity;
import com.swagger.blackloveconnect.main.SearchUsersActivity;
import com.swagger.blackloveconnect.models.ChatModel;
import com.swagger.blackloveconnect.models.MessageModel;
import com.swagger.blackloveconnect.registration.LoginActivity;
import com.swagger.blackloveconnect.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FloatingActionButton fab;
    RecyclerView recyclerChats;
    FirebaseAuth mAuth;
    TextView tvLogOut;
    List<ChatModel> chatModelList;
    FirebaseDatabase firebaseDatabase;
    AdapterInbox adapterInbox;
    DatabaseReference chatsReference;
    List<String> inboxKeys;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatsFragment() {
    }
    public static ChatsFragment newInstance(String param1, String param2) {
        ChatsFragment fragment = new ChatsFragment();
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
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        chatsReference = firebaseDatabase.getReference("Message");
        fab = view.findViewById(R.id.float_new_chat);
        recyclerChats = view.findViewById(R.id.recycler_chats);
        tvLogOut = view.findViewById(R.id.logout);
        chatModelList = new ArrayList<>();
        inboxKeys = new ArrayList<>();
        recyclerChats.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerChats.addItemDecoration(new DividerItemDecoration(recyclerChats.getContext(), DividerItemDecoration.VERTICAL));

        tvLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchUsersActivity.class);
                startActivity(intent);
            }
        });
        populateInbox();
        return view;
    }

    private void populateInbox(){
        chatModelList.clear();
        adapterInbox = new AdapterInbox(chatModelList, getActivity());
        recyclerChats.setAdapter(adapterInbox);
        RecycleClick.addTo(recyclerChats).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                for (int a = 0; a< Constants.usersModelList.size(); a++){
                    if (Constants.usersModelList.get(a).getUid().equals(chatModelList.get(i).getUserName())){
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("user", Constants.usersModelList.get(a));
                        startActivity(intent);
                    }
                }
            }
        });
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String userName = snapshot.getKey();
                List<MessageModel> messages = new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageModel.setKey(dataSnapshot.getKey());
                    messages.add(messageModel);
                }
                ChatModel chatModel = new ChatModel(userName, messages);
                chatModelList.add(chatModel);
                inboxKeys.add(snapshot.getKey());
                adapterInbox.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String userName = snapshot.getKey();
                List<MessageModel> messages = new ArrayList<>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messageModel.setKey(dataSnapshot.getKey());
                    messages.add(messageModel);
                }
                ChatModel chatModel = new ChatModel(userName, messages);
                int index = inboxKeys.indexOf(snapshot.getKey());
                chatModelList.set(index, chatModel);
                adapterInbox.notifyDataSetChanged();
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
        };
        chatsReference.child(mAuth.getCurrentUser().getUid()).addChildEventListener(childEventListener);
    }

}
