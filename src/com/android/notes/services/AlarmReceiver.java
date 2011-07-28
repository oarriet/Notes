package com.android.notes.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.notes.NoteDetailActivity;
import com.android.notes.R;
import com.android.notes.database.NotesDbAdapter;

public class AlarmReceiver extends BroadcastReceiver{
	
	private static int NOTIFICATION_ID = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Bundle extras=intent.getExtras();
		String title=extras.getString(NotesDbAdapter.KEY_NAME);
		String note=extras.getString(NotesDbAdapter.KEY_DESCRIPTION);
		long rowId=extras.getLong(NotesDbAdapter.KEY_ROWID);
		
		Log.w("NoteDetailActivity", "Row ID received:"+rowId);
		
		Intent i = new Intent(context,NoteDetailActivity.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, rowId);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context,
				NOTIFICATION_ID,
				i,
				PendingIntent.FLAG_UPDATE_CURRENT);		
		
		Notification notification = new Notification(R.drawable.icon, 
				"Note Reminder",
				System.currentTimeMillis());
		
		notification.setLatestEventInfo(context, note, title, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		
		NotificationManager manager = (NotificationManager)     
		context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		manager.notify(1234, notification);
	}

}
