package com.raj.shivanshu.timetodo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.raj.shivanshu.timetodo.database.AppDatabase;
import com.raj.shivanshu.timetodo.database.TaskEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskDetailActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_TASK_ID = "clicked_task_id";
    public static final int DEFAULT_TASK_ID = 0;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    private static final String TAG = "TaskDetailActivity";

    EditText titleEditText;
    TextView dateTv;
    EditText descriptionEditText;
    Toolbar toolbar;
    ImageButton imageButton;
    TextView priorityHighTv;
    TextView priorityMediumTv;
    TextView priorityLowTv;
    private int mTaskId;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        titleEditText = findViewById(R.id.editTextTaskTitle);
        dateTv = findViewById(R.id.textDate);
        toolbar = findViewById(R.id.toolbar2);
        imageButton = findViewById(R.id.edit_date);
        priorityHighTv = findViewById(R.id.priority_high_tv);
        priorityMediumTv = findViewById(R.id.priority_medium_tv);
        priorityLowTv = findViewById(R.id.priority_low_tv);
        descriptionEditText = findViewById(R.id.editTextDescription);
        database = AppDatabase.getInstance(this);
        setSupportActionBar(toolbar);

        priorityHighTv.setOnClickListener(v -> {
            clearPriority();
            priorityHighTv.setText("✓");
        });

        priorityMediumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPriority();
                priorityMediumTv.setText("✓");
            }
        });
        priorityLowTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearPriority();
                priorityLowTv.setText("✓");
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        //Getting data from the intent..
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId = getIntent().getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                loadTask(mTaskId);

            }
        }
    }

    public void loadTask(int itemId) {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                TaskEntry taskEntry = database.taskDao().loadTaskById(itemId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        titleEditText.setText(taskEntry.getTitle());
                        descriptionEditText.setText(taskEntry.getDescription());
                        Log.d(TAG, "onChanged: " + taskEntry.getDescription());
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
                        Date date = taskEntry.getDate();
                        dateTv.setText(sdf.format(date));
                        showPreviousPriority(taskEntry.getPriority());
                    }
                });
            }
        });

//
//        taskEntry.observe(this, new Observer<TaskEntry>() {
//            @Override
//            public void onChanged(TaskEntry taskEntry) {
//
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save) {
            try {
                onSaveButtonClicked();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void onSaveButtonClicked() throws ParseException {
        String newTitle = MainActivity.finaliseEditText(titleEditText);
        String newDescription = MainActivity.finaliseEditText(descriptionEditText);

//        Date newDueDate = MainActivity.finaliseEditText(dateEditText);
//            Date date = new Date();
        DateFormat format = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
        Date date = format.parse((String) dateTv.getText());
        Log.d(TAG, "onSaveButtonClicked: " + date);
        TaskEntry taskEntry = new TaskEntry(newTitle, date, getPriorityFromViews(), newDescription, false);
//        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
//            @Override
//            public void run() {
//                taskEntry.setId(mTaskId);
//                updateAsync(taskEntry);
//            }
//        });
        updateAsync(taskEntry);
        finish();

    }

    public void updateAsync(TaskEntry taskEntry) {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                taskEntry.setId(mTaskId);
//                taskEntry.setTitle(MainActivity.finaliseEditText(titleEditText));
//                taskEntry.setDate(new Date());
//                taskEntry.setDescription(MainActivity.finaliseEditText(descriptionEditText));
                database.taskDao().updateTask(taskEntry);

            }
        });
    }

    //This method is used to set priority that the user set while creating new task.
    public void showPreviousPriority(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                priorityHighTv.setText("✓");
                break;
            case PRIORITY_MEDIUM:
                priorityMediumTv.setText("✓");
                break;
            case PRIORITY_LOW:
                priorityLowTv.setText("✓");

        }
    }

    public int getPriorityFromViews() {
        int priority = 1;
        if (priorityHighTv.getText().equals("✓")) {
            priority = 1;
        } else if (priorityMediumTv.getText().equals("✓")) {
            priority = 2;
        } else {
            priority = 3;
        }
        return priority;
    }


    public void clearPriority() {
        priorityHighTv.setText("");
        priorityMediumTv.setText("");
        priorityLowTv.setText("");
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
        dateTv.setText(simpleDateFormat.format(mCalendar.getTime())
        );
    }
}
