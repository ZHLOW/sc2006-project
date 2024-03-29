package com.example.meetpoint;

import android.widget.Button;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;
import android.net.Uri;
import android.provider.MediaStore;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private FirebaseAuth authProfile;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    FirebaseUser user;
    ImageView imageView;
    private TextView textViewUsername, textViewFullName, textViewEmail,  textViewMobile , textViewUniqueID, textViewLocation;
    private Button EditAddressesBtn;

    Activity context = getActivity();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        textViewUniqueID = view.findViewById(R.id.textView_show_uniqueID);
        textViewUsername = view.findViewById(R.id.textView_show_username);
        textViewFullName = view.findViewById(R.id.textView_show_full_name);
        textViewEmail = view.findViewById(R.id.textView_show_email);
        textViewMobile = view.findViewById(R.id.textView_show_mobileNumber);
        textViewLocation = view.findViewById(R.id.textView_show_location);
        authProfile = FirebaseAuth.getInstance();
        imageView = view.findViewById(R.id.imageViewProfileDP);
        EditAddressesBtn = view.findViewById(R.id.Edit_Addresses_Btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermissionAndOpenImagePicker();
            }
        });

        EditAddressesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with the EditAddressesFragment.
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Create a new instance of EditAddressesFragment
                EditAddressesFragment editAddressesFragment = new EditAddressesFragment();

                // Add the user_id as an argument to the fragment
                Bundle args = new Bundle();
                args.putString("user_id", user.getUid());
                editAddressesFragment.setArguments(args);

                // Replace the current fragment with the new instance of EditAddressesFragment
                fragmentTransaction.replace(R.id.fragment_container, editAddressesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        String userID = firebaseUser.getUid();
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    textViewUniqueID.setText("ID: " + user.user_id);
                    textViewFullName.setText(user.fullName);
                    textViewUsername.setText("Welcome " + user.username + "!");
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
    private void requestStoragePermissionAndOpenImagePicker() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
        } else {
            openImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            imageView.setImageURI(imageUri);
            uploadImageToFirebaseStorage(imageUri);
        }
    }
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_pictures").child(user.getUid());

        storageReference.putFile(imageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    updateUserProfilePhoto(downloadUri);
                } else {
                    Toast.makeText(requireContext(), "Upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUserProfilePhoto(Uri photoUri) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        userRef.child("profilePhotoUrl").setValue(photoUri.toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireContext(), "Profile photo updated successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Failed to update profile photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
