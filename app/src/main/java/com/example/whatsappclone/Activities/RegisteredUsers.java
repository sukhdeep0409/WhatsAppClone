package com.example.whatsappclone.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.whatsappclone.Adapters.ContactAdapter;
import com.example.whatsappclone.Models.Contact;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ActivityRegisteredUsersBinding;

import java.util.ArrayList;
import java.util.List;

public class RegisteredUsers extends AppCompatActivity {

    ActivityRegisteredUsersBinding binding;

    ArrayList<Contact> list = new ArrayList<>();
    ContactAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisteredUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(RegisteredUsers.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(RegisteredUsers.this, new String[] {Manifest.permission.READ_CONTACTS}, 100);
        }
        else {
            getContact();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getContact() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        //sort by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        //Initialize cursor
        Cursor cursor = getContentResolver().query(uri, null, null,null,  sort);

        //check condition
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                //initialize phone cursor
                Cursor phoneCursor = getContentResolver().query(uriPhone, null,  selection, new String[]{id}, null);

                //check condition
                if (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //initialize model
                    Contact model = new Contact();
                    model.setName(name);
                    model.setPhoneNumber(number);

                    list.add(model);

                    phoneCursor.close();
                }
            }
            cursor.close();
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ContactAdapter(this, list);
        binding.recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContact();
        }
        else {
            //when permissions denied
            Toast.makeText(this, "You won't be able to see your contacts in this app", Toast.LENGTH_LONG).show();
            checkPermission();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisteredUsers.this, MainActivity.class));
    }
}