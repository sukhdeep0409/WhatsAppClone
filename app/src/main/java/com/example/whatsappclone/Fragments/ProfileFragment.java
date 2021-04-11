package com.example.whatsappclone.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.whatsappclone.Models.User;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.FragmentProfileBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater);

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                binding.name.setText(user.getName());
                binding.phone.setText(user.getPhoneNumber());

                String imageURL = user.getProfileImage();
                if (!imageURL.equals("No Image")) {
                    Glide.with(getContext()).load(imageURL).placeholder(R.drawable.avatar).into(binding.profilePicture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });

        binding.editName.setOnClickListener(view -> {
            final DialogPlus dialogPlus = DialogPlus.newDialog(getActivity())
                    .setContentHolder(new ViewHolder(R.layout.update_name))
                    .setExpanded(true, WindowManager.LayoutParams.WRAP_CONTENT)
                    .create();

            View layout =dialogPlus.getHolderView();

            final TextInputEditText editText = layout.findViewById(R.id.text);
            final AppCompatButton update = layout.findViewById(R.id.update);

            editText.setText(binding.name.getText());

            dialogPlus.show();

            update.setOnClickListener(v -> {
                String result = editText.getText().toString();

                reference.child(firebaseAuth.getCurrentUser().getUid()).child("name").setValue(result).addOnCompleteListener(task -> {
                    Toast.makeText(getActivity(), "Record Updated", Toast.LENGTH_SHORT).show();

                    dialogPlus.dismiss();
                });
            });
        });

        return binding.getRoot();
    }
}
