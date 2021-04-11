package com.example.whatsappclone.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.whatsappclone.Activities.HomeActivity;
import com.example.whatsappclone.Adapters.ContactsAdapter;
import com.example.whatsappclone.Models.Contact;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.FragmentUsersBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UsersFragment extends Fragment {

    ArrayList<Contact> list = new ArrayList<>();
    ContactsAdapter contactsAdapter;

    FragmentUsersBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkPermission();
        }
        return binding.getRoot();
    }

    private void  checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.READ_CONTACTS}, 100);
        }
        else {
            getContact();
        }
    }

    private void getContact() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        //sort by ascending
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        //Initialize cursor
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, sort);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                //initialize phone cursor
                Cursor phoneCursor = getContext().getContentResolver().query(uriPhone, null,  selection, new String[]{id}, null);

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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        FirebaseRecyclerOptions<Contact> options =
                new FirebaseRecyclerOptions.Builder<Contact>()
                        .setQuery(reference, Contact.class)
                        .build();

        contactsAdapter = new ContactsAdapter(options, list, getContext());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(contactsAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContact();
        }
        else {
            //when permissions denied
            Toast.makeText(getActivity(), "You won't be able to see your contacts in this app", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                checkPermission();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        contactsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        contactsAdapter.stopListening();
    }
}
