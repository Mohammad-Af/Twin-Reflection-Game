package com.example.lenovo.twinReflection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class StartActivity extends AppCompatActivity {


    static int screenWidth;
    static int screenHeight;
    static int screenDensity;

    private TextView state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        state = (TextView) findViewById(R.id.state_text);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenDensity = dm.densityDpi;

        Log.i("test", "height  " + screenHeight + " density " + screenDensity + " mm " + screenHeight*25.4 / (screenDensity*1.0));

    }



    public void onListener(View view) {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected void onPreExecute() {
                findViewById(R.id.state_text).setVisibility(View.VISIBLE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }

            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    init();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            private void init() throws IOException {
                ConnectionListener connectionInfoListener = new ConnectionListener(StartActivity.this);
                WifiP2pManager p2pManager = (WifiP2pManager) StartActivity.this.getSystemService(Context.WIFI_P2P_SERVICE);  //this provides the API for managing wifi p2p connectivity
                WifiP2pManager.Channel mChannel = p2pManager.initialize(StartActivity.this, StartActivity.this.getMainLooper(), null);    //channel connects the application to wifi p2p framework
                p2pManager.requestConnectionInfo(mChannel, connectionInfoListener);

            }

            @Override
            protected void onPostExecute(Object o) {
                // state.setText("connected");
            }
        };

        asyncTask.execute();

    }

    static Socket partner;
    static PrintWriter printWriter;
    static Scanner scanner;
    static double partnerScreenHeight;

    public void start(PrintWriter printWriter, Scanner scanner, Socket socket) {
        partner = socket;
        StartActivity.printWriter = printWriter;
        StartActivity.scanner = scanner;

//        printWriter.println((screenHeight*25.4) / (screenDensity*1.0));
//        partnerScreenHeight = scanner.nextDouble();

        Log.i("test", String.valueOf(partnerScreenHeight));

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
