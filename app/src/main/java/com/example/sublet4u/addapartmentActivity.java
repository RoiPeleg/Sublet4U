package com.example.sublet4u;

import android.content.Intent;
import android.os.Bundle;

import com.example.sublet4u.data.model.Aparetment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class addapartmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addapartment);
        final Button done = findViewById(R.id.done);
        final EditText name = findViewById(R.id.Name);
        final EditText desc = findViewById(R.id.description);
        final EditText address = findViewById(R.id.address);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("apartment").push().setValue(new Aparetment(name.getText().toString(),desc.getText().toString(),address.getText().toString(), mAuth.getCurrentUser().getUid()));
                Intent i = new Intent(new Intent(getApplicationContext(), OwnerActivity.class));
                startActivity(i);
            }
        });
    }
}