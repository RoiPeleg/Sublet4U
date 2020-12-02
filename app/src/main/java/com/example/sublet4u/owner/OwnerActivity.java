package com.example.sublet4u.owner;

import android.content.Intent;
import android.os.Bundle;

import com.example.sublet4u.R;
import com.example.sublet4u.owner.addapartmentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OwnerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        final  Button add_ap = findViewById(R.id.add_app);
        final TextView textView = findViewById(R.id.textViewName);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        textView.setText(mAuth.getCurrentUser().getDisplayName());
        add_ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), addapartmentActivity.class));
                startActivity(i);
            }
        });
    }
}