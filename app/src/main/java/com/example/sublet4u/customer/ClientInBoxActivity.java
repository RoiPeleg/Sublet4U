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
import android.widget.Button;
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
import com.example.sublet4u.owner.ApplyCommentActivity;
import com.example.sublet4u.owner.ConfinInviteActivity;
import com.example.sublet4u.owner.OwnerActivity;
import com.example.sublet4u.owner.PropertiesActivity;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
                ArrayList<Invitation> inv = new ArrayList<>();
                ArrayList<Invitation> unResponded = new ArrayList<>();
                for(DataSnapshot s : snapshot.getChildren()){
                    Invitation invite = s.getValue(Invitation.class);
                    assert invite != null;
                    if(invite.clientID.equals(mAuth.getUid()) && invite.responded)//get invites that were responded
                    {
                        invites_ids.add(s.getKey());
                        inv.add(invite);
                    }
                    if(invite.clientID.equals(mAuth.getUid()) && !invite.responded)
                        unResponded.add(invite);
                }
                pending.setAdapter(new ArrayAdapter<Invitation>(ClientInBoxActivity.this,android.R.layout.simple_list_item_1,unResponded));
                pending.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String ap_id = unResponded.get(position).getApartmentID();
                        myRef.child("apartment").orderByValue().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot s : snapshot.getChildren()) {

                                    if (s.getKey().equals(ap_id)) {
                                        Apartment apartment = s.getValue(Apartment.class);
                                        Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                                        i.putExtra("reID", apartment.ownerID);
                                        startActivity(i);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
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
                        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                            {
                                myRef.child("Invitations").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {
                                        String date = inv.get(position).leaveDate;
                                        Toast.makeText(getApplicationContext(), date, Toast.LENGTH_LONG).show();
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
                                        Calendar cal = Calendar.getInstance();
                                        cal.add(Calendar.DATE, 0);
                                        Date date1 = (cal.getTime());
                                        Date date2 = null;
                                        try {
                                            date2 = sdf.parse(date);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        assert date2 != null;
                                        if (!resp.get(position).status)
                                        {
                                            Toast.makeText(getApplicationContext(),"This invitation was declined",Toast.LENGTH_LONG).show();
                                        }
                                        else if (date2.compareTo(date1) > 0)
                                        {
                                            Toast.makeText(getApplicationContext(), "You need to finish your invitation", Toast.LENGTH_LONG).show();
                                        }
                                        else if (resp.get(position).status)
                                        {
                                            Intent i = new Intent(getApplicationContext(), RateApartmentActivity.class);
                                            i.putExtra("invitationID", resp.get(position).invitationID);
                                            startActivity(i);
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

        final Button back = findViewById(R.id.hoBackFrom);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(new Intent(getApplicationContext(), FindApartmentUser.class));
                startActivity(i);
            }
        });
    }
}