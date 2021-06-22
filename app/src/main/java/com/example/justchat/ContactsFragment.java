package com.example.justchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class ContactsFragment extends Fragment {
    private View contactsview;
    private RecyclerView contactlist;
    private DatabaseReference contactsref,userref;
    private FirebaseAuth mAuth;
    private String currentuserid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
    contactsview= inflater.inflate(R.layout.contactsfragment,container,false);
      contactlist=contactsview.findViewById(R.id.contacts_list);
      contactlist.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth=FirebaseAuth.getInstance();
        currentuserid=mAuth.getCurrentUser().getUid();
        contactsref= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserid);
        userref=FirebaseDatabase.getInstance().getReference().child("USERS");
        return contactsview;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contactde>().setQuery(contactsref,Contactde.class).build();
        FirebaseRecyclerAdapter<Contactde,ContactsView> adapter=new FirebaseRecyclerAdapter<Contactde, ContactsView>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsView holder, int position, @NonNull Contactde model) {

                String usersids=getRef(position).getKey();
                userref.child(usersids).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("profile")){
                            String profileimage=dataSnapshot.child("profile").getValue().toString();
                            String profilename=dataSnapshot.child("name").getValue().toString();
                            String profilestatus=dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);
                            Picasso.get().load(profileimage).placeholder(R.drawable.ic_profile).into(holder.profileimage);
                        }
                        else {
                            String profilename=dataSnapshot.child("name").getValue().toString();
                            String profilestatus=dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
                ContactsView viewholder=new ContactsView(view);

                return viewholder;
            }
        };
        contactlist.setAdapter(adapter);
        adapter.startListening();

    }
    public static class ContactsView extends RecyclerView.ViewHolder{
        TextView username,userstatus;
        CircularImageView profileimage;

        public ContactsView(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.groupnameid);
            userstatus=itemView.findViewById(R.id.groupdescid);
            profileimage=itemView.findViewById(R.id.profilegrouppic);
        }
    }
}
