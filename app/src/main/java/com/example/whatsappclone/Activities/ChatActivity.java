package com.example.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Adapters.MessageAdapter;
import com.example.whatsappclone.Fragments.HomeFragment;
import com.example.whatsappclone.Models.Message;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;

    MessageAdapter adapter;
    List<Message> messages;

    String senderRoom, receiverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;

    ProgressDialog dialog;

    String senderUid;
    String receiverUid;

    @Override
    @SuppressLint("SimpleDateFormat")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        String name = getIntent().getStringExtra("name");
        String profile = getIntent().getStringExtra("image");

        binding.name.setText(name);
        Glide.with(ChatActivity.this).load(profile).placeholder(R.drawable.avatar).into(binding.profileImage);

        binding.backArrow.setOnClickListener(view -> {
            startActivity(new Intent(ChatActivity.this, HomeActivity.class));
            finish();
        });

        receiverUid = getIntent().getStringExtra("uid");
        senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* @SuppressLint("UseCompatLoadingForDrawables")
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);*/

        database.getReference().child("presence").child(receiverUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.getValue(String.class);
                    if (! status.equals("offline")) {
                        binding.status.setText(status);
                        binding.status.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.status.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        senderRoom = senderUid + receiverUid;
        receiverRoom = receiverUid + senderUid;

        messages = new ArrayList<>();
        adapter = new MessageAdapter(ChatActivity.this, messages, senderRoom, receiverRoom);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sending Image ... ");
        dialog.setCancelable(false);

        database.getReference().child("chats")
                .child(senderRoom)
                .child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Message message = dataSnapshot.getValue(Message.class);
                            message.setMessageId(dataSnapshot.getKey());
                            messages.add(message);
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.sendButton.setOnClickListener(view -> {
            String msgTxt = binding.messageBox.getText().toString();

            binding.messageBox.setText("");
            Date date = new Date();
            Message messageObject = new Message(msgTxt, senderUid, date.getTime());
            messageObject.setMessage(msgTxt);

            String randomKey = database.getReference().push().getKey();

            HashMap<String, Object> lastMessage = getTime(messageObject.getMessage());

            /*SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.UK);
            Format format = new SimpleDateFormat("a");
            String markTime = format.format(date);

            lastMessage.put("lastMessage", messageObject.getMessage());
            lastMessage.put("lastMessageTime", dateFormat.format(date) + " " + markTime);

            Log.i("LAST_TIME_MESSAGE", dateFormat.format(date));*/

            database.getReference()
                    .child("chats")
                    .child(senderRoom)
                    .updateChildren(lastMessage);

            database.getReference()
                    .child("chats")
                    .child(receiverRoom)
                    .updateChildren(lastMessage);

            database.getReference().child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(randomKey)
                    .setValue(messageObject)
                    .addOnSuccessListener(aVoid -> {
                        Log.i("CHECK_ID", senderRoom);
                        database.getReference().child("chats")
                                .child(receiverRoom)
                                .child("messages")
                                .child(randomKey)
                                .setValue(messageObject)
                                .addOnSuccessListener(aVoid1 -> {

                                });
                    });
        });

        binding.attachment.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, 25);
        });

        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("presence").child(senderUid).setValue("typing ... ");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping, 1000);
            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderUid).setValue("online");
                }
            };
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 25) {
            if (data != null) {
                if (data.getData() != null)  {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats").child(calendar.getTimeInMillis() + "");
                    dialog.show();
                    reference.putFile(selectedImage).addOnCompleteListener(task -> {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                String filePath = uri.toString();
                                String msgTxt = binding.messageBox.getText().toString();

                                binding.messageBox.setText("");
                                Date date = new Date();
                                Message messageObject = new Message(msgTxt, senderUid, date.getTime());
                                messageObject.setImageURL(filePath);
                                messageObject.setMessage("photo");

                                String randomKey = database.getReference().push().getKey();

                                HashMap<String, Object> lastMessage = getTime(messageObject.getMessage());

                                /*HashMap<String, Object> lastMessage = new HashMap<>();
                                lastMessage.put("lastMessage", messageObject.getMessage());
                                lastMessage.put("lastMessageTime", date.getTime());

                                Log.i("LAST_TIME_MESSAGE", String.valueOf(date.getTime()));*/

                                database.getReference()
                                        .child("chats")
                                        .child(senderRoom)
                                        .updateChildren(lastMessage);

                                database.getReference()
                                        .child("chats")
                                        .child(receiverRoom)
                                        .updateChildren(lastMessage);

                                database.getReference().child("chats")
                                        .child(senderRoom)
                                        .child("messages")
                                        .child(randomKey)
                                        .setValue(messageObject)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.i("CHECK_ID", senderRoom);
                                            database.getReference().child("chats")
                                                    .child(receiverRoom)
                                                    .child("messages")
                                                    .child(randomKey)
                                                    .setValue(messageObject)
                                                    .addOnSuccessListener(aVoid1 -> {

                                                    });
                                        });

                                //Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }
            }
        }
    }

    private HashMap<String, Object> getTime(String message) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.UK);
        Format format = new SimpleDateFormat("a");
        String markTime = format.format(date);

        HashMap<String, Object> lastMessage = new HashMap<>();
        lastMessage.put("lastMessage", message);
        lastMessage.put("lastMessageTime", dateFormat.format(date) + " " + markTime);

        Log.i("LAST_TIME_MESSAGE", dateFormat.format(date));

        return lastMessage;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference().child("presence").child(uid).setValue("online");
    }

    @Override
    protected void onPause() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.getReference().child("presence").child(uid).setValue("offline");
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}