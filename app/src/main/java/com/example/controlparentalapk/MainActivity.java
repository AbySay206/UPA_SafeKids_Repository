package com.example.controlparentalapk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.provider.Settings;
import android.net.Uri;
import android.widget.Toast;
import android.widget.EditText;
import android.text.InputType;
import android.app.AlertDialog;
import android.content.SharedPreferences;




public class MainActivity extends AppCompatActivity {

    public static final String APP_A_BLOQUEAR = "com.falsa.nobloquear";
    public static final long TIEMPO_LIMITE_MS = 5 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, AppUsageMonitorService.class);
        startService(intent);

        Button btnVerUso = findViewById(R.id.btnVerUso);
        btnVerUso.setOnClickListener(v -> {
            Intent usageIntent = new Intent(MainActivity.this, UsageActivity.class);
            startActivity(usageIntent);

            if (!Settings.canDrawOverlays(this)) {
                Intent overlayintent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,Uri.parse("package:" + getPackageName()));
                Toast.makeText(this, "Activa El Permiso De Mostrar Sobre Otras Apps", Toast.LENGTH_LONG).show();
                startActivity(overlayintent);
            }

        });

        Button btnEstablecerPin = findViewById(R.id.btnEstablecerPin);
        btnEstablecerPin.setOnClickListener(v -> {
            EditText input = new EditText(MainActivity.this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Establecer PIN")
                    .setMessage("Ingresa un PIN de 4 dígitos:")
                    .setView(input)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String nuevoPin = input.getText().toString();
                        if (nuevoPin.length() == 4) {
                            PinUtils.guardarPin(MainActivity.this, nuevoPin);
                            Toast.makeText(MainActivity.this, "PIN guardado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "El PIN debe tener 4 dígitos", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isLoggedIn", false); // Solo cerramos sesión, no borramos usuario
            editor.apply();

            Toast.makeText(MainActivity.this,"Sesion Cerrada Con Exito",Toast.LENGTH_SHORT).show();

            Intent intentout = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentout);
            finish();
        });

        Button btnListaNegra = findViewById(R.id.btnListaNegra);
        btnListaNegra.setOnClickListener(v -> {
            Intent blacklistintent = new Intent(MainActivity.this, BlacklistActivity.class);
            startActivity(blacklistintent);
        });


    }
}