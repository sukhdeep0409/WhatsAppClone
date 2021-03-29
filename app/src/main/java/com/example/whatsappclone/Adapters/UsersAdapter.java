package com.example.whatsappclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activities.ChatActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.Models.User;
import com.example.whatsappclone.databinding.RowConversationBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    Context context;
    List<User> users;

    public UsersAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        holder.binding.username.setText(user.getName());
        Glide.with(context).load(user.getProfileImage())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.profilePic);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("uid", user.getUid());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public RowConversationBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
