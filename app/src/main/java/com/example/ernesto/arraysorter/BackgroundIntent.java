package com.example.ernesto.arraysorter;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class BackgroundIntent extends IntentService {

    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "BackgroundIntent";

    public BackgroundIntent() {
        super(BackgroundIntent.class.getName());
    }

    private static int [] arrayGenerator(int length){
        Random randomNumber = new Random();            /* array generator para el insertion sort*/
        int [] array = new int[length];
        int number;
        for(int i = 0; i < length; i++){
            number = randomNumber.nextInt(1000);
            array[i] = number;
        }
        return array;
    }

    private static void insertionSort(int[] arr) {             /* insertion sort */
        for (int i = 1; i < arr.length; i++) {
            int valueToSort = arr[i];
            int j = i;
            while (j > 0 && arr[j - 1] > valueToSort) {
                arr[j] = arr[j - 1];
                j--;
            }
            arr[j] = valueToSort;
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");

        Bundle bundle = new Bundle();

        /* Service Started */
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        try {

            long start = System.nanoTime();

                for(int i = 0; i < 10000; i++) {                /* ciclo para probar eficiencia */
                    int[] array = arrayGenerator(100000);
                    insertionSort(array);
                    Log.d(TAG, Arrays.toString(array));
                }
            long end = System.nanoTime();

            long elapsedTime = end - start;
            String res = elapsedTime + " nano seconds\n" +
                    "lo cual es " + TimeUnit.NANOSECONDS.toSeconds(elapsedTime) + " segundos";

            bundle.putString("exectime",res);
        }
        catch (Error err)
        {
            bundle.putString(Intent.EXTRA_TEXT, err.getMessage());
            receiver.send(STATUS_ERROR, bundle);
            Log.d(TAG,"Error");
        }

        /* Status Finished */
        receiver.send(STATUS_FINISHED, bundle);
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }
}
