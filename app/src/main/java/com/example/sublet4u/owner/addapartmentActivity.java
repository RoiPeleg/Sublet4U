package com.example.sublet4u.owner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Apartment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

public class  addapartmentActivity extends AppCompatActivity {
    private StorageReference storageRef;
    private String picturePath;
    private ImageSwitcher imageSwitcher;
    private Button previousBtn, nextBtn, addPicsBtn;
    private ArrayList<Uri> photos;
    private static final int PICK_IMAGES_CODE = 0;
    int position = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addapartment);
        final Button done = findViewById(R.id.done);
        addPicsBtn = findViewById(R.id.addPhotosbtn);
        previousBtn = findViewById(R.id.btnPrevious);
        nextBtn = findViewById(R.id.btnNext);
        final EditText name = findViewById(R.id.Name);
        final EditText desc = findViewById(R.id.description);
        final EditText address = findViewById(R.id.address);
        final EditText price = findViewById(R.id.price);
        imageSwitcher = findViewById(R.id.imageView);
        photos = new ArrayList<>();
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                return imageView;
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ap_id = myRef.child("apartment").push().getKey();
                if (name.getText().toString().equals("") || name.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    Toast.makeText(getApplicationContext(), "Enter a real name", Toast.LENGTH_LONG).show();
                } else if (desc.getText().toString().equals("") || desc.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    Toast.makeText(getApplicationContext(), "Enter a normal decription about you", Toast.LENGTH_LONG).show();
                } else if (address.getText().toString().equals("") || address.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    Toast.makeText(getApplicationContext(), "Enter a real address", Toast.LENGTH_LONG).show();
                } else if (price.getText().toString().equals("") || !(price.getText().toString().matches("\\d+(?:\\.\\d+)?"))) {
                    Toast.makeText(getApplicationContext(), "Enter a positive and correct price", Toast.LENGTH_LONG).show();
                }
                else if (photos.isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Add a picture please", Toast.LENGTH_LONG).show();
                }
                else
                {
                    myRef.child("apartment").child(ap_id).setValue(new Apartment(name.getText().toString(), desc.getText().toString(), address.getText().toString(),
                            mAuth.getCurrentUser().getUid(), Integer.parseInt(price.getText().toString())));
                    for (int i=0; i<photos.size();i++) {
                        StorageReference imageRef = storageRef.child("images/").child(ap_id + "").child("photo" + i);
                        UploadTask uploadTask = imageRef.putFile(photos.get(i));

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                    Intent i = new Intent(new Intent(getApplicationContext(), OwnerActivity.class));
                    startActivity(i);
                }
            }
        });
        addPicsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImagesIntent();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < photos.size()-1){
                    position++;
                    imageSwitcher.setImageURI(photos.get(position));
                }
                else{
                    Toast.makeText(addapartmentActivity.this, "No next images", Toast.LENGTH_SHORT).show();
                }
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0){
                    position--;
                    imageSwitcher.setImageURI(photos.get(position));
                }
                else{
                    Toast.makeText(addapartmentActivity.this, "No previous images", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickImagesIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image(s)"), PICK_IMAGES_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES_CODE){
            if (resultCode == Activity.RESULT_OK){
                if(data.getClipData() != null){
                    int count = data.getClipData().getItemCount();
                    for (int i=0;i<count;i++){
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        photos.add(imageUri);
                    }
                    imageSwitcher.setImageURI(photos.get(0));
                    position = 0;
                }
                else{
                    Uri imageUri = data.getData();
                    photos.add(imageUri);
                    imageSwitcher.setImageURI(photos.get(0));
                    position = 0;
                }
            }
        }
    }

}