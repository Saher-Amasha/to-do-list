package com.example.to_do_list_fv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class UpdateSuccess extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_success);

        Button go_back = findViewById(R.id.back_to_profile);

        go_back.setOnClickListener(v -> {
            Intent intent = new Intent(UpdateSuccess.this, ProfileActivity.class);
            startActivity(intent);
        });

    }
}
