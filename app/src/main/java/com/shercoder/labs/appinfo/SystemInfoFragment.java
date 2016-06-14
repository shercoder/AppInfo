package com.shercoder.labs.appinfo;

import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by shercoder on 6/13/16.
 */
public class SystemInfoFragment extends Fragment {
    private static final String TAG = SystemInfoFragment.class.getSimpleName();

    private TextView mAndroidOs;
    private TextView mUptime;
    private TextView mModel;
    private TextView mNetworkSsid;
    private TextView mIpAddress;
    private TextView mMemoryAvailable;
    private TextView mMemoryUsed;
    private TextView mTotalStorage;
    private TextView mStorageAvailable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_system_info, container, false);
        rootView.setTag(TAG);
        mAndroidOs = (TextView) rootView.findViewById(R.id.android_os);
        mUptime = (TextView) rootView.findViewById(R.id.device_uptime);
        mModel = (TextView) rootView.findViewById(R.id.device_model);
        mNetworkSsid = (TextView) rootView.findViewById(R.id.network_ssid);
        mIpAddress = (TextView) rootView.findViewById(R.id.ip_address);
        mMemoryAvailable = (TextView) rootView.findViewById(R.id.memory_available);
        mMemoryUsed = (TextView) rootView.findViewById(R.id.memory_used);
        mTotalStorage = (TextView) rootView.findViewById(R.id.storage_total);
        mStorageAvailable = (TextView) rootView.findViewById(R.id.storage_free);
        bindDataToViews();
        return rootView;
    }

    private void bindDataToViews() {
        setText(mAndroidOs, Build.VERSION.RELEASE);
        setText(mUptime, getUpTime());
        setText(mModel, Build.MANUFACTURER);
        setText(mNetworkSsid, getSSID());
        setText(mIpAddress, getIpAddress());
        setText(mMemoryAvailable, getTotalRAM());
        setText(mMemoryUsed, getRamUsed());
        setText(mTotalStorage, totalStorage());
        setText(mStorageAvailable, freeStorage());
    }

    private void setText(TextView view, String newText) {
        String old = view.getText().toString();
        view.setText(String.format("%s%s", old, newText));
    }

    private String getUpTime() {
        long time = SystemClock.elapsedRealtime();
        return Utility.getDate(time, "hh:mm:ss");
    }

    private String getSSID() {
        WifiManager wifiManager
                = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState()== SupplicantState.COMPLETED) {
            return wifiInfo.getSSID();
        }
        return "No WiFi Connection";
    }

    private String getIpAddress() {
        WifiManager wifiManager
                = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ip = "No IPAddress available";
        if (wifiInfo.getSupplicantState()== SupplicantState.COMPLETED) {
            ip = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        }
        return ip;
    }

    private String getTotalRAM() {
        ActivityManager actManager
                = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);

        long totRam = memInfo.totalMem;
        return normalizeBytes(totRam);
    }

    private String getRamUsed() {
        ActivityManager actManager
                = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);

        long usedRam = memInfo.totalMem - memInfo.availMem;
        return normalizeBytes(usedRam);
    }

    public String totalStorage() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytes = statFs.getBlockCountLong() * statFs.getBlockSizeLong();
        } else {
            bytes = statFs.getBlockCount() * statFs.getBlockSize();
        }
        return normalizeBytes(bytes);
    }

    public String freeStorage() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytes = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
        } else {
            bytes = statFs.getAvailableBlocks() * statFs.getBlockSize();
        }
        return normalizeBytes(bytes);
    }

    public String normalizeBytes(long bytes) {
        double kb = bytes / 1024.0;
        double mb = bytes / 1048576.0;
        double gb = bytes / 1073741824.0;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        String lastValue;
        if (gb > 1) {
            lastValue = twoDecimalForm.format(gb).concat(" GB");
        } else if (mb > 1) {
            lastValue = twoDecimalForm.format(mb).concat(" MB");
        } else if (kb > 1){
            lastValue = twoDecimalForm.format(kb).concat(" KB");
        } else {
            lastValue = twoDecimalForm.format(bytes).concat(" Bytes");
        }
        return lastValue;
    }
}
