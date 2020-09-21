package br.univates.mapspoints.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import br.univates.mapspoints.R;

public class ConfigRouteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_config_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_route);

        findViewById(R.id.iv_config_back).setOnClickListener(this);
        findViewById(R.id.bt_config_start).setOnClickListener(this);

        et_config_nickname = findViewById(R.id.et_config_nickname);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_config_back:
                onBackPressed();
                break;
            case R.id.bt_config_start:
                String nickname = et_config_nickname.getText().toString();
                    if (!nickname.isEmpty()) {
                        startActivity(new Intent(ConfigRouteActivity.this, MapsActivity.class)
                                .putExtra("nickname", nickname ));
                    } else {
                        Toast.makeText(this, "Preencha o apelido!", Toast.LENGTH_SHORT).show();
                    }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
