package com.example.sublet4u.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Invitation;
import com.example.sublet4u.data.model.Respond;
import com.example.sublet4u.owner.ConfinInviteActivity;
import com.example.sublet4u.owner.OwnerActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClientInBoxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_in_box);
        ListView mListView = findViewById(R.id.listviewInbox);

        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();

        myRef.child("Invitations").orderByChild("clientID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> invites_ids = new ArrayList<>();
                for(DataSnapshot s : snapshot.getChildren()){
                    Invitation invite = s.getValue(Invitation.class);
                    assert invite != null;
                    if(invite.clientID.equals(mAuth.getUid()) && invite.responded){//get invites that were responded
                        invites_ids.add(s.getKey());

                    }
                }

                myRef.child("Responds").orderByChild("invitationID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Respond> resp = new ArrayList<>();
                        for(DataSnapshot s : snapshot.getChildren()){
                            Respond respond = s.getValue(Respond.class);
                            System.out.println(s.getKey()+" "+respond.invitationID);
                            assert respond != null;
                            if(invites_ids.contains(respond.invitationID)){
                                resp.add(respond);
                                System.out.println("in"+s.getKey());
                            }
                        }
                        System.out.println(resp.size());
                        mListView.setAdapter(new ArrayAdapter<Respond>(ClientInBoxActivity.this,android.R.layout.simple_list_item_1,resp));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println(error.toString());
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.toString());
            }
        });
    }
}