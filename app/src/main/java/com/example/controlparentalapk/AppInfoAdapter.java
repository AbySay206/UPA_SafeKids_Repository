package com.example.controlparentalapk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.controlparentalapk.AppInfo;


import java.util.List;

public class AppInfoAdapter extends ArrayAdapter<AppInfo> {

    private List<AppInfo> appList;
    private Context context;
    private OnAddClickListener listener;

    public interface OnAddClickListener {
        void onAddClick(AppInfo app);
    }

    public AppInfoAdapter(Context context, List<AppInfo> apps, OnAddClickListener listener) {
        super(context, 0, apps);
        this.context = context;
        this.appList = apps;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AppInfo app = appList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_app_info, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.appIcon);
        TextView name = convertView.findViewById(R.id.appName);
        Button btnAdd = convertView.findViewById(R.id.btnAdd);

        icon.setImageDrawable(app.icon);
        name.setText(app.appName);

        btnAdd.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddClick(app);
            }
        });

        return convertView;
    }
}
