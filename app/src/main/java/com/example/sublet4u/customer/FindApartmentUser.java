package com.example.sublet4u.customer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.sublet4u.R;
import com.example.sublet4u.owner.WatchReviewsActivity;
import com.example.sublet4u.data.model.Apartment;
import com.example.sublet4u.data.model.Invitation;
import com.example.sublet4u.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class FindApartmentUser extends AppCompatActivity {
    DatabaseReference reference;
    public Apartment apart;
    public String apartID;
    private DatabaseReference myRef;
    private StorageReference storageRef;
    private FirebaseAuth mAuth;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener1;
    private TextView mDisplayArrive;
    private TextView mDisplayLeave;
    private TextView showTheArriveDate;
    private TextView showTheLeaveDate;
    public String arriveDate, leaveDate;
    int p=0;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findapartment);
        final TextView yourName = findViewById(R.id.yourName);
        List <String> allApartments = new ArrayList<String>();
        mDisplayLeave = findViewById(R.id.displayLeave);
        mDisplayArrive = findViewById(R.id.displayArrive);
        showTheArriveDate = (TextView) findViewById(R.id.showTheArrive);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //arrive
        showTheArriveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(FindApartmentUser.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {



                String int_day_tostring = "" + day;
                String int_month_tostring = ""+ (month+1);
                String date ="";
                if (int_day_tostring.length() < 2)
                    date = "0"+day+"/";
                else
                    date = day + "/";
                if (int_month_tostring.length() < 2)
                    date = date+"0"+(month+1)+"/"+year;
                else
                    date = date+(month+1)+"/"+year;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    Date date1 = (cal.getTime());
                    Date date2 = sdf.parse(date);
                    if (date1.compareTo(date2) > 0)
                    {
                        Toast.makeText(getApplicationContext(), "The arrive date is before than the current date", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        showTheArriveDate.setText(date);
                        arriveDate = (date);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        //leave
        showTheLeaveDate = (TextView) findViewById(R.id.showTheLeave);
        showTheLeaveDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(FindApartmentUser.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener1, year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day)
            {
                String int_day_tostring = "" + day;
                String int_month_tostring = ""+ (month+1);
                String date = "";
                if (int_day_tostring.length() < 2)
                    date = "0"+day+"/";
                else
                    date = day + "/";
                if (int_month_tostring.length() < 2)
                    date = date+"0"+(month+1)+"/"+year;
                else
                    date = date+(month+1)+"/"+year;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    Date date1 = sdf.parse(arriveDate);
                    Date date2 = sdf.parse(date);
                    if (date1.compareTo(date2) > 0)
                    {
                        Toast.makeText(getApplicationContext(), "The arrive date is later than the leave date", Toast.LENGTH_LONG).show();
                    }
                    else if (date1.compareTo(date2) == 0)
                    {
                        showTheLeaveDate.setText(date);
                        leaveDate = date;
                    }
                    else
                    {
                        showTheLeaveDate.setText(date);
                        leaveDate = date;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        yourName.setText(mAuth.getCurrentUser().getDisplayName());
        Query listApartment = myRef.child("apartment").orderByValue();
        listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot s : snapshot.getChildren()){
                    try {
                        allApartments.add(s.getKey());
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }

                check(allApartments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.client_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemProfile:
                Intent i1 = new Intent(new Intent(getApplicationContext(), ProfileActivity.class));
                startActivity(i1);
                return true;
            case R.id.itemInbox:
                Intent i2 = new Intent(new Intent(getApplicationContext(), ClientInBoxActivity.class));
                startActivity(i2);
                return true;
            case R.id.itemSetting:
                Intent i3 = new Intent(new Intent(getApplicationContext(), SettingsClientActivity.class));
                startActivity(i3);
                return true;
            default:
                Toast.makeText(this, "nothing chosen", Toast.LENGTH_SHORT);
            return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(String ID){
        final TextView addressInImg = findViewById(R.id.addressInImg);
        final TextView nameInImg = findViewById(R.id.nameInImg);
        final TextView descriptionInImg = findViewById(R.id.descriptionInImg);
        final TextView apartmentPrice = findViewById(R.id.price);
        final ImageView apaImage = findViewById(R.id.midImage);

        storageRef.child("images/"+ ID +"/firstIm").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        apaImage.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        myRef.child("apartment").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                apart = snapshot.getValue(Apartment.class);
                                descriptionInImg.setText(apart.desc);
                                nameInImg.setText(apart.name);
                                addressInImg.setText(apart.address);
                                apartmentPrice.setText(Integer.toString(apart.price));
                                p = apart.price;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "can't load apartment", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {@Override public void onFailure(@NonNull Exception exception) {Toast.makeText(getApplicationContext(), "image failed "+exception.toString(), Toast.LENGTH_LONG).show(); }});

    }

    private void check (List<String> apartments){
        final Button like = findViewById(R.id.like);
        final Button dislike = findViewById(R.id.dislike);
        final Button design = findViewById(R.id.design);

        Iterator<String> appartmentsIterator = apartments.iterator();
        apartID = appartmentsIterator.next();
        loadData(apartID);

        like.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                if(!(showTheArriveDate.getText().equals("Set here your arrive date")) && !(showTheLeaveDate.getText().equals("Set here your leave date"))) {
                    String invitation_id = myRef.child("Invitations").push().getKey();
                    System.out.println(apartID);
                    myRef.child("Invitations").child(invitation_id).setValue(new Invitation(apartID, mAuth.getUid(), arriveDate, leaveDate, p, mAuth.getCurrentUser().getDisplayName(), apart.name));
                    if (appartmentsIterator.hasNext()) {
                        apartID = appartmentsIterator.next();
                        loadData(apartID);
                        showTheArriveDate.setText("Set here your arrive date");
                        showTheLeaveDate.setText("Set here your leave date");
                    } else {
                        Toast.makeText(getApplicationContext(), "no more apartments", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You should choose your arrive date and your leave date", Toast.LENGTH_LONG).show();
                }
            }
        });
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //just skip the picture and keep going to the next apartment
                if (appartmentsIterator.hasNext()){
                    apartID = appartmentsIterator.next();
                    loadData(apartID);
                    showTheArriveDate.setText("Set here your arrive date");
                    showTheLeaveDate.setText("Set here your leave date");
                }
                else {
                    Toast.makeText(getApplicationContext(), "no more apartments", Toast.LENGTH_LONG).show();
                }
            }
        });
        design.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //just skip the picture and keep going to the next apartment
                Intent i = new Intent(new Intent(getApplicationContext(), ProfileActivity.class));
                startActivity(i);
            }
        });
        final Button watchRating = findViewById(R.id.ratingOfThisApa);
        watchRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), WatchReviewsActivity.class));
                i.putExtra("apartmentID", apartID);
                startActivity(i);
            }
        });
        final Button signOut = findViewById(R.id.signOut);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), LoginActivity.class));
                startActivity(i);
            }
        });

    }
}
