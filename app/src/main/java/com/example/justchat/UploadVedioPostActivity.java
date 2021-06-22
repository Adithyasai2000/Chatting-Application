package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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

import java.io.IOException;
import java.util.ArrayList;

public class UploadVedioPostActivity extends AppCompatActivity {

    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    private static int Pick_Image = 143;
    Uri imagepath;
    StorageReference myref1, myref2;
    ProgressDialog progressDialog;

    private String postlink;
    private String profilelink,postnamestr,postdespstr,postkey,vediooname,vedioodesp;
    private RecyclerView listView;
    private ArrayList<UpdateVedioPost> list;
    private VedioPostAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_vedio_post);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference().child("VedioPosts").child(firebaseAuth.getCurrentUser().getUid()).push();
        firebaseStorage=FirebaseStorage.getInstance();
        progressDialog = new ProgressDialog(this);
        openfilemanager();
    }

private void dialogbox1() {
    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
    builder.setTitle("Enter Vedio Name  :");
    final EditText vedioname = new EditText(UploadVedioPostActivity.this);
    builder.setView(vedioname);
    builder.setPositiveButton("NEXT", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            vediooname = vedioname.getText().toString();
            if (TextUtils.isEmpty(vediooname)) {
                Toast.makeText(UploadVedioPostActivity.this, "Please enter the Details", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(UploadVedioPostActivity.this, "UPLOADING", Toast.LENGTH_SHORT).show();
                dialogbox2();

            }
        }
    });
    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    });
    builder.show();
}
public void dialogbox2(){
    androidx.appcompat.app.AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.AlertDialog);
    builder1.setTitle("Enter Vedio Description :");
    final EditText vediodesp = new EditText(UploadVedioPostActivity.this);
    builder1.setView(vediodesp);
    builder1.setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
             vedioodesp = vediodesp.getText().toString();
            if (TextUtils.isEmpty(vedioodesp)) {
                Toast.makeText(UploadVedioPostActivity.this, "Please enter the Description", Toast.LENGTH_SHORT).show();
                dialogbox2();
            } else {
                Toast.makeText(UploadVedioPostActivity.this, "UPLOADING", Toast.LENGTH_SHORT).show();
                uploadingvediopost();
            }
        }
    });
    builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    });
    builder1.show();


}
    public void uploadingposttoserver() {



        myref1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                postlink = uri.toString();

                myref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        profilelink = uri.toString();
                    }
                });

            }
        });

        UpdateVedioPost up=new UpdateVedioPost(vediooname,vedioodesp, profilelink, postlink,postkey);


        reference.setValue(up);





    }

    private void uploadingvediopost() {


        postkey = reference.getKey();
        progressDialog.show();
        myref1 = firebaseStorage.getReference().child("VedioPosts").child(firebaseAuth.getCurrentUser().getUid()).child(postkey);
        myref2 = firebaseStorage.getReference().child(firebaseAuth.getCurrentUser().getUid()).child("Images").child("Profile Pic");
        UploadTask uploadTask = myref1.putFile(imagepath);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //String link=taskSnapshot.getM

                Toast.makeText(UploadVedioPostActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
uploadingposttoserver();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadVedioPostActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadVedioPostActivity.this, "pick profile pic", Toast.LENGTH_SHORT).show();
            }
        });


    }



    public void openfilemanager(){

        Intent intent=new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Vedio"),Pick_Image);
        progressDialog.show();
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait>>>...");
        progressDialog.setCanceledOnTouchOutside(false);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==Pick_Image && resultCode==RESULT_OK && data.getData()!=null){
            imagepath= data.getData();
            Toast.makeText(this,"hello",Toast.LENGTH_SHORT).show();
            myref1=firebaseStorage.getReference().child("VedioPosts").child(firebaseAuth.getCurrentUser().getUid());
            myref2=firebaseStorage.getReference().child(firebaseAuth.getCurrentUser().getUid()).child("Images").child("Profile Pic");
            UploadTask uploadTask=myref1.putFile(imagepath);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UploadVedioPostActivity.this,"don ra badcow",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    dialogbox1();
                    uploadingposttoserver();
                }
            });
            // Bitmap bitmap= MediaStore.Video.Media.getContentUri(imagepath);
            // uploadpicture.setImageBitmap(bitmap);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
