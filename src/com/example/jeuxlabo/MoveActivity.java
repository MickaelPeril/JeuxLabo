package com.example.jeuxlabo;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;

public class MoveActivity extends Activity implements SensorEventListener {
	
	protected PowerManager.WakeLock vWakeLock;

 
	// UI Variable
    BounceView mBounceView = null; // activity view
	
    SensorManager sensorManager; 
	private float[] mMagneticValues;
	private float[] mAccelerometerValues;
	
	private float mAzimuth;
	private float mPitch;
	private float mRoll; 

    /** Called when the activity is first created. */ 
    @SuppressWarnings("deprecation")
    
	@Override 
    public void onCreate(Bundle icicle) 
    { 
         super.onCreate(icicle); 
         Log.v("MoveActivity","onCreate");
         
         // Set fullscreen          
         requestWindowFeature(Window.FEATURE_NO_TITLE); 

         // Loading Activity View
         mBounceView = new BounceView(this); 
         setContentView(this.mBounceView); 
         
         sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE); 
          
         // L'application bloque la mise en veille (voir onDestroy pour la relacher)
         final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         vWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "BeerGame");
         vWakeLock.acquire();
    }

    @Override
    protected void onResume()
    {     
      super.onResume(); 
      Log.v("MoveActivity","onResume");
      
      // on enregistre l'activité comme Listener pour les 2 types de capteurs
      sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),  SensorManager.SENSOR_DELAY_UI);
      sensorManager.registerListener(this,  sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
     
    } 
    
    @Override
    protected void onStop() 
    { 
        sensorManager.unregisterListener(this); 
      super.onStop(); 
    } 
    
    // surchage de onDestroy qui est appelé quand on quitte l'application
    // ici on relache l'interdiction de mise en veille
    @Override
    public void onDestroy() 
    {
    	// on remet la veille ecran comme avant
        vWakeLock.release();
        super.onDestroy();
    }

   // Implementation of SensorEventListener functions

    public void onAccuracyChanged(Sensor sensor, int accuracy) 
    { 
    
    } 

    public void onSensorChanged(SensorEvent event)
    {   
    	 switch (event.sensor.getType()) {
	         case Sensor.TYPE_MAGNETIC_FIELD:
	           mMagneticValues = event.values.clone();
	           break;
	         case Sensor.TYPE_ACCELEROMETER:
	           mAccelerometerValues = event.values.clone();
	           break;
	     }

         if (mMagneticValues != null && mAccelerometerValues != null) {
           float[] R = new float[9];
           SensorManager.getRotationMatrix(R, null, mAccelerometerValues, mMagneticValues);
           float[] orientation = new float[3];
           SensorManager.getOrientation(R, orientation);
           // http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[], float[], float[], float[])
           // Angle en Radians
           mAzimuth = orientation[0]; // rotation selon l'axe Z
         
           mPitch = orientation[1];   // rotation selon l'axe X
           mRoll = orientation[2];    // rotation selon l'axe Y
           
           mBounceView.setTrayAngle(Math.toDegrees(mPitch), Math.toDegrees(mRoll));
           
         //  Log.v("onSensorChanged","mAzimuth:"+ mAzimuth+" mPitch:"+mPitch+" mRoll:"+mRoll);
          // Log.v("onSensorChanged","mAzimuth:"+ Math.toDegrees(mAzimuth)+" mPitch:"+Math.toDegrees(mPitch)+" mRoll:"+Math.toDegrees(mRoll));
         }
       }


}