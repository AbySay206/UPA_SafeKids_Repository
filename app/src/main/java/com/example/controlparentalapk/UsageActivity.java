package com.example.controlparentalapk;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UsageActivity extends AppCompatActivity {

    RecyclerView recyclerUsage;
    AppInfoAdapter adapter;
    ArrayList<AppInfo> appsUsadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage);

        recyclerUsage = findViewById(R.id.recyclerUsage);
        recyclerUsage.setLayoutManager(new LinearLayoutManager(this));

        if (!hasUsageStatsPermission()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            Toast.makeText(this, "Por favor, otorga permisos de uso.", Toast.LENGTH_LONG).show();
        } else {
            mostrarUsoApps();
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                getPackageName()
        );
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void mostrarUsoApps() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long startTime = calendar.getTimeInMillis();

        List<UsageStats> usageStatsList = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        );

        appsUsadas = new ArrayList<>();

        if (usageStatsList == null || usageStatsList.isEmpty()) {
            Toast.makeText(this, "No hay datos de uso disponibles.", Toast.LENGTH_SHORT).show();
            return;
        }

        PackageManager pm = getPackageManager();

        for (UsageStats stats : usageStatsList) {
            long totalTime = stats.getTotalTimeInForeground();
            if (totalTime > 0) {
                try {
                    ApplicationInfo appInfo = pm.getApplicationInfo(stats.getPackageName(), 0);
                    String appName = pm.getApplicationLabel(appInfo).toString();
                    Drawable icono = pm.getApplicationIcon(appInfo);

                    String tiempoFormateado = formatearTiempo(totalTime);
                    appsUsadas.add(new AppInfo(appName + " - " + tiempoFormateado, stats.getPackageName(), icono));


                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        AppUsageAdapter adapter = new AppUsageAdapter(this, appsUsadas);
        recyclerUsage.setAdapter(adapter);
    }

    private String formatearTiempo(long milliseconds) {
        long totalMinutes = milliseconds / 60000;
        long horas = totalMinutes / 60;
        long minutos = totalMinutes % 60;
        return horas + " hrs " + minutos + " min";
    }
}
