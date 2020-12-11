package com.example.sublet4u.owner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sublet4u.ConfinInviteActivity;
import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Invitation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OwnerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        final  Button add_ap = findViewById(R.id.add_app);
        final TextView textView = findViewById(R.id.textViewName);

        ListView mListView = findViewById(R.id.listview);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        textView.setText(mAuth.getCurrentUser().getDisplayName());
        ArrayList<String> allApartment = new ArrayList<>();
        //        ListView mListView = (ListView) findViewById(R.id.whoLikesu);
//        final Button whoLikes = findViewById(R.id.seeWhoLikesU);
//        CustomAdapter myAdapter = new CustomAdapter(getApplicationContext(), allApartment);
        textView.setText(mAuth.getCurrentUser().getDisplayName());
        ArrayList <String> allInvitations = new ArrayList<String>();
        Query listInvitations = myRef.child("Invitations");
        Query clientList = myRef.child("client").orderByChild("clienID");
        myRef.child("apartment").orderByChild("ownerID").equalTo(mAuth.getCurrentUser().getUid());
        Query ownerApa = myRef.child("apartment").orderByChild("ownerID");
//        Toast.makeText(getApplicationContext(), mAuth.getCurrentUser().getUid(), Toast.LENGTH_LONG).show();
//        .orderByChild("ownerID").equalTo(String.valueOf(ownerApa.startAt(mAuth.getCurrentUser().getUid())))
        ownerApa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot s : snapshot.getChildren()){
                    if (s.child("ownerID").getValue().equals(mAuth.getCurrentUser().getUid()))
                    {
                        allApartment.add(s.getKey());
                    }

                }
                mListView.setAdapter(new FirebaseListAdapter<Invitation>(OwnerActivity.this, Invitation.class,
                        android.R.layout.two_line_list_item, listInvitations.getRef())
                {
                    int j = 0;
                    // Populate view as needed
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void populateView(View view, Invitation invitation, int position)
                    {
//                        for (int i = 0; i < allApartment.size(); i++) {
//                            if (invitation.getApartmentID().equals(allApartment.get(i)))
//                            {

                                ((TextView) view.findViewById(android.R.id.text1)).setText(invitation.getClientID() + " ClientId");
                                ((TextView) view.findViewById(android.R.id.text2)).setText(invitation.getApartmentID() + " ApartmentId");
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent i = new Intent(OwnerActivity.this, ConfinInviteActivity.class);

                                        i.putExtra("clientID", invitation.getClientID());
                                        Toast.makeText(getApplicationContext(), invitation.getClientID(), Toast.LENGTH_LONG).show();
                                        startActivity(i);
                                    }
                                });
//                                break;
//                            }
//                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "unalbe to load data", Toast.LENGTH_LONG).show();
            }
        });
        add_ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), addapartmentActivity.class));
                startActivity(i);
            }
        });
    }
}
