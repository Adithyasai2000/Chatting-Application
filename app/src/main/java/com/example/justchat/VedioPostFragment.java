package com.example.justchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class VedioPostFragment extends Fragment {
    private View remo;

    private RecyclerView listView;
    private ArrayList<UpdateVedioPost> list;
    private VedioPostAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        remo=inflater.inflate(R.layout.vedio_post_fragment, container, false);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseDatabase= FirebaseDatabase.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();
        initializevariables();
        return remo;
    }

    public void initializevariables() {

        listView = remo.findViewById(R.id.vediorecyclerviewid);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("VedioPosts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list=new ArrayList<UpdateVedioPost>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    UpdateVedioPost p=dataSnapshot1.getValue(UpdateVedioPost.class);
                    list.add(p);
                }
                adapter=new VedioPostAdapter(getContext(),list);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
