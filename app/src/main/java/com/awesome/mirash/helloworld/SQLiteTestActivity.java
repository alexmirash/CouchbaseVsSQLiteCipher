package com.awesome.mirash.helloworld;

import android.graphics.Color;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

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
        Log.d("LOL", "loadLibs");
        SQLiteDatabase.loadLibs(this);
        Log.d("LOL", "libs'reLoadedMyMaster");
        File databaseFile = getDatabasePath("demo.db");
        databaseFile.mkdirs();
        databaseFile.delete();
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "test123", null);
        database.execSQL("create table t1(a, b)");
        database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money",
                "two for the show"});
        Log.d("LOL", "initialized successfully " + database.getPageSize());

    }
}