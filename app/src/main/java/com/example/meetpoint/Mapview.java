package com.example.meetpoint;

import static androidx.fragment.app.FragmentManager.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.Point;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.io.InputStream;


import org.xmlpull.v1.XmlPullParserException;

public class Mapview extends AppCompatActivity implements OnMapReadyCallback {

    //widgets
    private ImageView mGps;
    private AutocompleteSupportFragment autocompleteFragment1, autocompleteFragment2, autocompleteFragment3, autocompleteFragment4, autocompleteFragment5;
    private ImageView addAutocompleteButton, removeAutocompleteButton, addFriendLocationButton, addFriendLocationButton2, addFriendLocationButton3, addFriendLocationButton4, addFriendLocationButton5;

    //vars
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15f;
    private Marker mMarker;
    private LatLngBounds SG = new LatLngBounds(new LatLng(1.1304753,103.6920359),new LatLng(1.4504753,104.0120359));
    private KmlLayer layer1;
    private KmlLayer layer2;
    private boolean isLayer1Visible = true;
    private int COUNT = 5;
    LatLng[] locations = new LatLng[5];

    private float radius;
    private Circle mapCircle;
    private CircleOptions circleOptions;
    private LatLng resultLatLng = new LatLng(0,0);
    private Context con = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), "AIzaSyBcPAPb1wZzbRHda01bSt_ft_LpLjfJhiE");
        PlacesClient placesClient = Places.createClient(this);
        setContentView(R.layout.activity_mapview);

        autocompleteFragment1 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment1);
        //autocompleteFragment1.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment1.setLocationRestriction(RectangularBounds.newInstance(new LatLng(1.1304753,103.6920359),new LatLng(1.4504753,104.0120359)));
        autocompleteFragment1.setCountries("SG");
        autocompleteFragment1.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));


        autocompleteFragment2 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
        //autocompleteFragment2.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment2.setLocationRestriction(RectangularBounds.newInstance(new LatLng(1.1304753,103.6920359),new LatLng(1.4504753,104.0120359)));
        autocompleteFragment2.setCountries("SG");
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment2.setHint("Search 2nd Location");

        autocompleteFragment3 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment3);
        //autocompleteFragment3.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment3.setLocationRestriction(RectangularBounds.newInstance(new LatLng(1.1304753,103.6920359),new LatLng(1.4504753,104.0120359)));
        autocompleteFragment3.setCountries("SG");
        autocompleteFragment3.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment3.setHint("Search 3rd Location");

        autocompleteFragment4 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment4);
        //autocompleteFragment4.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment4.setLocationRestriction(RectangularBounds.newInstance(new LatLng(1.1304753,103.6920359),new LatLng(1.4504753,104.0120359)));
        autocompleteFragment4.setCountries("SG");
        autocompleteFragment4.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment4.setHint("Search 4th Location");

        autocompleteFragment5 = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment5);
        //autocompleteFragment5.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment5.setLocationRestriction(RectangularBounds.newInstance(new LatLng(1.1304753,103.6920359),new LatLng(1.4504753,104.0120359)));
        autocompleteFragment5.setCountries("SG");
        autocompleteFragment5.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment5.setHint("Search 5th Location");

        mGps = (ImageView) findViewById(R.id.ic_gps);
        addAutocompleteButton = findViewById(R.id.add_autocomplete_button);
        removeAutocompleteButton = findViewById(R.id.remove_autocomplete_button);
        addFriendLocationButton = findViewById(R.id.add_friend_location_button);
        addFriendLocationButton2 = findViewById(R.id.add_friend_location_button2);
        addFriendLocationButton3 = findViewById(R.id.add_friend_location_button3);
        addFriendLocationButton4 = findViewById(R.id.add_friend_location_button4);
        addFriendLocationButton5 = findViewById(R.id.add_friend_location_button5);

        hideBar();
        hideBar();
        hideBar();
        hideBar();

        getLocationPermission();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        loadKmlLayers(isLayer1Visible);

        mMap.setLatLngBoundsForCameraTarget(SG);

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init2();
            init();
        }
    }


    private void init(){

        ViewGroup searchView1 = (ViewGroup) autocompleteFragment1.getView();
        if (searchView1 != null) {
            searchView1.setBackgroundColor(Color.WHITE);
        }
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {}

            @SuppressLint("RestrictedApi")
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locations[0]=place.getLatLng();
                resultLatLng = place.getLatLng(); //update result in case user doesnt want more places
                autocompleteFragment1.setHint(place.getName());
                geoLocate(place.getLatLng());

            }
        });

        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {}

            @SuppressLint("RestrictedApi")
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locations[1]=place.getLatLng();
                autocompleteFragment2.setHint(place.getName());
                geoLocate(place.getLatLng());

            }
        });




        autocompleteFragment3.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {}

            @SuppressLint("RestrictedApi")
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locations[2]=place.getLatLng();
                autocompleteFragment3.setHint(place.getName());
                geoLocate(place.getLatLng());

            }
        });



        autocompleteFragment4.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {}

            @SuppressLint("RestrictedApi")
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locations[3]=place.getLatLng();
                autocompleteFragment4.setHint(place.getName());
                geoLocate(place.getLatLng());

            }
        });



        autocompleteFragment5.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {}

            @SuppressLint("RestrictedApi")
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                locations[4]=place.getLatLng();
                autocompleteFragment5.setHint(place.getName());
                geoLocate(place.getLatLng());

            }
        });


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultLatLng = findMP(locations);
                geoLocate(resultLatLng);

            }
        });

        addAutocompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBar();

            }
        });

        removeAutocompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideBar();
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // NOT WORKING AS INTENDED: CAN CHOOSE FRIEND BUT UNABLE TO EXTRACT FRIEND'S LOCATIONS, SAYS NULL
        addFriendLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a dialog where the user can choose a friend from the list
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // User is not logged in
                    return;
                }
                String currentUserId = currentUser.getUid();
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends").child(currentUserId).child("Friends");

                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> friends = new ArrayList<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendName = friendSnapshot.child("fullName").getValue(String.class);
                            friends.add(friendName);
                        }

                        // Show a dialog where the user can choose a friend from the list
                        AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                        builder.setTitle("Select a friend");

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, friends);

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Retrieve the selected friend's user_id from Firebase
                                String selectedFriendName = adapter.getItem(which);
                                Query selectedFriendQuery = friendsRef.orderByChild("fullName").equalTo(selectedFriendName).limitToFirst(1);
                                selectedFriendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            // Selected friend not found in the database
                                            return;
                                        }
                                        String selectedFriendId = snapshot.getChildren().iterator().next().getKey();
                                        // Retrieve the list of addresses associated with the user_id from Firebase
                                        DatabaseReference addressesRef = FirebaseDatabase.getInstance().getReference("Users/" + selectedFriendId + "/locations");
                                        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                List<String> addresses = new ArrayList<>();
                                                int counter = 0;
                                                for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                                                    if (counter >= 1) {
                                                        String address = addressSnapshot.getValue(String.class);
                                                        addresses.add(address);
                                                    }
                                                    counter++;
                                                }

                                                if (addresses.size() == 1) {
                                                    // No locations available for the selected friend
                                                    Toast toast = Toast.makeText(getApplicationContext(), "No locations available", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    return;
                                                }

                                                // Show a dialog where the user can choose an address from the list
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                                                builder.setTitle("Select an address");

                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, addresses);
                                                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        autocompleteFragment1.setText(adapter.getItem(which));
                                                        Geocoder geocoder = new Geocoder(Mapview.this);
                                                        List<Address> addressList = null;
                                                        try{
                                                            addressList = geocoder.getFromLocationName(adapter.getItem(which),1);
                                                            Address userAddress = addressList.get(0);
                                                            LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                                            locations[0]=latLng;
                                                            geoLocate(latLng);
                                                        } catch (Exception e) {
                                                            Toast.makeText(Mapview.this, "Address doesn't exist", Toast.LENGTH_SHORT).show();;
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                //Log.d(TAG, "Error retrieving data from Firebase");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        //Log.d(TAG, "Error retrieving data from Firebase");
                                    }
                                });
                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Log.d(TAG, "Error retrieving data from Firebase");
                    }
                });
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        addFriendLocationButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a dialog where the user can choose a friend from the list
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // User is not logged in
                    return;
                }
                String currentUserId = currentUser.getUid();
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends").child(currentUserId).child("Friends");

                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> friends = new ArrayList<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendName = friendSnapshot.child("fullName").getValue(String.class);
                            friends.add(friendName);
                        }

                        // Show a dialog where the user can choose a friend from the list
                        AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                        builder.setTitle("Select a friend");

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, friends);

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Retrieve the selected friend's user_id from Firebase
                                String selectedFriendName = adapter.getItem(which);
                                Query selectedFriendQuery = friendsRef.orderByChild("fullName").equalTo(selectedFriendName).limitToFirst(1);
                                selectedFriendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            // Selected friend not found in the database
                                            return;
                                        }
                                        String selectedFriendId = snapshot.getChildren().iterator().next().getKey();
                                        // Retrieve the list of addresses associated with the user_id from Firebase
                                        DatabaseReference addressesRef = FirebaseDatabase.getInstance().getReference("Users/" + selectedFriendId + "/locations");
                                        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                List<String> addresses = new ArrayList<>();
                                                int counter = 0;
                                                for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                                                    if (counter >= 1) {
                                                        String address = addressSnapshot.getValue(String.class);
                                                        addresses.add(address);
                                                    }
                                                    counter++;
                                                }

                                                if (addresses.size() == 1) {
                                                    // No locations available for the selected friend
                                                    Toast toast = Toast.makeText(getApplicationContext(), "No locations available", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    return;
                                                }

                                                // Show a dialog where the user can choose an address from the list
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                                                builder.setTitle("Select an address");

                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, addresses);
                                                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        autocompleteFragment2.setText(adapter.getItem(which));
                                                        Geocoder geocoder = new Geocoder(Mapview.this);
                                                        List<Address> addressList = null;
                                                        try{
                                                            addressList = geocoder.getFromLocationName(adapter.getItem(which),1);
                                                            Address userAddress = addressList.get(0);
                                                            LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                                            locations[1]=latLng;
                                                            geoLocate(latLng);
                                                        } catch (Exception e) {
                                                            Toast.makeText(Mapview.this, "Address doesn't exist", Toast.LENGTH_SHORT).show();;
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                //Log.d(TAG, "Error retrieving data from Firebase");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        //Log.d(TAG, "Error retrieving data from Firebase");
                                    }
                                });
                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Log.d(TAG, "Error retrieving data from Firebase");
                    }
                });
            }
        });

        addFriendLocationButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a dialog where the user can choose a friend from the list
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // User is not logged in
                    return;
                }
                String currentUserId = currentUser.getUid();
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends").child(currentUserId).child("Friends");

                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> friends = new ArrayList<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendName = friendSnapshot.child("fullName").getValue(String.class);
                            friends.add(friendName);
                        }

                        // Show a dialog where the user can choose a friend from the list
                        AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                        builder.setTitle("Select a friend");

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, friends);

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Retrieve the selected friend's user_id from Firebase
                                String selectedFriendName = adapter.getItem(which);
                                Query selectedFriendQuery = friendsRef.orderByChild("fullName").equalTo(selectedFriendName).limitToFirst(1);
                                selectedFriendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            // Selected friend not found in the database
                                            return;
                                        }
                                        String selectedFriendId = snapshot.getChildren().iterator().next().getKey();
                                        // Retrieve the list of addresses associated with the user_id from Firebase
                                        DatabaseReference addressesRef = FirebaseDatabase.getInstance().getReference("Users/" + selectedFriendId + "/locations");
                                        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                List<String> addresses = new ArrayList<>();
                                                int counter = 0;
                                                for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                                                    if (counter >= 1) {
                                                        String address = addressSnapshot.getValue(String.class);
                                                        addresses.add(address);
                                                    }
                                                    counter++;
                                                }

                                                if (addresses.size() == 1) {
                                                    // No locations available for the selected friend
                                                    Toast toast = Toast.makeText(getApplicationContext(), "No locations available", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    return;
                                                }

                                                // Show a dialog where the user can choose an address from the list
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                                                builder.setTitle("Select an address");

                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, addresses);
                                                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        autocompleteFragment3.setText(adapter.getItem(which));
                                                        Geocoder geocoder = new Geocoder(Mapview.this);
                                                        List<Address> addressList = null;
                                                        try{
                                                            addressList = geocoder.getFromLocationName(adapter.getItem(which),1);
                                                            Address userAddress = addressList.get(0);
                                                            LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                                            locations[2]=latLng;
                                                            geoLocate(latLng);
                                                        } catch (Exception e) {
                                                            Toast.makeText(Mapview.this, "Address doesn't exist", Toast.LENGTH_SHORT).show();;
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                //Log.d(TAG, "Error retrieving data from Firebase");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        //Log.d(TAG, "Error retrieving data from Firebase");
                                    }
                                });
                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Log.d(TAG, "Error retrieving data from Firebase");
                    }
                });
            }
        });

        addFriendLocationButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a dialog where the user can choose a friend from the list
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // User is not logged in
                    return;
                }
                String currentUserId = currentUser.getUid();
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends").child(currentUserId).child("Friends");

                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> friends = new ArrayList<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendName = friendSnapshot.child("fullName").getValue(String.class);
                            friends.add(friendName);
                        }

                        // Show a dialog where the user can choose a friend from the list
                        AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                        builder.setTitle("Select a friend");

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, friends);

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Retrieve the selected friend's user_id from Firebase
                                String selectedFriendName = adapter.getItem(which);
                                Query selectedFriendQuery = friendsRef.orderByChild("fullName").equalTo(selectedFriendName).limitToFirst(1);
                                selectedFriendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            // Selected friend not found in the database
                                            return;
                                        }
                                        String selectedFriendId = snapshot.getChildren().iterator().next().getKey();
                                        // Retrieve the list of addresses associated with the user_id from Firebase
                                        DatabaseReference addressesRef = FirebaseDatabase.getInstance().getReference("Users/" + selectedFriendId + "/locations");
                                        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                List<String> addresses = new ArrayList<>();
                                                int counter = 0;
                                                for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                                                    if (counter >= 1) {
                                                        String address = addressSnapshot.getValue(String.class);
                                                        addresses.add(address);
                                                    }
                                                    counter++;
                                                }

                                                if (addresses.size() == 1) {
                                                    // No locations available for the selected friend
                                                    Toast toast = Toast.makeText(getApplicationContext(), "No locations available", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    return;
                                                }

                                                // Show a dialog where the user can choose an address from the list
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                                                builder.setTitle("Select an address");

                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, addresses);
                                                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        autocompleteFragment4.setText(adapter.getItem(which));
                                                        Geocoder geocoder = new Geocoder(Mapview.this);
                                                        List<Address> addressList = null;
                                                        try{
                                                            addressList = geocoder.getFromLocationName(adapter.getItem(which),1);
                                                            Address userAddress = addressList.get(0);
                                                            LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                                            locations[3]=latLng;
                                                            geoLocate(latLng);
                                                        } catch (Exception e) {
                                                            Toast.makeText(Mapview.this, "Address doesn't exist", Toast.LENGTH_SHORT).show();;
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                //Log.d(TAG, "Error retrieving data from Firebase");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        //Log.d(TAG, "Error retrieving data from Firebase");
                                    }
                                });
                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Log.d(TAG, "Error retrieving data from Firebase");
                    }
                });
            }
        });

        addFriendLocationButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a dialog where the user can choose a friend from the list
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    // User is not logged in
                    return;
                }
                String currentUserId = currentUser.getUid();
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends").child(currentUserId).child("Friends");

                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> friends = new ArrayList<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendName = friendSnapshot.child("fullName").getValue(String.class);
                            friends.add(friendName);
                        }

                        // Show a dialog where the user can choose a friend from the list
                        AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                        builder.setTitle("Select a friend");

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, friends);

                        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Retrieve the selected friend's user_id from Firebase
                                String selectedFriendName = adapter.getItem(which);
                                Query selectedFriendQuery = friendsRef.orderByChild("fullName").equalTo(selectedFriendName).limitToFirst(1);
                                selectedFriendQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            // Selected friend not found in the database
                                            return;
                                        }
                                        String selectedFriendId = snapshot.getChildren().iterator().next().getKey();
                                        // Retrieve the list of addresses associated with the user_id from Firebase
                                        DatabaseReference addressesRef = FirebaseDatabase.getInstance().getReference("Users/" + selectedFriendId + "/locations");
                                        addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                List<String> addresses = new ArrayList<>();
                                                int counter = 0;
                                                for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                                                    if (counter >= 1) {
                                                        String address = addressSnapshot.getValue(String.class);
                                                        addresses.add(address);
                                                    }
                                                    counter++;
                                                }

                                                if (addresses.size() == 1) {
                                                    // No locations available for the selected friend
                                                    Toast toast = Toast.makeText(getApplicationContext(), "No locations available", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    return;
                                                }

                                                // Show a dialog where the user can choose an address from the list
                                                AlertDialog.Builder builder = new AlertDialog.Builder(Mapview.this);
                                                builder.setTitle("Select an address");

                                                ArrayAdapter<String> adapter = new ArrayAdapter<>(Mapview.this, android.R.layout.simple_list_item_1, addresses);
                                                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        autocompleteFragment5.setText(adapter.getItem(which));
                                                        Geocoder geocoder = new Geocoder(Mapview.this);
                                                        List<Address> addressList = null;
                                                        try{
                                                            addressList = geocoder.getFromLocationName(adapter.getItem(which),1);
                                                            Address userAddress = addressList.get(0);
                                                            LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
                                                            locations[4]=latLng;
                                                            geoLocate(latLng);
                                                        } catch (Exception e) {
                                                            Toast.makeText(Mapview.this, "Address doesn't exist", Toast.LENGTH_SHORT).show();;
                                                        }

                                                    }
                                                });
                                                builder.show();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                //Log.d(TAG, "Error retrieving data from Firebase");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        //Log.d(TAG, "Error retrieving data from Firebase");
                                    }
                                });
                            }
                        });
                        builder.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Log.d(TAG, "Error retrieving data from Firebase");
                    }
                });
            }
        });
    }

    public void hideBar(){

        switch(COUNT){
            case 2:
                View autocompleteView2 = autocompleteFragment2.getView();
                if (autocompleteView2 != null) {
                    autocompleteView2.setVisibility(View.GONE);
                    autocompleteView2.setEnabled(false);

                    removeAutocompleteButton.setVisibility(View.GONE);
                    removeAutocompleteButton.setEnabled(false);

                    locations[1] = null;
                    autocompleteFragment2.setText("");
                    autocompleteFragment2.setHint("Search 2nd Location");
                }
                addFriendLocationButton2.setVisibility(View.GONE);
                addFriendLocationButton2.setEnabled(false);
                break;

            case 3:
                View autocompleteView3 = autocompleteFragment3.getView();
                if (autocompleteView3 != null) {
                    autocompleteView3.setVisibility(View.GONE);
                    autocompleteView3.setEnabled(false);

                    locations[2] = null;
                    autocompleteFragment3.setText("");
                    autocompleteFragment3.setHint("Search 3rd Location");
                }
                addFriendLocationButton3.setVisibility(View.GONE);
                addFriendLocationButton4.setEnabled(false);
                break;

            case 4:
                View autocompleteView4 = autocompleteFragment4.getView();
                if (autocompleteView4 != null) {
                    autocompleteView4.setVisibility(View.GONE);
                    autocompleteView4.setEnabled(false);

                    locations[3] = null;
                    autocompleteFragment4.setText("");
                    autocompleteFragment4.setHint("Search 4th Location");
                }
                addFriendLocationButton4.setVisibility(View.GONE);
                addFriendLocationButton4.setEnabled(false);
                break;

            case 5:
                View autocompleteView5 = autocompleteFragment5.getView();
                if (autocompleteView5 != null) {
                    autocompleteView5.setVisibility(View.GONE);
                    autocompleteView5.setEnabled(false);

                    addAutocompleteButton.setVisibility(View.VISIBLE);
                    addAutocompleteButton.setEnabled(true);

                    addFriendLocationButton5.setVisibility(View.GONE);
                    addFriendLocationButton5.setEnabled(false);

                    locations[4] = null;
                    autocompleteFragment5.setText("");
                    autocompleteFragment5.setHint("Search 5th Location");
                }
                break;
        }

        COUNT--;


    }

    public void showBar(){
        COUNT++;
        switch(COUNT){
            case 2:
                View autocompleteView2 = autocompleteFragment2.getView();
                if (autocompleteView2 != null) {
                    autocompleteView2.setBackgroundColor(Color.WHITE);
                    autocompleteView2.setVisibility(View.VISIBLE);
                    autocompleteView2.setEnabled(true);

                    removeAutocompleteButton.setVisibility(View.VISIBLE);
                    removeAutocompleteButton.setEnabled(true);

                    addFriendLocationButton2.setVisibility(View.VISIBLE);
                    addFriendLocationButton2.setEnabled(true);
                }

                break;

            case 3:
                View autocompleteView3 = autocompleteFragment3.getView();
                if (autocompleteView3 != null) {
                    autocompleteView3.setBackgroundColor(Color.WHITE);
                    autocompleteView3.setVisibility(View.VISIBLE);
                    autocompleteView3.setEnabled(true);
                }
                addFriendLocationButton3.setVisibility(View.VISIBLE);
                addFriendLocationButton3.setEnabled(true);
                break;

            case 4:
                View autocompleteView4 = autocompleteFragment4.getView();
                if (autocompleteView4 != null) {
                    autocompleteView4.setBackgroundColor(Color.WHITE);
                    autocompleteView4.setVisibility(View.VISIBLE);
                    autocompleteView4.setEnabled(true);
                }
                addFriendLocationButton4.setVisibility(View.VISIBLE);
                addFriendLocationButton4.setEnabled(true);
                break;

            case 5:
                View autocompleteView5 = autocompleteFragment5.getView();
                if (autocompleteView5 != null) {
                    autocompleteView5.setBackgroundColor(Color.WHITE);
                    autocompleteView5.setVisibility(View.VISIBLE);
                    autocompleteView5.setEnabled(true);
                }
                addAutocompleteButton.setVisibility(View.INVISIBLE);
                addAutocompleteButton.setEnabled(false);

                addFriendLocationButton5.setVisibility(View.VISIBLE);
                addFriendLocationButton5.setEnabled(true);
                break;
        }
    }

    private void geoLocate(LatLng latLng){
        if (latLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
            mapCircle = mMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .fillColor(Color.rgb(194, 217, 252))
                    .strokeColor(Color.rgb(194, 217, 252))
                    .radius(500));
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        }
        else{
            Toast.makeText(Mapview.this, "LatLng returned null", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }

        //hideSoftKeyboard();

    }

    private void getLocationPermission(){
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }
            else{
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                            Toast.makeText(Mapview.this, "Got Location Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(Mapview.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
        catch (SecurityException e){
            //Log.e(TAG, "getDeviceLocation: SecurityException: "+e.getMessage());
        }
    }



    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(Mapview.this);
    }



    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void showGoogleInformation(String placeId, LatLng position) {
        PlacesClient placesClient = Places.createClient(this);
        List<Field> placeFields = Arrays.asList(Field.ID, Field.NAME, Field.ADDRESS, Field.PHONE_NUMBER, Field.WEBSITE_URI, Field.RATING, Field.USER_RATINGS_TOTAL, Field.OPENING_HOURS, Field.PHOTO_METADATAS);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            displayPlaceInformation(place);
        }).addOnFailureListener((exception) -> {
            Log.e("MapsActivity", "Error fetching place details", exception);
        });
    }
    private void displayPlaceInformation(Place place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.place_information_dialog, null);
        builder.setView(dialogView);
        TextView title = dialogView.findViewById(R.id.place_title);
        TextView info = dialogView.findViewById(R.id.place_info);
        ImageView placeImage = dialogView.findViewById(R.id.place_image);
        title.setText(place.getName());
        StringBuilder infoText = new StringBuilder(String.format(
                "Address: %s\nPhone: %s\nWebsite: %s\nRating: %.1f (based on %d user ratings)",
                place.getAddress(),
                place.getPhoneNumber(),
                place.getWebsiteUri() != null ? place.getWebsiteUri().toString() : "N/A",
                place.getRating(),
                place.getUserRatingsTotal()
        ));

        OpeningHours openingHours = place.getOpeningHours();
        if (openingHours != null) {
            infoText.append("\n\nOperating Hours:\n");
            List<String> weekdays = openingHours.getWeekdayText();
            for (String day : weekdays) {
                infoText.append(day).append("\n");
            }
        }

        info.setText(infoText.toString());

        List<PhotoMetadata> photoMetadataList = place.getPhotoMetadatas();
        if (photoMetadataList != null && !photoMetadataList.isEmpty()) {
            PhotoMetadata photoMetadata = photoMetadataList.get(0);
            FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(800)
                    .setMaxHeight(800)
                    .build();

            PlacesClient placesClient = Places.createClient(this);
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                placeImage.setImageBitmap(bitmap);
            }).addOnFailureListener((exception) -> {
                Log.e("MapsActivity", "Error fetching place image", exception);
            });
        }

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Jio Button", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get a list of the user's friends
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = currentUser.getUid();
                DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends/" + currentUserId + "/Friends");
                friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> friendIds = new ArrayList<>();
                        for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                            String friendId = friendSnapshot.getKey();
                            friendIds.add(friendId);
                        }
                        // Create a dialog to let the user select friends to notify
                        AlertDialog.Builder notifyBuilder = new AlertDialog.Builder(Mapview.this);
                        notifyBuilder.setTitle("Select Friends to Notify");
                        String[] friendNames = new String[friendIds.size()];
                        boolean[] checkedFriends = new boolean[friendIds.size()];
                        for (int i = 0; i < friendIds.size(); i++) {
                            final int index = i; // declare a new variable that is effectively final
                            String friendId = friendIds.get(index);
                            DatabaseReference friendsNameRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends/" + currentUserId + "/Friends/" + friendId + "/fullName");
                            friendsNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String friendName = snapshot.getValue(String.class);
                                    friendNames[index] = friendName;
                                    checkedFriends[index] = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //Log.e(TAG, "Failed to read value.", error.toException());
                                }
                            });
                        }


                        notifyBuilder.setMultiChoiceItems(friendNames, checkedFriends, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                // Update checkedFriends array
                                checkedFriends[which] = isChecked;
                            }
                        });
                        notifyBuilder.setPositiveButton("Notify", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Confirm with the user that they want to notify the selected friends
                                AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(Mapview.this);
                                confirmBuilder.setMessage("Are you sure you want to notify these friends?");
                                confirmBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Send notification to selected friends
                                        for (int i = 0; i < friendIds.size(); i++) {
                                            String friendId = friendIds.get(i);
                                            if (checkedFriends[i]) {
                                                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                                                //sendNotification(friendId); // helper function to send a notification to a friend
                                            }
                                        }
                                    }
                                });
                                confirmBuilder.setNegativeButton("Cancel", null);
                                AlertDialog confirmDialog = confirmBuilder.create();
                                confirmDialog.show();
                            }
                        });
                        notifyBuilder.setNegativeButton("Cancel", null);
                        AlertDialog notifyDialog = notifyBuilder.create();
                        notifyDialog.show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

//    private void sendNotification(String friendId) {
//        // Build the notification message
//        String notificationMessage = "You have a new message from your friend!";
//
//        // Get a reference to the friend's device token from the database
//        DatabaseReference friendTokenRef = FirebaseDatabase.getInstance().getReference("Users_Requests_And_Friends/" + friendId + "/deviceToken");
//        friendTokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String friendDeviceToken = dataSnapshot.getValue(String.class);
//
//                    // Use the friend's device token to send a notification using your preferred notification service
//                    // For example, using Firebase Cloud Messaging:
//                    FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(friendDeviceToken)
//                            .setMessageType("notification")
//                            .putData("message", notificationMessage)
//                            .build());
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Handle the error if there was an issue retrieving the friend's device token
//            }
//        });
//    }


    private void searchGooglePlace(String name, LatLng position) {
        PlacesClient placesClient = Places.createClient(this);
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(position.latitude - 0.01, position.longitude - 0.01),
                new LatLng(position.latitude + 0.01, position.longitude + 0.01)
        );

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setQuery(name)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i("KML", "Place found: " + prediction.getPlaceId());
                showGoogleInformation(prediction.getPlaceId(), position);
                break;
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("KML", "Place not found: " + apiException.getStatusCode());
            }
        });
    }
    private void showUnableToRetrieveLocationInfoAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Unable to retrieve location info");

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void loadKmlLayers(boolean showLayer1) {
        try {
            if (layer1 != null) {
                layer1.removeLayerFromMap();
            }
            if (layer2 != null) {
                layer2.removeLayerFromMap();
            }
            if (showLayer1) {
                layer1 = new KmlLayer(mMap, R.raw.eateries2, getApplicationContext());
                layer1.addLayerToMap();
            } else {
                layer2 = new KmlLayer(mMap, R.raw.relax, getApplicationContext());
                layer2.addLayerToMap();
            }

            KmlLayer currentLayer = showLayer1 ? layer1 : layer2;
            currentLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    Geometry geometry = feature.getGeometry();
                    Log.i("KML", "Feature clicked: " + feature.getId());
                    if (geometry instanceof Point) {
                        LatLng position = ((Point) geometry).getGeometryObject();
                        String name = feature.getProperty("name");
                        if (name != null) {
                            searchGooglePlace(name, position);
                        } else {
                            showUnableToRetrieveLocationInfoAlert();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
    private void init2() {
        ImageView toggleLayersButton = findViewById(R.id.toggle_layers_button);
        Slider radiusSlider = findViewById(R.id.radiusSlider);
        radius = 500;
        radiusSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                if (mapCircle == null){
                    radius = value;
                }
                else{
                    if(mapCircle != null) mapCircle.remove();
                    radius = value;
                    mapCircle = mMap.addCircle(new CircleOptions()
                            .center(resultLatLng)
                            .fillColor(Color.rgb(194, 217, 252))
                            .strokeColor(Color.rgb(194, 217, 252))
                            .radius(radius));
                }

            }
        });
        toggleLayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLayer1Visible = !isLayer1Visible;
                loadKmlLayers(isLayer1Visible);
            }
        });
    }

    private LatLng findMP(LatLng[] positions){
        int i = 0;
        double latAvg = 0;
        double longAvg = 0;
        LatLng result = positions[0];
        if(positions[0] == null) return null;
        //only 1 set of coordinates
        if(positions[1] == null) return result;

        //more than 1 set of coordinates
        //take the average

        else{
            while (positions[i] != null){
                //only calculating sum for now
                latAvg += positions[i].latitude;
                longAvg += positions[i].longitude;
                i++;
            }
            //calculate averages here
            latAvg = latAvg/(i);
            longAvg = longAvg/ (i);

            //combine to form a latlng

            result = new LatLng(latAvg,longAvg);
        }
        Toast.makeText(Mapview.this, "found the result", Toast.LENGTH_SHORT).show();
        //Log.d("MyApp",result.toString());
        //Log.d("MyApp",positions[0].toString());
        //Log.d("MyApp",positions[1].toString());
        //Log.d("MyApp",positions[2].toString());
        //Log.d("MyApp",positions[3].toString());
        //Log.d("MyApp",positions[4].toString());

        return result;
    }
}