package com.example.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.whatsappclone.Fragments.HomeFragment;
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

    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("auto_login", Context.MODE_PRIVATE);

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setTitle("WhatsApp Clone App");

        binding.phoneBox.requestFocus();

        binding.nameGet.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
            intent.putExtra("phoneNumber", binding.phoneBox.getText().toString());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("key", 1);
            editor.apply();
            finish();
            startActivity(intent);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        int prefs = sharedPreferences.getInt("key", 0);
        if (prefs > 0) {
            startActivity(new Intent(PhoneNumberActivity.this, HomeActivity.class));
            finish();
        }
    }
}