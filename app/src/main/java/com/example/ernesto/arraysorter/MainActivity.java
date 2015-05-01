package com.example.ernesto.arraysorter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Random;
import android.os.Handler;

import com.example.ernesto.arraysorter.BackgroundIntent;
import com.example.ernesto.arraysorter.BackgroundResultReceiver;


public class MainActivity extends ActionBarActivity implements BackgroundResultReceiver.Receiver {


    private BackgroundResultReceiver mReceiver;
    TextView msg1;
    TextView msg2;
    TextView batteryTxt;
    Button buttonOne;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msg1 = (TextView) findViewById(R.id.msg1);
        msg2 = (TextView) findViewById(R.id.msg2);
        batteryTxt = (TextView) findViewById(R.id.batteryTxt);
        getBatteryPercentage();
        buttonOne = (Button) findViewById(R.id.sort);

        buttonOne.setOnClickListener(new Button.OnClickListener() {  /* aqui se llama al proceso */
            public void onClick(View v) {
                executeInsertionSort(v);
            }
        });
    }

    private void getBatteryPercentage() {
        BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int nivelActual = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int escala = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int nivel = -1;
                if (nivelActual >= 0 && escala > 0) {
                    nivel = (nivelActual * 100) / escala;
                }
                batteryTxt.setText("Nivel de bateria: " + nivel + "%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case BackgroundIntent.STATUS_RUNNING:

                setProgressBarIndeterminateVisibility(true);
                msg1.setText("Iniciado @" + new Date().toString());
                getBatteryPercentage();
                buttonOne.setEnabled(false);
                break;
            case BackgroundIntent.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
                setProgressBarIndeterminateVisibility(false);
                String exectime = resultData.getString("exectime", "No Info");
                msg2.setText("Finalizado @" + new Date().toString() + " tomo " + exectime);
                getBatteryPercentage();
                buttonOne.setEnabled(true);
                break;
            case BackgroundIntent.STATUS_ERROR:
                /* Handle the error */
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }


    public void executeInsertionSort(View view) {    /* este es el proceso que llama al servicio */
         /* Starting Download Service */
        mReceiver = new BackgroundResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, BackgroundIntent.class);

        /* Send optional extras to Download IntentService */
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);
        startService(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}