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
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
    private AutocompleteSupportFragment autocompleteFragment;
    private ImageView addAutocompleteButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), "AIzaSyBcPAPb1wZzbRHda01bSt_ft_LpLjfJhiE");
        PlacesClient placesClient = Places.createClient(this);
        setContentView(R.layout.activity_mapview);


        autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setTypeFilter(TypeFilter.ESTABLISHMENT);
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(new LatLng(1.1304753,103.6920359),new LatLng(1.4504753,104.0120359)));
        autocompleteFragment.setCountries("SG");
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        mGps = (ImageView) findViewById(R.id.ic_gps);
        addAutocompleteButton = findViewById(R.id.add_autocomplete_button);


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

        ViewGroup searchView = (ViewGroup) autocompleteFragment.getView();
        if (searchView != null) {
            searchView.setBackgroundColor(Color.WHITE);
        }
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {}

            @SuppressLint("RestrictedApi")
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                geoLocate(place.getLatLng());
                Toast.makeText(Mapview.this, "Place: "+place.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        addAutocompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Mapview.this, "Clicked! ", Toast.LENGTH_SHORT).show();
                hideBar();
            }
        });

        //hideSoftKeyboard();
    }

    public void hideBar(){
        View autocompleteView = autocompleteFragment.getView();
        if (autocompleteView != null) {
            autocompleteView.setVisibility(View.GONE);
            autocompleteView.setEnabled(false);
        }
    }

    private void geoLocate(LatLng latLng){
        if (latLng != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
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
                            Toast.makeText(Mapview.this, " Got current location successfully", Toast.LENGTH_SHORT).show();
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
        List<Field> placeFields = Arrays.asList(Field.ID, Field.NAME, Field.ADDRESS, Field.PHONE_NUMBER, Field.WEBSITE_URI, Field.RATING, Field.USER_RATINGS_TOTAL);
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
        builder.setTitle(place.getName());
        String info = String.format(
                "Address: %s\nPhone: %s\nWebsite: %s\nRating: %.1f (based on %d user ratings)",
                place.getAddress(),
                place.getPhoneNumber(),
                place.getWebsiteUri() != null ? place.getWebsiteUri().toString() : "N/A",
                place.getRating(),
                place.getUserRatingsTotal()
        );

        builder.setMessage(info);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Jio Button", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Your custom action for the button
                Toast.makeText(getApplicationContext(), "Button clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
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
        toggleLayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLayer1Visible = !isLayer1Visible;
                loadKmlLayers(isLayer1Visible);
            }
        });
    }
        }