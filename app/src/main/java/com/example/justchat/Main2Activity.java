package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Main2Activity extends AppCompatActivity {
    private DrawerLayout drawer;
    private FloatingActionButton fab;
    private View hview;
    AppBarConfiguration appBarConfiguration;
    private TextView username,usermail;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private CircularImageView circularImageView;
    private DatabaseReference userref;

    private String currentusername,currentusermail,grpkey,currentuserid;
    //private AppBarConfiguration appBarConfiguration;
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_main2);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.floatingActionButton);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //init();
        navigationView=findViewById(R.id.nav_view);
        hview=navigationView.getHeaderView(0);
     /*   NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
*/

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeChatFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_homechat);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_homechat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeChatFragment()).commit();
                        break;
                    case R.id.nav_groupchat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupChatFragment()).commit();
                        break;
                    case R.id.nav_status:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatusFragment()).commit();
                        break;
                    case R.id.nav_vediopost:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VedioPostFragment()).commit();
                    case R.id.nav_contacts:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactsFragment()).commit();
                        break;
                    case R.id.nav_requests:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RequestsFragment()).commit();
                        break;
                    case R.id.nav_logout:
                        firebaseAuth = FirebaseAuth.getInstance();
                        firebaseAuth.signOut();
                        startActivity(new Intent(Main2Activity.this, MainActivity.class));
                        finish();//getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingsFragment()).commit();
                        break;
                    case R.id.nav_verification:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TwoStepVerification()).commit();
                        break;
                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(Main2Activity.this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        firebaseAuth=FirebaseAuth.getInstance();
        currentuserid=firebaseAuth.getCurrentUser().getUid();
        //bottomNavigationView.set
        username=hview.findViewById(R.id.usernameid);
      usermail=hview.findViewById(R.id.usermailid);
     firebaseStorage=FirebaseStorage.getInstance();
        final ImageView profilepic=hview.findViewById(R.id.profileimageid);
        StorageReference storageReference = firebaseStorage.getReference();
        storageReference.child(firebaseAuth.getUid()).child("Images").child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(String.valueOf(uri)).placeholder(R.drawable.ic_profile).fit().into(profilepic);
                Toast.makeText(Main2Activity.this,"Done photo",Toast.LENGTH_SHORT).show();
                //        Picasso.get().load(String.valueOf(uri)).into(image);
            }
        });
        userref=FirebaseDatabase.getInstance().getReference().child("Users");

        String currentuserid=firebaseAuth.getCurrentUser().getUid();
        userref.child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currentusername=dataSnapshot.child("firstname").getValue().toString();
                    currentusermail=dataSnapshot.child("gmail").getValue().toString();

                    username.setText(currentusername);
                    usermail.setText(currentusermail);
                    Toast.makeText(Main2Activity.this,"Done",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(this,"Double done",Toast.LENGTH_SHORT).show();


    }


    private void init() {

        drawer = findViewById(R.id.drawer_layout);

       // navigationView = findViewById(R.id.navigationView);

       // bottomNavigationView = findViewById(R.id.bottomNavigation);


        navigationView=findViewById(R.id.nav_view);


         navController = Navigation.findNavController(Main2Activity.this,R.id.bottomnav_homechat);

        appBarConfiguration = new AppBarConfiguration.Builder(new int[]{R.id.bottomnav_homechat,R.id.bottomnav_groupchat,R.id.bottomnav_status,R.id.bottomnav_profile})

                .setDrawerLayout(drawer)

                .build();

    }

    @Override

    public boolean onSupportNavigateUp() {

        return NavigationUI.navigateUp(navController,appBarConfiguration );

    }
    public void requestnewgroup(View view){

        AlertDialog.Builder builder=new AlertDialog.Builder(this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name :");
        final EditText groupname=new EditText(Main2Activity.this);
        builder.setView(groupname);
        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String grouname=groupname.getText().toString();
                if(TextUtils.isEmpty(grouname)){
                    Toast.makeText(Main2Activity.this,"Please enter The Group Name",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(Main2Activity.this,"HELLO GROUP",Toast.LENGTH_SHORT).show();
                    CreateNewGroup(grouname);
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

    private void CreateNewGroup(String gname) {
       // DatabaseReference dref=firebaseDatabase.g
DatabaseReference dref=databaseReference.child("Groups");
        grpkey=dref.push().getKey();
Profile profile=new Profile(gname,"i love u ok",grpkey);

dref.child(grpkey).setValue(profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.find_new_friends:{
                //firebaseAuth=FirebaseAuth.getInstance();
                //  finish();
                startActivity(new Intent(Main2Activity.this,FindFriendsActivity.class));
                break;
            }
            case R.id.find_user_logout:{
                updateuserstate("offline");
                firebaseAuth.signOut();
                break;
            }
            case R.id.find_upload_post:{
                startActivity(new Intent(Main2Activity.this,UploadPostActivity.class));
            }
            case R.id.find_new_setting:{
              //  startActivity(new Intent(HomeActivity.this,Main2Activity.class));
                break;
            }
            case R.id.find_new_group:{
               // startActivity(new Intent(HomeActivity.this,MapFragment.class));
                break;
            }
            case R.id.find_upload_postvedio:{
                startActivity(new Intent(Main2Activity.this,UploadVedioPostActivity.class));
            }

        }
        return super.onOptionsItemSelected(item);
    }
    private void updateuserstate(String state){
        Calendar calfordate =Calendar.getInstance();
        SimpleDateFormat currentdateformate=new SimpleDateFormat("MMM dd,yyyy");
        String currentdate=currentdateformate.format(calfordate.getTime());


        Calendar calfortime =Calendar.getInstance();
        SimpleDateFormat currenttimeformate=new SimpleDateFormat("hh:mm a");
        String currenttime=currenttimeformate.format(calfortime.getTime());


        HashMap<String,Object> onlinestate=new HashMap<>();
        onlinestate.put("time",currenttime);
        onlinestate.put("date",currentdate);
        onlinestate.put("state",state);



        userref=FirebaseDatabase.getInstance().getReference().child("USERS").child(currentuserid).child("UserState");
        userref.updateChildren(onlinestate);

    }

    @Override
    protected void onStop() {
        firebaseAuth = FirebaseAuth.getInstance();
        super.onStop();
        updateuserstate("offline");
    }

    @Override
    protected void onStart() {
        firebaseAuth = FirebaseAuth.getInstance();
        super.onStart();
        updateuserstate("online");
    }

    @Override
    protected void onDestroy() {
        firebaseAuth = FirebaseAuth.getInstance();
        super.onDestroy();
        updateuserstate("offline");
    }

    public void click1(View view) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeChatFragment()).commit();

    }

    public void click4(View view) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ContactsFragment()).commit();

    }
    public void click2(View view) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupChatFragment()).commit();
    }
    public void click3(View view) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StatusFragment()).commit();
    }

    public void click6(View view) {

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VedioPostFragment()).commit();
    }
    public void click5(View view) {
        updateuserstate("offline");
        firebaseAuth.signOut();
        startActivity(new Intent(Main2Activity.this, MainActivity.class));
        finish();

    }
}


