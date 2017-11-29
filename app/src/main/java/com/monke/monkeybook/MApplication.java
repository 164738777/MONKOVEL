//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.monke.monkeybook.service.DownloadService;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

public class MApplication extends Application {

    private static MApplication instance;
    private RefWatcher refWatcher;

    public static RefWatcher getRefWatcher(Context context) {
        MApplication application = (MApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.IS_RELEASE) {
            String channel = "debug";
            try {
                ApplicationInfo appInfo = getPackageManager()
                        .getApplicationInfo(getPackageName(),
                                PackageManager.GET_META_DATA);
                channel = appInfo.metaData.getString("UMENG_CHANNEL_VALUE");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, getString(R.string.umeng_key), channel, MobclickAgent.EScenarioType.E_UM_NORMAL, true));
        }
        instance = this;
        startService(new Intent(this, DownloadService.class));

        // LeakCanary will detect any resource leaks in debug runs.
        refWatcher = LeakCanary.install(this);

        CrashReport.initCrashReport(getApplicationContext(), "9e59e08150", true);
    }

    public static MApplication getInstance() {
        return instance;
    }
}
