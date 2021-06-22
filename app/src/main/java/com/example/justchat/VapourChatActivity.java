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
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VapourChatActivity extends AppCompatActivity {
    private RecyclerView myrecycleview;
    private Toolbar toolbar;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;
    private ImageButton sendbutton, sendfilebutton;
    private CircularImageView userimage;
    private TextView username, userstatus;
    private EditText edittext;
    private final List<VapourMessages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private VapourMessageAdapter messageAdapter;
    private DatabaseReference reference;
    private Uri fileUri;
    private ToggleButton vapourbutton;
    private StorageTask storageTask;
    private String checker = "", myurl = "", msgreceiverid, msgreceivername, msgreceiverimage, msgsenderid, currentdate, currenttime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vapour_chat);
        msgreceiverid = getIntent().getExtras().get("visited_user_id").toString();
        msgreceivername = getIntent().getExtras().get("visited_user_name").toString();
        msgreceiverimage = getIntent().getExtras().get("visited_user_image").toString();
        mAuth = FirebaseAuth.getInstance();
        msgsenderid = mAuth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference();
        Toast.makeText(this, "Vapour chat", Toast.LENGTH_SHORT).show();
        initializevariables();
        username.setText(msgreceivername);
        // userstatus.setText(msgreceiverid);
        Picasso.get().load(msgreceiverimage).placeholder(R.drawable.ic_profile).into(userimage);
        reference.child("VapourMessages").child(msgsenderid).child(msgreceiverid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                VapourMessages messages = dataSnapshot.getValue(VapourMessages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                myrecycleview.smoothScrollToPosition(myrecycleview.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.remove(messages);
                messagesList.notifyAll();
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
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });

    }

    private void sendmessage() {
        String message = edittext.getText().toString();


        if (message.isEmpty()) {
            Toast.makeText(this, "enter message", Toast.LENGTH_SHORT);
        } else {
            String senderuseref = "VapourMessages/" + msgsenderid + "/" + msgreceiverid;
            String receiveruserref = "VapourMessages/" + msgreceiverid + "/" + msgsenderid;

            DatabaseReference userMessagekeyref = reference.child("VapourMessages").child(msgsenderid).child(msgreceiverid).push();
            final String msgpushid = userMessagekeyref.getKey();
            Map msgtext = new HashMap();
            msgtext.put("message", message);
            msgtext.put("type", "text");
            msgtext.put("messageid", msgpushid);
            msgtext.put("from", msgsenderid);
            msgtext.put("to", msgreceiverid);
            msgtext.put("time", currenttime);
            msgtext.put("date", currentdate);
            msgtext.put("status","seen");

            final Map mesgbody = new HashMap();
            mesgbody.put(senderuseref + "/" + msgpushid, msgtext);
            mesgbody.put(receiveruserref + "/" + msgpushid, msgtext);
            reference.updateChildren(mesgbody).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(VapourChatActivity.this, "message sent", Toast.LENGTH_SHORT).show();
                    }
                    edittext.setText("");
                }
            });
        }

    }

    private void initializevariables() {
        toolbar = findViewById(R.id.mytoolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        vapourbutton = findViewById(R.id.toggleButton);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionview = layoutInflater.inflate(R.layout.chattoolbar, null);
        actionBar.setCustomView(actionview);
        sendbutton = findViewById(R.id.chatsend_id);
        userimage = findViewById(R.id.profilechatpic);
        username = findViewById(R.id.chatnameid);
        userstatus = findViewById(R.id.chatsdescid);
        edittext = findViewById(R.id.userchatmessage);
        messageAdapter = new VapourMessageAdapter(messagesList);
        sendfilebutton = findViewById(R.id.filesend_id);
        loadingbar = new ProgressDialog(VapourChatActivity.this);
        Calendar calfordate = Calendar.getInstance();
        SimpleDateFormat currentdateformate = new SimpleDateFormat("MMM dd,yyyy");
        currentdate = currentdateformate.format(calfordate.getTime());
        myrecycleview = findViewById(R.id.message_listid);
        linearLayoutManager = new LinearLayoutManager(this);
        myrecycleview.setLayoutManager(linearLayoutManager);
        myrecycleview.setAdapter(messageAdapter);

        Calendar calfortime = Calendar.getInstance();
        SimpleDateFormat currenttimeformate = new SimpleDateFormat("hh:mm a");
        currenttime = currenttimeformate.format(calfortime.getTime());
    }

    private void deleteitemall(int position) {
        DatabaseReference userMessagekeyref = reference.child("Messages").child(msgsenderid).child(msgreceiverid);
        VapourMessages p = messagesList.get(position);
        String key = p.getMessageid();
        userMessagekeyref.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        DatabaseReference userMessagekeyref1 = reference.child("Messages").child(msgreceiverid).child(msgsenderid);
        VapourMessages pi = messagesList.get(position);
        String keys = pi.getMessageid();
        userMessagekeyref1.child(keys).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(VapourChatActivity.this, "chat deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void deleteitem(int position) {
        DatabaseReference userMessagekeyref = reference.child("Messages").child(msgsenderid).child(msgreceiverid);
        VapourMessages p = messagesList.get(position);
        String key = p.getMessageid();
        userMessagekeyref.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(VapourChatActivity.this, "chat deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
