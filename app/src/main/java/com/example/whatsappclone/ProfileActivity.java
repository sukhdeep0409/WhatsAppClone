package com.example.whatsappclone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;


import com.example.whatsappclone.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;

    Uri image;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setTitle("Profile Setup");

        dialog = new ProgressDialog(this);
        dialog.setMessage("Logging you in ... ");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.profilePic.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("images/*");
            startActivityForResult(intent, 45);
        });
        
        binding.nameGet.setOnClickListener(view -> {
            String name = binding.nameBox.getText().toString();
            dialog.show();

            if (name.isEmpty()) {
                binding.nameBox.setError("Please type your name");
                return;
            }

            if (image != null) {
                StorageReference reference = storage.getReference().child("Profile").child(auth.getCurrentUser().getUid());
                reference.putFile(image).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            String uid = auth.getCurrentUser().getUid();
                            String phone = auth.getCurrentUser().getPhoneNumber();
                            String nm = binding.nameBox.getText().toString();

                            User user = new User(uid, nm, phone, imageUrl);

                            database.getReference().child("Users").child(auth.getCurrentUser().getUid()).setValue(user).addOnSuccessListener(this, aVoid -> {
                                dialog.dismiss();
                                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                finish();
                            });
                        });
                    }
                });
            }
            else {
                String uid = auth.getCurrentUser().getUid();
                String phone = auth.getCurrentUser().getPhoneNumber();

                User user = new User(uid, name, phone, "No Image");

                database.getReference().child("Users").child(auth.getCurrentUser().getUid())
                        .setValue(user).addOnSuccessListener(this, aVoid -> {
                                dialog.dismiss();
                                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                finish();
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (data.getData() != null) {
                binding.profilePic.setImageURI(data.getData());
                image = data.getData();
            }
        }
    }
}