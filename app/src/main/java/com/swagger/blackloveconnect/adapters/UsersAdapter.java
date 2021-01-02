package com.swagger.blackloveconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.models.UsersModel;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    List<UsersModel> usersModelList;
    Context context;

    public UsersAdapter(List<UsersModel> usersModelList, Context context) {
        this.usersModelList = usersModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_item, null, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.tvUserName.setText(usersModelList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return usersModelList.size();
    }

    public class UsersViewHolder extends RecyclerView.ViewHolder{

        TextView tvUserName;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }
    }
}
