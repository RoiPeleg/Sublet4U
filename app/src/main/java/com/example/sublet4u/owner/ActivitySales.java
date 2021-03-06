package com.example.sublet4u.owner;

import android.content.Intent;
import android.os.Bundle;

import com.example.sublet4u.customer.FindApartmentUser;
import com.example.sublet4u.data.model.Apartment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sublet4u.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitySales extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        Spinner spinner = findViewById(R.id.apa_chooser);
        Button upload = findViewById(R.id.upload);
        Button back = findViewById(R.id.btnBack);
        TextView discount = findViewById(R.id.discount);
        CheckBox enable = findViewById(R.id.Enable);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        DatabaseReference ap = myRef.child("apartment").getRef();
        Map<String, Apartment> mapper = new HashMap<>();
        ap.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                List<String> ls = new ArrayList<>();
                for (DataSnapshot s: dataSnapshot.getChildren()) {
                    String ownerID = s.child("ownerID").getValue(String.class);
                    if (ownerID!=null && ownerID.equals(mAuth.getUid())){
                        ls.add(s.child("name").getValue(String.class));
                        mapper.put(s.getKey(),s.getValue(Apartment.class));
                    }
                }

                ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(ActivitySales.this, android.R.layout.simple_spinner_item,ls);
                addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(addressAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        InputFilter in = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    // Remove the string out of destination that is to be replaced
                    String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
                    // Add the new string in
                    newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
                    int input = Integer.parseInt(newVal);
                    if (isInRange(1, 99, input))
                        return null;
                } catch (NumberFormatException nfe) { }
                return "";
            }
            private boolean isInRange(int a, int b, int c) {
                return b > a ? c >= a && c <= b : c >= b && c <= a;
            }
        };
        discount.setFilters(new InputFilter[]{in});
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spinner.getSelectedItemPosition()!= AdapterView.INVALID_POSITION){
                    int id = spinner.getSelectedItemPosition();
                    List<Apartment> ls = new ArrayList<>(mapper.values());
                    List<String> ids = new ArrayList<>(mapper.keySet());
                    Apartment a = ls.get(id);
                    a.isOnSale = enable.isChecked();
                    a.discount = Integer.parseInt(discount.getText().toString());
                    ap.child(ids.get(id)).setValue(a);
                }
                Intent i = new Intent(new Intent(getApplicationContext(), OwnerActivity.class));
                startActivity(i);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(new Intent(getApplicationContext(), OwnerActivity.class));
                startActivity(i);
            }
        });
    }
}