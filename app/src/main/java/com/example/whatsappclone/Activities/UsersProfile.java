 package com.example.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Models.User;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityUsersProfileBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

 public class UsersProfile extends AppCompatActivity {

     ActivityUsersProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String uid = getIntent().getStringExtra("uid");

        FirebaseDatabase.getInstance().getReference("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                binding.nameOfUser.setText(user.getName());
                binding.phoneNumberOfUser.setText(user.getPhoneNumber());
                Glide.with(getApplicationContext()).load(user.getProfileImage()).placeholder(R.drawable.avatar).into(binding.profilePictureOfUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        FirebaseDatabase.getInstance().getReference("presence").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String status = snapshot.getValue(String.class);
                if(status.equals("offline")) {
                    binding.statusOfUser.setVisibility(View.GONE);
                }
                else {
                    binding.statusOfUser.setText(status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }
        });
    }
}