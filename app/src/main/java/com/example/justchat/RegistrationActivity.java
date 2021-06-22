package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class RegistrationActivity extends AppCompatActivity {

    private TextView tvalready;
    public EditText edusername,edusermail,eduserpassword;
    private Button register;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

//    private FirebaseAuth firebaseAuth;
    //String myusername;
  //  Settings.Global myuseremail;
    String name,mail,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        settingView();
        firebaseAuth=FirebaseAuth.getInstance();

        tvalready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name=edusername.getText().toString();
                password=eduserpassword.getText().toString();
                mail=edusermail.getText().toString();
                if(validate()){
                    String user_email = edusermail.getText().toString().trim();
                    String user_password = eduserpassword.getText().toString().trim();
                    Toast.makeText(RegistrationActivity.this,"Data retrived",Toast.LENGTH_SHORT).show();
                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(RegistrationActivity.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                sendemailverification();
                            }
                            else{
                                Toast.makeText(RegistrationActivity.this,"Registration UnSuccessful\nRetry Later or Check your Connection",Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }
            }
        });
    }
    private void settingView(){

        tvalready=findViewById(R.id.tvalready);
        edusername=findViewById(R.id.edusername);
        name=edusername.getText().toString();
        eduserpassword=findViewById(R.id.eduserpassword);
        password=eduserpassword.getText().toString();
        edusermail=findViewById(R.id.edusermail);
        mail=edusermail.getText().toString();
        register=findViewById(R.id.bregister);
    }
    private  Boolean validate(){
        Boolean result=false;
        if(name.isEmpty()||mail.isEmpty()||password.isEmpty()){
            Toast.makeText(this,"Enter all Details",Toast.LENGTH_SHORT).show();
        }
        else
            result=true;
        return result;
    }
    private void sendemailverification(){
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        Toast.makeText(RegistrationActivity.this,"ok",Toast.LENGTH_LONG).show();

        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        senddatatoserver();
                        Toast.makeText(RegistrationActivity.this,"Registered,verification mail has been sent\nplease verify that",Toast.LENGTH_LONG).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this,MainActivity.class));
                    }
                    else{
                        Toast.makeText(RegistrationActivity.this,"not Registered,verification mail hasn't been sent",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
    private void senddatatoserver(){


        firebaseDatabase= FirebaseDatabase.getInstance();
        DatabaseReference dref=firebaseDatabase.getReference().child("Users").child(firebaseAuth.getUid());


     //   StorageReference storageReference = firebaseStorage.getReference();

        Profiledata prm =new Profiledata(name,mail);//,Phoneno,Address,Gender,Link);//,"male",gmail,Address,age,Phoneno);
        dref.setValue(prm);
    }

}