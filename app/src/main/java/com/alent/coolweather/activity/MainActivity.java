package com.alent.coolweather.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.alent.coolweather.R;
import com.alent.coolweather.util.HttpCallbackListener;
import com.alent.coolweather.util.HttpUtil;

import org.litepal.tablemanager.Connector;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
