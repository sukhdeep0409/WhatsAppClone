package com.example.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.whatsappclone.databinding.ActivityPhoneNumberBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setTitle("WhatsApp Clone App");

        binding.phoneBox.requestFocus();

        binding.nameGet.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
            intent.putExtra("phoneNumber", binding.phoneBox.getText().toString());
            finish();
            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.i("CHECK_THE_UID", String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid() ));
        if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
            startActivity(new Intent(PhoneNumberActivity.this, MainActivity.class));
            finish();
        }
    }
}