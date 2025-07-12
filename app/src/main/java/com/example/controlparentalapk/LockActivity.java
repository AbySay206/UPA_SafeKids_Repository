package com.example.controlparentalapk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.Set;

public class LockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout principal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 200, 40, 200);
        layout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));

        EditText inputPin = new EditText(this);
        inputPin.setHint("Introduce el NIP");
        inputPin.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        layout.addView(inputPin);

        Button btnDesbloquear = new Button(this);
        btnDesbloquear.setText("Desbloquear");
        layout.addView(btnDesbloquear);

        setContentView(layout);

        btnDesbloquear.setOnClickListener(v -> {
            String nipIngresado = inputPin.getText().toString().trim();
            if (verificarNip(nipIngresado)) {
                guardarAppDesbloqueada();
                finish(); // Cierra la pantalla solo si es correcto
            } else {
                Toast.makeText(this, "NIP incorrecto", Toast.LENGTH_SHORT).show();
            }
        });

        // Evita que se cierre con el botón "Atrás"
        setFinishOnTouchOutside(false);
    }

    private boolean verificarNip(String nip) {
        SharedPreferences prefs = getSharedPreferences("control_parental", Context.MODE_PRIVATE);
        String nipGuardado = prefs.getString("nip_guardado", null);
        return nipGuardado != null && nipGuardado.equals(nip);
    }

    private void guardarAppDesbloqueada() {
        String paquete = getIntent().getStringExtra("package_name");
        if (paquete != null) {
            SharedPreferences prefs = getSharedPreferences("UnlockedAppsPrefs", MODE_PRIVATE);
            Set<String> desbloqueadas = new HashSet<>(prefs.getStringSet("UnlockedAppsSet", new HashSet<>()));
            desbloqueadas.add(paquete);
            prefs.edit().putStringSet("UnlockedAppsSet", desbloqueadas).apply();
        }
    }

    @Override
    public void onBackPressed() {
        // Evita cerrar con botón físico de retroceso
        Toast.makeText(this, "No puedes salir sin ingresar el NIP", Toast.LENGTH_SHORT).show();
    }
}