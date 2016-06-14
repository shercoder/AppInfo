package com.shercoder.labs.appinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shercoder on 6/12/16.
 */
public class InstalledAppAdapter extends RecyclerView.Adapter<InstalledAppAdapter.ViewHolder> {
    private final ArrayList<ApplicationInfo> mDataSet;

    public InstalledAppAdapter(ArrayList<ApplicationInfo> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.installed_app_row_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ApplicationInfo applicationInfo = mDataSet.get(position);
        holder.bindView(applicationInfo);
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Context mContext;

        private final TextView mAppName;
        private final ImageView mAppIcon;
        private final TextView mSystemLabel;
        private final TextView mDisabledLabel;
        private final TextView mRunningLabel;

        private ApplicationInfo mAppInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();

            mAppName = (TextView) itemView.findViewById(R.id.app_name);
            mAppIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            mSystemLabel = (TextView) itemView.findViewById(R.id.app_system_label);
            mDisabledLabel = (TextView) itemView.findViewById(R.id.app_disabled_label);
            mRunningLabel = (TextView) itemView.findViewById(R.id.app_running_label);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, AppInfoDetailsActivity.class);
            intent.putExtra(AppInfoDetailsActivity.INTENT_EXTRA_APP_INFO, mAppInfo);
            mContext.startActivity(intent);
        }

        public void bindView(ApplicationInfo applicationInfo) {
            mAppInfo = applicationInfo;
            final PackageManager pm = mContext.getPackageManager();

            mAppName.setText(Utility.getAppName(mContext, applicationInfo));
            mAppIcon.setImageDrawable(applicationInfo.loadIcon(pm));

            String systemLabel = Utility.isUserApp(applicationInfo) ? "User Installed" : "System App";
            mSystemLabel.setText(systemLabel);

            mDisabledLabel.setVisibility(applicationInfo.enabled ? View.GONE : View.VISIBLE);

            mRunningLabel.setVisibility(isAppRunning() ? View.VISIBLE : View.GONE);
        }

        private boolean isAppRunning() {
            ActivityManager activityManager = (ActivityManager)
                    mContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> procInfos
                    = activityManager.getRunningAppProcesses();
            boolean isRunning = false;
            for (ActivityManager.RunningAppProcessInfo process : procInfos) {
                if (process.processName.equals(mAppInfo.packageName)) {
                    isRunning = true;
                    break;
                }
            }
            return isRunning;
        }
    }
}
