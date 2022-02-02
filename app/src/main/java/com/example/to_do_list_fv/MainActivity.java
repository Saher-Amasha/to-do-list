package com.example.to_do_list_fv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolBar;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference referenceToUser;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            updateUI(currentUser);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        rootNode = FirebaseDatabase.getInstance();
//        //add empty user
//        reference = rootNode.getReference("users");
//        reference.child("safa").setValue(" ");
//        reference.child("amasha").setValue(" ");
//
//
//        //adds task to user saher69.999
//        reference = rootNode.getReference("users/safa");
//
//        //make new task
//        TaskHelper TH = new TaskHelper("dentist", "go to dentest ur teeth bad", new Date(2021, 1, 19), new Date(2021, 1, 19), Urgency.MEDIUM, 0);
//
//        reference.setValue(TH);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolBar);

        Button goToSignUp = (Button) findViewById(R.id.button6);
        goToSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    private void updateUI(FirebaseUser currentUser) {
    }

}