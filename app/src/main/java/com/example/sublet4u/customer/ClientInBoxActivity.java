package com.example.sublet4u.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sublet4u.ChatActivity;
import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Apartment;
import com.example.sublet4u.data.model.Invitation;
import com.example.sublet4u.data.model.Message;
import com.example.sublet4u.data.model.Respond;
import com.example.sublet4u.owner.ConfinInviteActivity;
import com.example.sublet4u.owner.OwnerActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClientInBoxActivity extends AppCompatActivity {
    public static class InvitationViewHolder extends RecyclerView.ViewHolder {
        TextView txt;
        public  InvitationViewHolder(View v) {
            super(v);
            txt = (TextView) itemView.findViewById(R.id.txt);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_in_box);
        ListView mListView = findViewById(R.id.listviewInbox);
        ListView pending = findViewById(R.id.pending);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        myRef.child("Invitations").orderByChild("clientID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> invites_ids = new ArrayList<>();
                ArrayList<Invitation> unResponded = new ArrayList<>();
                for(DataSnapshot s : snapshot.getChildren()){
                    Invitation invite = s.getValue(Invitation.class);
                    assert invite != null;
                    if(invite.clientID.equals(mAuth.getUid()) && invite.responded)//get invites that were responded
                        invites_ids.add(s.getKey());
                    if(invite.clientID.equals(mAuth.getUid()) && !invite.responded)
                        unResponded.add(invite);
                }
                pending.setAdapter(new ArrayAdapter<Invitation>(ClientInBoxActivity.this,android.R.layout.simple_list_item_1,unResponded));
                pending.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String ap_id = snapshot.getValue(Invitation.class).getApartmentID();

                        myRef.child("apartment").child(ap_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String Owner_id = snapshot.getValue(Apartment.class).name;
                                Toast.makeText(getApplicationContext(),Owner_id,Toast.LENGTH_LONG).show();
                                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                                i.putExtra("reID",Owner_id);
                                startActivity(i);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                myRef.child("Responds").orderByChild("invitationID").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<Respond> resp = new ArrayList<>();
                        for(DataSnapshot s : snapshot.getChildren()){
                            Respond respond = s.getValue(Respond.class);
                            assert respond != null;
                            if(invites_ids.contains(respond.invitationID)) resp.add(respond);
                        }
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