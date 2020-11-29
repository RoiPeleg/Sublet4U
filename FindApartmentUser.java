package com.example.sublet4u;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sublet4u.data.model.Aparetment;
import com.example.sublet4u.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FindApartmentUser extends AppCompatActivity
{
    DatabaseReference reference;
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
        DatabaseReference listApartment = myRef.child("apartment");
        yourName.setText(mAuth.getCurrentUser().getDisplayName());
        String firstApartment = listApartment.limitToFirst(1).getRef().getKey();
//        description.setText;
        storageRef.child("images/"+ firstApartment +"/firstIm").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image

                apaImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
//        listApartment.limitToFirst(1).getRef().addListenerForSingleValueEvent(new ValueEventListener()
//        {
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                Aparetment apart = snapshot.getValue(Aparetment.class);
//                descriptionInImg.setText(apart.desc);
//                nameInImg.setText(apart.name);
//                addressInImg.setText(apart.address);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error)
//            {
//                System.out.println(error);
//            }
//        });

        int i = 2;
        like.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //should go to the chat with the manager
//                        firstApartment = listApartment.limitToFirst(i);
//                        i++;
//                        Intent i = new Intent(new Intent(getApplicationContext(), FindApartmentUser.class));
//                        startActivity(i);

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
