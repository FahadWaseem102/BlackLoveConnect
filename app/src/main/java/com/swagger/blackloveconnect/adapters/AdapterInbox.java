package com.swagger.blackloveconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.models.ChatModel;
import com.swagger.blackloveconnect.utils.Constants;

import java.util.List;

public class AdapterInbox extends RecyclerView.Adapter<AdapterInbox.InboxViewHolder> {

    List<ChatModel> chatModelList;
    Context context;

    public AdapterInbox(List<ChatModel> chatModelList, Context context) {
        this.chatModelList = chatModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, null, false);
        return new InboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int position) {
        String lastMessage = chatModelList.get(position).getMessages().get(chatModelList.get(position).getMessages().size()-1).getMessage();
        holder.lastMessage.setText(lastMessage);

        for (int i = 0; i< Constants.usersModelList.size(); i++){
            if (Constants.usersModelList.get(i).getUid().equals(chatModelList.get(position).getUserName())){
                holder.tvUserName.setText(Constants.usersModelList.get(i).getUserName());
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    public class InboxViewHolder extends RecyclerView.ViewHolder{

        TextView tvUserName, lastMessage;

        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
        }
    }
}
