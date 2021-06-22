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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class profileactivity extends AppCompatActivity {

    private String firstname,lastname,age,gmail,Phoneno,Address,Gender,text1,text2,Link;
    EditText fname,lname,mmail,address,str,str1,phone,Age;
    CheckBox male,female,rterms;
    ImageView photo;
    Button bok;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    private static int Pick_Image=143 ;
    Uri imagepath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==Pick_Image && resultCode==RESULT_OK && data.getData()!=null){
            imagepath= data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
                photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        settingviews();
        try {
          //  alreadydetails();
        }catch (Exception e){
            Toast.makeText(this,"Enter details",Toast.LENGTH_SHORT).show();
        }
        rterms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bok.setEnabled(true);
                Toast.makeText(profileactivity.this, "TERMS AND CONDITIONS ACCEPTED", Toast.LENGTH_SHORT).show();
                Gender=findgender();
            }
        });

        bok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gettingdetaiols();
                //     senddatatoserver();
                if(Phoneno.isEmpty()||lastname.isEmpty()||Address.isEmpty()||age.isEmpty()||firstname.isEmpty()||gmail.isEmpty()||checkgender()){//||checkgender()){
                    Toast.makeText(profileactivity.this,"ENTER ALL DETAILS",Toast.LENGTH_SHORT).show();
                }
                else{
                    senddatatoserver();
                    finish();
                    startActivity(new Intent(profileactivity.this,Main2Activity.class));
                }
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Image"),Pick_Image);


            }
        });

    }

    private void settingviews() {
        fname = findViewById(R.id.etfirstname);
        lname = findViewById(R.id.etlast);
        mmail = findViewById(R.id.etusermail);
        address = findViewById(R.id.etaddress);
        Age=findViewById(R.id.etage);
        phone = findViewById(R.id.etphone);
        male = findViewById(R.id.cd1);
        female = findViewById(R.id.cb2);
        bok = findViewById(R.id.bok);
        rterms = findViewById(R.id.rbterms);
        photo=findViewById(R.id.ivprofile);
        bok.setEnabled(false);
    }
    private void gettingdetaiols(){
        firstname=fname.getText().toString();
        lastname=lname.getText().toString();
        gmail=mmail.getText().toString();
        Address=address.getText().toString();
        Phoneno=phone.getText().toString();
        age=Age.getText().toString();
        //photo=findViewById(R.id.ivprofile);
        Toast.makeText(profileactivity.this," DETAILS retriving" ,Toast.LENGTH_SHORT).show();
    }
    private Boolean checkgender(){
        if((male.isSelected()&&female.isSelected())){
            return true;
        }
        else{
            return false;
        }

    }
    public String findgender(){
        if(male.isChecked())
            return "MALE";
        else if(female.isChecked())
            return "FEMALE";
        else
            return "NONE";
    }
    private void senddatatoserver(){
        firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference dref=firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).child("profile");


        //StorageReference storageReference = firebaseStorage.getReference();

        Profiledata prm =new Profiledata(firstname,lastname,age,gmail,Phoneno,Address,Gender,Link);//,"male",gmail,Address,age,Phoneno);
        dref.setValue(prm);
        if(photo.isSaveEnabled()){
            uploadprofile();
        }
        Toast.makeText(profileactivity.this,"PROFILE DETAILS UPLOADED",Toast.LENGTH_SHORT).show();
    }
    private void uploadprofile(){
        StorageReference myref1=firebaseStorage.getReference().child(firebaseAuth.getUid()).child("Images").child("Profile Pic");
        UploadTask uploadTask=myref1.putFile(imagepath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(profileactivity.this,"Upload successful",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profileactivity.this,"Upload failed",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profileactivity.this,"pick profile pic",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void alreadydetails() {
        DatabaseReference databaseReference=firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid()).child("profile");
        //DatabaseReference databaseReference = firebaseDatabase.getReference(firebaseAuth.getUid());


        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri  uri) {
                Picasso.get().load(String.valueOf(uri)).into(photo);
                //        Picasso.get().load(String.valueOf(uri)).into(image);
            }
        });

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
        });
    }
}
