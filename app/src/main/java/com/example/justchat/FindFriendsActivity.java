package com.example.justchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference useref;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);
        recyclerView=findViewById(R.id.recyclelist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");
        useref= FirebaseDatabase.getInstance().getReference().child("USERS");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contactde> options;
        options = new FirebaseRecyclerOptions.Builder<Contactde>().setQuery(useref,Contactde.class).build();
        FirebaseRecyclerAdapter<Contactde,FindFriendViewHolder> adapter=new FirebaseRecyclerAdapter<Contactde, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull Contactde model) {
            holder.userName.setText(model.getName());
            holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getProfile()).into(holder.profileimage);
                //Toast.makeText(FindFriendsActivity.this, "retrived", Toast.LENGTH_SHORT).show();


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String vi_user_id=getRef(position).getKey();
                        Intent i=new Intent(FindFriendsActivity.this,ShowActivity.class);
                        i.putExtra("vi_user_id",vi_user_id);
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview,parent,false);
                FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
                return viewHolder;
            }
        };
        recyclerView.setAdapter(adapter);
      adapter.startListening();
    }
    public static class FindFriendViewHolder extends RecyclerView.ViewHolder{

        TextView userName,userStatus;
        CircularImageView profileimage;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.groupnameid);
            userStatus=itemView.findViewById(R.id.groupdescid);
            profileimage=itemView.findViewById(R.id.profilegrouppic);
        }
    }
}
