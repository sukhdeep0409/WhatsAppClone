package com.example.whatsappclone.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.whatsappclone.Adapters.MessageAdapter;
import com.example.whatsappclone.Models.Message;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;

    MessageAdapter adapter;
    List<Message> messages;

    String senderRoom, receiverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages = new ArrayList<>();
        adapter = new MessageAdapter(ChatActivity.this, messages);

        String name = getIntent().getStringExtra("name");
        String uid = getIntent().getStringExtra("uid");

        Toolbar toolbar = binding.toolbar;
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        @SuppressLint("UseCompatLoadingForDrawables")
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}