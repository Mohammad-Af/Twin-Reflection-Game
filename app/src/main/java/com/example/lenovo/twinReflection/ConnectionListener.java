package com.example.lenovo.twinReflection;


import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionListener implements WifiP2pManager.ConnectionInfoListener {

    private Socket socket;
    private InetAddress groupAddress;
    private StartActivity startActivity;

    public ConnectionListener(StartActivity startActivity) {
        this.startActivity = startActivity;
    }


    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        groupAddress = info.groupOwnerAddress;

        if (info.groupFormed && info.isGroupOwner) {
            createServer();
        } else if (info.groupFormed) {
            createClient();
        }
    }

    private void createClient() {

        Server.mood = PlayerMood.Client;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("test", "trying to create client");
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(groupAddress.getHostAddress(), 9090), 100);
                    createIO();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void createServer() {

        Server.mood = PlayerMood.Host;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("test", "trying to create sever");
                    ServerSocket server = new ServerSocket(9090);
                    socket = server.accept();
                    createIO();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void createIO() throws IOException {
        Log.i("test", "createIO called");

        if (socket == null){
            if(Server.mood == PlayerMood.Client)
                createClient();
            else
                createServer();
        }

        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        Scanner scanner = new Scanner(socket.getInputStream());

        Log.i("test", "IO set");

        startActivity.start(printWriter,scanner,socket);
    }

}
