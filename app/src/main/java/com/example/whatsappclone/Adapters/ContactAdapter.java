package com.example.whatsappclone.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.Models.Contact;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.ContactCardBinding;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    Activity activity;
    ArrayList<Contact> arrayList;

    public ContactAdapter(Activity activity, ArrayList<Contact> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact model = arrayList.get(position);

        holder.binding.name.setText(model.getName());
        holder.binding.phoneNumber.setText(model.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ContactCardBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ContactCardBinding.bind(itemView);
        }
    }

}
