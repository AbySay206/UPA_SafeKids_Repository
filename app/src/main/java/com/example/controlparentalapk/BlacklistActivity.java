package com.example.controlparentalapk;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import com.example.controlparentalapk.AppInfo;

import java.util.*;

public class BlacklistActivity extends AppCompatActivity {

    private EditText editAppName;
    private Button btnAddApp;
    private ListView listViewApps;
    private ListView listViewInstalledApps;

    private ArrayList<String> appList;
    private ArrayAdapter<String> adapter;
    private SharedPreferences sharedPreferences;

    private static final String PREF_NAME = "BlacklistPrefs";
    private static final String KEY_BLACKLIST = "blacklist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);

        editAppName = findViewById(R.id.editAppName);
        btnAddApp = findViewById(R.id.btnAddApp);
        listViewApps = findViewById(R.id.listViewApps);
        listViewInstalledApps = findViewById(R.id.listViewInstalledApps);

        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        appList = new ArrayList<>(getBlacklist());

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appList);
        listViewApps.setAdapter(adapter);

        btnAddApp.setOnClickListener(v -> {
            String appName = editAppName.getText().toString().trim();
            if (!appName.isEmpty() && !appList.contains(appName)) {
                appList.add(appName);
                adapter.notifyDataSetChanged();
                saveBlacklist(appList);
                editAppName.setText("");
            }
        });

        listViewApps.setOnItemLongClickListener((parent, view, position, id) -> {
            appList.remove(position);
            adapter.notifyDataSetChanged();
            saveBlacklist(appList);
            return true;
        });

        mostrarAppsInstaladas();

        Button btnResetUnlocks = findViewById(R.id.btnResetUnlocks);
        btnResetUnlocks.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UnlockedAppsPrefs", MODE_PRIVATE);
            prefs.edit().remove("UnlockedAppsSet").apply();
            Toast.makeText(this, "Se reactivaron los bloqueos temporales", Toast.LENGTH_SHORT).show();
        });

    }


    private void mostrarAppsInstaladas() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<AppInfo> appInfoList = new ArrayList<>();

        for (ApplicationInfo app : apps) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                String nombre = app.loadLabel(pm).toString();
                Drawable icono = app.loadIcon(pm);
                appInfoList.add(new AppInfo(nombre, app.packageName, icono));
            }
        }

        AppInfoAdapter installedAdapter = new AppInfoAdapter(this, appInfoList, app -> {
            if (!appList.contains(app.packageName)) {
                appList.add(app.packageName);
                saveBlacklist(appList);
                adapter.notifyDataSetChanged();
            }
        });

        listViewInstalledApps.setAdapter(installedAdapter);
    }

    private void saveBlacklist(ArrayList<String> list) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(list);
        editor.putStringSet(KEY_BLACKLIST, set);
        editor.apply();
    }

    private Set<String> getBlacklist() {
        return sharedPreferences.getStringSet(KEY_BLACKLIST, new HashSet<>());
    }
}
