package com.android.notes;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;

import com.android.notes.database.NotesDbAdapter;
import com.android.notes.services.AlarmReceiver;

public class NoteDetailActivity extends Activity {
	
	private EditText mNameText;
	private EditText mDescripionText;
	private Long mRowId;
	private NotesDbAdapter mDbHelper;
	
	static final int TIME_DIALOG_ID = 0;
	private int mHour;
    private int mMinute;
    Calendar calendar;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.edit_note);
		
		mNameText = (EditText) findViewById(R.id.editTextName);
		mDescripionText = (EditText) findViewById(R.id.editTextDescription);
		
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState
				.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(NotesDbAdapter.KEY_ROWID);
		}
		
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		
		populateFields();
	}
	
	private void populateFields() {
		if (mRowId != null) {			
			Log.e(null, "RowId="+mRowId);
			Cursor note = mDbHelper.fetchNote(mRowId);						
			
			mNameText.setText(note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_NAME)));
			mDescripionText.setText(note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_DESCRIPTION)));
		}
	}
	
	private void saveNote(){		
		String name = mNameText.getText().toString();
		String description = mDescripionText.getText().toString();

		if (mRowId == null) {
			long id = mDbHelper.createNote(name, description);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateNote(mRowId, name, description);
		}
		
		setResult(RESULT_OK);
        finish();
	}
	
	private void deleteNote(){
		mDbHelper.deleteNote(mRowId);
		
		setResult(RESULT_OK);
        finish();
	}
	
	private void showTimeDialog(){
		calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
		showDialog(TIME_DIALOG_ID);
	}
	
	// the callback received when the user "sets" the time in the dialog
	private TimePickerDialog.OnTimeSetListener mTimeSetListener =
	    new TimePickerDialog.OnTimeSetListener() {
	        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {	            
	            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
	            calendar.set(Calendar.MINUTE, minute);
	            addReminder(calendar);
	        }
	    };
	    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case TIME_DIALOG_ID:
            return new TimePickerDialog(this,
                    mTimeSetListener, mHour, mMinute, false);
        }
        return null;
    }
	
	private void addReminder(Calendar cal){
		//Calendar cal = Calendar.getInstance();
		//cal.add(Calendar.MINUTE, 1);
		
		Intent alarmintent = new Intent(getApplicationContext(), AlarmReceiver.class);
		alarmintent.putExtra(NotesDbAdapter.KEY_NAME,mNameText.getText().toString());
		alarmintent.putExtra(NotesDbAdapter.KEY_DESCRIPTION,mDescripionText.getText().toString());
		alarmintent.putExtra(NotesDbAdapter.KEY_ROWID,mRowId);
		
		PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 1,
		alarmintent,PendingIntent.FLAG_UPDATE_CURRENT|Intent.FILL_IN_DATA);
		
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		
		Log.w("NoteDetailActivity", "Row ID sent:"+mRowId);
		
		setResult(RESULT_OK);
        finish();
		
		//Toast.makeText(this, "Alarm time: "+cal.getTimeInMillis(), Toast.LENGTH_LONG).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    if(mRowId!=null)
	    	inflater.inflate(R.menu.edit_note_menu, menu);
	    else
	    	inflater.inflate(R.menu.add_note_menu, menu);	
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.save_note_menu:
	        saveNote();
	        break;
	    case R.id.delete_note_menu:
	    	deleteNote();
	    	break;
	    case R.id.add_reminder_menu:
	    	showTimeDialog();
	    	break;
	    case R.id.cancel_menu:	        
	        setResult(RESULT_CANCELED);
	        finish();
	        break;	    
	    }
	    return true;
	}
}
