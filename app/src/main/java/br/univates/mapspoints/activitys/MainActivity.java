package br.univates.mapspoints.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import br.univates.mapspoints.R;
import br.univates.mapspoints.utils.Permissao;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private final int REQUEST_GPS_ACCESS = 1;
    private String[] permissoesNecessarias = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.iv_main_close).setOnClickListener(this);
        findViewById(R.id.bt_enviar_localizacao).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.iv_main_close:
                finish();
                break;
            case R.id.bt_enviar_localizacao:
                if (Permissao.validarPermissoes(this, permissoesNecessarias, REQUEST_GPS_ACCESS)) {
                    startActivity(new Intent(MainActivity.this, ConfigRouteActivity.class));
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_GPS_ACCESS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findViewById(R.id.bt_enviar_localizacao).callOnClick();
                    return;
                }
            }
        }
    }
}
