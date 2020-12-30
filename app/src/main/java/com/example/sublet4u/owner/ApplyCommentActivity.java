package com.example.sublet4u.owner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sublet4u.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ApplyCommentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_comment);
        final EditText commentOfOwner = findViewById(R.id.commentReview);
        final Button applyComment = findViewById(R.id.apply);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        applyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentOfOwner.getText().toString().equals("") || commentOfOwner.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    Toast.makeText(getApplicationContext(), "Enter a normal comment", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent receivedIntent  = getIntent();
                    String ID = receivedIntent.getStringExtra("ratingID");
                    myRef.child("Rating").child(ID).child("commentOwner").setValue(commentOfOwner.getText().toString());
                    Toast.makeText(getApplicationContext(), "Comment updated", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(ApplyCommentActivity.this, PropertiesActivity.class);
                    startActivity(i);
                }
            }
        });
    }
}