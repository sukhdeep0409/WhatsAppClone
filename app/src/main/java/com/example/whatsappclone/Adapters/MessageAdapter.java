package com.example.whatsappclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Models.Message;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ItemRecieveBinding;
import com.example.whatsappclone.databinding.ItemSendBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    List<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVED = 2;

    String senderRoom, receiverRoom;

    public MessageAdapter(Context context, List<Message> messages, String senderRoom, String receiverRoom) {
        this.context = context;
        this.messages = messages;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
        notifyDataSetChanged();
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

        int[] reactions = new int[] {
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry,
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if (holder.getClass() == sentViewHolder.class) {
                sentViewHolder viewHolder = (sentViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                receiverViewHolder viewHolder = (receiverViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .child("messages")
                    .child(message.getMessageId())
                    .setValue(message);

            return true;            //true is closing popup, false is requesting a new selection
        });

        if (holder.getClass() == sentViewHolder.class) {
            sentViewHolder viewHolder = (sentViewHolder) holder;

            if (message.getMessage().equals("photo")) {
                viewHolder.binding.sendImage.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageURL()).placeholder(R.drawable.placeholder).into(viewHolder.binding.sendImage);
            }

            viewHolder.binding.message.setText(message.getMessage());
            viewHolder.binding.timeOfMessage.setText(message.getTimeStamp());

            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener((view, motionEvent) -> {
                popup.onTouch(view, motionEvent);
                return false;
            });

            viewHolder.binding.sendImage.setOnTouchListener((view, motionEvent) -> {
                popup.onTouch(view, motionEvent);
                return false;
            });
        }
        else {
            receiverViewHolder viewHolder = (receiverViewHolder) holder;

            if (message.getMessage().equals("photo")) {
                viewHolder.binding.receiveImage.setVisibility(View.VISIBLE);
                viewHolder.binding.message.setVisibility(View.GONE);
                Glide.with(context).load(message.getImageURL()).placeholder(R.drawable.placeholder).into(viewHolder.binding.receiveImage);
            }

            viewHolder.binding.message.setText(message.getMessage());
            viewHolder.binding.timeOfMessage.setText(message.getTimeStamp());

            if (message.getFeeling() >= 0) {
                viewHolder.binding.feeling.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.binding.feeling.setVisibility(View.GONE);
            }

            viewHolder.binding.message.setOnTouchListener((view, motionEvent) -> {
                popup.onTouch(view, motionEvent);
                return false;
            });

            viewHolder.binding.receiveImage.setOnTouchListener((view, motionEvent) -> {
                popup.onTouch(view, motionEvent);
                return false;
            });
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
