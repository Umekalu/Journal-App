package com.example.umekalu.dailyjournal;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.umekalu.dailyjournal.db.Task;
import com.example.umekalu.dailyjournal.db.TaskHelper;

import java.util.ArrayList;

/**
 * Created by Umekalu on 7/1/2018.
 */

public class JournalActivity extends AppCompatActivity {

    private static final String TAG = "JournalActivity";
    private TaskHelper mHelper;
    private Task task;
    private ListView mListView;
    private ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new TaskHelper(this);
        mListView = findViewById(R.id.list_to_do);


        updateUI();
    }

    //This method updates the database
    private void updateUI() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(Task.TaskEntry.TABLE, new String[]{Task.TaskEntry._ID, Task.TaskEntry.COL_TASK_TITLE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(Task.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(index));
//            mTaskListView.setAdapter(mAdapter);
        }

        if (mAdapter == null){
            mAdapter = new ArrayAdapter<>(this,R.layout.item_todo, R.id.task_title);
            mListView.setAdapter(mAdapter);
        }
        else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_add:
                final EditText text = new EditText(this);
                text.setPadding(40,0,40,30);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("New Task")
                        .setMessage("Add your new task")
                        .setView(text)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String task = String.valueOf(text.getText());

                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(Task.TaskEntry.COL_TASK_TITLE, task);
                                db.insertWithOnConflict(Task.TaskEntry.TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUI();
                            }
                        })
                        .setNegativeButton("CAncel", null)
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);

        }
    }

    @Override
    public void onCreateContextMenu (ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        menu.setHeaderTitle("What would you like to do?");
        String options[] = {"Delete", "Cancel"};
        for(String option : options) {
            menu.add(option);

            if(option == "Delete") {

            }

        }
    }

    public void deletetask(View v){
        View parent = (View) v.getParent();
        TextView tx = (TextView) parent.findViewById(R.id.task_title);
        String task_ = String.valueOf(tx.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(Task.TaskEntry.TABLE, Task.TaskEntry.COL_TASK_TITLE + "= ?", new String[]{task_});
        db.close();
        updateUI();
    }

}
