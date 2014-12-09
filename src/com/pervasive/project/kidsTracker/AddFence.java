package com.pervasive.project.kidsTracker;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.GregorianCalendar;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewDebug.FlagToString;
import android.widget.Toast;

public class AddFence 
{

	private Context context;

	DBAdapter myDb;

	private static final long MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATE = 1000; // in Milliseconds

	private static final long POINT_RADIUS = 1000; // in Meters
	private static final long PROX_ALERT_EXPIRATION = -1; 

	private static final String POINT_LATITUDE_KEY = "POINT_LATITUDE_KEY";
	private static final String POINT_LONGITUDE_KEY = "POINT_LONGITUDE_KEY";

	private static final String PROX_ALERT_INTENT = 
			"com.javacodegeeks.android.lbs.ProximityAlert";

	private static final String PROHIBITED_ALERT_INTENT = 
			"com.pervasive.prohibitedAreaAlert";


	private static final NumberFormat nf = new DecimalFormat("##.########");

	private LocationManager locationManager;

	public AddFence(Context c) {
		this.context = c;
		this.locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.
				GPS_PROVIDER,
				MINIMUM_TIME_BETWEEN_UPDATE,
				MINIMUM_DISTANCECHANGE_FOR_UPDATE,
				new MyLocationListener()
				);
		retrieveidFromPreferences("unique");
	}

	private int retrieveidFromPreferences(String id) {
		SharedPreferences prefs = MainActivity.getContext().getSharedPreferences(
				"abc", 
				Context.MODE_WORLD_READABLE);
		String val=prefs.getString(id, "null");
		SharedPreferences.Editor prefsEditor = prefs.edit();
		if(val.equals("null"))
		{
			prefsEditor.putString(id, "1");
			prefsEditor.commit();
			return 1;
		}
		
		int v=Integer.parseInt(val);
		v++;
		prefsEditor.putString(id, String.valueOf(v));
		prefsEditor.commit();
		
		return v;
	}

	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			/*Location pointLocation = retrievelocationFromPreferences();
			float distance = location.distanceTo(pointLocation);
			Toast.makeText(context, 
					"Distance from Point:"+distance, Toast.LENGTH_LONG).show();*/
		}
		public void onStatusChanged(String s, int i, Bundle b) {            
		}
		public void onProviderDisabled(String s) {
		}
		public void onProviderEnabled(String s) {            
		}

	}


	private void openDB() {
		myDb = new DBAdapter(context);
		myDb.open();
	}

	private void closeDB() {
		myDb.close();
	}


	public void saveDataInDatabase(String address,double latitude,double longitude,int child_id, String schedDate, String startTime,String endTime)
	{
		openDB();
		//insert the data in the co-ordinates table
		long newCordsId=insertDataInCords(address, latitude, longitude);
		if(newCordsId!=-1)
		{
			insertRowsInSchedTable(1, schedDate, startTime, endTime, (int)(newCordsId));
		}

	}

	public long insertDataInCords(String address,double latitude,double longitude)
	{
		long newCordsId = myDb.insertRowInCordsTable(address, latitude, longitude);
		return newCordsId;
	}

	public void insertRowsInSchedTable(int child_id, String schedDate, String startTime,String endTime,int loc_fk)
	{
		long newSchedId=myDb.insertRowInScheduleTable(child_id, schedDate, startTime, endTime, loc_fk);
		Log.d("schedId","");
	}
	/*public void saveCoordinatesInPreferences(float latitude, float longitude) {

		SharedPreferences prefs = context.getSharedPreferences(
				getClass().getSimpleName(), 
				Context.MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putFloat(POINT_LATITUDE_KEY, latitude);
		prefsEditor.putFloat(POINT_LONGITUDE_KEY, longitude);
		prefsEditor.commit();
	}*/

	/*private Location retrievelocationFromPreferences() {
		SharedPreferences prefs = context.getSharedPreferences(
				getClass().getSimpleName(), 
				Context.MODE_PRIVATE);
		Location location = new Location("POINT_LOCATION");
		location.setLatitude(prefs.getFloat(POINT_LATITUDE_KEY, 0));
		location.setLongitude(prefs.getFloat(POINT_LONGITUDE_KEY, 0));
		return location;
	}*/

	/*public void saveProximityAlertPoint() {
		Location location = 
				locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location==null) {
			Toast.makeText(context, "No last known location. Aborting...", 
					Toast.LENGTH_LONG).show();
			return;
		}
		saveCoordinatesInPreferences((float)location.getLatitude(),
				(float)location.getLongitude());
		addProximityAlert(location.getLatitude(), location.getLongitude(),false,);
	}*/

	public void saveCoordinatesInPreferences(String id,String val) {

		SharedPreferences prefs = MainActivity.getContext().getSharedPreferences(
				"abc", 
				Context.MODE_WORLD_READABLE);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(id, val);

		prefsEditor.commit();
	}

	/*public void addProhibitedAlert(double latitude, double longitude,boolean entering, String location)
	{
		String val= String.valueOf(latitude)+";"+String.valueOf(longitude)+";"+String.valueOf(entering)+";"+location;
		saveCoordinatesInPreferences("5678", val);
		Intent intent = new Intent(PROHIBITED_ALERT_INTENT);

		Bundle b=new Bundle();
		b.putString("xyz", "999");
		intent.putExtras(b);

		PendingIntent proximityIntent = PendingIntent.getBroadcast(context, 1001, intent, 0);

		locationManager.addProximityAlert(
				latitude, // the latitude of the central point of the alert region
				longitude, // the longitude of the central point of the alert region
				POINT_RADIUS, // the radius of the central point of the alert region, in meters
				PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration 
				proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
				);

		IntentFilter filter = new IntentFilter(PROHIBITED_ALERT_INTENT);
		context.registerReceiver(new ProhibitedAreaReceiver(), filter);


	}*/


	public void addProximityAlert(double latitude, double longitude,boolean entering, String location) {

		int id=retrieveidFromPreferences("unique");
		id++;

		String val= String.valueOf(latitude)+";"+String.valueOf(longitude)+";"+String.valueOf(entering)+";"+location;

		saveCoordinatesInPreferences(String.valueOf(id), val);
		Intent intent = new Intent(PROX_ALERT_INTENT);
		//Intent intent = new Intent(this.context,ProximityIntentReceiver.class);
		Bundle b=new Bundle();
		b.putString("idddd", String.valueOf(id));
		b.putString("latitude", String.valueOf(latitude));
		b.putString("longitude", String.valueOf(longitude));
		b.putString("entering", String.valueOf(entering));
		b.putString("address", location);
		intent.putExtras(b);

		PendingIntent proximityIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		locationManager.addProximityAlert(
				latitude, // the latitude of the central point of the alert region
				longitude, // the longitude of the central point of the alert region
				POINT_RADIUS, // the radius of the central point of the alert region, in meters
				PROX_ALERT_EXPIRATION, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration 
				proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
				);

		IntentFilter filter = new IntentFilter(PROX_ALERT_INTENT);
		context.registerReceiver(new ProximityIntentReceiver(), filter);

	}

}
