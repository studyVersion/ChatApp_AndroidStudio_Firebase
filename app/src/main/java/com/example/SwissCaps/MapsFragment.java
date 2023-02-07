package com.example.SwissCaps;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private MapView mMapView;
    private View mView;
    private Marker marker;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("marker_data", MODE_PRIVATE);
        editor = sharedPreferences.edit();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_maps, container, false);
        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).enableSwipe();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).disableSwipe();

        float markerLatitude = sharedPreferences.getFloat("marker_latitude", 0);
        float markerLongitude = sharedPreferences.getFloat("marker_longitude", 0);
        LatLng latLng = new LatLng(markerLatitude, markerLongitude);

        if (latLng.latitude != 0 && latLng.longitude != 0) {
            addMarkerOnMap(latLng);
        }
    }

    private void setButtonClickListener(View view) {
        Button btncoche = view.findViewById(R.id.btncoche);
        btncoche.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.black)));
        btncoche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker != null) {
                    LatLng markerPosition = marker.getPosition();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17f));
                } else {
                    Toast.makeText(getActivity(), "No car is parked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeMap() {
        mMapView.onCreate(null);
        mMapView.onResume();
        mMapView.getMapAsync(this);
    }

    private void checkLocationPermissionAndInitializeMap() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            initializeMap();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = mView.findViewById(R.id.map);
        setButtonClickListener(view);
        checkLocationPermissionAndInitializeMap();

    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {

                    if (result) {
                        initializeMap();
                    } else {
                        Toast.makeText(getActivity(), "Location permission is required to display the map", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void saveMarkerPositionToSharedPreferences(LatLng latLng) {
        editor.putFloat("marker_latitude", (float) latLng.latitude);
        editor.putFloat("marker_longitude", (float) latLng.longitude);
        editor.apply();
    }

    private void setMapLongClickListener() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                addMarkerOnMap(latLng);
            }
        });
    }

    private void addMarkerOnMap(LatLng latLng) {
        mMap.clear();
        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptor(getContext(), R.drawable.car)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        saveMarkerPositionToSharedPreferences(latLng);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getActivity());
        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    // mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                } else {
                    Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        float markerLatitude = sharedPreferences.getFloat("marker_latitude", 0);
        float markerLongitude = sharedPreferences.getFloat("marker_longitude", 0);
        LatLng latLng = new LatLng(markerLatitude, markerLongitude);

        if (latLng.latitude != 0 && latLng.longitude != 0) {
            addMarkerOnMap(latLng);
        }
        getCurrentLocation();
        setMapLongClickListener();
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vector) {
        //crear el icono del coche
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vector);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


}
