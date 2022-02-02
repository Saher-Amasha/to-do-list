package com.example.to_do_list_fv;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends Activity {
    FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_tasks);

            mAuth = FirebaseAuth.getInstance();

            FirebaseUser user = mAuth.getCurrentUser();
            System.out.println("from profile activity yessssssssssssssssssssssssssssssssssssssssssssssssssss" +user.getEmail());
            Button goToSignUp = (Button) findViewById(R.id.button2);
            goToSignUp.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                signout();
            }
        });

    }

    public void signout()
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, SignupActivity.class);
        startActivity(intent);
    }

}
