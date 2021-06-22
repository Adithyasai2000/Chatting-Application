package com.example.justchat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StatusFragment extends Fragment {
    @Nullable
    private View remo;
    private RecyclerView listView;
    private ArrayList<UpdatePost> list;
    private PostAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        remo = inflater.inflate(R.layout.statusfragment, container, false);
        initializevariables();
        return remo;
    }

    public void initializevariables() {
        firebaseAuth=FirebaseAuth.getInstance();
        listView = remo.findViewById(R.id.post_listid);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        list=new ArrayList<UpdatePost>();
                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                            UpdatePost p=dataSnapshot1.getValue(UpdatePost.class);
                            list.add(p);
                        }
                        adapter=new PostAdapter(getContext(), list, new PostAdapter.OnPostListener() {
                            @Override
                            public void onPostclick(int position) {
                                Toast.makeText(getContext(),"image clicked",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public boolean onLongPostclick(final int position) {
                                Toast.makeText(getContext(),"image long clicked",Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
                        listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
