package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText mail,phoneno,password;
    private Button bsignin,phonelogin;
    private int count;
    private TextView forgetpassword;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        settingView();
        firebaseAuth= FirebaseAuth.getInstance();

        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            finish();
           // firebaseAuth.signOut();
            startActivity(new Intent(SignUpActivity.this,Main2Activity.class));
        }
        progressDialog=new ProgressDialog(this);
        bsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate(mail.getText().toString().trim(), password.getText().toString().trim());
            }
        });
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mail.getText().toString().trim().isEmpty()){
                    Toast.makeText(SignUpActivity.this,"Please Enter Email",Toast.LENGTH_SHORT).show();
                }
                else{
                    resetactivity(mail.getText().toString().trim());
                }
               // startActivity(new Intent(SignUpActivity.this,PasswordChangeActivity.class));
            }
        });
        phonelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,PhoneSignAuthentication.class));
            }
        });
    }
    private void resetactivity(String changeemail ){
        firebaseAuth.sendPasswordResetEmail(changeemail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this,"Reset Password email has sent to your\nREGISTERED MAIL",Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                }
                else{
                    Toast.makeText(SignUpActivity.this,"Reset Password email has not sent \nVERIFY REGISTERED MAIL",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    private void settingView(){
        mail=findViewById(R.id.mailid);
        phonelogin=findViewById(R.id.phonesignupid);
       // phoneno=findViewById(R.id.phoneid);
        password=findViewById(R.id.passwordid);
        forgetpassword=findViewById(R.id.forgetpasswordid);
        bsignin=findViewById(R.id.singupbuttonid);
    }

    public void validate(String name,String password){
        if (name.isEmpty()||password.isEmpty() ) {
            Toast.makeText(SignUpActivity.this,"Enter EMAIL and PASSWORD",Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(name, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        checkemailverification();
//firebaseAuth.signOut();

                    } else {
                        count--;
       //                 tvattempts.setText("no of attempts remaining " + count);
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "Login unsucessful", Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignUpActivity.this, "You are not a User", Toast.LENGTH_SHORT).show();
                        if (count == 0) {
                            bsignin.setEnabled(false);
                        }
                    }
                }
            });
        }
    }

    private void checkemailverification(){
        FirebaseUser firebaseUser=firebaseAuth.getInstance().getCurrentUser();
        Boolean acce =firebaseUser.isEmailVerified();
        if(acce){
            finish();

            startActivity(new Intent(SignUpActivity.this,UserProfileDetailsActivity.class));
        }
        else{
            Toast.makeText(SignUpActivity.this,"Email Verification is required",Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}
