package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mtool;
    private MessageAdapter messageAdapter;
    private ImageButton sendbutton;
    private EditText usermessage;
    private ScrollView scrollView;
    private TextView textView;
    private RecyclerView myrecycleview;
    private final List<Messages> messagesList=new ArrayList<Messages>();
    private LinearLayoutManager linearLayoutManager;
   // private MessageAdapter messageAdapter;
    private DatabaseReference reference;
    private String currentgrpname,currentuserid,currentusername,currentdate,currenttime;
    private FirebaseAuth mAuth;
    private DatabaseReference userref,grpref,groupmessagekeyref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_group_chat);
        currentgrpname=getIntent().getExtras().get("groupname").toString();
        currentuserid=mAuth.getCurrentUser().getUid();
        userref=FirebaseDatabase.getInstance().getReference().child("Users");
        givingidentities();
        getUserInformation();
        grpref=FirebaseDatabase.getInstance().getReference().child("GroupsMessage").child(currentgrpname);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senddatatodatabase();
                usermessage.setText("");
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


    }

    private void senddatatodatabase() {
        String message=usermessage.getText().toString();
        String messagekey=grpref.push().getKey();
        if(message.isEmpty()){
            Toast.makeText(this,"please enter message",Toast.LENGTH_SHORT).show();
        }
        else{
            Calendar calfordate =Calendar.getInstance();
            SimpleDateFormat currentdateformate=new SimpleDateFormat("MMM dd,yyyy");
            currentdate=currentdateformate.format(calfordate.getTime());


            Calendar calfortime =Calendar.getInstance();
            SimpleDateFormat currenttimeformate=new SimpleDateFormat("hh:mm a");
            currenttime=currenttimeformate.format(calfortime.getTime());
            HashMap<String,Object> groupmessageKey=new HashMap<>();
            grpref.updateChildren(groupmessageKey);

            groupmessagekeyref=grpref.child(messagekey);

            HashMap<String,Object> messageinfomap=new HashMap<>();
            messageinfomap.put("name",currentusername);
            messageinfomap.put("message",message);
            messageinfomap.put("date",currentdate);
            messageinfomap.put("time",currenttime);

            groupmessagekeyref.updateChildren(messageinfomap);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        grpref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                   // DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserInformation() {
userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
      if(dataSnapshot.exists()){
          currentusername=dataSnapshot.child("firstname").getValue().toString();

      }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
    }

    private void givingidentities() {
        mtool=findViewById(R.id.toolbar);
        setSupportActionBar(mtool);
       mtool.setTitle(currentgrpname);
        getSupportActionBar().setTitle(currentgrpname);
        sendbutton=findViewById(R.id.send_id);
        usermessage=findViewById(R.id.usermessage);
        myrecycleview=findViewById(R.id.message_listid);
        messageAdapter=new MessageAdapter(messagesList);
        linearLayoutManager=new LinearLayoutManager(this);
        myrecycleview.setLayoutManager(linearLayoutManager);
        myrecycleview.setAdapter(messageAdapter);
        //scrollView=findViewById(R.id.my_scroll_view);
        //textView=findViewById(R.id.viewmessage);
        //myrecycleview=findViewById(R.id.grouprecycleview);
        messageAdapter=new MessageAdapter(messagesList);
        linearLayoutManager=new LinearLayoutManager(this);
        myrecycleview.setLayoutManager(linearLayoutManager);
        myrecycleview.setAdapter(messageAdapter);
    }

    private void DisplayMessages(DataSnapshot dataSnapshot){
        Iterator iterator=dataSnapshot.getChildren().iterator();
        while(iterator.hasNext()){
            String chatdate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatmessage=(String)((DataSnapshot)iterator.next()).getValue();
          String chatname=(String)((DataSnapshot)iterator.next()).getValue();
            String chattime=(String) ((DataSnapshot)iterator.next()).getValue();
            textView.append(chatname+" :\n"+chatmessage+"\n"+chattime+"   "+chatdate+"\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
           Messages messages=dataSnapshot.getValue(Messages.class);


            /*String senderuseref="Messages/"+msgsenderid+"/"+msgreceiverid;
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
            messagesList.add(chatmessage);
            messageAdapter.notifyDataSetChanged();*/
        }
    }
}
