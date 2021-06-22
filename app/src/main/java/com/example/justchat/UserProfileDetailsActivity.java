package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

public class UserProfileDetailsActivity extends AppCompatActivity {

    private Button update;
    private EditText name, status;
    private CircularImageView profile;
    Uri imagepath;
    private static int Pick_Image = 143;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference userref;
    FirebaseStorage firebaseStorage;
    String na,sa,pi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_details);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        update = findViewById(R.id.updatebuttom);
        name = findViewById(R.id.entername);
        status = findViewById(R.id.enterstatus);
        profile = findViewById(R.id.circular);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                senddata();
                startActivity(new Intent(UserProfileDetailsActivity.this,Main2Activity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Pick_Image);


            }
        });
    }

    private void senddata() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference dref = firebaseDatabase.getReference().child("USERS1").child(firebaseAuth.getUid());
        data();
       // Contactde cc=new Contactde(na,sa,pi);
       // dref.setValue(cc);
        // Profiledata prm =new Profiledata(firstname,lastname,age,gmail,Phoneno,Address,Gender,Link);//,"male",gmail,Address,age,Phoneno);
        // dref.setValue(prm);
        if (profile.isSaveEnabled()) {
            uploadprofile();
        }
        Toast.makeText(UserProfileDetailsActivity.this, "PROFILE DETAILS UPLOADED", Toast.LENGTH_SHORT).show();


    }
    private void data(){
        //DatabaseReference databaseReference=firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).child("profile");
        //DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());


        StorageReference storageReference = firebaseStorage.getReference();
        //storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic").getDownloadUrl().toString;
        storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(String.valueOf(uri)).placeholder(R.drawable.ic_profile).into(profile);
                //Picasso.get().
                //        Picasso.get().load(String.valueOf(uri)).into(image);
            }
        });

        na=name.getText().toString();
        sa=status.getText().toString();
        String currentuserid=firebaseAuth.getCurrentUser().getUid();
        userref=FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuserid);
        userref.child("name").setValue(na);
        userref.child("status").setValue(sa);
        //userref.child("profile").setValue(pi);
        Toast.makeText(this,"i love u ",Toast.LENGTH_SHORT).show();
/*
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Profiledata profiledata = dataSnapshot.getValue(Profiledata.class);
                fname.setText(profiledata.getFirstname());
                // lname.setText(profiledata.getLastname());
                // phone.setText(profiledata.getPhoneno());
                //t4.setText(profiledata.getGender());
                // Age.setText(profiledata.getAge());
                //address.setText(profiledata.getAddress());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(profileactivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void uploadprofile() {
        final StorageReference myref1 = firebaseStorage.getReference().child(firebaseAuth.getUid()).child("Images").child("Profile Pic");
        UploadTask uploadTask = myref1.putFile(imagepath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                myref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap<String,String> hashMap=new HashMap<>();
                        hashMap.put("profile",String.valueOf(uri));
                        hashMap.put("name",na);
                        hashMap.put("status",sa);
                      userref.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void aVoid) {
                              Toast.makeText(UserProfileDetailsActivity.this,"image,detais data upload iyende ra",Toast.LENGTH_SHORT).show();
                          }
                      });
                    }
                });
                Toast.makeText(UserProfileDetailsActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserProfileDetailsActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        if(requestCode==Pick_Image && resultCode==RESULT_OK && data.getData()!=null){
            imagepath= data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}