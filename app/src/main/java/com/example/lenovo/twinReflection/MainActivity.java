package com.example.lenovo.twinReflection;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Server server ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        server = new Server(this);

        setContentView(R.layout.activity_main);
    }

    public Server getServer() {
        return server;
    }
}
