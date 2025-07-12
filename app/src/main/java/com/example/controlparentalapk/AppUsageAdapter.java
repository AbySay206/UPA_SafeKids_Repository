package com.example.controlparentalapk;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppUsageAdapter extends RecyclerView.Adapter<AppUsageAdapter.ViewHolder> {

    private List<AppInfo> apps;
    private Context context;

    public AppUsageAdapter(Context context, List<AppInfo> apps) {
        this.context = context;
        this.apps = apps;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;

        public ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
        }
    }

    @NonNull
    @Override
    public AppUsageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_usage, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppUsageAdapter.ViewHolder holder, int position) {
        AppInfo app = apps.get(position);

        holder.appIcon.setImageDrawable(app.icon);
        holder.appName.setText(app.appName); // Ya incluye el tiempo formateado en appName
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}
