package br.univates.mapspoints.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import br.univates.mapspoints.R;
import br.univates.mapspoints.utils.Permissao;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private final int REQUEST_GPS_ACCESS = 1;

    private EditText et_config_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.iv_main_close).setOnClickListener(this);
        findViewById(R.id.bt_config_start).setOnClickListener(this);

        et_config_nickname = findViewById(R.id.et_config_nickname);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.iv_main_close:
                finish();
                break;
            case R.id.bt_config_start:
                String nickname = et_config_nickname.getText().toString();
                if (!nickname.isEmpty()) {
                    startActivity(new Intent(MainActivity.this, MapsActivity.class)
                            .putExtra("nickname", nickname ));
                } else {
                    Toast.makeText(this, "Preencha o apelido!", Toast.LENGTH_SHORT).show();
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
                    findViewById(R.id.bt_config_start).callOnClick();
                    return;
                }
            }
        }
    }
}
