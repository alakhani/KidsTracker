package com.pervasive.project.kidsTracker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class TrackLocation implements LocationListener {

	private Context cont;
	private LocationManager lm;

	private String addr;

	private static String physAddr;
	
	public TrackLocation(Context c) {
		this.cont = c;
	}

	public String initLocationManager() {
		lm = (LocationManager) cont.getSystemService(Context.LOCATION_SERVICE);

		// Creating an empty criteria object
		Criteria criteria = new Criteria();

		// Get name of the provider in this case which would be gps
		String provider = lm.getBestProvider(criteria, false);

		if(provider!=null && !provider.equals(""))
		{ 
			// Get the location
			Location location = lm.getLastKnownLocation(provider);

			if(location != null) {
				double lat = location.getLatitude();
				double longit = location.getLongitude();
				getAddress(lat, longit);
				return addr;
			}
			else
			{
				Toast.makeText(cont, "Location can't be retrieved", Toast.LENGTH_SHORT).show();
			}

		}
		else
		{
			//create a toast indicating error.
			Toast.makeText(cont, "OOPS something went wrong", Toast.LENGTH_SHORT).show();

		}
		
		return "";
	}
	
	public String getCurrentLocation()
	{
		LocationManager locationManager = (LocationManager) MainActivity.getContext()
				.getSystemService("location");

		Location l=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		Geocoder geocoder = new Geocoder(MainActivity.getContext(), Locale.getDefault());
		//getAddress(l.getLatitude(), l.getLongitude());
		String result = null;
		try {
			List<Address> addressList = geocoder.getFromLocation(
					l.getLatitude(), l.getLongitude(), 1);
			if (addressList != null && addressList.size() > 0) {
				Address address = addressList.get(0);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
					sb.append(address.getAddressLine(i)).append("\n");
				}
				sb.append(address.getLocality()).append("\n");
				//sb.append(address.getPostalCode()).append("\n");
				//sb.append(address.getCountryName());
				result = sb.toString();
			}
		} catch (IOException e) {
			Log.e("Error", "Unable connect to Geocoder", e);
		}
		
		return result.toString();

	}

	private void getAddress(double lat, double longit) {
		// TODO Auto-generated method stub
		//LocationAddress locationAddress = new LocationAddress();
		LocationAddress.getAddressFromLocation(lat, longit, 
                cont, new GeocoderHandler());
//        Log.d("address", addr);
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		physAddr = this.initLocationManager();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}
	
	private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            addr = locationAddress;
        }
    }

}