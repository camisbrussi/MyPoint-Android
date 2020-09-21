package br.univates.mapspoints.activitys;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import br.univates.mapspoints.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, android.location.LocationListener {

    private GoogleMap mMap;

    private LocationManager mlocManager;

    private String nickname;
    private boolean atualizar;

    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mypoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                System.out.println("Provider ON");
            }
            nickname = bundle.getString("nickname");

            mypoint = reference.child(nickname);

        } else {
            Toast.makeText(this, "Problemas ao iniciar!", Toast.LENGTH_SHORT).show();
        }

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear();
                Iterable<DataSnapshot> points = dataSnapshot.getChildren();
                for (DataSnapshot point : points) {
                    System.out.println(point.getValue());
                    System.out.println(point.getKey());
                    System.out.println("LONG " + point.child("lng").getValue());
                    System.out.println("LAT " + point.child("lat").getValue());


                    double lat = Double.parseDouble(point.child("lat").getValue().toString()) ;
                    double lng = Double.parseDouble(point.child("lng").getValue().toString());

                    LatLng latLng = new LatLng(lat, lng);

                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(nickname));
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
        try {


            mypoint.child("lat").setValue(location.getLatitude());
            mypoint.child("lng").setValue(location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(!atualizar){
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .zoom(17)
                    .bearing(90)
                    .tilt(30)
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            atualizar = true;
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onStop() {
        mypoint.removeValue();
        super.onStop();
    }
}
