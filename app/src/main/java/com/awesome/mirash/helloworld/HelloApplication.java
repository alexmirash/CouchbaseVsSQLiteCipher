package com.awesome.mirash.helloworld;

import android.support.multidex.MultiDexApplication;

/**
 * @author Mirash
 */
public class HelloApplication extends MultiDexApplication {
    public static final String LOG_TAG = "LOL";

    @Override
    public void onCreate() {
        super.onCreate();
/*        Log.d(LOG_TAG, "loadSqlLibs");
        SQLiteDatabase.loadLibs(this);
        Log.d(LOG_TAG, "SqlLibs'reLoadedMyMaster");*/
    }
}
