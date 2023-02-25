package com.example.meetpoint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Button logout;
    private FirebaseAuth db = FirebaseAuth.getInstance();
    private TextView textViewaddFriendID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.logOut);

        textViewaddFriendID = findViewById(R.id.addFriendID);
        logout.setOnClickListener(v -> {
            String receiverID;
            receiverID = String.valueOf(textViewaddFriendID.getText());
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this, Login.class));

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
                    if (user != null) {

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

                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(MainActivity.this, "ID does not exist!", Toast.LENGTH_LONG).show();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
    }

}


