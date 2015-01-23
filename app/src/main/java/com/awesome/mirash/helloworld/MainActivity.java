package com.awesome.mirash.helloworld;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.joanzapata.pdfview.PDFView;

/**
 * @author Mirash
 */
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PDFView pdfView = (PDFView) findViewById(R.id.pdf_view);
        pdfView.setVisibility(View.VISIBLE);

        initBtnListener(R.id.cdb_btn, CouchDbTestActivity.class);
        initBtnListener(R.id.sql_btn, SQLiteTestActivity.class);
    }

    private void initBtnListener(int id, final Class<? extends BaseTestActivity> activityClass) {
        findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, activityClass);
                startActivity(intent);
            }
        });
    }
}
