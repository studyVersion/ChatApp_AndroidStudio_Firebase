package com.example.chatapp;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

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
    GoogleMap mMap;
    MapView mMapView;
    View mView;
    final Marker[] marker = {null};
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("marker_data", MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_maps, null);
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

        if (marker == null) {

            if (markerLatitude != 0 && markerLongitude != 0) {
                LatLng lastMarkerPosition = new LatLng(markerLatitude, markerLongitude);
                marker[0] = mMap.addMarker(new MarkerOptions().position(lastMarkerPosition).icon(bitmapDescriptor(getContext(),R.drawable.car)));
            }
        }
    }

    private void setButtonClickListener(View view) {
        Button btncoche = view.findViewById(R.id.btncoche);
        btncoche.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.black)));
        btncoche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker[0] != null) {
                    LatLng markerPosition = marker[0].getPosition();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerPosition, 17f));
                } else {
                    Toast.makeText(getActivity(), "No car is parked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        setButtonClickListener(view);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        float markerLatitude = sharedPreferences.getFloat("marker_latitude", 0);
        float markerLongitude = sharedPreferences.getFloat("marker_longitude", 0);

        if (markerLatitude != 0 && markerLongitude != 0) {
            LatLng lastMarkerPosition = new LatLng(markerLatitude, markerLongitude);
            marker[0] = mMap.addMarker(new MarkerOptions().position(lastMarkerPosition).icon(bitmapDescriptor(getContext(),R.drawable.car)));
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                if (marker != null) {
                    marker[0].remove();
                }
                marker[0] = mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmapDescriptor(getContext(),R.drawable.car)));
                editor.putFloat("marker_latitude", (float) latLng.latitude);
                editor.putFloat("marker_longitude", (float) latLng.longitude);
                editor.apply();
            }

        });
    }
    private BitmapDescriptor bitmapDescriptor(Context context, int vector){
        //crear el icono del coche
        Drawable vectorDrawable= ContextCompat.getDrawable(context,vector);
        vectorDrawable.setBounds(0,0,vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap= Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



}
