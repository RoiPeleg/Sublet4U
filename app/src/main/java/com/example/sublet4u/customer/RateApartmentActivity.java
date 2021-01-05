package com.example.sublet4u.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Invitation;
import com.example.sublet4u.data.model.Rating;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;

public class RateApartmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_apartment);
        final Button sendReview = findViewById(R.id.SendReview);
        final Button declineReview = findViewById(R.id.declineReview);
        final EditText rateNumber = findViewById(R.id.rateNumber);
        final EditText writeReview = findViewById(R.id.reviewLetter);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        declineReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(RateApartmentActivity.this, ClientInBoxActivity.class);
                startActivity(i);
            }
        });
        sendReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (rateNumber.getText().toString().matches("\\d+(?:\\.\\d+)?"))
                {
                    if (writeReview.getText().toString().equals(""))
                    {
                        Toast.makeText(getApplicationContext(), "Enter your review please", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        final int value = Integer.parseInt(rateNumber.getText().toString());
                        if(value <= 10 && value >= 1)
                        {
                            String rate_id = myRef.child("Rating").push().getKey();
                            Intent receivedIntent  = getIntent();
                            String invitationID = receivedIntent.getStringExtra("invitationID");
                            myRef.child("Invitations").child(invitationID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    Invitation inv = snapshot.getValue(Invitation.class);
                                    String ap_id = inv.getApartmentID();
                                    myRef.child("Rating").child(rate_id).setValue(new Rating(invitationID, ap_id ,rateNumber.getText().toString(), writeReview.getText().toString()));
                                    Query ownerApa =  myRef.child("Rating").orderByChild("apartmentID").equalTo(ap_id);
                                    ownerApa.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            double sum = 0;
                                            int count = 0;
                                            for (DataSnapshot s : snapshot.getChildren())
                                            {
                                                Rating rate = s.getValue(Rating.class);
                                                assert rate != null;
                                                sum += Double.parseDouble(rate.rateNumber);
                                                count++;
                                            }
                                            DecimalFormat twoDForm = new DecimalFormat("#.##");
                                            myRef.child("apartment").child(ap_id).child("grade").setValue(Double.valueOf(twoDForm.format(sum / count)));
                                            myRef.child("apartment").child(ap_id).child("invertedGrade").setValue(Double.valueOf(twoDForm.format(sum / (-1 * count) )));

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    Toast.makeText(getApplicationContext(), "sent", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(RateApartmentActivity.this, ClientInBoxActivity.class);
                                    startActivity(i);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "The rate should be between 1 to 10", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "The rate should be between 1 to 10", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}