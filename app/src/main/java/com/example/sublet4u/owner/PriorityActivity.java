package com.example.sublet4u.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Apartment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PriorityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority);
        final TextView textView = findViewById(R.id.ownerNamePriority);
        ListView mListView = findViewById(R.id.allOfApartments);

        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();



        textView.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
        ArrayList<String> ownerApartments = new ArrayList<>();
        Query ownerApa =  myRef.child("apartment").orderByChild("ownerID").equalTo(mAuth.getCurrentUser().getUid());
        ownerApa.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ArrayList<String> allApartment = new ArrayList<>();
                ArrayList<Apartment> allApa = new ArrayList<>();

                for (DataSnapshot s : snapshot.getChildren())
                {
                    allApartment.add(s.getKey());//get owner's apartments
                    Apartment apartment = s.getValue(Apartment.class);
                    allApa.add(apartment);
                }
                mListView.setAdapter(new ArrayAdapter<Apartment>(PriorityActivity.this,android.R.layout.simple_list_item_1,allApa));
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(PriorityActivity.this, WatchReviewsActivity.class);
                        i.putExtra("apartmentID",allApartment.get(position));
                        startActivity(i);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
            }
        });

        final Button back = findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), OwnerActivity.class));
                startActivity(i);
            }
        });
    }

}