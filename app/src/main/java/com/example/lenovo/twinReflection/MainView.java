package com.example.lenovo.twinReflection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

public class MainView extends View {


    private Server server;

    public MainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        server = ((MainActivity) context).getServer();

        server.setView(this);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(10);
                        makeFirstMoves();
                        moveBalls();
                        postInvalidate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event);
        }
        return true;
    }

    private void touchEnded(MotionEvent event) {
        int pointerID = event.getPointerId(event.getActionIndex());
        int pointerIndex = event.findPointerIndex(pointerID);

        int newX = (int) event.getX(pointerIndex);
        int newY = (int) event.getY(pointerIndex);

        Point point = startPointMap.get(pointerID);

        float deltaX = (newX - point.x) / 10;
        float deltaY = (newY - point.y) / 10;

        if (!ballInLocation(point.x, point.y)) {
            Ball ball = new Ball(new Point(point.x, point.y));
            ball.setVector(deltaX, deltaY);
            balls.add(ball);
        }


        if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
            point.x = newX;
            point.y = newY;
        }

    }

    private boolean ballInLocation(int x, int y) {
        for (int i = balls.size() - 1; i > -1; i--) {
            if (balls.get(i).getDistanceOf(new Point(x, y)) <= 2 * balls.get(i).getRadius())
                return true;
        }
        return false;
    }


    private Map<Integer, Point> startPointMap = new HashMap<>();
    private static final int TOUCH_TOLERANCE = 20;
    private final List<Ball> balls = new ArrayList<>();


    private void touchStarted(float x, float y, int pointerId) {

        Point point;

        if (startPointMap.containsKey(pointerId)) {
            point = startPointMap.get(pointerId);
        } else {
            point = new Point();
            startPointMap.put(pointerId, point);
        }
        point.x = (int) x;
        point.y = (int) y;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (balls) {
            for (int i = balls.size() - 1; i > -1; i--) {
                if (!balls.get(i).render(canvas)) {
                    sendBall(balls.get(i));
                }
            }
        }
    }

    private void sendBall(Ball ball) {
        synchronized (balls) {
            balls.remove(ball);
            server.sendBall(ball);
        }
    }

    private void moveBalls() {
        synchronized (balls) {
            for (int i = balls.size() - 1; i > -1; i--) {
                balls.get(i).move(balls);
            }
        }
    }

    private void makeFirstMoves() {
        synchronized (balls) {
            for (int i = balls.size() - 1; i > -1; i--) {
                balls.get(i).makeFirstMove();
            }
        }
    }

    public void addBall(Ball ball) {

        if (Server.mood == PlayerMood.Client) {
            ball.getCenter().x = 0;
        } else {
            ball.getCenter().x = StartActivity.screenWidth;
        }

        balls.add(ball);
    }
}
