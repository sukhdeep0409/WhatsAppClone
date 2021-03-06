package com.example.whatsappclone.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Activities.ChatActivity;
import com.example.whatsappclone.Models.Contact;
import com.example.whatsappclone.Models.User;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ContactCardBinding;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsAdapter extends FirebaseRecyclerAdapter<Contact, ContactsAdapter.MyViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    ArrayList<Contact> list;
    Context context;

    String imageURL;
    boolean flag;

    public ContactsAdapter(@NonNull FirebaseRecyclerOptions<Contact> options, ArrayList<Contact> list, Context context) {
        super(options);
        this.list = list;
        this.context = context;
        notifyDataSetChanged();
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Contact model) {

        String uid = getRef(position).getKey();

        flag = false;
        for(Contact ct : list) {
            if (ct.getPhoneNumber().replaceAll("\\s", "").equals(model.getPhoneNumber())) {
                flag = true;
                break;
            }
        }

        final String[] copyURL = new String[1];
        FirebaseDatabase.getInstance().getReference("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageURL = snapshot.getValue(User.class).getProfileImage();
                copyURL[0] = imageURL;
                if (flag) {
                    Glide.with(context).load(imageURL).placeholder(R.drawable.avatar).into(holder.binding.profilePic);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {   }
        });

        if (flag) {
            holder.binding.name.setText(model.getName());
            holder.binding.phoneNumber.setText(model.getPhoneNumber());
        }
        else {
            holder.itemView.getLayoutParams().height = 0;
            holder.itemView.getLayoutParams().width = 0;
        }

        //String finalImageURL = imageURL[0];
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("name", model.getName());
            intent.putExtra("image", copyURL[0]);
            intent.putExtra("uid", uid);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
        return new MyViewHolder(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ContactCardBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ContactCardBinding.bind(itemView);
        }
    }
}
