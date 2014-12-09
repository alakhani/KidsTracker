package com.pervasive.project.kidsTracker;

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

	/////////////////////////////////////////////////////////////////////
	//	Constants & Data
	/////////////////////////////////////////////////////////////////////
	// For logging:
	private static final String TAG = "DBAdapter";

	// DB Fields

	/*
	 * CHANGE 1:
	 */
	// TODO: Setup your fields here:

	//Fields for the schedule table
	public static final String SCHEDULE_KEY_ROWID = "_idSchedule";
	public static final String KEY_CHILD_ID = "childId";
	public static final String KEY_SCHEDULE_DATE = "scheduleDate";
	public static final String KEY_SCHEDULE_START_TIME = "scheduleStartTime";
	public static final String KEY_SCHEDULE_END_TIME = "scheduleEndTime";
	public static final String KEY_SCHEDULE_LOCATION_FK = "scheduleLocationFk";


	//fields for the co-ordinates table
	public static final String CORD_KEY_ROWID = "_idCord";
	public static final String KEY_CORD_ADDRESS = "locationAddres";
	public static final String KEY_CORD_LAT = "locationLatitude";
	public static final String KEY_CORD_LONGIT = "locationLongitude";



	// TODO: Setup your field numbers here (0 = KEY_ROWID, 1=...)
	/*public static final int COL_ROWID = 0;
	public static final int COL_NAME = 1;
	public static final int COL_STUDENTNUM = 2;
	public static final int COL_FAVCOLOUR = 3;*/


	public static final String[] ALL_KEYS_SCHEDULETABLE = new String[] {SCHEDULE_KEY_ROWID, KEY_SCHEDULE_DATE, KEY_SCHEDULE_START_TIME, KEY_SCHEDULE_END_TIME,KEY_SCHEDULE_LOCATION_FK};
	public static final String[] ALL_KEYS_CORDTABLE = new String[] {CORD_KEY_ROWID,KEY_CORD_ADDRESS,KEY_CORD_LAT,KEY_CORD_LONGIT};

	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "kidsTracker";
	public static final String DATABASE_TABLE_SCHEDULE = "schedule";
	public static final String DATABASE_TABLE_CORD = "coordinates";
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 1;	

	private static final String DATABASE_CREATE_SQL_SCHEDULE = 
			"create table " + DATABASE_TABLE_SCHEDULE
			+ " (" + SCHEDULE_KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_CHILD_ID + " string not null, "
			+ KEY_SCHEDULE_DATE + " string not null, "
			+ KEY_SCHEDULE_START_TIME + " string not null, "
			+ KEY_SCHEDULE_END_TIME + " string not null, "
			+ KEY_SCHEDULE_LOCATION_FK + " int not null,"
			+" FOREIGN KEY ("+KEY_SCHEDULE_LOCATION_FK+") REFERENCES "+DATABASE_TABLE_CORD+" ("+CORD_KEY_ROWID+")"
			+ ");";

	private static final String DATABASE_CREATE_SQL_CORD = 
			"create table " + DATABASE_TABLE_CORD
			+ " (" + CORD_KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_CORD_ADDRESS + " string not null, "
			+ KEY_CORD_LAT + " double not null, "
			+ KEY_CORD_LONGIT + " double not null"
			+ ");";

	// Context of application who uses us.
	private final Context context;

	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	/////////////////////////////////////////////////////////////////////
	//	Public methods:
	/////////////////////////////////////////////////////////////////////

	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}

	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}

	// Add a new set of values to the database.
	public long insertRowInCordsTable(String address, double latitude, double longitude ) {
		ContentValues initialValues = new ContentValues();
		//initialValues.put(CORD_KEY_ROWID ,cord_id);
		initialValues.put(KEY_CORD_ADDRESS , address);
		initialValues.put(KEY_CORD_LAT , latitude);
		initialValues.put(KEY_CORD_LONGIT  , longitude);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_CORD, null, initialValues);
	}
	
	public long insertRowInScheduleTable(int child_id, String schedDate, String startTime,String endTime,int loc_fk ) {
		ContentValues initialValues = new ContentValues();
		//initialValues.put(SCHEDULE_KEY_ROWID ,schedule_id);
		initialValues.put(KEY_CHILD_ID , child_id);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//sdf.format(schedDate)
		initialValues.put(KEY_SCHEDULE_DATE ,schedDate );
		initialValues.put(KEY_SCHEDULE_START_TIME  , startTime);
		initialValues.put(KEY_SCHEDULE_END_TIME  , endTime);
		initialValues.put(KEY_SCHEDULE_LOCATION_FK  , loc_fk);

		// Insert it into the database.
		return db.insert(DATABASE_TABLE_SCHEDULE, null, initialValues);
	}


	// Get a specific row (by rowId)
	public Cursor getRow(long rowId) {
		String where = SCHEDULE_KEY_ROWID + "=" + rowId;
		Cursor c = 	db.query(true, DATABASE_TABLE_CORD, ALL_KEYS_SCHEDULETABLE, 
				where, null, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	/////////////////////////////////////////////////////////////////////
	//	Private Helper Classes:
	/////////////////////////////////////////////////////////////////////

	/**
	 * Private class which handles database creation and upgrading.
	 * Used to handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_SQL_SCHEDULE);
			_db.execSQL(DATABASE_CREATE_SQL_CORD);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data!");

			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_CORD);
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SCHEDULE);

			// Recreate new database:
			onCreate(_db);
		}
	}
}
