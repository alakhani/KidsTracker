package com.pervasive.project.kidsTracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AlarmReciever extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{
		Bundle extras=intent.getExtras();
		String id=extras.getString("id");
		Double lat=Double.parseDouble(extras.getString("latitude"));
		Double longit=Double.parseDouble(extras.getString("longitude"));
		Boolean entering=Boolean.parseBoolean(extras.getString("entering"));
		String address=extras.getString("address");

		//String store=extras.getString("latitude")+";"+extras.getString("longitude")+";"+extras.getString("entering")+";"+extras.getString("address");
		//int ids=retrieveidFromPreferences("unique");
		//saveCoordinatesInPreferences(String.valueOf(ids),store);

		Log.d("id",id);
		Log.d("lat",extras.getString("latitude"));
		Log.d("longit",extras.getString("longitude"));
		Log.d("entering",extras.getString("entering"));
		Log.d("address",extras.getString("address"));

		AddFence fenceObj = new AddFence(MainActivity.getContext());
		fenceObj.addProximityAlert(lat, longit, entering, address);

		Toast.makeText(context, "Fired for "+address, Toast.LENGTH_LONG).show();
	}

	/*private int retrieveidFromPreferences(String id) {
		SharedPreferences prefs = MainActivity.getContext().getSharedPreferences(
				"abc", 
				Context.MODE_WORLD_READABLE);
		String val=prefs.getString(id, "null");
		int v=Integer.parseInt(val);
		return v;
	}*/


	public void saveCoordinatesInPreferences(String id,String val) {

		SharedPreferences prefs = MainActivity.getContext().getSharedPreferences(
				"abc", 
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(id, val);

		prefsEditor.commit();
	}

}
