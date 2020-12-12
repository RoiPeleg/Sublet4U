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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class SettingsClientActivity extends AppCompatActivity
{
    private ImageView imageView;
    private StorageReference storageRef;
    private String picturePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_client);
        final Button back = findViewById(R.id.goBack);
        final Button updateDesc = findViewById(R.id.updateDesc);
        final Button updateSex = findViewById(R.id.updateSex);
        final Button updateImg = findViewById(R.id.updateImg);
        final Button addPics = findViewById(R.id.addPics);
        final EditText desc = findViewById(R.id.clientDesc);
        final EditText sex = findViewById(R.id.clientSex);
        imageView = findViewById(R.id.clientPic);
        FirebaseAuth mAuth;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String client_id = myRef.child("client").child(mAuth.getUid()).getKey();
        myRef.child("client").child(client_id).child("clientID").setValue(client_id);
        updateDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (desc.getText().toString().equals("") || desc.getText().toString().matches("\\d+(?:\\.\\d+)?")) {
                    Toast.makeText(getApplicationContext(), "Enter a normal decription about you", Toast.LENGTH_LONG).show();
                }
                else {
                    String client_id = myRef.child("client").child(mAuth.getUid()).getKey();
                    myRef.child("client").child(client_id).child("desc").setValue(desc.getText().toString());
                    Toast.makeText(getApplicationContext(), "Description updated", Toast.LENGTH_LONG).show();
                }
            }
        });
        updateSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), sex.getText().toString(), Toast.LENGTH_LONG).show();
                if (sex.getText().toString().equals("Male") || sex.getText().toString().equals("Female") ||sex.getText().toString().equals("Other")) {
                    String client_id = myRef.child("client").child(mAuth.getUid()).getKey();
                    myRef.child("client").child(client_id).child("sex").setValue(sex.getText().toString());
                    Toast.makeText(getApplicationContext(), "Sex updated", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Enter Female or Male or Other", Toast.LENGTH_LONG).show();
                }
            }
        });
        updateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (picturePath != null) {
                    String client_id = myRef.child("client").child(mAuth.getUid()).getKey();
                    Uri file = Uri.fromFile(new File(picturePath));
                    StorageReference riversRef = storageRef.child("imagesClient/").child(client_id+"").child("/firstIm");
                    UploadTask uploadTask = riversRef.putFile(file);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(getApplicationContext(),"Upload Failed",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                        }});
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"you can't enter nothing",Toast.LENGTH_SHORT).show();
                }
            }
        });
        addPics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (ActivityCompat.checkSelfPermission(SettingsClientActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SettingsClientActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(new Intent(getApplicationContext(), ProfileActivity.class));
                startActivity(i);
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