package com.example.meetpoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import java.util.HashMap;
import java.util.Map;

public class AddFriendsFragment extends Fragment {

    private DatabaseReference UsersRef, RequestFriendRef;
    private View AddFriendFragmentView;
    Button AddFriendByIdButton, BackButton;
    private RecyclerView myAddFriendsList;
    private EditText idEditText;

    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AddFriendFragmentView = inflater.inflate(R.layout.fragment_add_friends, container, false);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RequestFriendRef = FirebaseDatabase.getInstance().getReference().child("Users_Requests_And_Friends");
        myAddFriendsList = (RecyclerView) AddFriendFragmentView.findViewById(R.id.Add_Friends_List);
        myAddFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));
        AddFriendByIdButton = AddFriendFragmentView.findViewById(R.id.AddFriendByIdButton);
        BackButton = AddFriendFragmentView.findViewById(R.id.BackButton);
        idEditText = AddFriendFragmentView.findViewById(R.id.add_Friend_EditText);

        AddFriendByIdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String receiverID;
                receiverID = String.valueOf(idEditText.getText());
                String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference requestsSentRef = database.getInstance().getReference("Users_Requests_And_Friends")
                        .child(id).child("Requests").child("Sent").child(receiverID);
                DatabaseReference requestsReceivedRef = database.getInstance().getReference("Users_Requests_And_Friends")
                        .child(receiverID).child("Requests").child("Received").child(id);

                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

                usersRef.child(receiverID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User user = snapshot.getValue(User.class);
                        if (user.user_id != null) {

                            usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);
                                    if (user != null) {

                                        Map<String, Object> requestsReceivedCreate = new HashMap<>();
                                        requestsReceivedCreate.put("fullName", user.fullName);
                                        requestsReceivedCreate.put("username", user.username);
                                        requestsReceivedCreate.put("user_id", user.user_id);
                                        requestsReceivedRef.updateChildren(requestsReceivedCreate);
                                        Toast.makeText(getContext(), "Friend request sent!", Toast.LENGTH_LONG).show();
                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                                }
                            });

                            Map<String, Object> requestsSentCreate = new HashMap<>();
                            requestsSentCreate.put("fullName", user.fullName);
                            requestsSentCreate.put("username", user.username);
                            requestsSentCreate.put("status", "pending...");
                            requestsSentCreate.put("user_id", user.user_id);
                            requestsSentRef.updateChildren(requestsSentCreate);
                        }
                        else
                            Toast.makeText(getContext(), "ID does not exist!", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });

        return AddFriendFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(UsersRef, User.class)
                .build();
        FirebaseRecyclerAdapter<User, AddFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<User, AddFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddFriendsViewHolder holder, int position, @NonNull User model) {

                final String list_user_id = getRef(position).getKey();

                RequestFriendRef.child(id).child("Friends").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.hasChild(list_user_id)){
                            RequestFriendRef.child(id).child("Requests").child("Sent").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.hasChild(list_user_id)){
                                        RequestFriendRef.child(id).child("Requests").child("Received").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(!snapshot.hasChild(list_user_id)){

                                                    UsersRef.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (!snapshot.child("user_id").getValue().toString().equals(id)) {

                                                                final String addfriendUserID = snapshot.child("user_id").getValue().toString();
                                                                final String addfriendUserName = snapshot.child("username").getValue().toString();
                                                                final String addfriendFullName = snapshot.child("fullName").getValue().toString();

                                                                holder.userid.setText("ID: " + addfriendUserID);
                                                                holder.username.setText("Username: " + addfriendUserName);
                                                                holder.fullname.setText("Full Name: " + addfriendFullName);


                                                            }
                                                            else holder.itemView.findViewById(R.id.add_friend_visibility).setVisibility(View.GONE);

                                                            holder.addFriendBtn.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {

                                                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                                    DatabaseReference requestsSentRef = database.getInstance().getReference("Users_Requests_And_Friends")
                                                                            .child(id).child("Requests").child("Sent").child(list_user_id);
                                                                    DatabaseReference requestsReceivedRef = database.getInstance().getReference("Users_Requests_And_Friends")
                                                                            .child(list_user_id).child("Requests").child("Received").child(id);

                                                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

                                                                    usersRef.child(list_user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                            User user = snapshot.getValue(User.class);
                                                                            if (user.user_id != null) {

                                                                                usersRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        User user = snapshot.getValue(User.class);
                                                                                        if (user != null) {

                                                                                            Map<String, Object> requestsReceivedCreate = new HashMap<>();
                                                                                            requestsReceivedCreate.put("fullName", user.fullName);
                                                                                            requestsReceivedCreate.put("username", user.username);
                                                                                            requestsReceivedCreate.put("user_id", user.user_id);
                                                                                            requestsReceivedRef.updateChildren(requestsReceivedCreate);
                                                                                            Toast.makeText(getContext(), "Friend request sent!", Toast.LENGTH_LONG).show();

                                                                                        }

                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                                                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                });

                                                                                Map<String, Object> requestsSentCreate = new HashMap<>();
                                                                                requestsSentCreate.put("fullName", user.fullName);
                                                                                requestsSentCreate.put("username", user.username);
                                                                                requestsSentCreate.put("status", "pending...");
                                                                                requestsSentCreate.put("user_id", user.user_id);
                                                                                requestsSentRef.updateChildren(requestsSentCreate);
                                                                            } else
                                                                                Toast.makeText(getContext(), "ID does not exist!", Toast.LENGTH_LONG).show();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                                        }
                                                                    });
                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }else holder.itemView.findViewById(R.id.add_friend_visibility).setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }else holder.itemView.findViewById(R.id.add_friend_visibility).setVisibility(View.GONE);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else holder.itemView.findViewById(R.id.add_friend_visibility).setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                }

            @NonNull
            @Override
            public AddFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_add_friends_display_layout, parent, false);
                AddFriendsViewHolder holder = new AddFriendsViewHolder(view);
                return holder;
            }
        };

        myAddFriendsList.setAdapter(adapter);
        adapter.startListening();

        BackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackButton.setVisibility(View.GONE);
                idEditText.setVisibility(View.GONE);
                AddFriendByIdButton.setVisibility(View.GONE);
                Fragment friendsFragment = new FriendsFragment();
                FragmentTransaction fm = getActivity().getSupportFragmentManager().beginTransaction();
                fm.replace(R.id.Add_Friends_Container,friendsFragment).commit();
                myAddFriendsList.setAdapter(adapter);
                adapter.stopListening();
            }
        });
    }

    public static class AddFriendsViewHolder extends RecyclerView.ViewHolder {

        TextView userid, username, fullname;
        Button addFriendBtn;
        LinearLayout visibility;
        public AddFriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userid = itemView.findViewById(R.id.user_Friend_ID);
            username = itemView.findViewById(R.id.user_Friend_Username);
            fullname = itemView.findViewById(R.id.user_Friend_Fullname);
            addFriendBtn = itemView.findViewById(R.id.Add_Friend_Btn);
            visibility = itemView.findViewById(R.id.add_friend_visibility);
        }
    }
}