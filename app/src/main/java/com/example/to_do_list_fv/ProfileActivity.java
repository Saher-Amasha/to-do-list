package com.example.to_do_list_fv;

import static android.content.Intent.ACTION_CAMERA_BUTTON;
import static java.lang.Boolean.TRUE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ProfileActivity extends  FragmentActivity {
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    private RecyclerView tasksRecyclerView;

    private TasksAdapter tasksAdapter;

    private FloatingActionButton add_task;

    private List<TaskHelper> tasksList;

    private Button picker;

    private Date currently_selected_date;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_tasks);

        tasksList = new ArrayList<>();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        // get the current users' email to get all the tasks from the database
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String[] email_parts = user.getEmail().split("@");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(email_parts[0]).child("tasks").get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {

                            for (DataSnapshot task_ : task.getResult().getChildren()) {
                                     tasksList.add(task_.getValue(TaskHelper.class));
                                     tasksAdapter.setTasks(tasksList);

                            }
                        }
                    }
                }
        );


        // the tasks list
        tasksRecyclerView = findViewById(R.id.all_tasks);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new TasksAdapter(this);
        tasksRecyclerView.setAdapter(tasksAdapter);
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        // sign out button
        ImageView goToSignUp = (ImageView) findViewById(R.id.log_out);
        goToSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Sign Out");
                builder.setMessage("Are you sure you want to sign out?");
                builder.setPositiveButton("SignOut",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signout();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // add task button (floating action button)
        add_task =
                findViewById(R.id.floating_action_button);
        add_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });
        // date
        picker =  findViewById(R.id.date_picker_all_Tasks);

        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        materialDateBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();


        picker.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    if (materialDatePicker.getHeaderText() != null) {
                        String[] date_parts = materialDatePicker.getHeaderText().split(" ");
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear(); // Sets hours/minutes/seconds/milliseconds to zero
                        calendar.set(Integer.parseInt(date_parts[2]) + 1900, getMonthNumber(date_parts[0]), Integer.parseInt(date_parts[1].split(",")[0]));
                        currently_selected_date = calendar.getTime();
                        show_tasks_for_selected_date(email_parts);

                    } else {
                        picker.setText(R.string.select_due_date);
                        picker.setTextColor(Color.RED);
                    }
                }
        );

    }



    public void signout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private int getMonthNumber(String month) {
        int num = 0;
        switch (month) {
            case "Jan":
                num = 1;
                break;
            case "Feb":
                num = 2;
                break;
            case "Mar":
                num = 3;
                break;
            case "Apr":
                num = 4;
                break;
            case "May":
                num = 5;
                break;
            case "Jun":
                num = 6;
                break;
            case "Jul":
                num = 7;
                break;
            case "Aug":
                num = 8;
                break;
            case "Sep":
                num = 9;
                break;
            case "Oct":
                num = 10;
                break;
            case "Nov":
                num = 11;
                break;
            case "Dec":
                num = 12;
                break;
            default:
                break;
        }
        return num;
    }

    void show_tasks_for_selected_date(String[] email_parts)
    {
        tasksList = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        mDatabase.child("users").child(email_parts[0]).child("tasks").get().addOnCompleteListener(
                task -> {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        for (DataSnapshot task_ : task.getResult().getChildren()) {

                            if (Objects.requireNonNull(task_.getValue(TaskHelper.class)).getExecuted()==1&&fmt.format(Objects.requireNonNull(task_.getValue(TaskHelper.class)).getToExecute()).equals(fmt.format(currently_selected_date))) {

                                tasksList.add(task_.getValue(TaskHelper.class));

                            }


                        }
                        for (DataSnapshot task_ : task.getResult().getChildren()) {

                                if (Objects.requireNonNull(task_.getValue(TaskHelper.class)).getExecuted()==0&&fmt.format(Objects.requireNonNull(task_.getValue(TaskHelper.class)).getToExecute()).equals(fmt.format(currently_selected_date))) {

                                    tasksList.add(task_.getValue(TaskHelper.class));

                                }


                        }
                        tasksAdapter.setTasks(tasksList);
                    }
                }
        );
    }

}
