package com.awesome.mirash.helloworld;

import android.graphics.Color;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

import static com.awesome.mirash.helloworld.HelloApplication.LOG_TAG;

/**
 * @author Mirash
 */
public class SQLiteTestActivity extends BaseTestActivity {
    @Override
    protected int giveMeAColorMaster() {
        return Color.YELLOW;
    }

    @Override
    protected void doSth() {
        Log.d(LOG_TAG, "loadLibs");
        //java.lang.UnsatisfiedLinkError: Couldn't load sqlcipher_android from loader dalvik.system.PathClassLoader[DexPathList[[zip file "/data/app/com.awesome.mirash.helloworld-1.apk"],nativeLibraryDirectories=[/data/app-lib/com.awesome.mirash.helloworld-1, /vendor/lib, /system/lib]]]: findLibrary returned null
        SQLiteDatabase.loadLibs(this);
        Log.d(LOG_TAG, "libs'reLoadedMyMaster");

        File databaseFile = getDatabasePath("demo.db");
        databaseFile.mkdirs();
        databaseFile.delete();
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null);
        database.execSQL("create table t1(a, b)");
        database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
                "two for the show"});
        Log.d(LOG_TAG, "initialized successfully " + database.getPageSize());

    }
}
