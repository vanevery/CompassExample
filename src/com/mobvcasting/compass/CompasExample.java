/*
 * Compass Example with help from:
 * http://www.codingforandroid.com/2011/01/using-orientation-sensors-simple.html
 */

package com.mobvcasting.compass;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;

public class CompasExample extends Activity implements SensorEventListener {

	static final String LOGTAG = "Compass";
	
	SensorManager sensorManager;
    SensorEventListener sensorListener;
    
    Sensor magnometerSensor;
    Sensor accelerometerSensor;
    
    AniView animationView;
    
    float[] accelerometerMatrix;
    float[] magnometerMatrix;
    
    float azimuth = 0.0f;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
        
        AniView animationView = new AniView(this);
        setContentView(animationView);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }
    
    public void onResume() {
        super.onResume();
        
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    
    public void onPause() {
    	super.onPause();
    	sensorManager.unregisterListener(this);
    }
    
    public void onSensorChanged(SensorEvent event) {
    	
    	if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) 
    	{
    		magnometerMatrix = event.values;
    	}
    	else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) 
    	{
    		accelerometerMatrix = event.values;
    	}
    	
    	
    	if (magnometerMatrix != null && accelerometerMatrix != null) {
    		float R[] = new float[9];
    		float I[] = new float[9];
    		
    		//https://developer.android.com/reference/android/hardware/SensorManager.html
    		boolean rotationMatrixSuccess = SensorManager.getRotationMatrix(R, I, accelerometerMatrix, magnometerMatrix);
    	    
    		if (rotationMatrixSuccess) {
    	        float orientation[] = new float[3];
    	        SensorManager.getOrientation(R, orientation);
    	        azimuth = orientation[0]; // orientation contains: azimuth (around z axis), pitch (around y axis) and roll
    	    }    		
    	}
    	
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    class AniView extends View {

        public AniView(Context context) {
            super(context);
        }
        
    	@Override
        protected void onDraw(Canvas canvas) {
    		Paint p = new Paint();
    		p.setColor(Color.BLUE);

    		int width = getWidth();
    		int height = getHeight();
    		int centerx = width/2;
    		int centery = height/2;
    		canvas.drawLine(centerx, 0, centerx, height, p);
    		canvas.drawLine(0, centery, width, centery, p);
  
    		// Rotate the canvas with the azimuth      
    		canvas.rotate(-azimuth*360/(float)(2*Math.PI), centerx, centery);

    		p.setColor(Color.RED);
    		canvas.drawLine(centerx, -1000, centerx, +1000, p);
    		canvas.drawLine(-1000, centery, 1000, centery, p);
    		
    		canvas.drawText("N", centerx+5, centery-10, p);
    		canvas.drawText("S", centerx-10, centery+15, p);
  		
            invalidate();
    	}
    }
}