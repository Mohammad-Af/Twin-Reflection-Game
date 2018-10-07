package com.example.lenovo.twinReflection;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

class Ball {

    private Point center;
    private int radius = StartActivity.screenDensity/5;
    private int color;
    private double speed;                //0  to 100+e
    private Point vector;
    private Paint paint;

    Ball(Point center) {
        this.center = center;
        paint = new Paint();
        int[] colors = {Color.GREEN, Color.RED, Color.BLUE};
        color = colors[(int)(3*Math.random())];
        paint.setColor(color);
    }


    boolean render(Canvas canvas) {
        boolean b = checkBounds(canvas.getHeight(), canvas.getWidth());
        canvas.drawCircle(center.x, center.y, radius, paint);

        return b ;
    }

    void makeFirstMove(){
        center.offset(vector.x, vector.y);
        hitBalls.clear();
    }

    private List<Ball>hitBalls = new ArrayList<>();
    void move(List<Ball> balls) {

        for (int i = balls.size() - 1; i > -1; i--) {
            Ball ball = balls.get(i);
            double dist = getDistanceOf(ball.center);
            if (dist <= 2 * radius && !ball.equals(this) && !ball.hitBalls.contains(this)) {

            //    Log.i("test","c1 "+hashCode()+ " x: "+center.x+" y: "+center.y+" "+ vector.x+" "+vector.y+ " c2 "+ball.hashCode()+" x: "+ball.center.x+" y: "+ball.center.y+" "+ball.vector.x+" "+ball.vector.y);

                hitBalls.add(ball);
                Point swap = vector;
                vector = ball.vector;
                ball.vector = swap;


                while ( getDistanceOf(ball.center) <= 2*radius){
                    Log.i("test","called");
                    center.offset(vector.x , vector.y);
                    ball.center.offset(ball.vector.x,ball.vector.y);
                }

                int red1 = Color.red(color);
                int green1 = Color.green(color);
                int blue1 = Color.blue(color);


                int red2 = Color.red(ball.color);
                int green2 = Color.green(ball.color);
                int blue2 = Color.blue(ball.color);

                changeColor(Color.rgb((red1 + red2)/4,(green1+green2)/4,(blue1+blue2)/4));

                ball.changeColor(Color.rgb(3*(red1 + red2)/4,3*(green1+green2)/4,3*(blue1+blue2)/4));

                break;
            }

        }
    }

    public void changeColor(int color) {
        this.color = color;
        paint.setColor(color);
    }

    private boolean checkBounds(int height, int width) {
        Point previousLocation = new Point(center.x, center.y);


        if (center.x < 0 && vector.x!=0) {
            if(Server.mood == PlayerMood.Host) {
                center.set(0, previousLocation.y + (previousLocation.x * vector.y / vector.x));
                vector.x = -vector.x;
                return true;
            }
            return false;
        }
        if (center.y < 0 && vector.y!=0) {
            center.set(previousLocation.x + (previousLocation.y * vector.x / vector.y), 0);
            vector.y = -vector.y;
        }
        if (center.x > width && vector.x!=0) {
            if(Server.mood == PlayerMood.Client) {
                center.set(width, previousLocation.y + ((width - previousLocation.x) * vector.y / vector.x));
                vector.x = -vector.x;
                return true;
            }
            return false;
        }
        if (center.y > height && vector.y!=0) {
            center.set(previousLocation.x + ((height - previousLocation.y) * vector.x / vector.y), height);
            vector.y = -vector.y;
        }

        return true;

    }


    void setVector(float deltaX, float deltaY) {
        vector = new Point((int) deltaX, (int) deltaY);
        speed = Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    }

    double getDistanceOf(Point p) {
        return Math.sqrt(Math.pow(center.x - p.x, 2) + Math.pow(center.y - p.y, 2));
    }

    public int getRadius() {
        return radius;
    }

    public Point getCenter() {
        return center;
    }

    public int getColor() {
        return color;
    }

    public Point getVector() {
        return vector;
    }

}


