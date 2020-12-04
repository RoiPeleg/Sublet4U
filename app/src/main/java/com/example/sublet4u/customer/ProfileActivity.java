package com.example.sublet4u;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sublet4u.customer.FindApartmentUser;
import com.example.sublet4u.data.model.Apartment;
import com.example.sublet4u.data.model.Client;
import com.example.sublet4u.data.model.Invitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    DatabaseReference reference;
    public Client client;
    public String apartID;
    private DatabaseReference myRef;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final Button back = findViewById(R.id.back);
        final Button settings = findViewById(R.id.settings);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
//        viewName.setText(mAuth.getCurrentUser().getDisplayName());
//        viewSex.setText(mAuth.getCurrentUser().getDisplayName());
//        viewDesc.setText(mAuth.getCurrentUser().getDisplayName());

        String ID = myRef.child("client").child("client").push().getKey();
//        String hello = myRef.child("client").child("client").child(ID);
        Toast.makeText(getApplicationContext(), ID, Toast.LENGTH_LONG).show();
        loadData(ID);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), FindApartmentUser.class));
                startActivity(i);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), SettingsClientActivity.class));
                startActivity(i);
            }
        });

    }
    private void loadData(String ID){
        final TextView viewName = findViewById(R.id.viewName);
        final TextView viewSex = findViewById(R.id.viewSex);
        final TextView viewDesc = findViewById(R.id.viewDesc);
        final ImageView apaImage = findViewById(R.id.midImage);
        storageRef.child("imagesClient/"+ ID +"/firstIm").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                apaImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                myRef.child("client").child("client").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        client = snapshot.getValue(Client.class);
                        viewDesc.setText(client.desc);
                        viewName.setText(client.name);
                        viewSex.setText(client.sex);
                        Toast.makeText(getApplicationContext(), "uploaded", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "can't load client", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {@Override public void onFailure(@NonNull Exception exception) {Toast.makeText(getApplicationContext(), "image failed "+exception.toString(), Toast.LENGTH_LONG).show(); }});
    }

}