package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView myrecycleview;
    private Toolbar toolbar;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;
    private ImageButton sendbutton,sendfilebutton;
    private CircularImageView userimage;
    private TextView username,userstatus;
    private EditText edittext;
    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private DatabaseReference reference;
    private Uri fileUri;
    private FloatingActionButton vapourbutton;
    private StorageTask storageTask;
    private String checker="",myurl="",msgreceiverid,msgreceivername,msgreceiverimage,msgsenderid,currentdate,currenttime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        msgreceiverid=getIntent().getExtras().get("visited_user_id").toString();
        msgreceivername=getIntent().getExtras().get("visited_user_name").toString();
        msgreceiverimage=getIntent().getExtras().get("visited_user_image").toString();
        Toast.makeText(this,"hi"+msgreceivername,Toast.LENGTH_SHORT).show();
        mAuth=FirebaseAuth.getInstance();
        msgsenderid=mAuth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference();
        initializevariables();
        vapourbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ChatActivity.this,VapourChatActivity.class);
                i.putExtra("visited_user_id",msgreceiverid);
                i.putExtra("visited_user_name",msgreceivername);
                i.putExtra("visited_user_image",msgreceiverimage);
                startActivity(i);
            }
        });

        username.setText(msgreceivername);
       // userstatus.setText(msgreceiverid);
        Picasso.get().load(msgreceiverimage).placeholder(R.drawable.ic_profile).into(userimage);

        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });
        sendfilebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // sendfilefunction();
                CharSequence options[] =new CharSequence[]{
                        "Images",
                        "PDF Files",
                        "MS Word Files"
                };
                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("SELECT YOUR CHOICE");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            checker="image";
                            Intent i=new Intent();
                            i.setType("image/*");
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(i.createChooser(i,"Select IMAGE"),143);
                        }
                        if(which==1){
                            checker="pdf";
                            Intent i=new Intent();
                            i.setType("application/pdf");
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(i.createChooser(i,"Select PDF"),143);
                        }
                        if(which==2){
                            checker="docx";
                            checker="application/msword";
                            Intent i=new Intent();
                            i.setType("docx/*");
                            i.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(i.createChooser(i,"Select MSWORD DOC"),143);
                        }
                    }
                });
                builder.show();
            }
        });

        reference.child("Messages").child(msgsenderid).child(msgreceiverid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                myrecycleview.smoothScrollToPosition(myrecycleview.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                messagesList.remove(messages);
       //         messagesList.notifyAll();
                messageAdapter.notifyDataSetChanged();
                myrecycleview.smoothScrollToPosition(myrecycleview.getAdapter().getItemCount());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendmessage() {
            String message=edittext.getText().toString();


            if(message.isEmpty()){
                Toast.makeText(this,"enter message",Toast.LENGTH_SHORT);
            }
            else{
                String senderuseref="Messages/"+msgsenderid+"/"+msgreceiverid;
                String receiveruserref="Messages/"+msgreceiverid+"/"+msgsenderid;

                DatabaseReference userMessagekeyref=reference.child("Messages").child(msgsenderid).child(msgreceiverid).push();
                final  String msgpushid=userMessagekeyref.getKey();
                Map msgtext=new HashMap();
                msgtext.put("message",message);
                msgtext.put("type","text");
                msgtext.put("messageid",msgpushid);
                msgtext.put("from",msgsenderid);
                msgtext.put("to",msgreceiverid);
                msgtext.put("time",currenttime);
                msgtext.put("date",currentdate);


                final Map mesgbody=new HashMap();
                mesgbody.put(senderuseref+"/"+msgpushid,msgtext);
                mesgbody.put(receiveruserref+"/"+msgpushid,msgtext);
                reference.updateChildren(mesgbody).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                        }
                        edittext.setText("");
                    }
                });
            }

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void initializevariables() {
        toolbar=findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
         ActionBar actionBar=getSupportActionBar();
         actionBar.setDisplayHomeAsUpEnabled(true);
         actionBar.setDisplayShowCustomEnabled(true);
         vapourbutton=findViewById(R.id.floatingButtonVapourmessageid);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionview=layoutInflater.inflate(R.layout.chattoolbar,null);
        actionBar.setCustomView(actionview);
        sendbutton=findViewById(R.id.chatsend_id);
        userimage=findViewById(R.id.profilechatpic);
        username=findViewById(R.id.chatnameid);
        userstatus=findViewById(R.id.chatsdescid);
        edittext=findViewById(R.id.userchatmessage);
        messageAdapter=new MessageAdapter(messagesList, new MessageAdapter.OnMessageListener() {
            @Override
            public boolean onLongMessageclick(final int position) {
                CharSequence options[] =new CharSequence[]{
                        "DELETE from me",
                        "DELETE for every one",
                };
                AlertDialog.Builder builder=new AlertDialog.Builder(ChatActivity.this);
                builder.setTitle("SELECT YOUR CHOICE");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            deleteitem(position);
                        }
                        if(which==1){
                            deleteitemall(position);
                        }

                    }
                });
                builder.show();
                return false;

            }
        });
        sendfilebutton=findViewById(R.id.filesend_id);
        loadingbar=new ProgressDialog(ChatActivity.this);
        Calendar calfordate =Calendar.getInstance();
        SimpleDateFormat currentdateformate=new SimpleDateFormat("MMM dd,yyyy");
        currentdate=currentdateformate.format(calfordate.getTime());
        myrecycleview=findViewById(R.id.message_listid);
        linearLayoutManager=new LinearLayoutManager(this);
        myrecycleview.setLayoutManager(linearLayoutManager);
        myrecycleview.setAdapter(messageAdapter);

        Calendar calfortime =Calendar.getInstance();
        SimpleDateFormat currenttimeformate=new SimpleDateFormat("hh:mm a");
        currenttime=currenttimeformate.format(calfortime.getTime());
    }

    private void deleteitemall(int position) {
        DatabaseReference userMessagekeyref=reference.child("Messages").child(msgsenderid).child(msgreceiverid);
        Messages p = messagesList.get(position);
        String key=p.getMessageid();
        userMessagekeyref.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        DatabaseReference userMessagekeyref1=reference.child("Messages").child(msgreceiverid).child(msgsenderid);
        Messages pi = messagesList.get(position);
        String keys=pi.getMessageid();
        userMessagekeyref1.child(keys).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChatActivity.this, "chat deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void deleteitem(int position) {
        DatabaseReference userMessagekeyref=reference.child("Messages").child(msgsenderid).child(msgreceiverid);
        Messages p = messagesList.get(position);
        String key=p.getMessageid();
        userMessagekeyref.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ChatActivity.this, "chat deleted", Toast.LENGTH_SHORT).show();
            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==143 && resultCode==RESULT_OK &&data!=null&& data.getData()!=null){
           loadingbar.setTitle("Sending Attachment");
           loadingbar.setMessage("Please wait ....");
           loadingbar.setCanceledOnTouchOutside(false);
           loadingbar.show();

            fileUri=data.getData();
            if(!checker.equals("image")){

                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document Chat Files");
                final String senderuseref="Messages/"+msgsenderid+"/"+msgreceiverid;
                final String receiveruserref="Messages/"+msgreceiverid+"/"+msgsenderid;

                DatabaseReference userMessagekeyref=reference.child("Messages").child(msgsenderid).child(msgreceiverid).push();
                final String msgpushid=userMessagekeyref.getKey();

                final StorageReference filepath=storageReference.child(msgpushid +"."+checker);
                filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Map msgtext=new HashMap();
                            msgtext.put("message",task.getResult().toString());
                            msgtext.put("name",fileUri.getLastPathSegment());
                            msgtext.put("type",checker);
                            msgtext.put("from",msgsenderid);
                            msgtext.put("to",msgreceiverid);
                            msgtext.put("time",currenttime);
                            msgtext.put("date",currentdate);
                            msgtext.put("messageid",msgpushid);

                            Map mesgbody=new HashMap();
                            mesgbody.put(senderuseref+"/"+msgpushid,msgtext);
                            mesgbody.put(receiveruserref+"/"+msgpushid,msgtext);
                            reference.updateChildren(mesgbody).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        loadingbar.dismiss();
                                        Toast.makeText(ChatActivity.this, "document sent", Toast.LENGTH_SHORT).show();
                                    }
                                    loadingbar.dismiss();
                                    edittext.setText("");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingbar.dismiss();
                                    Toast.makeText(ChatActivity.this, "document not sent", Toast.LENGTH_SHORT).show();

                                }
                            });//.addOnProgre


                        }
                    }
                });
            }
            else if(checker.equals("image")){
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Chat Files");
                final String senderuseref="Messages/"+msgsenderid+"/"+msgreceiverid;
                final String receiveruserref="Messages/"+msgreceiverid+"/"+msgsenderid;

                DatabaseReference userMessagekeyref=reference.child("Messages").child(msgsenderid).child(msgreceiverid).push();
                final String msgpushid=userMessagekeyref.getKey();

                 final StorageReference filepath=storageReference.child(msgpushid +"."+"jpeg");
                storageTask=filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    }
                });

                storageTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                       if(!task.isSuccessful()){
                           throw task.getException();
                       }

                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                          Uri downloadUrl=task.getResult();
                          myurl=downloadUrl.toString();
                            Map msgtext=new HashMap();
                            msgtext.put("message",myurl);
                            msgtext.put("name",fileUri.getLastPathSegment());
                            msgtext.put("type",checker);
                            msgtext.put("from",msgsenderid);
                            msgtext.put("to",msgreceiverid);
                            msgtext.put("time",currenttime);
                            msgtext.put("date",currentdate);
                            msgtext.put("messageid",msgpushid);

                            Map mesgbody=new HashMap();
                            mesgbody.put(senderuseref+"/"+msgpushid,msgtext);
                            mesgbody.put(receiveruserref+"/"+msgpushid,msgtext);
                            reference.updateChildren(mesgbody).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        loadingbar.dismiss();
                                        Toast.makeText(ChatActivity.this, "image sent", Toast.LENGTH_SHORT).show();
                                    }
                                    loadingbar.dismiss();
                                    edittext.setText("");
                                }
                            });

                        }
                    }
                });
            }
            else{
                loadingbar.dismiss();
Toast.makeText(this,"Nothing is selected",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
