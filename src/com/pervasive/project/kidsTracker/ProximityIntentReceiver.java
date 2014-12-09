package com.pervasive.project.kidsTracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ProximityIntentReceiver extends BroadcastReceiver {

	private Boolean isScheduleBased=false;
	private String address;

	@Override
	public void onReceive(Context context, Intent intent) {

		String x=intent.getStringExtra("idddd");
		Location loc=retrievelocationFromPreferences(x);
		if(loc!=null)
		{


			LocationManager locationManager = (LocationManager) context
					.getSystemService("location");

			Location l=locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);

			float distance = loc.distanceTo(l);
			Toast.makeText(context, 
					"Distance from Point:"+distance, Toast.LENGTH_LONG).show();


			//calculate the distance between current point and the actual point

			/*Double lat=Double.parseDouble(intent.getStringExtra("latitude"));
		Double longit=Double.parseDouble(intent.getStringExtra("longitude"));
		Boolean enter=Boolean.parseBoolean(intent.getStringExtra("entering"));
        String address=intent.getStringExtra("address");
			 */
			/*Bundle extras=intent.getExtras();
		String id=extras.getString("id");
		Double lat=Double.parseDouble(extras.getString("latitude"));
		Double longit=Double.parseDouble(extras.getString("longitude"));
		Boolean enter=Boolean.parseBoolean(extras.getString("entering"));
        String address=extras.getString("address");
			 */
			String key = LocationManager.KEY_PROXIMITY_ENTERING;

			Boolean entering = intent.getBooleanExtra(key, false);


			if (entering) {
				Log.d(getClass().getSimpleName(), "entering");
			}
			else {
				Log.d(getClass().getSimpleName(), "exiting");
				if(isScheduleBased && distance>804.672)//0.5 miles
				{
					Log.d("send msg","send sms for leaving the area");
					SendAlert sms=new SendAlert();
					sms.sendMsg("6024102574", "Child leaving the proximity of "+address);
				}

				if(!isScheduleBased && distance<804.672)
				{
					Log.d("send msg","send sms for entering the area");
					SendAlert sms=new SendAlert();
					sms.sendMsg("6024102574", "Child entering the proximity of "+address);
				}
			}
		}

	}

	private Location retrievelocationFromPreferences(String id) {
		SharedPreferences prefs = MainActivity.getContext().getSharedPreferences(
				"abc", 
				Context.MODE_WORLD_READABLE);
		Location location = new Location("POINT_LOCATION");
		String x=prefs.getString(id, "null");
		if(x.equals("null"))
		{
		
		}
		else
		{
			String[] params=x.split(";");
			location.setLatitude(Double.parseDouble(params[0]));
			location.setLongitude(Double.parseDouble(params[1]));
			isScheduleBased=!(Boolean.parseBoolean(params[2]));
			address=params[3];
			return location;
		
		}
		
		return null;
	}



}