package com.example.sublet4u.customer;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Respond;
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
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
    double p=0;

    class ViewPagerAdapter extends PagerAdapter {

        // Context object
        Context context;

        // Array of images
        ArrayList<StorageReference> images;

        // Layout Inflater
        LayoutInflater mLayoutInflater;


        // Viewpager Constructor
        public ViewPagerAdapter(Context context, ArrayList<StorageReference> images) {
            this.context = context;
            this.images = images;
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // return the number of images
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((LinearLayout) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            // inflating the item.xml
            View itemView = mLayoutInflater.inflate(R.layout.item_image, container, false);

            // referencing the image view from the item.xml file
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageToClient);

            // setting the image in the imageView
            images.get(position).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>(){

                @Override
                public void onSuccess(byte[] bytes) {
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            });

            // Adding the View
            Objects.requireNonNull(container).addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

            container.removeView((LinearLayout) object);
        }
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findapartment);
        final TextView yourName = findViewById(R.id.yourName);
        List <String> allApartments = new ArrayList<String>();
        mDisplayLeave = findViewById(R.id.displayLeave);
        mDisplayArrive = findViewById(R.id.displayArrive);
        showTheArriveDate = (TextView) findViewById(R.id.showTheArrive);
        Toolbar toolbar = findViewById(R.id.clientToolbar);
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
                    if (!showTheLeaveDate.getText().equals("Set here your leave date"))
                    {
                        Date date5 = sdf.parse(showTheLeaveDate.getText().toString());
                        if (date2.compareTo(date5) > 0)
                        {
                            Toast.makeText(getApplicationContext(), "The arrive date is later than the leave date", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            showTheArriveDate.setText(date);
//                            Toast.makeText(getApplicationContext(), showTheArriveDate.getText().toString(), Toast.LENGTH_LONG).show();
                            arriveDate = (date);
                        }
                    }
                    else
                    {
                        myRef.child("Invitations").orderByChild("apartmentID").equalTo(apartID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                for (DataSnapshot s : snapshot.getChildren())
                                {
                                    Invitation invitation = s.getValue(Invitation.class);
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
                                    Date date1 = null;
                                    Date date2 = null;
                                    Date date3 = null;
                                    try {
                                        date1 = sdf.parse(showTheArriveDate.getText().toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        assert invitation != null;
                                        date2 = sdf.parse(invitation.arriveDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        date3 = sdf.parse(invitation.leaveDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if ((date1.compareTo(date2) >= 0 && date1.compareTo(date3) <= 0))
                                    {
                                        myRef.child("Responds").orderByChild("invitationID").equalTo(s.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                for (DataSnapshot ss : snapshot.getChildren())
                                                {
                                                    Respond respond = ss.getValue(Respond.class);
                                                    if (respond.status)
                                                    {
                                                        Toast.makeText(getApplicationContext(), "This date already caught up between:", Toast.LENGTH_LONG).show();
                                                        Toast.makeText(getApplicationContext(),invitation.arriveDate + "-" + invitation.leaveDate , Toast.LENGTH_LONG).show();
                                                        showTheArriveDate.setText("Set here your arrive date");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (date1.compareTo(date2) > 0)
                        {
                            Toast.makeText(getApplicationContext(), "The arrive date is before than the current date", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            showTheArriveDate.setText(date);
//                            Toast.makeText(getApplicationContext(), showTheArriveDate.getText().toString(), Toast.LENGTH_LONG).show();
                            arriveDate = (date);
                        }
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
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
                    if (showTheArriveDate.getText().equals("Set here your arrive date"))
                    {
                        Toast.makeText(getApplicationContext(), "Choose your arrive date first", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Date date1 = sdf.parse(arriveDate);
                        Date date2 = sdf.parse(date);
                        myRef.child("Invitations").orderByChild("apartmentID").equalTo(apartID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                for (DataSnapshot s : snapshot.getChildren())
                                {
                                    Invitation invitation = s.getValue(Invitation.class);
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
                                    Date date = null;
                                    Date date1 = null;
                                    Date date2 = null;
                                    Date date3 = null;
                                    try {
                                        date = sdf.parse(showTheArriveDate.getText().toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        date1 = sdf.parse(showTheLeaveDate.getText().toString());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        assert invitation != null;
                                        date2 = sdf.parse(invitation.arriveDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        date3 = sdf.parse(invitation.leaveDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if ((date1.compareTo(date2) >= 0 && date1.compareTo(date3) <= 0) || ((date2.compareTo(date) >= 0 && date2.compareTo(date1) <= 0)
                                    && (date3.compareTo(date) >= 0 && date3.compareTo(date1) <= 0)))
                                    {
                                        myRef.child("Responds").orderByChild("invitationID").equalTo(s.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot)
                                            {
                                                for (DataSnapshot ss : snapshot.getChildren())
                                                {
                                                    Respond respond = ss.getValue(Respond.class);
                                                    if (respond.status)
                                                    {
                                                        Toast.makeText(getApplicationContext(), "This date already caught up between:", Toast.LENGTH_LONG).show();
                                                        Toast.makeText(getApplicationContext(),invitation.arriveDate + "-" + invitation.leaveDate , Toast.LENGTH_LONG).show();
                                                        showTheLeaveDate.setText("Set here your arrive date");
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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
        Query listApartment = myRef.child("apartment").orderByChild("invertedGrade");
        listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot s : snapshot.getChildren()){
                    try
                    {
//                        Apartment ap = s.getValue(Apartment.class);
//                        if (ap.price > 170)
//                        {
                            allApartments.add(s.getKey());
//                        }
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
            case R.id.itemCLientSignOut:
                mAuth.signOut();
                finish();
                Toast.makeText(getApplicationContext(), "Sign Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
            default:
                Toast.makeText(getApplicationContext(), "nothing chosen", Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
    }

    private void loadData(String ID){
        final TextView addressInImg = findViewById(R.id.addressInImg);
        final TextView nameInImg = findViewById(R.id.nameInImg);
        final TextView descriptionInImg = findViewById(R.id.descriptionInImg);
        final TextView textGrade = findViewById(R.id.textGrade);
        final TextView apartmentPrice = findViewById(R.id.price);
        final ViewPager apaImage = findViewById(R.id.midImage);
        final ArrayList<StorageReference> photos = new ArrayList<>();

        StorageReference listRef = storageRef.child("images/"+ID);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                        }

                        for (StorageReference item : listResult.getItems()) {
                            photos.add(item);
                        }
                        ViewPagerAdapter IPA = new ViewPagerAdapter(getApplicationContext(), photos);

                        // Adding the Adapter to the ViewPager
                        apaImage.setAdapter(IPA);
                        myRef.child("apartment").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                apart = snapshot.getValue(Apartment.class);
                                descriptionInImg.setText(apart.desc);
                                nameInImg.setText(apart.name);
                                addressInImg.setText(apart.address);
                                p = apart.price;
                                if (apart.isOnSale ) {
                                    apartmentPrice.setTextColor(Color.RED);
                                    p = p * ((100-(double)apart.discount)/100);
                                    apartmentPrice.setText(Double.toString(p) + " SALE!");
                                }
                                else {
                                    apartmentPrice.setText(Double.toString(p));
                                    apartmentPrice.setTextColor(Color.BLACK);
                                }
                                if (apart.grade == 0)
                                {
                                    textGrade.setText("Apartment Grade: No Rating Yet");
                                }
                                else
                                {
                                    textGrade.setText("Apartment Grade: " + apart.grade);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "can't load apartment", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });
    }

    private void check (List<String> apartments){
        final Button like = findViewById(R.id.like);
        final Button dislike = findViewById(R.id.dislike);
        final EditText aboveGrade = findViewById(R.id.aboveGrade);
        final EditText untilPrice = findViewById(R.id.untilPrice);
        final Switch switchSale = findViewById(R.id.switchSale);

        switchSale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                List <String> allApartments1 = new ArrayList<String>();
                Query listApartment = myRef.child("apartment").orderByChild("invertedGrade");
                listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot s : snapshot.getChildren()){
                            try
                            {
                                Apartment ap = s.getValue(Apartment.class);
                                if (!aboveGrade.getText().toString().equals("") && !untilPrice.getText().toString().equals(""))
                                {
                                    if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString())
                                            && ap.isOnSale == isChecked && ap.isOnSale
                                    && ap.grade >= Double.parseDouble(aboveGrade.getText().toString()))
                                    {
                                        allApartments1.add(s.getKey());
                                    }
                                    else if (ap.grade >= Double.parseDouble(aboveGrade.getText().toString()) && !isChecked
                                    && ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString()))
                                    {
                                        allApartments1.add(s.getKey());
                                    }
                                }
                                else if (aboveGrade.getText().toString().equals("") && !untilPrice.getText().toString().equals(""))
                                {
                                    if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString()) && ap.isOnSale == isChecked && ap.isOnSale)
                                    {
                                        allApartments1.add(s.getKey());
                                    }
                                    else if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString()) && !isChecked)
                                    {
                                        allApartments1.add(s.getKey());
                                    }
                                }
                                else if (!aboveGrade.getText().toString().equals("") && untilPrice.getText().toString().equals(""))
                                {
                                    if (ap.grade >= Double.parseDouble(aboveGrade.getText().toString()) && ap.isOnSale == isChecked && ap.isOnSale)
                                    {
                                        allApartments1.add(s.getKey());
                                    }
                                    else if (ap.grade >= Double.parseDouble(aboveGrade.getText().toString()) && !isChecked)
                                    {
                                        allApartments1.add(s.getKey());
                                    }
                                }
                                else if (aboveGrade.getText().toString().equals("") && untilPrice.getText().toString().equals(""))
                                {
                                    if (ap.isOnSale == isChecked && ap.isOnSale)
                                    {
                                        allApartments1.add(s.getKey());
                                    }
                                    else if (!isChecked)
                                    {
                                        allApartments1.add(s.getKey());
                                    }
                                }

                            }
                            catch (Exception e){
                                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }

                        if (allApartments1.isEmpty())
                        {
                            Toast.makeText(getApplicationContext(), "No Apartments available", Toast.LENGTH_LONG).show();
                        }
                        else
                            check(allApartments1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        untilPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (untilPrice.getText().toString().matches("\\d+(?:\\.\\d+)?"))
                {
                    List <String> allApartments2 = new ArrayList<String>();
                    Query listApartment = myRef.child("apartment").orderByChild("invertedGrade");
                    listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot s : snapshot.getChildren()){
                                try
                                {
                                    Apartment ap = s.getValue(Apartment.class);
                                    if (!aboveGrade.getText().toString().equals("") && switchSale.isChecked())
                                    {
                                        if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString())
                                        && ap.grade >= Double.parseDouble(aboveGrade.getText().toString())
                                        && ap.isOnSale)
                                        {
                                            allApartments2.add(s.getKey());
                                        }
                                    }
                                    else if (!aboveGrade.getText().toString().equals("") && ! switchSale.isChecked())
                                    {
                                        if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString())
                                                && ap.grade >= Double.parseDouble(aboveGrade.getText().toString()))
                                        {
                                            allApartments2.add(s.getKey());
                                        }
                                    }
                                    else if (aboveGrade.getText().toString().equals("") &&  switchSale.isChecked())
                                    {
                                        if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString()) && ap.isOnSale)
                                        {
                                            allApartments2.add(s.getKey());
                                        }
                                    }
                                    else if (aboveGrade.getText().toString().equals("") && ! switchSale.isChecked())
                                    {
                                        if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString()))
                                        {
                                            allApartments2.add(s.getKey());
                                        }
                                    }
                                }
                                catch (Exception e){
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                            if (allApartments2.isEmpty())
                            {
                                Toast.makeText(getApplicationContext(), "No Apartments available", Toast.LENGTH_LONG).show();
                            }
                            else
                                check(allApartments2);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else if (untilPrice.getText().toString().equals(""))
                {
                    List <String> allApartments2 = new ArrayList<String>();
                    Query listApartment = myRef.child("apartment").orderByChild("invertedGrade");
                    listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot s : snapshot.getChildren()){
                                try
                                {
                                    Apartment ap = s.getValue(Apartment.class);
                                    if (!aboveGrade.getText().toString().equals("") &&  switchSale.isChecked())
                                    {
                                        if (ap.grade >= Double.parseDouble(aboveGrade.getText().toString())
                                                && ap.isOnSale)
                                        {
                                            allApartments2.add(s.getKey());
                                        }
                                    }
                                    else if (!aboveGrade.getText().toString().equals("") && !switchSale.isChecked())
                                    {
                                        if (ap.grade >= Double.parseDouble(aboveGrade.getText().toString()))
                                        {
                                            allApartments2.add(s.getKey());
                                        }
                                    }
                                    else if (aboveGrade.getText().toString().equals("") && switchSale.isChecked())
                                    {
                                        if (ap.isOnSale)
                                        {
                                            allApartments2.add(s.getKey());
                                        }
                                    }
                                    else if (aboveGrade.getText().toString().equals("") && !switchSale.isChecked())
                                    {
                                        allApartments2.add(s.getKey());
                                    }
                                }
                                catch (Exception e){
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                            if (allApartments2.isEmpty())
                            {
                                Toast.makeText(getApplicationContext(), "No Apartments available", Toast.LENGTH_LONG).show();
                            }
                            else
                                check(allApartments2);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {
                            Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You should write a number", Toast.LENGTH_LONG).show();
                }
            }
        });
        aboveGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (aboveGrade.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    if (Integer.parseInt(aboveGrade.getText().toString()) <= 10 && Integer.parseInt(aboveGrade.getText().toString()) >= 1) {
                        List<String> allApartments3 = new ArrayList<String>();
                        Query listApartment = myRef.child("apartment").orderByChild("invertedGrade");
                        listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                for (DataSnapshot s : snapshot.getChildren()) {
                                    try {
                                        Apartment ap = s.getValue(Apartment.class);
                                        if (!untilPrice.getText().toString().equals("") && switchSale.isChecked()) {
                                            if (ap.price * ((100 - (double) ap.discount) / 100) <= Integer.parseInt(untilPrice.getText().toString())
                                                    && ap.grade >= Double.parseDouble(aboveGrade.getText().toString())
                                                    && ap.isOnSale) {
                                                allApartments3.add(s.getKey());
                                            }
                                        } else if (!untilPrice.getText().toString().equals("") && !switchSale.isChecked()) {
                                            if (ap.price * ((100 - (double) ap.discount) / 100) <= Integer.parseInt(untilPrice.getText().toString())
                                                    && ap.grade >= Double.parseDouble(aboveGrade.getText().toString())) {
                                                allApartments3.add(s.getKey());
                                            }
                                        } else if (untilPrice.getText().toString().equals("") && switchSale.isChecked()) {
                                            if (ap.grade >= Double.parseDouble(aboveGrade.getText().toString()) && ap.isOnSale) {
                                                allApartments3.add(s.getKey());
                                            }
                                        } else if (untilPrice.getText().toString().equals("") && !switchSale.isChecked()) {
                                            if (ap.grade >= Double.parseDouble(aboveGrade.getText().toString())) {
                                                allApartments3.add(s.getKey());
                                            }
                                        }

                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                                if (allApartments3.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "No Apartments available", Toast.LENGTH_LONG).show();
                                } else
                                    check(allApartments3);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                else if (aboveGrade.getText().toString().equals(""))
                {
                    List <String> allApartments3 = new ArrayList<String>();
                    Query listApartment = myRef.child("apartment").orderByChild("invertedGrade");
                    listApartment.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            for (DataSnapshot s : snapshot.getChildren()){
                                try
                                {
                                    Apartment ap = s.getValue(Apartment.class);
                                    if (!untilPrice.getText().toString().equals("") && switchSale.isChecked())
                                    {
                                        if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString())
                                                && ap.isOnSale)
                                        {
                                            allApartments3.add(s.getKey());
                                        }
                                    }
                                    else if (!untilPrice.getText().toString().equals("") && !switchSale.isChecked())
                                    {
                                        if (ap.price * ((100-(double)ap.discount)/100) <= Integer.parseInt(untilPrice.getText().toString()))
                                        {
                                            allApartments3.add(s.getKey());
                                        }
                                    }
                                    else if (untilPrice.getText().toString().equals("") &&  switchSale.isChecked())
                                    {
                                        if (ap.isOnSale)
                                        {
                                            allApartments3.add(s.getKey());
                                        }
                                    }
                                    else if (untilPrice.getText().toString().equals("") && !switchSale.isChecked())
                                    {
                                        allApartments3.add(s.getKey());
                                    }

                                }
                                catch (Exception e){
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                }
                            }
                            if (allApartments3.isEmpty())
                            {
                                Toast.makeText(getApplicationContext(), "No Apartments available", Toast.LENGTH_LONG).show();
                            }
                            else
                                check(allApartments3);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "unable to load data", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    Toast.makeText(getApplicationContext(), aboveGrade.getText().toString() + "1", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Enter a number between 1-10", Toast.LENGTH_LONG).show();
                }
            }
        });
        Iterator<String> appartmentsIterator = apartments.iterator();

        apartID = appartmentsIterator.next();
        loadData(apartID);
        like.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v){
                if(!(showTheArriveDate.getText().equals("Set here your arrive date")) && !(showTheLeaveDate.getText().equals("Set here your leave date")))
                {
                    String invitation_id = myRef.child("Invitations").push().getKey();
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

        final Button watchRating = findViewById(R.id.ratingOfThisApa);
        watchRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(new Intent(getApplicationContext(), WatchReviewsActivity.class));
                i.putExtra("apartmentID", apartID);
                startActivity(i);
            }
        });

    }
}
