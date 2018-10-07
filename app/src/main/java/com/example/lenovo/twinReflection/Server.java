package com.example.lenovo.twinReflection;

import android.graphics.Point;
import android.util.Log;

import java.io.PrintWriter;
import java.util.Scanner;

class Server {

    private MainActivity startActivity;
    private PrintWriter printWriter;
    private Scanner scanner;
    private MainView mainView;
    static PlayerMood mood;

    Server(MainActivity startActivity) {
        this.startActivity = startActivity;
        printWriter = StartActivity.printWriter;
        scanner = StartActivity.scanner;

        startTransfer();
    }


    private void startTransfer()  {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (scanner != null) {
                    //  int x = scanner.nextInt();
                    double y = scanner.nextDouble();
                    int color = scanner.nextInt();
                    int xVector = scanner.nextInt();
                    int yVector = scanner.nextInt();

                    y *= StartActivity.screenHeight;

                    Ball ball = new Ball(new Point(0, (int) y));
                    ball.setVector(xVector, yVector);
                    ball.changeColor(color);
                    mainView.addBall(ball);

                    Log.i("test", "received " + 0 + " " + y + " " + " " + xVector + " " + yVector + " " + String.valueOf(ball.getColor()));
                }

            }
        });
        thread.start();

    }

    public void sendBall(final Ball ball) {

        Log.i("test", "want to send " + ball.getCenter().x + " " + ball.getCenter().y + " " + ball.getColor() + " " + ball.getVector().x + " " + ball.getVector().y);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //  printWriter.println(ball.getCenter().x);
                printWriter.println(ball.getCenter().y / (1.0*StartActivity.screenHeight));
                printWriter.println(ball.getColor());
                printWriter.println(ball.getVector().x);
                printWriter.println(ball.getVector().y);
                Log.i("test", "sent ");
            }
        });
        thread.start();

    }

    public void setView(MainView view) {
        this.mainView = view;
    }


}
