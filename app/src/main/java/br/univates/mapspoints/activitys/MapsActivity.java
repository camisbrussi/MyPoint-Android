package br.univates.mapspoints.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.univates.mapspoints.R;
import br.univates.mapspoints.classes.Point;
import br.univates.mapspoints.utils.Permissao;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, android.location.LocationListener {

    private final String TAG = "MapsActivity";
    private GoogleMap mMap;

    private LocationManager mlocManager;

    private String nickname;
    private boolean atualizar;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//    private DatabaseReference mypoint = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Permissao.validarPermissoes(MapsActivity.this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1 );

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            nickname = bundle.getString("nickname");

            mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                System.out.println("Provider ON");
            }

        } else {
            Toast.makeText(this, "Problemas ao iniciar!", Toast.LENGTH_SHORT).show();
        }





        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mMap.clear();
                Iterable<DataSnapshot> points = dataSnapshot.getChildren();
                for (DataSnapshot point : points) {
                    /*System.out.println(point.getValue());
                    System.out.println(point.getKey());
                    System.out.println("LAT " + point.child("lat").getValue());
                    System.out.println("LONG " + point.child("lng").getValue());*/

                    Point p = point.getValue(Point.class);

                    LatLng latLng = new LatLng(p.getLat(), p.getLng());

                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(point.getKey()));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged: La: " + location.getLatitude() + " - Lo: " + location.getLongitude());


        Point point = new Point();
        try {
            point.setLat(location.getLatitude());
            point.setLng(location.getLongitude());
            reference.child(nickname).setValue(point);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!atualizar) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(15)
                    .bearing(0)
                    .tilt(30)
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            atualizar = true;
        }

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged: " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onDestroy() {
        reference.child(nickname).removeValue();
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                System.out.println("Provider ON");
            }
        }
    }
}
