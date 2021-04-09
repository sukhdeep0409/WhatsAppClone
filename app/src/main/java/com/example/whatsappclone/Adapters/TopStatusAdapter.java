 package com.example.whatsappclone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activities.HomeActivity;
import com.example.whatsappclone.Models.Status;
import com.example.whatsappclone.Models.UserStatus;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ItemStatusBinding;

import java.util.ArrayList;
import java.util.List;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStatusAdapter extends RecyclerView.Adapter<TopStatusAdapter.TopStatusViewHolder> {

    Context context;
    List<UserStatus> userStatuses;

    public TopStatusAdapter(Context context, List<UserStatus> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {
        UserStatus userStatus = userStatuses.get(position);

        Status lastStatus = userStatus.getStatuses().get(userStatus.getStatuses().size()-1);

        Glide.with(context).load(lastStatus.getImageURL()).into(holder.binding.imageStatus);

        holder.binding.circularStatusView.setPortionsCount(userStatus.getStatuses().size());
        holder.binding.nameStatus.setText(userStatus.getName());

        holder.binding.circularStatusView.setOnClickListener(view -> {
            ArrayList<MyStory> myStories = new ArrayList<>();

            for (Status status : userStatus.getStatuses()) {
                myStories.add(new MyStory(status.getImageURL()));
            }

            new StoryView.Builder(((HomeActivity)context).getSupportFragmentManager())
                    .setStoriesList(myStories) // Required
                    .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                    .setTitleText(userStatus.getName()) // Default is Hidden
                    .setSubtitleText("") // Default is Hidden
                    .setTitleLogoUrl(userStatus.getProfileImage()) // Default is Hidden
                    .setStoryClickListeners(new StoryClickListeners() {
                        @Override
                        public void onDescriptionClickListener(int position) {
                            //your action
                        }

                        @Override
                        public void onTitleIconClickListener(int position) {
                            //your action
                        }
                    }) // Optional Listeners
                    .build() // Must be called before calling show method
                    .show();
        });
        
    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public static class TopStatusViewHolder extends RecyclerView.ViewHolder {

        ItemStatusBinding binding;
        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemStatusBinding.bind(itemView);
        }
    }
}
