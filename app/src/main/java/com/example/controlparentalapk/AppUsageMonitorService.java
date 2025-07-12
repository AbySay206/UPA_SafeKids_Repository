package com.example.controlparentalapk;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppUsageMonitorService extends Service {

    private Handler handler;
    private Runnable checkRunnable;
    private static final long INTERVALO = 5000; // 5 segundos
    private boolean bloqueoMostrado = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        checkRunnable = new Runnable() {
            @Override
            public void run() {
                revisarAppActiva();
                handler.postDelayed(this, INTERVALO);
            }
        };
        handler.post(checkRunnable);
        return START_STICKY;
    }

    private void revisarAppActiva() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> stats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        //  USAR EL MISMO SharedPreferences que en BlacklistActivity
        SharedPreferences prefs = getSharedPreferences("BlacklistPrefs", MODE_PRIVATE);
        Set<String> blacklist = prefs.getStringSet("blacklist", new HashSet<>());



        if (stats != null) {
            UsageStats ultima = null;
            for (UsageStats usage : stats) {
                String packageName = usage.getPackageName();



                if (ultima == null || usage.getLastTimeUsed() > ultima.getLastTimeUsed()) {
                    ultima = usage;
                }
            }

            if (ultima != null) {
                String packageName = ultima.getPackageName();

                // Verificar si está en la blacklist
                if (blacklist.contains(packageName)) {
                    Toast.makeText(this, "¡App bloqueada!: " + packageName, Toast.LENGTH_LONG).show();

                    long lastUsed = ultima.getLastTimeUsed();
                    long now = System.currentTimeMillis();

                    if ((now - lastUsed < 5000) && Settings.canDrawOverlays(this) && !bloqueoMostrado) {
                        bloqueoMostrado = true;

                        Intent i = new Intent(this, OverlayService.class);
                        startService(i);
                    } else if ((now - lastUsed >= 5000)) {
                        bloqueoMostrado = false;
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}