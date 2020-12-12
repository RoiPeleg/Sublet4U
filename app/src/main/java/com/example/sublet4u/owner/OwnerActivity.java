package com.example.sublet4u.owner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sublet4u.data.model.Apartment;
import com.example.sublet4u.owner.ConfinInviteActivity;
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
import java.util.Objects;

public class OwnerActivity extends AppCompatActivity {
    private static LayoutInflater inflater = null;
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

        textView.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
        ArrayList<String> allApartment = new ArrayList<>();
        textView.setText(mAuth.getCurrentUser().getDisplayName());
        ArrayList <String> allInvitations = new ArrayList<String>();


        Query ownerApa =  myRef.child("apartment").orderByChild("ownerID").equalTo(mAuth.getCurrentUser().getUid());


        ownerApa.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot s : snapshot.getChildren()) {
                    allApartment.add(s.getKey());//get owner's apartments
                }
                ArrayList<String> inv_ids = new ArrayList<>();
                myRef.child("Invitations").orderByChild("apartmentID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Invitation> inv = new ArrayList<>();
                        for(DataSnapshot s : snapshot.getChildren()){
                            Invitation invite = s.getValue(Invitation.class);
                            assert invite != null;
                            if(allApartment.contains(invite.apartmentID) && !invite.responded){
                                inv.add(invite);
                                inv_ids.add(s.getKey());
                            }
                        }

                        mListView.setAdapter(new ArrayAdapter<Invitation>(OwnerActivity.this,android.R.layout.simple_list_item_1,inv));
                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent i = new Intent(OwnerActivity.this, ConfinInviteActivity.class);
                                i.putExtra("clientID",inv.get(position).getClientID());
                                i.putExtra("clientName",inv.get(position).clientName);
                                i.putExtra("invitationID",inv_ids.get(position));
                                i.putExtra("ap_name",inv.get(position).apartmentName);
                                startActivity(i);
                            }
                        });

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
            }
        });

        //Sends to add new apartment page
        add_ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), addapartmentActivity.class));
                startActivity(i);
            }
        });
    }
}
