package com.mvw.wallpaper;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class WallpaperTestPainting extends Thread implements SensorEventListener {

        /** Reference to the View and the context */
        private SurfaceHolder surfaceHolder;
        //private Context context;
        
        /** State */
        private boolean wait;
        private boolean run;
        
        /** Dimensions */
        private int width;
        private int height;
        private int radius;
        
        /** Touch points */
        private List<TouchPoint> points;
        
        /** Time tracking */
        private long previousTime;
        
        SensorManager sensorManager;
        Sensor accelSensor;
        
        Bitmap logo;
        
        float xAccel = 0;
        float yAccel = 0;
        float zAccel = 0;
        
        public WallpaperTestPainting(SurfaceHolder surfaceHolder, Context context, int radius) {
                // keep a reference of the context and the surface
                // the context is needed is you want to inflate
                // some resources from your livewallpaper .apk
                this.surfaceHolder = surfaceHolder;
                //this.context = context;
                // don't animate until surface is created and displayed
                this.wait = true;
                // initialize touch paints
                this.points = new ArrayList<TouchPoint>();
                // initialize radius
                this.radius = radius;
                BitmapDrawable bitDraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.vizio_logo);
                this.logo = bitDraw.getBitmap();
                sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
                final List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
                if (sensors.size() > 0) {
                	accelSensor = sensors.get(0);
                }
                if (accelSensor != null) {
                	sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_UI);
                }
        }
        
        /**
         * Change the radius preference
         */
        public void setRadius(int radius) {
                this.radius = radius;
        }

        /**
         * Pauses the livewallpaper animation
         */
        public void pausePainting() {
                this.wait = true;
                synchronized(this) {
                        this.notify();
                }
                if (accelSensor != null) {
                	sensorManager.unregisterListener(this,accelSensor);
                }
        }

        /**
         * Resume the livewallpaper animation
         */
        public void resumePainting() {
                this.wait = false;
                synchronized(this) {
                        this.notify();
                }
                if (accelSensor != null) {
                	sensorManager.registerListener(this,accelSensor,SensorManager.SENSOR_DELAY_UI);
                }
        }

        /**
         * Stop the livewallpaper animation
         */
        public void stopPainting() {
                this.run = false;
                synchronized(this) {
                        this.notify();
                }
                if (accelSensor != null) {                	
                	sensorManager.unregisterListener(this,accelSensor);
                }
        }

        @Override
        public void run() {
                this.run = true;
                Canvas c = null;
                while (run) {
                        try {
                                c = this.surfaceHolder.lockCanvas(null);
                                synchronized (this.surfaceHolder) {
                                        doDraw(c);
                                }
                        } finally {
                                if (c != null) {
                                        this.surfaceHolder.unlockCanvasAndPost(c);
                                }
                        }
                        // pause if no need to animate
                        synchronized (this) {
                                if (wait) {
                                        try {
                                                wait();
                                        } catch (Exception e) {}
                                }
                        }
                }
        }

        /**
         * Invoke when the surface dimension change
         * 
         * @param width
         * @param height
         */
        public void setSurfaceSize(int width, int height) {
                this.width = width;
                this.height = height;
                synchronized(this) {
                        this.notify();
                }
        }
        
        /**
         * Invoke while the screen is touched
         * 
         * @param event
         */
        public void doTouchEvent(MotionEvent event) {
                synchronized(this.points) {
                        points.add(new TouchPoint(
                                        (int) event.getX(), 
                                        (int) event.getY(),
                                        0xDDE22E31, 
                                        Math.min(width, height) / this.radius));
                }
                this.wait = false;
                synchronized(this) {
                        notify();
                }
        }

        /**
         * Do the actual drawing stuff
         * 
         * @param canvas
         */
        private void doDraw(Canvas canvas) {
                long currentTime = System.currentTimeMillis();
                long elapsed = currentTime - previousTime;
                if (elapsed > 20) {
                        // clear background
                        canvas.drawColor(Color.rgb(0x08, 0x06, 0x06));               
                        Paint paint2 = new Paint();
                       // float[] f = {0,1,1};
                        //EmbossMaskFilter embossFilter = new EmbossMaskFilter(f,.5f,8,3);
                        //paint2.setAlpha(proximity);
                        paint2.setAlpha((int)Math.abs(100-(7*zAccel)));
                        canvas.drawBitmap(logo, (this.width/2)-100+(-10*xAccel),(this.height/2)-140+(15*yAccel), paint2);
                        // draw touch points
                        Paint paint = new Paint();
                        List<TouchPoint> pointsToRemove = new ArrayList<TouchPoint>();
                        synchronized(this.points) {
                                for (TouchPoint point : points) {
                                        paint.setColor(point.color);
                                        point.radius -= elapsed / 20;
                                        if (point.radius <= 0) {
                                                pointsToRemove.add(point);
                                        } else {
                                                canvas.drawCircle(point.x, point.y, point.radius, paint);
                                        }
                                }
                                points.removeAll(pointsToRemove);
                        }
                        previousTime = currentTime;
                        if (points.size() == 0) {
                                wait = true;
                        }
                }
        }
        
        class TouchPoint {
                
                int x;
                int y;
                int color;
                int radius;
                
                public TouchPoint(int x, int y, int color, int radius) {
                        this.x = x;
                        this.y = y;
                        this.radius = radius;
                        this.color = color;
                }
                
        }

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			xAccel = event.values[0];
			yAccel = event.values[1];
			zAccel = event.values[2]-9.81f;
			synchronized(this) {
                this.notify();
			}
		}
}
