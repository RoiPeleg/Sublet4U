package com.example.sublet4u;

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

import com.example.sublet4u.customer.FindApartmentUser;
import com.example.sublet4u.data.model.Client;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ConfinInviteActivity extends AppCompatActivity {
    DatabaseReference reference;
    public Client client;
    public String apartID;
    private DatabaseReference myRef;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confin_invite);
        final Button confrim = findViewById(R.id.confrim);
        final Button decline = findViewById(R.id.decline);
        final Button chat = findViewById(R.id.chat);

        List<String> allClients = new ArrayList<String>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        Bundle bundle = getIntent().getExtras();
        Intent receivedIntent  = getIntent();
        String ID = receivedIntent.getStringExtra("clientID");
//        Toast.makeText(getApplicationContext(), t, Toast.LENGTH_LONG).show();


//        String hello = myRef.child("client").child("client").child(ID);

        loadData(ID);
//        Toast.makeText(getApplicationContext(), ID, Toast.LENGTH_LONG).show();
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //should go to chat
                Intent i = new Intent(new Intent(getApplicationContext(), FindApartmentUser.class));
                startActivity(i);
            }
        });






    }
    private void loadData(String ID){

        final TextView viewName = findViewById(R.id.confrimName);
        final TextView viewSex = findViewById(R.id.confrimSex);
        final TextView viewDesc = findViewById(R.id.confrimDesc);
        final ImageView apaImage = findViewById(R.id.confrimImage);
        storageRef.child("imagesClient/"+ ID +"/firstIm").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                apaImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                myRef.child("client").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
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