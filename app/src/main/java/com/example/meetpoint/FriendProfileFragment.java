package com.example.meetpoint;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FriendProfileFragment extends Fragment {

    private FirebaseAuth authProfile;
    private String friendProfileID;
    FirebaseUser user;
    ImageView imageView;
    private TextView textViewUsername, textViewFullName, textViewEmail, textViewMobile, textViewUniqueID, textViewLocation;

    Activity context = getActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friends_profile, container, false);
        Bundle bundle = this.getArguments();
        friendProfileID = bundle.getString("friendUserID");
        textViewUniqueID = view.findViewById(R.id.textView_show_uniqueID);
        textViewUsername = view.findViewById(R.id.textView_show_username);
        textViewFullName = view.findViewById(R.id.textView_show_full_name);
        textViewEmail = view.findViewById(R.id.textView_show_email);
        textViewMobile = view.findViewById(R.id.textView_show_mobileNumber);
        textViewLocation = view.findViewById(R.id.textView_show_location);
        imageView = view.findViewById(R.id.imageViewProfileDP);
        authProfile = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        String userID = firebaseUser.getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(friendProfileID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {

                    textViewUniqueID.setText("ID: " + user.user_id);
                    textViewFullName.setText(user.fullName);
                    textViewUsername.setText(user.username);
                    textViewEmail.setText(user.email);
                    textViewMobile.setText("+65 " + user.mobileNumber);
                    ArrayList<String> locationsSnapshot = (ArrayList<String>) snapshot.child("locations").getValue();
                    if (locationsSnapshot.size() == 1) {
                        textViewLocation.setText("No location shared");
                    } else {
                        for (int i = 1; i < locationsSnapshot.size(); i++) {
                            textViewLocation.append("Location " + i + ": " + locationsSnapshot.get(i) + "\n");
                        }
                    }
                    String profilePhotoUrl = user.getProfilePhotoUrl();
                    if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                        Glide.with(requireContext())
                                .load(profilePhotoUrl)
                                .placeholder(R.drawable.no_profile_pic)
                                .into(imageView);
                    } else {
                        imageView.setImageResource(R.drawable.no_profile_pic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }

}
