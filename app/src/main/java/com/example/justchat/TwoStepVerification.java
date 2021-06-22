package com.example.justchat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class TwoStepVerification extends Fragment {

    private TextView steptwotext;
    private int flag=0;
    private FirebaseAuth mAuth;
    private EditText phoneno,otpno;
    private Button verify,sendotp;
    private View remo;
    private ImageView verificationbatch;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId;
    private ProgressDialog progressDialog;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        //initializefields();
        remo= inflater.inflate(R.layout.verificationuserfragment,container,false);
   //     return remo;
    //}
   // private void initializefields(){
   // }

    //@Override
    //public void onCreate(@Nullable Bundle savedInstanceState) {
      //  super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        verificationbatch=remo.findViewById(R.id.vrerr);
        phoneno=remo.findViewById(R.id.phoneid);
        otpno=remo.findViewById(R.id.verificationcode);
        verify=remo.findViewById(R.id.verifybutton);
        sendotp=remo.findViewById(R.id.sendotppbutton);
        steptwotext=remo.findViewById(R.id.phoneverification);
        progressDialog=new ProgressDialog(getContext());
        if(flag==1){

            phoneno.setVisibility(View.INVISIBLE);
            sendotp.setVisibility(View.INVISIBLE);
            otpno.setVisibility(View.INVISIBLE);
            verify.setVisibility(View.INVISIBLE);
        }
        //initializefields();
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                  phoneno.setVisibility(View.INVISIBLE);
                  sendotp.setVisibility(View.INVISIBLE);
                    String code=otpno.getText().toString();
                    if(code.isEmpty()){
                        Toast.makeText(getContext(),"msfhgkl",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog.setTitle("Code Verification");
                        progressDialog.setMessage("please wait");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);                            }
                                      }
                                  });
                sendotp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String number = phoneno.getText().toString();
                        if (number.isEmpty()) {
                            Toast.makeText(getContext(), "Please ente pnone no", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.setTitle("Phone Verification");
                            progressDialog.setMessage("please wait");
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                    number,        // Phone number to verify
                                    60,                 // Timeout duration
                                    TimeUnit.SECONDS,   // Unit of timeout
                                    (Activity) getContext(),               // Activity (for callback binding)
                                    callbacks);        // OnVerificationStateChangedCallbacks
                        }
                    }
                });
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
             progressDialog.dismiss();
                phoneno.setVisibility(View.INVISIBLE);
                sendotp.setVisibility(View.INVISIBLE);
                otpno.setVisibility(View.INVISIBLE);
                verify.setVisibility(View.INVISIBLE);
                steptwotext.setVisibility(View.VISIBLE);
                flag=1;
                verificationbatch.setVisibility(View.VISIBLE);
                //signInWithPhoneAuthCredential(phoneAuthCredential);
                Toast.makeText(getContext(),"Verification completed",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(),"verificationm failed",Toast.LENGTH_SHORT).show();
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
        return remo;
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Intent i=new Intent(getContext(),Main2Activity.class);
                            startActivity(i);
                        } else {

                        }
                    }
                });
    }
}
