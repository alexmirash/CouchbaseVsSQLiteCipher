package com.awesome.mirash.helloworld;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author Mirash
 */
public abstract class BaseTestActivity extends Activity {
    @Override
    final protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView = new TextView(this);
        textView.setBackgroundColor(giveMeAColorMaster());
        textView.setText(getMyTitle());
        setContentView(textView);
        doSth();
    }

    protected String getMyTitle() {
        return getClass().getSimpleName();
    }

    abstract protected int giveMeAColorMaster();

    abstract protected void doSth();
}
