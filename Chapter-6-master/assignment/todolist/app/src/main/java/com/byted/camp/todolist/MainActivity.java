package com.byted.camp.todolist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.byted.camp.todolist.beans.Note;
import com.byted.camp.todolist.beans.State;
import com.byted.camp.todolist.db.TodoContract;
import com.byted.camp.todolist.db.TodoDbHelper;
import com.byted.camp.todolist.operation.activity.DatabaseActivity;
import com.byted.camp.todolist.operation.activity.DebugActivity;
import com.byted.camp.todolist.operation.activity.SettingActivity;
import com.byted.camp.todolist.ui.NoteListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private String TAG = "oooo";
    private static final int REQUEST_CODE_ADD = 1002;

    private RecyclerView recyclerView;
    private NoteListAdapter notesAdapter;

    TodoDbHelper dbHelper = new TodoDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(
                        new Intent(MainActivity.this, NoteActivity.class),
                        REQUEST_CODE_ADD);
            }
        });

        recyclerView = findViewById(R.id.list_todo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        notesAdapter = new NoteListAdapter(new NoteOperator() {
            @Override
            public void deleteNote(Note note) {
                MainActivity.this.deleteNote(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }

            @Override
            public void updateNote(Note note) {
                MainActivity.this.updateNode(note);
                notesAdapter.refresh(loadNotesFromDatabase());
            }
        });
        recyclerView.setAdapter(notesAdapter);

        notesAdapter.refresh(loadNotesFromDatabase());
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            case R.id.action_debug:
                startActivity(new Intent(this, DebugActivity.class));
                return true;
            case R.id.action_database:
                startActivity(new Intent(this, DatabaseActivity.class));
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD
                && resultCode == Activity.RESULT_OK) {
            notesAdapter.refresh(loadNotesFromDatabase());
        }
    }

    private List<Note> loadNotesFromDatabase() {
        // TODO 从数据库中查询数据，并转换成 JavaBeans
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                TodoContract.ListEntry._ID,
                TodoContract.ListEntry.COLUMN_CONTENT,
                TodoContract.ListEntry.COLUMN_DATE,
                TodoContract.ListEntry.COLUMN_STATE,
                TodoContract.ListEntry.COLUMN_AUTHORITY
        };

        String sortOrder = TodoContract.ListEntry.COLUMN_AUTHORITY + " DESC";

        Cursor cursor = db.query(TodoContract.ListEntry.TABLE_NAME, projection,null,null,null,null,sortOrder);

        List<Note> noteList = new ArrayList<>();

        while(cursor.moveToNext())
        {
            long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.ListEntry._ID));
            String content = cursor.getString(cursor.getColumnIndex(TodoContract.ListEntry.COLUMN_CONTENT));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(TodoContract.ListEntry.COLUMN_DATE));
            int state = cursor.getInt(cursor.getColumnIndexOrThrow(TodoContract.ListEntry.COLUMN_STATE));
            int authority = cursor.getInt(cursor.getColumnIndexOrThrow(TodoContract.ListEntry.COLUMN_AUTHORITY));

            Note note = new Note(itemId);
            note.setContent(content);
            note.setDate(new Date(date));
            note.setState(State.from(state));
            note.setAuthority(authority);
            noteList.add(note);
//            Log.i(TAG, "i");
        }
        cursor.close();
//        db.close();
        if(noteList.isEmpty())
            return null;
        else
            return noteList;
    }

    private void deleteNote(Note note) {
        // TODO 删除数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TodoContract.ListEntry._ID + " = ?";
        String[] selectionArgs = {Long.toString(note.id)};

        db.delete(TodoContract.ListEntry.TABLE_NAME, selection, selectionArgs);
//        db.close();
    }

    private void updateNode(Note note) {
        // 更新数据
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TodoContract.ListEntry.COLUMN_STATE, note.getState().intValue);

        String selection = TodoContract.ListEntry._ID + " = ?";

        db.update(TodoContract.ListEntry.TABLE_NAME, values, selection, new String[]{Long.toString(note.id)});
//        db.close();
    }

}
