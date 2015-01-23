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
        TextView tv = new TextView(this);
        tv.setBackgroundColor(giveMeAColorMaster());
        tv.setText(getMyTitle());
        setContentView(tv);
        doSth();
    }

    protected String getMyTitle() {
        return getClass().getSimpleName();
    }

    abstract protected int giveMeAColorMaster();

    abstract protected void doSth();
}
