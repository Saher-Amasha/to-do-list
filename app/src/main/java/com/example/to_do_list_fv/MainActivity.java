package com.example.to_do_list_fv;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference referenceToUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rootNode=FirebaseDatabase.getInstance();
        //add empty user
        reference=rootNode.getReference("users");
        reference.child("safa").setValue(" ");

        //adds task to user saher69.999
            reference=rootNode.getReference("users/saher/tasks/task0");

            //make new task
                TaskHelper TH=new TaskHelper("dentist","go to dentest ur teeth bad",new Date(2021,1,19),new Date(2021,1,19),Urgency.MEDIUM,0);

        reference.setValue(TH);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}