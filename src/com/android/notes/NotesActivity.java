package com.android.notes;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.android.notes.database.NotesDbAdapter;

public class NotesActivity extends ListActivity {
	
	private NotesDbAdapter dbHelper;
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;	
	private Cursor cursor;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        

        setContentView(R.layout.main);
        
        dbHelper = new NotesDbAdapter(this);
		dbHelper.open();
		fillData();

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
            // When clicked, create activity for edit
            editNote(id);
          }
        });
        
        Button btnAdd = (Button)findViewById(R.id.button);
        btnAdd.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//When clicked, create activity for creating
				createNote();
				}
		});
    }
    
    private void editNote(long id){
    	Intent i = new Intent(this, NoteDetailActivity.class);    	
    	i.putExtra(NotesDbAdapter.KEY_ROWID, id);
    	startActivityForResult(i, ACTIVITY_EDIT);
    }
    
    private void createNote(){
    	Intent i = new Intent(this, NoteDetailActivity.class);
    	startActivityForResult(i, ACTIVITY_CREATE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
		case RESULT_OK:
			fillData();
			break;
		default:
			//do nothing
			break;
		}
    }
    
    private void fillData() {
		cursor = dbHelper.fetchAllNotes();
		startManagingCursor(cursor);

		String[] from = new String[] { NotesDbAdapter.KEY_NAME };
		int[] to = new int[] { android.R.id.text1 };
		
		ListAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, cursor,
				from, to);

		setListAdapter(adapter);
	}
}