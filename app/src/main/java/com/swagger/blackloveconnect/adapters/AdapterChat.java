package com.swagger.blackloveconnect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.swagger.blackloveconnect.R;
import com.swagger.blackloveconnect.models.MessageModel;
import com.swagger.blackloveconnect.utils.HelperClass;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.ChatViewHolder> {

    List<MessageModel> messageModelList;
    Context context;
    FirebaseAuth mAuth;

    public AdapterChat(List<MessageModel> messageModelList, Context context) {
        this.messageModelList = messageModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.messages_item, null, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        if (messageModelList.get(position).getFrom().equals(mAuth.getCurrentUser().getUid())){
            holder.linearOther.setVisibility(View.GONE);
            holder.linearCurrent.setVisibility(View.VISIBLE);
            holder.tvCurrentUserMsg.setText(messageModelList.get(position).getMessage());
            holder.currentDate.setText(HelperClass.getDate(messageModelList.get(position).getTimeStamp(), "MMM dd, yyyy hh:mm aa"));
        }else {
            holder.linearOther.setVisibility(View.VISIBLE);
            holder.linearCurrent.setVisibility(View.GONE);
            holder.tvOtherUserMsg.setText(messageModelList.get(position).getMessage());
            holder.otherDate.setText(HelperClass.getDate(messageModelList.get(position).getTimeStamp(), "MMM dd, yyyy hh:mm aa"));
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView tvOtherUserMsg, tvCurrentUserMsg, otherDate, currentDate;
        LinearLayout linearOther, linearCurrent;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOtherUserMsg = itemView.findViewById(R.id.otherUserMsg);
            tvCurrentUserMsg = itemView.findViewById(R.id.currentUserMsg);
            linearCurrent = itemView.findViewById(R.id.linearCurrent);
            linearOther = itemView.findViewById(R.id.linearOther);
            otherDate = itemView.findViewById(R.id.otherUserDate);
            currentDate = itemView.findViewById(R.id.currentUserDate);
        }
    }
}
