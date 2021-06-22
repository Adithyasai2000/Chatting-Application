package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UploadPostActivity extends AppCompatActivity {
    private ImageView uploadpicture,camerapicture;
    private Button uploadbutton;
    private EditText postname,postdescip;
    private String linkpost;
    private String postlink;
   private String profilelink,postnamestr,postdespstr,postkey;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference myref1,myref2;
    private static int Pick_Image=143 ;
    Uri imagepath;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog=new ProgressDialog(this);
        setContentView(R.layout.activity_upload_post);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        initializingvariables();

        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadingposttoserver();

            }
        });
        camerapicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencamera();
            }
        });
        uploadpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openfilemanager();
            }
        });


    }
    public void uploadingposttoserver(){

        progressDialog.setTitle("Uploading task");
        progressDialog.setMessage("please wait");
        progressDialog.setCanceledOnTouchOutside(false);

         postnamestr=postname.getText().toString();
         postdespstr=postdescip.getText().toString();
        reference=firebaseDatabase.getReference().child("Posts").child(firebaseAuth.getCurrentUser().getUid()).push();
        postkey=reference.getKey();
        if(!(postnamestr.isEmpty()||postdespstr.isEmpty())){
            progressDialog.show();
            myref1=firebaseStorage.getReference().child("Posts").child(firebaseAuth.getCurrentUser().getUid()).child(postkey);
            myref2=firebaseStorage.getReference().child(firebaseAuth.getCurrentUser().getUid()).child("Images").child("Profile Pic");
            UploadTask uploadTask=myref1.putFile(imagepath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //String link=taskSnapshot.getM

                    Toast.makeText(UploadPostActivity.this,"Upload successful",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    myref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            postlink =uri.toString();

                            myref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profilelink =uri.toString();
                                    method();
                                }
                            });

                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadPostActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadPostActivity.this,"pick profile pic",Toast.LENGTH_SHORT).show();
                }
            });


        }
        else {
            Toast.makeText(this,"Please set all details",Toast.LENGTH_SHORT).show();
        }
    }
    public void method(){

        //DatabaseReference reference=firebaseDatabase.getReference().child("Posts").child(firebaseAuth.getUid());


        UpdatePost up=new UpdatePost(postnamestr,postdespstr, profilelink, postlink,postkey);


        reference.setValue(up);
    }
    public void opencamera(){}
    public void openfilemanager(){

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),Pick_Image);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==Pick_Image && resultCode==RESULT_OK && data.getData()!=null){
            imagepath= data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
                uploadpicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initializingvariables(){
        uploadbutton=findViewById(R.id.uploadpostbuttonid);
        postname=findViewById(R.id.postnameid);
        postdescip=findViewById(R.id.postdescid);
        uploadpicture=findViewById(R.id.uploadpicid);
        camerapicture=findViewById(R.id.cameraimageid);
    }
}
