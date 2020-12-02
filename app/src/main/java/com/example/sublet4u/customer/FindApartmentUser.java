package com.example.sublet4u.customer;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Apartment;
import com.example.sublet4u.data.model.Invitation;
import com.example.sublet4u.ui.login.LoginActivity;
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

public class FindApartmentUser extends AppCompatActivity
{
    DatabaseReference reference;
    public Apartment apart;
    public String apartID;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findapartment);
        final Button like = findViewById(R.id.like);
        final Button dislike = findViewById(R.id.dislike);
        final Button design = findViewById(R.id.design);
        final ImageView apaImage = findViewById(R.id.midImage);
        final TextView descriptionInImg = findViewById(R.id.descriptionInImg);
        final TextView addressInImg = findViewById(R.id.addressInImg);
        final TextView nameInImg = findViewById(R.id.nameInImg);
        final TextView yourName = findViewById(R.id.yourName);


//        imageView = findViewById(R.id.imageView);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        yourName.setText(mAuth.getCurrentUser().getDisplayName());
        Query listApartment = myRef.child("apartment").orderByValue();
        listApartment.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    Toast.makeText(getApplicationContext(),s.getKey(),Toast.LENGTH_LONG).show();
                    storageRef.child("images/"+ s.getKey() +"/firstIm").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        apaImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                }).addOnFailureListener(new OnFailureListener() {@Override public void onFailure(@NonNull Exception exception) {Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show(); }});

                     apart = s.getValue(Apartment.class);
                descriptionInImg.setText(apart.desc);
                nameInImg.setText(apart.name);
                addressInImg.setText(apart.address);
                apartID = s.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
            }
        });


        int i = 2;
        like.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v){
                String invitation_id = myRef.child("Invitations").push().getKey();
                myRef.child("Invitations").child(invitation_id).setValue(new Invitation(apartID, mAuth.getUid()));
                listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot s : snapshot.getChildren()){

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //just skip the picture and keep going to the next apartment
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //should go to the chat with the manager
                        String username = snapshot.child("ownerID").getValue().toString();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        design.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //just skip the picture and keep going to the next apartment
                Intent i = new Intent(new Intent(getApplicationContext(), LoginActivity.class));
                startActivity(i);
            }
        });
    }



}
