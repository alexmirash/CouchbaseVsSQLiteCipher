package com.awesome.mirash.helloworld;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * @author Mirash
 */
public class HelloApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LOL", "loadLibs");
        SQLiteDatabase.loadLibs(this);
        Log.d("LOL", "libsLoadedMyMaster");
    }
}
