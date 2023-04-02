package com.example.meetpoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EditAddressesFragment extends Fragment {

    private EditText editTextLocation;
    private ListView listViewLocations;
    private Button buttonAddLocation;
    private DatabaseReference UsersRef;
    private String user_id;
    private ArrayList<String> locationsList;

    public static EditAddressesFragment newInstance(String user_id) {
        EditAddressesFragment fragment = new EditAddressesFragment();
        Bundle args = new Bundle();
        args.putString("user_id", user_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_addresses, container, false);

        editTextLocation = view.findViewById(R.id.editTextLocation);
        listViewLocations = view.findViewById(R.id.listViewLocations);
        buttonAddLocation = view.findViewById(R.id.buttonAddLocation);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        user_id = getArguments().getString("user_id");

        locationsList = new ArrayList<>();

        // Retrieve the user's locations from Realtime Database
        UsersRef.child(user_id).child("locations").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        locationsList.clear();
                        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                            String location = locationSnapshot.getValue(String.class);
                            locationsList.add(location);
                        }

                        // Create a new ArrayList and add all the elements starting from the second index
                        ArrayList<String> newList = new ArrayList<>(locationsList.subList(1, locationsList.size()));

                        // Update the ListView with the new list of locations
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, newList);
                        listViewLocations.setAdapter(adapter);
                    }
                }
            }
        });

        // Set a listener for the "Add Location" button
        buttonAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = editTextLocation.getText().toString().trim();
                if (location.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter a location", Toast.LENGTH_SHORT).show();
                } else {
                    // Add the new location to the list in Realtime Database
                    locationsList.add(location);
                    updateLocationsInRealtimeDatabase();
                }

                // Create a new ArrayList and add all the elements starting from the second index
                ArrayList<String> newList = new ArrayList<>(locationsList.subList(1, locationsList.size()));

                // Update the ListView to display the updated location
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, newList);
                listViewLocations.setAdapter(adapter);
            }
        });

        // Set a listener for when an item in the ListView is clicked
        listViewLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Display a toast showing the selected location
                Toast.makeText(getActivity(), "Selected Location: " + locationsList.get(position+1), Toast.LENGTH_SHORT).show();

                // Create a dialog to allow the user to edit or remove the selected location
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Location Options");

                // Set up the buttons
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Create a dialog to allow the user to edit the selected location
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Edit Location");

                        // Set up the input
                        final EditText input = new EditText(getActivity());
                        // Set the text of the EditText to the selected location
                        input.setText(locationsList.get(position+1));

                        builder.setView(input);

                        // Set up the buttons
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get the new location from the EditText
                                String newLocation = input.getText().toString().trim();

                                // Update the selected location in the list in Realtime Database
                                locationsList.set(position+1, newLocation);
                                updateLocationsInRealtimeDatabase();

                                // Update the ListView to display the updated location
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, locationsList);
                                listViewLocations.setAdapter(adapter);
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        // Show the dialog
                        builder.show();
                    }
                });

                builder.setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the selected location from the list in Realtime Database
                        locationsList.remove(position+1);
                        updateLocationsInRealtimeDatabase();

                        // Create a new ArrayList and add all the elements starting from the second index
                        ArrayList<String> newList = new ArrayList<>(locationsList.subList(1, locationsList.size()));

                        // Update the ListView to display the updated list of locations
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, newList);
                        listViewLocations.setAdapter(adapter);
                    }
                });

                // Show the dialog
                builder.show();
            }
        });

        return view;
    }

    private void updateLocationsInRealtimeDatabase() {
        // Update the list of locations in Realtime Database for the user
        UsersRef.child(user_id).child("locations").setValue(locationsList);
    }
}