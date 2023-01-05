package com.raj.shivanshu.timetodo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.raj.shivanshu.timetodo.database.AppDatabase;
import com.raj.shivanshu.timetodo.database.TaskEntry;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener {
    private static final String TAG = "MainActivity";
    private static final String TASK_ID = "clicked_task_id";
    AppDatabase database;
    EditText editText;
    TextView priorityHighTv;
    TextView priorityMediumTv;
    TextView priorityLowTv;
    TaskAdapter adapter;
    Toolbar toolbar;
    TaskEntry loadedTaskEntry;
    boolean updateHelperTag;

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "settings opening...", Toast.LENGTH_SHORT).show();
                break;
            //Sharing functionality will be implemented after uploading on the Play store.
//            case R.id.share_app:
//                Toast.makeText(this, "Sharing app...", Toast.LENGTH_SHORT).show();
//                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rv = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.task_title_edit_text);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database = AppDatabase.getInstance(getApplicationContext());
        adapter = new TaskAdapter(this);
        adapter.setClickListener(this);
        rv.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                List<TaskEntry> taskEntries = adapter.getTasks();
                AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        database.taskDao().deleteTask(taskEntries.get(position));
                    }
                });
            }
        }).attachToRecyclerView(rv);
        priorityHighTv = findViewById(R.id.priority_high_tv);
        priorityHighTv.setOnClickListener(v -> {
            if (updateHelperTag) {
                Log.d(TAG, "onClick: updateHelperTag value:" + updateHelperTag + "");
                updateAsync(loadedTaskEntry, 1);
                Log.d(TAG, "onClick: loadedTaskEntry" + loadedTaskEntry.getTitle());
            } else {
                insertAsync(1);
            }
        });
        priorityMediumTv = findViewById(R.id.priority_medium_tv);
        priorityMediumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateHelperTag) {
                    Log.d(TAG, "onClick: UpdateAsync called from within onClick");
                    updateAsync(loadedTaskEntry, 2);
                } else {
                    insertAsync(2);
                }
            }
        });
        priorityLowTv = findViewById(R.id.priority_low_tv);
        priorityLowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateHelperTag) {
                    Log.d(TAG, "onClick: UpdateAsync called from within onClick");
                    updateAsync(loadedTaskEntry, 3);
                } else {
                    insertAsync(3);
                }

            }
        });
        retrieveTasks();
    }

    // check if the text from user is empty. If yes, then show a toast.
    public static String finaliseEditText(EditText receivedEditText) {
        String finalString = receivedEditText.getText().toString().trim();
        if (finalString.length() > 0) {
            return finalString;
        } else {
            return null;
        }

    }

    //to refresh tasks by loading from db and setting to adapter.
    private void retrieveTasks() {

        LiveData<List<TaskEntry>> taskEntries = database.taskDao().loadAllTasks();

        Log.d(TAG, "retrieveTasks: Actively retrieving from the db");
        taskEntries.observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                adapter.setTasks(taskEntries);
            }
        });
    }


    //using appexecutors to insert task to the db.
    private void insertAsync(int priority) {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                if(finaliseEditText(editText)!=null){
                    database.taskDao().insertTask(new TaskEntry(finaliseEditText(editText), new Date(), priority, false));
                    editText.getText().clear();
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Please add task", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

            }
        });
    }

    @Override
    //called when item of recyclerView is called
    public void onItemClick(int itemId) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }

    //update task on back thread by appexecutors
    public void updateAsync(TaskEntry taskEntry, int priority) {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                taskEntry.setTitle(finaliseEditText(editText));
                taskEntry.setDate(new Date());
                taskEntry.setPriority(priority);
                database.taskDao().updateTask(taskEntry);
                editText.getText().clear();
                Log.d(TAG, "run: value of taskEntry" + taskEntry.getTitle());
                updateHelperTag = false;
                Log.d(TAG, "run: updateHelperTag value: " + updateHelperTag);
            }
        });
    }
}
