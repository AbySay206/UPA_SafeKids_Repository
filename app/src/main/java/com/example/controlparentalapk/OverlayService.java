package com.example.controlparentalapk;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class OverlayService extends Service {

    private WindowManager windowManager;
    private View overlayView;
    private Handler handler;
    private Runnable usageChecker;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        usageChecker = new Runnable() {
            @Override
            public void run() {
                String appEnUso = obtenerAppEnUso();
                Set<String> appsBloqueadas = obtenerListaNegra();
                Set<String> appsDesbloqueadas = obtenerAppsDesbloqueadas();

                if (appsBloqueadas.contains(appEnUso) && !appsDesbloqueadas.contains(appEnUso)) {
                    mostrarPantallaBloqueo(appEnUso);
                }

                handler.postDelayed(this, 2000);
            }
        };
        handler.post(usageChecker);
    }

    private Set<String> obtenerListaNegra() {
        SharedPreferences prefs = getSharedPreferences("BlacklistPrefs", MODE_PRIVATE);
        return prefs.getStringSet("blacklist", new HashSet<>());
    }

    private Set<String> obtenerAppsDesbloqueadas() {
        SharedPreferences prefs = getSharedPreferences("UnlockedAppsPrefs", MODE_PRIVATE);
        return prefs.getStringSet("UnlockedAppsSet", new HashSet<>());
    }

    private String obtenerAppEnUso() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long tiempo = System.currentTimeMillis();
        UsageEvents eventos = usm.queryEvents(tiempo - 10000, tiempo);

        UsageEvents.Event evento = new UsageEvents.Event();
        String paquete = "";

        while (eventos.hasNextEvent()) {
            eventos.getNextEvent(evento);
            if (evento.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                paquete = evento.getPackageName();
            }
        }
        return paquete;
    }

    private void mostrarPantallaBloqueo(String packageName) {
        if (overlayView != null) return;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(this);
        overlayView = inflater.inflate(R.layout.overlay_blocked_screen, null);

        int layoutFlag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.CENTER;

        EditText edtPin = overlayView.findViewById(R.id.edtPin);
        Button btnCerrar = overlayView.findViewById(R.id.btnCerrar);

        edtPin.requestFocus();
        edtPin.post(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        btnCerrar.setOnClickListener(v -> {
            String inputPin = edtPin.getText().toString().trim();
            String storedPin = PinUtils.obtenerPin(this);

            if (inputPin.equals(storedPin)) {
                guardarAppDesbloqueada(packageName);

                if (windowManager != null && overlayView != null) {
                    windowManager.removeView(overlayView);
                    overlayView = null;
                }
            } else {
                Toast.makeText(this, "NIP incorrecto", Toast.LENGTH_SHORT).show();
                edtPin.setText("");
            }
        });

        windowManager.addView(overlayView, params);
    }

    private void guardarAppDesbloqueada(String packageName) {
        SharedPreferences prefs = getSharedPreferences("UnlockedAppsPrefs", MODE_PRIVATE);
        Set<String> desbloqueadas = new HashSet<>(prefs.getStringSet("UnlockedAppsSet", new HashSet<>()));
        desbloqueadas.add(packageName);
        prefs.edit().putStringSet("UnlockedAppsSet", desbloqueadas).apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(usageChecker);
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
}