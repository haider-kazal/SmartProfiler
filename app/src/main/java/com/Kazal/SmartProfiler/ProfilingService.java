package com.Kazal.SmartProfiler;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 */

/**
 * @author HaiderAli
 *
 */
public class ProfilingService extends Service implements SensorEventListener {

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Destroy", Toast.LENGTH_SHORT).show();
		super.onDestroy();
		sensorManager.unregisterListener(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Toast.makeText(getBaseContext(), "Start", Toast.LENGTH_SHORT).show();
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);		
		if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null)
		{
			accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
		}
		else
		{
			// Sensor Not Availiable
		}
		if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null)
		{
			lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
			sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
		}			
		else
		{
			// Sensor Not Availiable
		}
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);		
		return START_STICKY;
	}

	/* (non-Javadoc)
	 * @see android.hardware.SensorEventListener#onAccuracyChanged(android.hardware.Sensor, int)
	 */
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
	 */
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			float[] g = arg0.values.clone();
			float normOfG = (float) Math.sqrt(g[0] * g[0] + g[1] * g[1] + g[2] * g[2]);

			// Normalize the accelerometer vector
			g[0] = g[0] / normOfG;
			g[1] = g[1] / normOfG;
			g[2] = g[2] / normOfG;
			int inclination = (int) Math.round(Math.toDegrees(Math.acos(g[2])));
			if (inclination < 45 || inclination > 135)
			{			
			    // device is flat
				if(isDarkEnvironment)
				{					
					StateChange(3);
				}
				else
				{					
					StateChange(1);
				}
			}
			else
			{
			    // device is not flat
				if(isDarkEnvironment && !isVibrating)
				{
					StateChange(1);
					StateChange(2);										
				}
			}
			
		}
		else if(arg0.sensor.getType() == Sensor.TYPE_LIGHT)
		{
			float[] sensorValue = arg0.values.clone();
			if(sensorValue[0] == 0)
			{
				isDarkEnvironment = true;				
			}
			else
			{
				isDarkEnvironment = false;				
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void StateChange(int newState)
	{
		if(state == newState)
		{
			return;
		}
		else
		{
			state = newState;
			switch(state)
			{
			case 1:
				audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				isVibrating = false;
				Log.d("Condition", "Loud Sound");				
				break;
			case 2:
				audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				isVibrating = true;
				Log.d("Condition", "Vibration");

				break;
			case 3:
				audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				isVibrating = false;
				Log.d("Condition", "Silent");
				break;
			}
		}
	}
	
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private Sensor lightSensor;
	private boolean isDarkEnvironment = false;
	private boolean isVibrating;
	private int state;
	
	private AudioManager audioManager;
}
