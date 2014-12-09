package com.pervasive.project.kidsTracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will display the three primary sections of the app, one at a
	 * time.
	 */
	ViewPager mViewPager;

	private static EditText fordate, startTime, endTime, location;	// local instances to items in "Enter Schedule" tab
	private static EditText prohibArea;
	//	private static String physAddr;
	private static TextView physAddr_tv;
	private int id=0;
	private static Context mainCon;
	private Boolean done=false;

	public static void setContext(Context c)
	{
		mainCon=c;
	}

	public static Context getContext()
	{
		return mainCon;
	}

	public void clearSharedData()
	{
		if(!done)
		{

			SharedPreferences prefs = MainActivity.getContext().getSharedPreferences(
					"abc", 
					Context.MODE_WORLD_READABLE);
			SharedPreferences.Editor prefsEditor = prefs.edit();
			prefsEditor.clear();
			prefsEditor.commit();
			done=true;
		}

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setContext(this.getApplicationContext());
		//clearSharedData();
		// Create the adapter that will return a fragment for each of the three primary sections
		// of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();

		// Specify that the Home/Up button should not be enabled, since there is no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);

		// Specify that we will be displaying tabs in the action bar.
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager, attaching the adapter and setting up a listener for when the
		// user swipes between sections.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// When swiping between different app sections, select the corresponding tab.
				// We can also use ActionBar.Tab#select() to do this if we have a reference to the
				// Tab.
				actionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by the adapter.
			// Also specify this Activity object, which implements the TabListener interface, as the
			// listener for when this tab is selected.
			actionBar.addTab(
					actionBar.newTab()
					.setText(mAppSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			switch (i) {
			case 0:
				// The first section of the app is the most interesting -- it offers
				// a launchpad into the other demonstrations in this example application.
				return new LaunchpadSectionFragment();

			case 1:
				return new ProhibitedTab();

			case 2:
				return new CurrLocFragment(); 

			default:
				// The other sections of the app are dummy placeholders.
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
				fragment.setArguments(args);
				return fragment;
			}
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int num) {
			switch (num) {
			case 0:
				return "Enter Schedule";
			case 1:
				return "Prohibited Areas";
			case 2:
				return "Get Current Location";
			default:
				return "Anonymous";
			}
		}
	}

	/**
	 * A fragment that launches other parts of the demo application.
	 */
	public static class LaunchpadSectionFragment extends Fragment {

		private int uid=0;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

			// get local references to all EditText variables
			fordate = (EditText) rootView.findViewById(R.id.selDate);
			startTime = (EditText) rootView.findViewById(R.id.startTime);
			endTime = (EditText) rootView.findViewById(R.id.endTime);
			location = (EditText) rootView.findViewById(R.id.loc);

			rootView.findViewById(R.id.submitBtn)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String dateStr = fordate.getText().toString();
					String startTimeStr = startTime.getText().toString();
					String endTimeStr = endTime.getText().toString();
					String locationStr = location.getText().toString();
					Log.d("startDate", dateStr);

					Date time2=null;
					//format the date of the form 12/04/2014 13:52:00
					try
					{
						dateStr+=" "+startTimeStr;
						SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
						time2=format.parse(dateStr);
					}
					catch(Exception e)
					{
						String esd = e.getMessage();
						Log.d("error", e.getMessage());
					}


					Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());

					List<Address> address;

					try {
						address=geocoder.getFromLocationName(locationStr, 5);

						Address loc=address.get(0);

						double lat=loc.getLatitude();
						double longit=loc.getLongitude();
						Log.d(

								"lat", " "+lat+" "+longit);
						AddFence fenceObj = new AddFence(view.getContext());
						fenceObj.saveDataInDatabase(locationStr, lat, longit, 1, dateStr, startTimeStr, endTimeStr);
						//fenceObj.saveCoordinatesInPreferences((float)lat, (float) longit);


						//add an alarm
						Intent intentAlarm = new Intent(view.getContext(), AlarmReciever.class);
						Bundle extras=new Bundle();
						int id=(int)System.nanoTime();
						extras.putString("id", String.valueOf(id));
						extras.putString("latitude", String.valueOf(lat));
						extras.putString("longitude", String.valueOf(longit));
						extras.putString("entering", String.valueOf(false));
						extras.putString("address", loc.getAddressLine(0));
						intentAlarm.putExtras(extras);

						// create the object
						AlarmManager alarmManager = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);

						//set the alarm for particular time
						alarmManager.set(AlarmManager.RTC_WAKEUP,time2.getTime(), PendingIntent.getBroadcast(view.getContext(),id,  intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
						Toast.makeText(view.getContext(), "Alarm Scheduled for Tommrrow", Toast.LENGTH_LONG).show();


						//fenceObj.addProximityAlert(lat, longit,false,loc.getAddressLine(0));
					} 
					catch(Exception e) {
						String esd = e.getMessage();
						Log.d("error", e.getMessage());

					}
				}
			});

			return rootView;
		}

	}

	public static class ProhibitedTab extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_section_prohibited, container, false);

			prohibArea = (EditText) rootView.findViewById(R.id.prohib_et);

			rootView.findViewById(R.id.submitAreaBtn)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String prohibAreaStr = prohibArea.getText().toString();
					Log.d("prohib area string value", prohibAreaStr);

					Geocoder geocoder = new Geocoder(view.getContext(), Locale.getDefault());

					List<Address> address;

					try {
						address = geocoder.getFromLocationName(prohibAreaStr, 5);

						Address loc=address.get(0);

						double lat=loc.getLatitude();
						double longit=loc.getLongitude();
						Log.d(

								"lat", " "+lat+" "+longit);
						AddFence fenceObj = new AddFence(view.getContext());
						//fenceObj.saveCoordinatesInPreferences((float)lat, (float) longit);
						AddFence proibitedObj = new AddFence(MainActivity.getContext());
						proibitedObj.addProximityAlert(lat, longit, true, prohibAreaStr);
						//fenceObj.addProximityAlert(lat, longit);
					}

					catch(Exception e) {
						String esd = e.getMessage();
						Log.d("error", e.getMessage());
					}
				}
			});

			return rootView;
		}

	}

	public static class CurrLocFragment extends Fragment {

		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View rootView = inflater.inflate(R.layout.fragment_section_currloc, container, false);
			physAddr_tv = (TextView) rootView.findViewById(R.id.phys_loc);
			rootView.findViewById(R.id.currLocBtn)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					TrackLocation tloc = new TrackLocation(view.getContext());
					String address=tloc.getCurrentLocation();
					physAddr_tv.setText(address);
				}
			});

			return rootView;
		}

	}

	public static class DummySectionFragment extends Fragment {

		public static final String ARG_SECTION_NUMBER = "section_number";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
			Bundle args = getArguments();
			((TextView) rootView.findViewById(android.R.id.text1)).setText(
					getString(R.string.dummy_section_text, args.getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}


}
