package com.awesome.mirash.helloworld;

import android.support.multidex.MultiDexApplication;

/**
 * @author Mirash
 */
public class HelloApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
/*        Log.d("LOL", "loadLibs");
        SQLiteDatabase.loadLibs(this);
        Log.d("LOL", "libsLoadedMyMaster");*/
    }
}
