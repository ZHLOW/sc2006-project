package com.example.meetpoint;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FriendRequestsReceivedFragment extends Fragment {

    private DatabaseReference FriendRequestRef, FriendRef, UserRef;
    private View FriendRequestsFragmentView;
    Button SwtichRequestViewBtn;
    private RecyclerView myFriendRequestsList;

    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FriendRequestsFragmentView = inflater.inflate(R.layout.fragment_friend_requests_received, container, false);
        FriendRequestRef = FirebaseDatabase.getInstance().getReference().child("Users_Requests_And_Friends");
        FriendRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends");
        UserRef = FirebaseDatabase.getInstance().getReference("Users");
        myFriendRequestsList = (RecyclerView) FriendRequestsFragmentView.findViewById(R.id.Friend_Requests_Received_List);
        myFriendRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));
        SwtichRequestViewBtn = FriendRequestsFragmentView.findViewById(R.id.SwtichRequestSentViewBtn);

        return FriendRequestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FriendRequestRef.child(id).child("Requests").child("Received"), User.class)
                .build();
        FirebaseRecyclerAdapter<User, FriendRequestsViewHolder> adapter = new FirebaseRecyclerAdapter<User, FriendRequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendRequestsViewHolder holder, int position, @NonNull User model) {
                holder.itemView.findViewById(R.id.friend_request_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.friend_request_cancel_btn).setVisibility(View.VISIBLE);

                final String list_user_id = getRef(position).getKey();

                FriendRequestRef.child(id).child("Requests").child("Received").child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            final String requestUserID = snapshot.child("user_id").getValue().toString();
                            final String requestUserName = snapshot.child("username").getValue().toString();
                            final String requestFullName = snapshot.child("fullName").getValue().toString();

                            holder.userid.setText("ID: " + requestUserID);
                            holder.username.setText("Username: " + requestUserName);
                            holder.fullname.setText("Full Name: " + requestFullName);
                        }

                        holder.AcceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DatabaseReference currentUserRef = FriendRef.child(id).child("Friends").child(list_user_id);
                                DatabaseReference otherUserRef = FriendRef.child(list_user_id).child("Friends").child(id);

                                UserRef.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        User user = snapshot.getValue(User.class);
                                        if (user != null) {

                                            UserRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    User user = snapshot.getValue(User.class);
                                                    if (user != null) {

                                                        Map<String, Object> friendCreate = new HashMap<>();
                                                        friendCreate.put("fullName", user.fullName);
                                                        friendCreate.put("username", user.username);
                                                        friendCreate.put("user_id", user.user_id);
                                                        friendCreate.put("email", user.email);
                                                        friendCreate.put("mobileNumber", user.mobileNumber);
                                                        friendCreate.put("location", user.location);
                                                        otherUserRef.updateChildren(friendCreate);

                                                        FriendRequestRef.child(id).child("Requests").child("Received").child(list_user_id)
                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            FriendRequestRef.child(list_user_id).child("Requests").child("Sent").child(id)
                                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                Toast.makeText(getContext(), "Request Accepted! New Friend Added!", Toast.LENGTH_LONG).show();

                                                                                            }
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                });

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                                                }
                                            });

                                            Map<String, Object> friendCreate = new HashMap<>();
                                            friendCreate.put("fullName", user.fullName);
                                            friendCreate.put("username", user.username);
                                            friendCreate.put("user_id", user.user_id);
                                            friendCreate.put("email", user.email);
                                            friendCreate.put("mobileNumber", user.mobileNumber);
                                            friendCreate.put("location", user.location);
                                            currentUserRef.updateChildren(friendCreate);
                                        } else
                                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });

                            }
                        });

                        holder.CancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                FriendRequestRef.child(id).child("Requests").child("Received").child(list_user_id)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FriendRequestRef.child(list_user_id).child("Requests").child("Sent").child(id)
                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Toast.makeText(getContext(), "Request Cancelled!", Toast.LENGTH_LONG).show();
                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });
                            }
                        });

                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_request_display_layout, parent, false);
                FriendRequestsViewHolder holder = new FriendRequestsViewHolder(view);
                return holder;
            }
        };

        myFriendRequestsList.setAdapter(adapter);
        adapter.startListening();

        SwtichRequestViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SwtichRequestViewBtn.setVisibility(View.GONE);
                Fragment sentRequestsFrag = new FriendRequestsSentFragment();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.Friend_Requests_Received_Container,sentRequestsFrag).commit();
                myFriendRequestsList.setAdapter(adapter);
                adapter.stopListening();
            }
        });
    }

    public static class FriendRequestsViewHolder extends RecyclerView.ViewHolder {

        TextView userid, username, fullname;
        Button AcceptButton, CancelButton;

        public FriendRequestsViewHolder(@NonNull View itemView) {
            super(itemView);

            userid = itemView.findViewById(R.id.user_Request_ID);
            username = itemView.findViewById(R.id.user_Request_Username);
            fullname = itemView.findViewById(R.id.user_Request_Fullname);
            AcceptButton = itemView.findViewById(R.id.friend_request_accept_btn);
            CancelButton = itemView.findViewById(R.id.friend_request_cancel_btn);
        }
    }
}