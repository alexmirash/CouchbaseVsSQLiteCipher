package com.awesome.mirash.helloworld;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;

/**
 * @author Mirash
 */
public class MyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LOL", "wat de fcuk ");
        setContentView(new View(this) {
            {
                setBackgroundColor(Color.CYAN);
            }
        });
        initializeSQLCipher();
    }

    private void initializeSQLCipher() {
        Log.d("LOL", "initializeSQLCipher ");
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
