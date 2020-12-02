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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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
import java.util.concurrent.ExecutionException;

public class FindApartmentUser extends AppCompatActivity {
    DatabaseReference reference;
    public Apartment apart;
    public String apartID;
    private DatabaseReference myRef;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findapartment);
        final TextView yourName = findViewById(R.id.yourName);
        List <String> allApartments = new ArrayList<String>();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        yourName.setText(mAuth.getCurrentUser().getDisplayName());
        Query listApartment = myRef.child("apartment").orderByValue();
        listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot s : snapshot.getChildren()){
                    try {
                        allApartments.add(s.getKey());
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                check(allApartments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "unalbe to load data", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void loadData(String ID){
        Toast.makeText(getApplicationContext(), ID, Toast.LENGTH_LONG).show();
        final TextView addressInImg = findViewById(R.id.addressInImg);
        final TextView nameInImg = findViewById(R.id.nameInImg);
        final TextView descriptionInImg = findViewById(R.id.descriptionInImg);
        final TextView apartmentPrice = findViewById(R.id.price);
        storageRef.child("images/"+ ID +"/firstIm").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        final ImageView apaImage = findViewById(R.id.midImage);
                        apaImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                }).addOnFailureListener(new OnFailureListener() {@Override public void onFailure(@NonNull Exception exception) {Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show(); }});

                myRef.child("apartment").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        apart = snapshot.getValue(Apartment.class);
                        descriptionInImg.setText(apart.desc);
                        nameInImg.setText(apart.name);
                        addressInImg.setText(apart.address);
                        apartmentPrice.setText(apart.price);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "can't load apartment", Toast.LENGTH_LONG).show();
                    }
                });

    }

    private void check (List<String> apartments){
        final Button like = findViewById(R.id.like);
        final Button dislike = findViewById(R.id.dislike);
        final Button design = findViewById(R.id.design);

        Iterator<String> appartmentsIterator = apartments.iterator();
        loadData(appartmentsIterator.next());

        like.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v){
                String invitation_id = myRef.child("Invitations").push().getKey();
                myRef.child("Invitations").child(invitation_id).setValue(new Invitation(apartID, mAuth.getUid()));
                if (appartmentsIterator.hasNext()){
                    apartID = appartmentsIterator.next();
                    loadData(apartID);
                }
                else {
                    Toast.makeText(getApplicationContext(), "no more apartments", Toast.LENGTH_LONG).show();
                }
            }

        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //just skip the picture and keep going to the next apartment
                if (appartmentsIterator.hasNext()){
                    apartID = appartmentsIterator.next();
                    loadData(apartID);
                }
                else {
                    Toast.makeText(getApplicationContext(), "no more apartments", Toast.LENGTH_LONG).show();
                }
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
