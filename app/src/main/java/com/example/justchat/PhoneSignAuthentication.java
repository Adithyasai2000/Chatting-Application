package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class PhoneSignAuthentication extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText phoneno,otpno;
    private Button verify,sendotp;
    private View remo;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private void initializefields(){
        phoneno=findViewById(R.id.phoneid);
        otpno=findViewById(R.id.verificationcode);
        verify=findViewById(R.id.verifybutton);
        sendotp=findViewById(R.id.sendotppbutton);
        progressDialog=new ProgressDialog(PhoneSignAuthentication.this);
    }
    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sign_authentication);
        initializefields();
        mAuth=FirebaseAuth.getInstance();
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneno.setVisibility(View.INVISIBLE);
                sendotp.setVisibility(View.INVISIBLE);
                String code=otpno.getText().toString();
                if(code.isEmpty()){
                    Toast.makeText(PhoneSignAuthentication.this,"msfhgkl", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressDialog.setTitle("Code Verification");
                    progressDialog.setMessage("please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = phoneno.getText().toString();
                if (number.isEmpty()) {
                    Toast.makeText(PhoneSignAuthentication.this, "Please ente pnone no", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Phone Verification");
                    progressDialog.setMessage("please wait");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            number,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            (Activity) PhoneSignAuthentication.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks
                }
            }
        });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(PhoneSignAuthentication.this,"verificationm failed",Toast.LENGTH_SHORT).show();
                phoneno.setVisibility(View.VISIBLE);
                sendotp.setVisibility(View.VISIBLE);
                otpno.setVisibility(View.INVISIBLE);
                verify.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                mVerificationId = verificationId;
                mResendToken = token;

                phoneno.setVisibility(View.INVISIBLE);
                sendotp.setVisibility(View.INVISIBLE);
                otpno.setVisibility(View.VISIBLE);
                verify.setVisibility(View.VISIBLE);

            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent i=new Intent(PhoneSignAuthentication.this,Main2Activity.class);
                            startActivity(i);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(PhoneSignAuthentication.this,"hjfyjf",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
