package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ShowActivity extends AppCompatActivity {

    private String re_user_id,currentstate,senderuserauth;
    private TextView userprofilename,userprofilestatus;
    private Button sendrequest,cancelrequest;
    private CircularImageView userprofileimage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userref,chatrequestref,contactsref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        senderuserauth=FirebaseAuth.getInstance().getCurrentUser().getUid();
        userref=FirebaseDatabase.getInstance().getReference().child("USERS");
        chatrequestref=FirebaseDatabase.getInstance().getReference().child("ChatRequest");
        contactsref=FirebaseDatabase.getInstance().getReference().child("Contacts");
        re_user_id=getIntent().getExtras().get("vi_user_id").toString();
        //Toast.makeText(this,"id is"+re_user_id,Toast.LENGTH_SHORT).show();
        userprofilename=findViewById(R.id.usernameid);
        userprofilestatus=findViewById(R.id.userststusid);
        sendrequest=findViewById(R.id.sendreqbutton);
        cancelrequest=findViewById(R.id.cancelreqbutton);
        userprofileimage=findViewById(R.id.circularImageViewprofile);
        currentstate="new";
retrieve();
        if(!senderuserauth.equals(re_user_id)){
            sendrequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendrequest.setEnabled(false);
                    if(currentstate.equals("new")){
                        Toast.makeText(ShowActivity.this,"new ra",Toast.LENGTH_SHORT).show();
                        sendchatrequest();
                    }
                    else if(currentstate.equals("request_sent")){
                        Toast.makeText(ShowActivity.this,"sent ra",Toast.LENGTH_SHORT).show();
                        cancelchatrequest();
                    }

                    else if(currentstate.equals("request_received")){
                        Toast.makeText(ShowActivity.this,"received ra",Toast.LENGTH_SHORT).show();
                        acceptchatrequest();
                    }

                    else if(currentstate.equals("friends")){
                        removecontactspecified();
                    }
                    else {
                        Toast.makeText(ShowActivity.this,"nothing ra",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else {
            sendrequest.setVisibility(View.INVISIBLE);
        }
    }

    private void retrieve() {
        userref.child(re_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists() &&(dataSnapshot.hasChild("profile"))){
                   String username=dataSnapshot.child("name").getValue().toString();
                   String userstatus=dataSnapshot.child("status").getValue().toString();
                   String userimage=dataSnapshot.child("profile").getValue().toString();
                   Picasso.get().load(userimage).placeholder(R.drawable.ic_profile).into(userprofileimage);
                   userprofilename.setText(username);
                   userprofilestatus.setText(userstatus);
                    mangechat();
               }
               else{

                   String username=dataSnapshot.child("name").getValue().toString();
                   String userstatus=dataSnapshot.child("status").getValue().toString();
                   userprofilename.setText(username);
                   userprofilestatus.setText(userstatus);
                    mangechat();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void mangechat(){

        chatrequestref.child(senderuserauth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(re_user_id)){
                    String request_type=dataSnapshot.child(re_user_id).child("request type").getValue().toString();
                    if(request_type.equals("sent")){
                        currentstate="request_sent";
                        sendrequest.setText("Cancel chat Request");
                    }
                    else if(request_type.equals("received")){
                        currentstate="request_received";
                        sendrequest.setText("Accept Chat Request");
                        cancelrequest.setVisibility(View.VISIBLE);
                        cancelrequest.setEnabled(true);
                        cancelrequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                 cancelchatrequest();
                            }
                        });
                    }
                }
                else {

            contactsref.child(senderuserauth).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   if (dataSnapshot.hasChild(re_user_id)) {
                       currentstate = "friends";
                       sendrequest.setText("Remove this Contact");
                   }
               }

                    @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //ikadda

    }

    private void removecontactspecified() {
        contactsref.child(senderuserauth).child(re_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    contactsref.child(re_user_id).child(senderuserauth).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                sendrequest.setEnabled(true);
                                currentstate="new";
                                sendrequest.setText("SEND REQUEST");
                                cancelrequest.setVisibility(View.INVISIBLE);
                                cancelrequest.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }


    private void acceptchatrequest() {
        Toast.makeText(this,"fuck u",Toast.LENGTH_SHORT).show();
contactsref.child(senderuserauth).child(re_user_id).child("contactsList").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
    @Override
    public void onComplete(@NonNull Task<Void> task) {
if(task.isSuccessful()) {
    contactsref.child(re_user_id).child(senderuserauth).child("contactsList").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                chatrequestref.child(senderuserauth).child(re_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatrequestref.child(re_user_id).child(senderuserauth).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //if(task.isSuccessful()){
                                sendrequest.setEnabled(true);
                                currentstate="friends";
                                sendrequest.setText("Remove this Contacts");
                                cancelrequest.setVisibility(View.INVISIBLE);
                                cancelrequest.setEnabled(false);
                           // }
                        }
                    });
}
                    }
                });
            }
        }
    });
}
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(ShowActivity.this,"failed ra",Toast.LENGTH_SHORT).show();
    }
});

    }

    private void cancelchatrequest() {
        chatrequestref.child(senderuserauth).child(re_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatrequestref.child(re_user_id).child(senderuserauth).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                            sendrequest.setEnabled(true);
                            currentstate="new";/*error unde rs badacow*/
                            sendrequest.setText("SEND REQUEST");
                            cancelrequest.setVisibility(View.INVISIBLE);
                            cancelrequest.setEnabled(false);
                    }
                        }
                    });
                }
            }
        });
    }

    private void sendchatrequest() {
    chatrequestref.child(senderuserauth).child(re_user_id).child("request type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful()){
                chatrequestref.child(re_user_id).child(senderuserauth).child("request type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            sendrequest.setEnabled(true);
                            currentstate="request_sent";
                            sendrequest.setText("Cancel chat request");
                        }
                    }
                });

            }
        }
    });
    }
}
