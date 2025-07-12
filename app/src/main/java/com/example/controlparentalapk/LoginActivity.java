package com.example.controlparentalapk;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editUsername, editPassword;
    Button btnLogin;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);


        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            goToMain();
            return;
        }

        setContentView(R.layout.activity_login);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llena ambos campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Guardar usuario/contraseña si aún no existe
            if (!sharedPreferences.contains("username")) {
                editor.putString("username", username);
                editor.putString("password", password);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();
                Toast.makeText(this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                goToMain();
            } else {

                String savedUser = sharedPreferences.getString("username", "");
                String savedPass = sharedPreferences.getString("password", "");
                if (username.equals(savedUser) && password.equals(savedPass)) {
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    Toast.makeText(this, "Sesión iniciada", Toast.LENGTH_SHORT).show();
                    goToMain();
                } else {
                    Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Cierra esta pantalla para que no regrese atrás
    }
}
