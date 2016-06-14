package com.shercoder.labs.appinfo;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

public class AppInfoDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String INTENT_EXTRA_APP_INFO = "INTENT_EXTRA_APP_INFO";

    private ApplicationInfo mApplicationInfo;
    private PackageInfo mPackageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info_details);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mApplicationInfo = intent.getParcelableExtra(INTENT_EXTRA_APP_INFO);

        PackageManager pm = getPackageManager();
        try {
            mPackageInfo = pm.getPackageInfo(mApplicationInfo.packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        bindViews();

        String appName = Utility.getAppName(this, mApplicationInfo);
        setTitle(appName);
    }

    private void bindViews() {
        Button letsCheat = (Button) findViewById(R.id.app_info_page_button);
        letsCheat.setOnClickListener(this);

        TextView packageName = (TextView) findViewById(R.id.app_package_name_value);
        packageName.setText(mApplicationInfo.packageName);

        TextView versionName = (TextView) findViewById(R.id.app_version_name_value);
        versionName.setText(mPackageInfo.versionName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStats usageStats = getUsageStats();
            if (usageStats != null) {
                TextView lastTimeUsed = (TextView) findViewById(R.id.app_last_time_used_value);
                TextView lastTimeUsedLabel = (TextView) findViewById(R.id.app_last_time_used_label);
                lastTimeUsed.setText(Long.toString(usageStats.getLastTimeUsed()));
                lastTimeUsed.setVisibility(View.VISIBLE);
                lastTimeUsedLabel.setVisibility(View.VISIBLE);
            }
        }

        TextView installTime = (TextView) findViewById(R.id.app_first_install_time_value);
        String installTimeStr = Utility.getDate(mPackageInfo.firstInstallTime, "dd/MM/yyyy hh:mm:ss");
        installTime.setText(installTimeStr);

        TextView updateTime = (TextView) findViewById(R.id.app_last_update_time_value);
        String updateTimeStr = Utility.getDate(mPackageInfo.lastUpdateTime, "dd/MM/yyyy hh:mm:ss");
        updateTime.setText(updateTimeStr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private UsageStats getUsageStats() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);

        List<UsageStats> queryUsageStats = usageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.getTimeInMillis(),
                        System.currentTimeMillis());

        UsageStats usageStats = null;
        if (queryUsageStats.size() > 0) {
            for (UsageStats us : queryUsageStats) {
                if (us.getPackageName().equals(mApplicationInfo.packageName)) {
                    usageStats = us;
                    break;
                }
            }
        }

        return usageStats;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.app_info_page_button:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", mApplicationInfo.packageName, null);
                intent.setData(uri);
                startActivity(intent);
                break;
        }
    }
}
