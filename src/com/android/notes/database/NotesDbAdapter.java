package com.android.notes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class NotesDbAdapter {
	// Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_DESCRIPTION = "description";
	private static final String DATABASE_TABLE = "note";
	private Context context;
	private SQLiteDatabase database;
	private NotesDatabaseHelper dbHelper;

	public NotesDbAdapter(Context context) {
		this.context = context;
	}

	public NotesDbAdapter open() throws SQLException {
		dbHelper = new NotesDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Create a new note If the note is successfully created return the new
	 * rowId for that note, otherwise return a -1 to indicate failure.
	 */
	public long createNote(String name, String description) {
		ContentValues initialValues = createContentValues(name, description);

		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Update the note
	 */
	public boolean updateNote(long rowId, String name, String description) {
		ContentValues updateValues = createContentValues(name, description);

		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
				+ rowId, null) > 0;
	}

	/**
	 * Deletes note
	 */
	public boolean deleteNote(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all note in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllNotes() {
		return database.query(DATABASE_TABLE, new String[] { KEY_ROWID,
				KEY_NAME, KEY_DESCRIPTION }, null, null, null,
				null, null);
	}

	/**
	 * Return a Cursor positioned at the defined note
	 */
	public Cursor fetchNote(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String[] {
				KEY_ROWID, KEY_NAME, KEY_DESCRIPTION },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String name, String description) {
		ContentValues values = new ContentValues();		
		values.put(KEY_NAME, name);
		values.put(KEY_DESCRIPTION, description);
		return values;
	}
}
