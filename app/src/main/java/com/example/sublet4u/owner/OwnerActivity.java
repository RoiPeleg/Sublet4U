package com.example.sublet4u.owner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sublet4u.R;
import com.example.sublet4u.customer.ClientInBoxActivity;
import com.example.sublet4u.customer.ProfileActivity;
import com.example.sublet4u.customer.SettingsClientActivity;
import com.example.sublet4u.data.model.Invitation;
import com.example.sublet4u.ui.login.LoginActivity;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);
        final TextView textView = findViewById(R.id.textViewName);
        ListView mListView = findViewById(R.id.listview);
        Toolbar toolbar = findViewById(R.id.ownerToolbar);
        setSupportActionBar(toolbar);

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

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.owner_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemAddApartment:
                Intent i1 = new Intent(new Intent(getApplicationContext(), addapartmentActivity.class));
                startActivity(i1);
                return true;
            case R.id.itemProperty:
                Intent i2 = new Intent(new Intent(getApplicationContext(), PropertiesActivity.class));
                startActivity(i2);
                return true;
            case R.id.itemOwnerSignOut:
                Toast.makeText(this, "Sign Out", Toast.LENGTH_SHORT);
                return true;
            default:
                Toast.makeText(this, "nothing chosen", Toast.LENGTH_SHORT);
                return super.onOptionsItemSelected(item);
        }
    }
}
