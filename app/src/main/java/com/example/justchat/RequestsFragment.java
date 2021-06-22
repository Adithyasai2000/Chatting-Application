package com.example.justchat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class RequestsFragment extends Fragment {
    private View view;
    private DatabaseReference chatrequestref,userref;
    private RecyclerView requestlist;
    private FirebaseAuth mAuth;
    private String currentuserid;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
    view= inflater.inflate(R.layout.requestsfragment,container,false);
   chatrequestref= FirebaseDatabase.getInstance().getReference().child("ChatRequest");
   mAuth=FirebaseAuth.getInstance();
   userref=FirebaseDatabase.getInstance().getReference().child("USERS");
   currentuserid=mAuth.getCurrentUser().getUid();
    requestlist=view.findViewById(R.id.requestslist);
    requestlist.setLayoutManager(new LinearLayoutManager(getContext()));








        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();


    FirebaseRecyclerOptions<Contactde> options=new FirebaseRecyclerOptions.Builder<Contactde>().setQuery(chatrequestref.child(currentuserid),Contactde.class).build();
        FirebaseRecyclerAdapter<Contactde,RequestViewHolder> adapter=new FirebaseRecyclerAdapter<Contactde, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Contactde model) {

                holder.itemView.findViewById(R.id.acceptbuttonid).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.rejectbuttonid).setVisibility(View.VISIBLE);
                final String list_id=getRef(position).getKey();
                DatabaseReference gettyperef=getRef(position).child("request type").getRef();
                gettyperef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String type=dataSnapshot.getValue().toString();

                            if(type.equals("received")){
                             //   Toast.makeText(getContext(),"ikkada una ra",Toast.LENGTH_SHORT).show();
                                userref.child(list_id).addValueEventListener(new ValueEventListener() {
                                                                          @Override
                                                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                             if(dataSnapshot.hasChild("profile")){
                                                                                 Toast.makeText(getContext(),"ikkada una ra",Toast.LENGTH_SHORT).show();
                                                                                 final String requestusername=dataSnapshot.child("name").getValue().toString();
                                                                                 final String requestuserstatus=dataSnapshot.child("status").getValue().toString();
                                                                                 final String requestuserprofile=dataSnapshot.child("profile").getValue().toString();
                                                                                 holder.username.setText(requestusername);
                                                                                 holder.userstatus.setText(requestuserstatus);
                                                                                 Picasso.get().load(requestuserprofile).placeholder(R.drawable.ic_profile).into(holder.profileimage);
                                                                             }
                                                                             else {

                                                                                 final String requestusername=dataSnapshot.child("name").getValue().toString();
                                                                                 final String requestuserstatus=dataSnapshot.child("status").getValue().toString();
                                                                                 holder.username.setText(requestusername);
                                                                                 holder.userstatus.setText(requestuserstatus);

                                                                             }
                                                                          }

                                                                          @Override
                                                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                          }
                                                                      });
                            }
                            else if(type.equals("sent")){
                              /* Button req_sent_btn=holder.itemView.findViewById(R.id.sendreqbutton);
                                req_sent_btn.setText("Req Sent");
                                Button req_can_btn=holder.itemView.findViewById(R.id.cancelreqbutton);
                                req_can_btn.setVisibility(View.INVISIBLE);



                               // Toast.makeText(getContext(),"ikkada una ra",Toast.LENGTH_SHORT).show();
                                userref.child(list_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild("profile")){
                                       //     Toast.makeText(getContext(),"ikkada una ra",Toast.LENGTH_SHORT).show();
                                            final String requestusername=dataSnapshot.child("name").getValue().toString();
                                            final String requestuserstatus=dataSnapshot.child("status").getValue().toString();
                                            final String requestuserprofile=dataSnapshot.child("profile").getValue().toString();
                                            holder.username.setText(requestusername);
                                            holder.userstatus.setText("you have sent to request");
                                            Picasso.get().load(requestuserprofile).placeholder(R.drawable.ic_profile).into(holder.profileimage);
                                        }
                                        else {

                                            final String requestusername=dataSnapshot.child("name").getValue().toString();
                                            final String requestuserstatus=dataSnapshot.child("status").getValue().toString();
                                            holder.username.setText(requestusername);
                                            holder.userstatus.setText(requestuserstatus);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
*/
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewrequests,parent,false);
             RequestViewHolder holder=new RequestViewHolder(v);
                return holder;
            }
        };
requestlist.setAdapter(adapter);
adapter.startListening();

    }
    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        TextView username,userstatus;
        CircularImageView profileimage;
        Button acceptbutton,rejectbutton;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.groupnameid);
            userstatus=itemView.findViewById(R.id.groupdescid);
            //Picasso.get().load()
            profileimage=itemView.findViewById(R.id.profilegrouppic);
            acceptbutton=itemView.findViewById(R.id.acceptbuttonid);
            rejectbutton=itemView.findViewById(R.id.rejectbuttonid);
        }
    }
}
