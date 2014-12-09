package com.pervasive.project.kidsTracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class ProhibitedAreaReceiver extends BroadcastReceiver
{
	
	Boolean isProhibitedAreaBase=false;
	String address;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle b=intent.getExtras();
		String x=b.getString("xyz");
		//Location l=retrievelocationFromPreferences(x);
		String key = LocationManager.KEY_PROXIMITY_ENTERING;

		Boolean entering = intent.getBooleanExtra(key, false);

		if(entering)
		{
			Log.d("msdg","send an alert that prohibited area entered");
		}
		
	}
	
	
	private Location retrievelocationFromPreferences(String id) {
		SharedPreferences prefs = MainActivity.getContext().getSharedPreferences(
				"abc", 
				Context.MODE_WORLD_READABLE);
		Location location = new Location("POINT_LOCATION");
		String x=prefs.getString(id, "null");
		String[] params=x.split(";");
		location.setLatitude(Double.parseDouble(params[0]));
		location.setLongitude(Double.parseDouble(params[1]));
		isProhibitedAreaBase=(Boolean.parseBoolean(params[2]));
		address=params[3];
		return location;
	}

}
