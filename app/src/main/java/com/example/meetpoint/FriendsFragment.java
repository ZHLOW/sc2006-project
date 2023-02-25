package com.example.meetpoint;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class FriendsFragment extends Fragment {

    private DatabaseReference FriendRef;
    private View FriendFragmentView;
    Button AddFriendButton;
    private RecyclerView myFriendsList;

    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FriendFragmentView = inflater.inflate(R.layout.fragment_friends, container, false);
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Users_Requests_And_Friends");
        myFriendsList = (RecyclerView) FriendFragmentView.findViewById(R.id.Friends_List);
        myFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        AddFriendButton = FriendFragmentView.findViewById(R.id.AddFriendBtn);

        return FriendFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(FriendRef.child(id).child("Friends"), User.class)
                .build();
        FirebaseRecyclerAdapter<User, FriendsViewHolder> adapter = new FirebaseRecyclerAdapter<User, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull User model) {

                final String list_user_id = getRef(position).getKey();

                FriendRef.child(id).child("Friends").child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            final String friendUserID = snapshot.child("user_id").getValue().toString();
                            final String friendUserName = snapshot.child("username").getValue().toString();
                            final String friendFullName = snapshot.child("fullName").getValue().toString();
                            final String friendMobileNumber = snapshot.child("mobileNumber").getValue().toString();
                            final String friendEmail = snapshot.child("email").getValue().toString();

                            holder.userid.setText("ID: " + friendUserID);
                            holder.username.setText("Username: " + friendUserName);
                            holder.fullname.setText("Full Name: " + friendFullName);
                            holder.mobileNumber.setText("Contact No.: " + friendMobileNumber);
                            holder.email.setText("Email: " + friendEmail);

                            holder.SendMsgBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                    chatIntent.putExtra("visit_user_username", friendUserName);
                                    chatIntent.putExtra("visit_user_ID", friendUserID);
                                    startActivity(chatIntent);
                                }
                            });
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                        bundle.putString("friendUserID",friendUserID);
                                        FriendProfileFragment friendProfileFragment = new FriendProfileFragment();
                                        friendProfileFragment.setArguments(bundle);
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, friendProfileFragment).addToBackStack(null).commit();

                                }
                            });
                        }

                        holder.RemoveFriendBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setMessage("Are you sure you want to remove this friend?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FriendRef.child(id).child("Friends").child(list_user_id)
                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FriendRef.child(list_user_id).child("Friends").child(id)
                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(getContext(), "Friend Removed!", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
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
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_friends_display_layout, parent, false);
                FriendsViewHolder holder = new FriendsViewHolder(view);
                return holder;
            }
        };

        myFriendsList.setAdapter(adapter);
        adapter.startListening();

        AddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendButton.setVisibility(View.GONE);
                Fragment addFriendFrag = new AddFriendsFragment();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.Friends_Container, addFriendFrag).commit();
                myFriendsList.setAdapter(adapter);
                adapter.stopListening();

            }
        });
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        TextView userid, username, fullname, email, mobileNumber;
        Button SendMsgBtn, RemoveFriendBtn;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userid = itemView.findViewById(R.id.user_Friend_ID);
            username = itemView.findViewById(R.id.user_Friend_Username);
            fullname = itemView.findViewById(R.id.user_Friend_Fullname);
            email = itemView.findViewById(R.id.user_Friend_Email);
            mobileNumber = itemView.findViewById(R.id.user_Friend_MobileNumber);
            SendMsgBtn = itemView.findViewById(R.id.Send_Msg_btn);
            RemoveFriendBtn = itemView.findViewById(R.id.Remove_Friend_Btn);

        }
    }
}