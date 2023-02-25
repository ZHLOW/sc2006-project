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

public class FriendProfileFragment extends Fragment {

    private FirebaseAuth authProfile;
    private String friendProfileID;
    FirebaseUser user;
    ImageView imageView;
    private TextView textViewUsername, textViewFullName, textViewEmail, textViewMobile, textViewUnqiueID, textViewLocation;

    Activity context = getActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle bundle = this.getArguments();
        friendProfileID = bundle.getString("friendUserID");
        textViewUnqiueID = view.findViewById(R.id.textView_show_unqiueID);
        textViewUsername = view.findViewById(R.id.textView_show_username);
        textViewFullName = view.findViewById(R.id.textView_show_full_name);
        textViewEmail = view.findViewById(R.id.textView_show_email);
        textViewMobile = view.findViewById(R.id.textView_show_mobileNumber);
        textViewLocation = view.findViewById(R.id.textView_show_location);
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

                    textViewUnqiueID.setText("ID: " + user.user_id);
                    textViewFullName.setText(user.fullName);
                    textViewUsername.setText(user.username);
                    textViewEmail.setText(user.email);
                    textViewMobile.setText("+65 " + user.mobileNumber);
                    if(snapshot.child("location").getValue().equals("null"))
                        textViewLocation.setText("No location shared");
                    else
                        textViewLocation.setText(user.location);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;
    }

}
