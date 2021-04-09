package com.example.whatsappclone.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.whatsappclone.Adapters.TopStatusAdapter;
import com.example.whatsappclone.Adapters.UsersAdapter;
import com.example.whatsappclone.Models.Status;
import com.example.whatsappclone.Models.User;
import com.example.whatsappclone.Models.UserStatus;
import com.example.whatsappclone.R;
import com.example.whatsappclone.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;

    List<User> users;
    UsersAdapter adapter;
    TopStatusAdapter statusAdapter;
    List<UserStatus> userStatuses;

    String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {

        binding = FragmentHomeBinding.inflate(inflater);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        binding.statusList.setLayoutManager(manager);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        adapter = new UsersAdapter(getActivity(), users);
        statusAdapter = new TopStatusAdapter(getActivity(), userStatuses);

        binding.recyclerView.setAdapter(adapter);
        binding.statusList.setAdapter(statusAdapter);

        database.getReference().child("Users")
                .child(firebaseAuth.getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);

                        name = user.getName();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        getUserAndMessage();

        getStoriesUpdate();

        return binding.getRoot();
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void getUserAndMessage() {
        binding.recyclerView.showShimmerAdapter();
        database.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        users.add(user);
                    }
                }

                binding.recyclerView.hideShimmerAdapter();
                Log.i("OKAY_GOT_IT", "YES");
                adapter = new UsersAdapter(getActivity(), users);
                binding.recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getStoriesUpdate() {
        binding.statusList.showShimmerAdapter();
        database.getReference().child("Stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userStatuses.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        UserStatus userStatus = new UserStatus();
                        userStatus.setName(dataSnapshot.child("name").getValue(String.class));
                        userStatus.setProfileImage(dataSnapshot.child("profileImage").getValue(String.class));
                        userStatus.setLastUpdated(dataSnapshot.child("lastUpdated").getValue(Long.class));

                        ArrayList<Status> statuses = new ArrayList<>();

                        for (DataSnapshot statusSnapshot : dataSnapshot.child("statuses").getChildren()) {
                            Status sampleStatus = statusSnapshot.getValue(Status.class);
                            statuses.add(sampleStatus);
                        }

                        userStatus.setStatuses(statuses);
                        userStatuses.add(userStatus);
                    }
                    statusAdapter.notifyDataSetChanged();
                }
                binding.statusList.hideShimmerAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        });
    }

    @Override
    public void onStart() {
        getUserAndMessage();
        getStoriesUpdate();
        super.onStart();
    }
}
