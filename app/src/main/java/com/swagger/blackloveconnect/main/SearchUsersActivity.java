package com.swagger.blackloveconnect.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.chootdev.recycleclick.RecycleClick;
import com.google.firebase.auth.FirebaseAuth;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.adapters.UsersAdapter;
import com.swagger.blackloveconnect.models.UsersModel;

import java.util.ArrayList;
import java.util.List;

import static com.swagger.blackloveconnect.utils.Constants.usersModelList;

public class SearchUsersActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton goBack;
    RecyclerView recyclerUsers;
    List<UsersModel> usersList;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);
        goBack = findViewById(R.id.goBack);
        mAuth = FirebaseAuth.getInstance();
        usersList = new ArrayList<>();
        goBack.setOnClickListener(this);
        recyclerUsers = findViewById(R.id.recycler_users);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsers.addItemDecoration(new DividerItemDecoration(recyclerUsers.getContext(), DividerItemDecoration.VERTICAL));
        for (int i= 0; i<usersModelList.size(); i++){
            if (!usersModelList.get(i).getUid().equals(mAuth.getCurrentUser().getUid())){
                usersList.add(usersModelList.get(i));
            }
        }
        UsersAdapter usersAdapter = new UsersAdapter(usersList, this);
        recyclerUsers.setAdapter(usersAdapter);
        RecycleClick.addTo(recyclerUsers).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(SearchUsersActivity.this, ChatActivity.class);
                intent.putExtra("user", usersList.get(i));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.goBack:
                finish();
                break;
        }
    }

}
