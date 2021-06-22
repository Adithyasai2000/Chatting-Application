package com.example.justchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class HomeChatFragment extends Fragment {
    private View ve;
    private DatabaseReference contactsref,userref;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private String currentuserid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        ve =inflater.inflate(R.layout.homechatfragment,container,false);
        recyclerView=ve.findViewById(R.id.chatlistid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth= FirebaseAuth.getInstance();
        currentuserid=mAuth.getCurrentUser().getUid();
        contactsref= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentuserid);
        userref=FirebaseDatabase.getInstance().getReference().child("USERS");


        return ve;





    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contactde>().setQuery(contactsref,Contactde.class).build();
        FirebaseRecyclerAdapter<Contactde, ContactsFragment.ContactsView> adapter=new FirebaseRecyclerAdapter<Contactde, ContactsFragment.ContactsView>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsFragment.ContactsView holder, int position, @NonNull Contactde model) {

                final String usersids=getRef(position).getKey();
                final String[] profileimage = {"Defalut_pic"};
                userref.child(usersids).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profile")) {
                                profileimage[0] = dataSnapshot.child("profile").getValue().toString();

                                Picasso.get().load(profileimage[0]).placeholder(R.drawable.ic_profile).into(holder.profileimage);
                            }

                            final String profilename = dataSnapshot.child("name").getValue().toString();
                            final String profilestatus = dataSnapshot.child("status").getValue().toString();

                            holder.username.setText(profilename);
                            holder.userstatus.setText(profilestatus);
                            if(dataSnapshot.child("UserState").hasChild("state"))
                            {
                                String dtae=dataSnapshot.child("UserState").child("date").getValue().toString();
                                String time=dataSnapshot.child("UserState").child("time").getValue().toString();
                                String state=dataSnapshot.child("UserState").child("state").getValue().toString();
                                if(state.equals("online")){

                                    holder.userstatus.setText(profilestatus);
                                }
                            }
                            else {
                                holder.userstatus.setText("offline");
                            }
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i =new Intent(getContext(),ChatActivity.class);
                                            i.putExtra("visited_user_id",usersids);
                                    i.putExtra("visited_user_name",profilename);
                                    i.putExtra("visited_user_image",profileimage[0]);
                                    startActivity(i);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsFragment.ContactsView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
                ContactsFragment.ContactsView viewholder=new ContactsFragment.ContactsView(view);

                return viewholder;
            }
        };
        recyclerView.setAdapter(adapter);
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
