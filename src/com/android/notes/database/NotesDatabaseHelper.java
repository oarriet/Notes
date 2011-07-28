package com.android.notes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "applicationdata";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table note (_id integer primary key autoincrement, "
			+ "name text not null, description text not null);";
	
	public NotesDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(NotesDatabaseHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS note");
		onCreate(db);		
	}

}
