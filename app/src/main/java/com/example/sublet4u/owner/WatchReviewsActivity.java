package com.example.sublet4u.owner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.example.sublet4u.data.model.Rating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class WatchReviewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_reviews);

        ListView mListView = findViewById(R.id.allReviews);

        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();

        ArrayList<String> ownerApartments = new ArrayList<>();
        Intent receivedIntent  = getIntent();
        String ID = receivedIntent.getStringExtra("apartmentID");
        final TextView grade = findViewById(R.id.gradeApa);
        Query ownerApa =  myRef.child("Rating").orderByChild("apartmentID").equalTo(ID);
        ownerApa.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                ArrayList<String> ratingID = new ArrayList<>();
                ArrayList<Rating> rating = new ArrayList<>();
                double sum = 0;
                int count = 0;
                for (DataSnapshot s : snapshot.getChildren())
                {
                    ratingID.add(s.getKey());//get owner's apartments
                    Rating rate = s.getValue(Rating.class);
                    rating.add(rate);
                    assert rate != null;
                    sum += Double.parseDouble(rate.rateNumber);
                    count++;
                }
                grade.setText("Apartment Grade: " + sum / count);
                mListView.setAdapter(new ArrayAdapter<Rating>(WatchReviewsActivity.this,android.R.layout.simple_list_item_1,rating));
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        myRef.child("usersType").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Toast.makeText(getApplicationContext(),  snapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                                if (snapshot.getValue().toString().equals("owner"))
                                {
                                    Intent i = new Intent(new Intent(getApplicationContext(), ApplyCommentActivity.class));
                                    i.putExtra("ratingID", ratingID.get(position));
                                    startActivity(i);
                                }
                                else
                                {
                                    Toast.makeText(getApplicationContext(), "You can't comment to other client's comment", Toast.LENGTH_LONG).show();
                                }
                            }
                            //
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
            }
        });

        final Button back = findViewById(R.id.backToPriority);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                myRef.child("usersType").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Toast.makeText(getApplicationContext(),  snapshot.getValue().toString(), Toast.LENGTH_LONG).show();
                        if (snapshot.getValue().toString().equals("owner"))
                        {
                            Intent i = new Intent(new Intent(getApplicationContext(), PropertiesActivity.class));
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Click on the back arrow of your android", Toast.LENGTH_LONG).show();
                        }
                    }
//
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}