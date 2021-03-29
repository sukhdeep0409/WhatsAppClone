package com.example.whatsappclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Models.Message;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ItemRecieveBinding;
import com.example.whatsappclone.databinding.ItemSendBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    List<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVED = 2;

    public MessageAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new sentViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve, parent, false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())) {
            return ITEM_SENT;
        }
        else {
            return ITEM_RECEIVED;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder.getClass() == sentViewHolder.class) {
            sentViewHolder viewHolder = (sentViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());
        }
        else {
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class sentViewHolder extends RecyclerView.ViewHolder {

        ItemSendBinding binding;

        public sentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSendBinding.bind(itemView);
        }
    }

    static class receiverViewHolder extends RecyclerView.ViewHolder {

        ItemRecieveBinding binding;

        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecieveBinding.bind(itemView);
        }
    }
}
