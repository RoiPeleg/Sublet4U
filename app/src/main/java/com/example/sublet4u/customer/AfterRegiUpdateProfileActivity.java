package com.example.sublet4u.customer;

import android.Manifest;
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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sublet4u.R;
import com.example.sublet4u.data.model.Client;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class AfterRegiUpdateProfileActivity extends AppCompatActivity
{
    private ImageView imageView;
    private StorageReference storageRef;
    private String picturePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_regi_update_profile);
        final Button updated = findViewById(R.id.update10);
        final Button addPics = findViewById(R.id.addPics2);
        final EditText name = findViewById(R.id.clientName2);
        final EditText desc = findViewById(R.id.clientDesc2);
        final EditText sex = findViewById(R.id.clientSex2);
        imageView = findViewById(R.id.clientPic2);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        updated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String client_id = myRef.child("client").child(mAuth.getUid()).getKey();
                if (name.getText().toString().equals("") || name.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    Toast.makeText(getApplicationContext(), "Enter a real name", Toast.LENGTH_LONG).show();
                } else if (desc.getText().toString().equals("") || desc.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    Toast.makeText(getApplicationContext(), "Enter a normal decription about you", Toast.LENGTH_LONG).show();
                } else if (!sex.getText().toString().equals("Male") && !sex.getText().toString().equals("Female") && !sex.getText().toString().equals("Other")) {
                    Toast.makeText(getApplicationContext(), "Enter Female or Male or Other", Toast.LENGTH_LONG).show();
                }
                else if (picturePath == null)
                {
                    Toast.makeText(getApplicationContext(), "Add a picture please", Toast.LENGTH_LONG).show();
                }
                else
                {
                    myRef.child("client").child(client_id).setValue(new Client(name.getText().toString(), desc.getText().toString(),
                            mAuth.getCurrentUser().getUid(), sex.getText().toString()));
                    Uri file = Uri.fromFile(new File(picturePath));
                    StorageReference riversRef = storageRef.child("imagesClient/").child(client_id + "").child("/firstIm");
                    UploadTask uploadTask = riversRef.putFile(file);

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

                    Intent i = new Intent(new Intent(getApplicationContext(), FindApartmentUser.class));
                    startActivity(i);
                }
            }
        });
        addPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (ActivityCompat.checkSelfPermission(AfterRegiUpdateProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AfterRegiUpdateProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (selectedImage != null) {
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        picturePath = cursor.getString(columnIndex);

                        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                        cursor.close();
                    }
                }
            }
        }
    }

}