
package com.example.to_do_list_fv;

        import android.content.Intent;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.widget.Button;
        import android.widget.RadioButton;
        import android.widget.RadioGroup;
        import android.widget.TextView;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.FragmentActivity;

        import com.google.android.material.button.MaterialButtonToggleGroup;
        import com.google.android.material.datepicker.MaterialDatePicker;
        import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
        import com.google.android.material.textfield.TextInputEditText;
        import com.google.android.material.textfield.TextInputLayout;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.Calendar;
        import java.util.Date;
public class UpdateTaskActivity extends FragmentActivity {
    private DatabaseReference mDatabase;
    private String[] email_parts;

    private RadioGroup urgencies;
    private TextInputEditText title, description;
    private MaterialButtonToggleGroup categories1, categories2;

    private TaskHelper new_task;

    private Date due_date;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null && user.getEmail() != null;
        // if the user isn't null get a reference of this user data to change later
        email_parts = user.getEmail().split("@");

        // button toggle group
        categories1 = findViewById(R.id.category1);
        categories2 = findViewById(R.id.category2);

        // date
        MaterialDatePicker.Builder<Long> materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText("SELECT A DATE");
        materialDateBuilder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        final MaterialDatePicker<Long> materialDatePicker = materialDateBuilder.build();

        // text views
        title = findViewById(R.id.task_title_);
        description = findViewById(R.id.description_);

        // radio buttons
        urgencies = findViewById(R.id.urgencies);

        // buttons
        Button go_back = findViewById(R.id.go_back);
        go_back.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateTaskActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button pick_date = findViewById(R.id.date_picker);
        ExtendedFloatingActionButton add_task = findViewById(R.id.floating_action_button);
        add_task.setOnClickListener(v -> {
            RadioButton radioButton = findViewById(urgencies.getCheckedRadioButtonId());
            if (due_date == null) {
                pick_date.setText(R.string.select_due_date);
                pick_date.setTextColor(Color.RED);
            }
            if (radioButton != null && !findInvalidInput() && due_date != null) {
                String categories_names = getCategories();
                assert title.getText() != null && description.getText() != null;
                new_task = new TaskHelper(title.getText().toString(), description.getText().toString(),
                        due_date, due_date, 0, categories_names, getUrgency(radioButton.getText().toString()));
                mDatabase.child("users").child(email_parts[0]).child("tasks").
                        child(title.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // if the title doesn't exists then add the task
                            mDatabase.child("users").child(email_parts[0]).child("tasks").child(title.getText().toString()).setValue(new_task);
                            Intent intent = new Intent(UpdateTaskActivity.this, UpdateSuccess.class);
                            startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Failed
                    }
                });


            }
        });

        pick_date.setOnClickListener(v -> materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER"));
        materialDatePicker.addOnPositiveButtonClickListener(
                selection -> {
                    if (materialDatePicker.getHeaderText() != null) {
                        String[] date_parts = materialDatePicker.getHeaderText().split(" ");
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear(); // Sets hours/minutes/seconds/milliseconds to zero
                        calendar.set(Integer.parseInt(date_parts[2]) + 1900, getMonthNumber(date_parts[0]), Integer.parseInt(date_parts[1].split(",")[0]));
                        due_date = calendar.getTime();
                        pick_date.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_check_circle_outline_24, 0, 0, 0);
                    } else {
                        pick_date.setText(R.string.select_due_date);
                        pick_date.setTextColor(Color.RED);
                    }
                }
        );

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("name");
            String value1 = extras.getString("description");
            title.setText(value.split(",")[0]);
            description.setText(value1);
        }
    }

    private Urgency getUrgency(String urgency) {
        switch (urgency) {
            case "Medium":
                return Urgency.MEDIUM;
            case "High":
                return Urgency.HIGH;
            default:
                return Urgency.LOW;
        }
    }

    private String getCategories() {
        StringBuilder categories = new StringBuilder();
        for (int i = 0; i < categories1.getCheckedButtonIds().size(); i++) {
            Button b = findViewById(categories1.getCheckedButtonIds().get(i));
            categories.append(b.getText()).append(" ");
        }
        for (int i = 0; i < categories2.getCheckedButtonIds().size(); i++) {
            Button b = findViewById(categories2.getCheckedButtonIds().get(i));
            categories.append(b.getText()).append(" ");
        }
        return categories.toString();
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

    private boolean findInvalidInput() {
        boolean invalid = false;
        if (title.getText() == null || title.getText().toString().equals("")) {
            TextInputLayout textInputLayout = findViewById(R.id.task_title);
            textInputLayout.setHint("");
            title.setHint("Please enter a valid title!");
            title.setHintTextColor(Color.RED);
            invalid = true;
        }
        if (description.getText() == null || description.getText().toString().equals("")) {
            TextInputLayout textInputLayout = findViewById(R.id.task_description);
            textInputLayout.setHint("");
            description.setHint("Please enter a non empty description!");
            description.setHintTextColor(Color.RED);
            invalid = true;
        }

        if (getCategories().equals("")) {
            TextView categoryTitle = findViewById(R.id.category_title);
            categoryTitle.setText(R.string.choose_category);
            categoryTitle.setTextColor(Color.RED);
            invalid = true;
        }
        return invalid;
    }
}
