package com.example.to_do_list_fv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private List<TaskHelper> tasks;
    private final ProfileActivity activity;

    public TasksAdapter(ProfileActivity profileActivity) {
        this.activity = profileActivity;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the current user to use in case the tasks needs to update
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null && user.getEmail() != null;
        String[] email_parts = user.getEmail().split("@");
        // get the task of the current user
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(email_parts[0]).child("tasks");
        // update the task(item) page
        TaskHelper item = tasks.get(position);
        holder.task.setText(item.getDescription());
        if (toBoolean(item.getExecuted())) {
            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.task.setChecked(toBoolean(item.getExecuted()));
        Calendar cal = Calendar.getInstance();
        cal.setTime(item.getToExecute());
        String title = item.getName() + ", " + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + "/" + (cal.get(Calendar.YEAR) - 1900);
        holder.title.setText(title);
        switch (item.getTaskUrgency()) {
            case MEDIUM:
                holder.title.setTextColor(Color.BLACK);
                break;
            case HIGH:
                holder.title.setTextColor(Color.RED);
                break;
            default:
                holder.title.setTextColor(Color.GREEN);
                break;

        }
        holder.task_category.setText(item.getCategory());
        // change the text style (and data) if the check box is selected
        holder.task.setOnClickListener(v -> {
            //is chkIos checked?
            if (((CheckBox) v).isChecked()) {
                item.setExecuted(1);
                mDatabase.child(item.getName()).child("executed").setValue(1)
                        .addOnSuccessListener(aVoid -> {
                            // Write was successful!
                            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        })
                        .addOnFailureListener(e -> {
                            // Write failed
                        });
                // add the strike through
            } else {
                item.setExecuted(0);
                mDatabase.child(item.getName()).child("executed").setValue(0)
                        .addOnSuccessListener(aVoid -> {
                            // Write was successful!
                            holder.task.setPaintFlags(holder.task.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        })
                        .addOnFailureListener(e -> {
                            // Write failed
                        });
                // remove the strike throw
            }
        });

        holder.edit.setOnClickListener(v -> {
            Intent intent = new Intent(this.activity, UpdateTaskActivity.class);
            intent.putExtra("name",holder.title.getText().toString());
            intent.putExtra("description",holder.task.getText().toString());
            intent.putExtra("category",holder.task_category.getText());
            activity.startActivity(intent);
        });
    }

    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public void setTasks(List<TaskHelper> tasksList) {
        this.tasks = tasksList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(int position) {
        // get the current user to use in case the tasks needs to update
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null && user.getEmail() != null;
        String[] email_parts = user.getEmail().split("@");
        // get the task of the current user
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(email_parts[0]).child("tasks");
        // update the task(item) page
        TaskHelper item = tasks.get(position);
        mDatabase.child(item.getName()).removeValue();
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView title, task_category;
        Button edit;
        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            title = view.findViewById(R.id.show_title);
            task_category = view.findViewById(R.id.task_category);
            edit=view.findViewById(R.id.edit_button);

        }
    }

}
