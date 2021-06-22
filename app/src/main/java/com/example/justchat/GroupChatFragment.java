package com.example.justchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GroupChatFragment extends Fragment implements MyAdapter.OnGroupListener {
  /*  @Override
    public void onGroupclick(int position) {
        String grpname=adapter.profiles.get(position).getGroupname();
        Intent i=new Intent(getContext(),GroupChatActivity.class);
                i.putExtra("groupname",grpname);
        startActivity(i);
    }*/

    private View remo;
    private RecyclerView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listofgroups=new ArrayList<>();
  //  private ListAdapter<String,String> arrayAdapter;
    //private ArrayList<String> listgroup=new ArrayList<>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference Groupref;
    private DatabaseReference databaseReference,databaseReference1;
    private ArrayList<Profile> list;
    private MyAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
    remo= inflater.inflate(R.layout.groupchatfragment,container,false);
initializevariables();
    return remo;
    }
    public void initializevariables(){

        listView=remo.findViewById(R.id.recyclelist_view);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference1=FirebaseDatabase.getInstance().getReference().child("GroupsMessage");
        databaseReference=FirebaseDatabase.getInstance().getReference().child("Groups");
       // listView=remo.findViewById(R.id.recyclelist_view);
        //arrayAdapter=new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1 ,listofgroups);
        //listView.setAdapter(arrayAdapter);
databaseReference.addValueEventListener(new ValueEventListener() {

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        list=new ArrayList<Profile>();
        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
            Profile p=dataSnapshot1.getValue(Profile.class);
            list.add(p);
        }
        adapter=new MyAdapter(getContext(), list, new MyAdapter.OnGroupListener() {
            @Override
            public void onGroupclick(int position) {
                String grpname=adapter.profiles.get(position).getGroupname();
                Intent i=new Intent(getContext(),GroupChatActivity.class);
                i.putExtra("groupname",grpname);
                startActivity(i);
            }

            @Override
            public boolean onLongGroupclick(final int position) {
                CharSequence options[] =new CharSequence[]{
                        "DELETE",
                        "PDF Files",
                        "MS Word Files"
                };
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("SELECT YOUR CHOICE");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            deleteitem(position);
                        }

                    }
                });
                builder.show();
                return false;
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(getContext(),"dshgfoigz ush",Toast.LENGTH_SHORT).show();
    }
});


    }

    private void deleteitem(int position) {
        Profile p = list.get(position);
        String key=p.getGroupkey();
        databaseReference1.child(p.getGroupname()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "chat deleted", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"deleted",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStart() {
        Groupref=FirebaseDatabase.getInstance().getReference().child("Groups");

        //initializevariables();
        //retriveanddisplay();

        super.onStart();
    }

    public void retriveanddisplay() {
        Groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    set.add(((DataSnapshot) iterator.next()).getKey());
                }
              //  listgroup.clear();
               // listgroup.addAll(set);
        //        arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onGroupclick(int position) {

    }

    @Override
    public boolean onLongGroupclick(int position) {

        return false;
    }
}

