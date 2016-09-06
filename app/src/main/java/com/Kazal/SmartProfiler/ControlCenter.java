package com.Kazal.SmartProfiler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class ControlCenter extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_control_center);				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.control_center, menu);
		return true;
	}
	public void startMonitor(View view)
	{
		try
		{
			startService(new Intent(getBaseContext(), ProfilingService.class));
		}
		catch(Exception e)
		{
			Toast.makeText(this, "Profiling Service Initiate Error", Toast.LENGTH_SHORT).show();
		}
		try
		{
			CallListener.registerService(getBaseContext());
		}
		catch(Exception e)
		{
			Toast.makeText(this, "Call Listener Initiate Error, shutting down all service", Toast.LENGTH_SHORT).show();
			stopService(new Intent(getBaseContext(), ProfilingService.class));
		}		
	}
	
	public void stopMonitor(View view)
	{
		try
		{
			stopService(new Intent(getBaseContext(), ProfilingService.class));
		}
		catch(Exception e)
		{
			Toast.makeText(this, "Profiling Service Stop Error", Toast.LENGTH_SHORT).show();
		}
		try
		{
			CallListener.unregisterService();
		}
		catch(Exception e)
		{
			Toast.makeText(this, "Call Listener Stop Error, sensors stopped", Toast.LENGTH_SHORT).show();
			stopService(new Intent(getBaseContext(), ProfilingService.class));
		}				
	}
}
