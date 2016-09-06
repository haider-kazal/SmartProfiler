/**
 * 
 */
package com.Kazal.SmartProfiler;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author HaiderAli
 *
 */
public class CallListener extends PhoneStateListener implements	SensorEventListener {

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
		float[] rotationMatrix = new float[16];
		SensorManager.getRotationMatrixFromVector(rotationMatrix, arg0.values);
		determineOrientation(rotationMatrix);		
	}
	
	private void determineOrientation(float[] rotationMatrix) {
		// TODO Auto-generated method stub
		float[] orientationValues = new float[3];
		SensorManager.getOrientation(rotationMatrix, orientationValues);
		double pitch = Math.toDegrees(orientationValues[1]);
		double roll = Math.toDegrees(orientationValues[2]);
		if(pitch <= 10)
		{
			if(Math.abs(roll) >= 170)
			{
				//Log.d("Sensor", "Face Down");
				if(isFaceUp && isRinging)
				{
					//rejectCall();
					isFaceUp = false;
					Log.d("Call", "Rejected");
										
					previousAudioState = audioManager.getRingerMode();
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					
					//Toast.makeText(this, "Call Rejected", Toast.LENGTH_SHORT).show();
				}
			}
			else if(Math.abs(roll) <= 10)
			{
				//Log.d("Sensor", "Face Up");
				isFaceUp = true;
			}
		}
		if(!isRinging)
		{
			sensorManager.unregisterListener(this, rotationVectorSensor);
			Log.d("Sensor", "Unregistered");
		}
	}
	
	public static void registerService(Context newContext)
	{	
		if(callListener != null)
		{
			unregisterService();
		}
		context = newContext;		
		callListener = new CallListener();
		((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}
		
	public static void unregisterService()
	{
		((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).listen(callListener, PhoneStateListener.LISTEN_NONE);
	}
	
	@Override
	public void onCallStateChanged(int state, String incomingNumber) {				        
		switch (state) {
		//Hangup
		case TelephonyManager.CALL_STATE_IDLE:
			Log.d("Phone", "Idle");
			audioManager.setRingerMode(previousAudioState);
			isRinging = false;
			break;
		//Incoming	
		case TelephonyManager.CALL_STATE_RINGING:
			Log.d("Phone Call", "Ringing");			
			sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);		
			if(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null)
			{
				rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
				sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI);
			}
			else
			{
				// Sensor Not Availiable
			}	
			Log.d("Sensor", "Registered");
			isRinging = true;
		}
	}
	
	private SensorManager sensorManager;
	private Sensor rotationVectorSensor;
	private boolean isFaceUp = false;
	private boolean isRinging = false;
	private static AudioManager audioManager;
	private static CallListener callListener;
	private static Context context;
	private int previousAudioState;	
}
