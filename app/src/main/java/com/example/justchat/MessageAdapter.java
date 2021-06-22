package com.example.justchat;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Messages> usermessageslist;
    private DatabaseReference userref;
    private FirebaseAuth mAuth;
    private OnMessageListener mOnMessageListener;
    private int i;

    public MessageAdapter(List<Messages> usermessageslist,OnMessageListener onMessageListener){
        this.usermessageslist=usermessageslist;
        this.mOnMessageListener=onMessageListener;
    }


    public MessageAdapter(List<Messages> usermessageslist) {
        this.usermessageslist = usermessageslist;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        public TextView sendermessagetext,receivermessagetext;
        public CircularImageView receiverprofileview;
        public ImageView receivermessageimage,sendermessageimage;
        public OnMessageListener onMessageListener;
        public MessageViewHolder(@NonNull View itemView,OnMessageListener onMessageListener) {
            super(itemView);
            mAuth=FirebaseAuth.getInstance();
            this.onMessageListener=onMessageListener;
            itemView.setOnLongClickListener(this);
            sendermessagetext=itemView.findViewById(R.id.sender_message_text);
           receivermessagetext=itemView.findViewById(R.id.receiver_message_text);
            receiverprofileview=itemView.findViewById(R.id.message_profile);
            receivermessageimage=itemView.findViewById(R.id.message_receiver_imageview);
            sendermessageimage=itemView.findViewById(R.id.message_sender_imageview);
        }



        @Override
        public boolean onLongClick(View v) {
            onMessageListener.onLongMessageclick(getAdapterPosition());
            return true;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       return new MessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cust_mes_lay,parent,false),mOnMessageListener);
    }

    @NonNull

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {

    String msgsenderid=mAuth.getCurrentUser().getUid();
Messages messages=usermessageslist.get(position);
final String fromuserid=messages.getFrom();
final String fromMessageType=messages.getType();
i=0;

userref= FirebaseDatabase.getInstance().getReference().child("USERS").child(fromuserid);
userref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
       if(dataSnapshot.hasChild("profile")){
           String receiverimage=dataSnapshot.child("profile").getValue().toString();
           Picasso.get().load(receiverimage).placeholder(R.drawable.ic_profile).into(holder.receiverprofileview);
       }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
        holder.receivermessagetext.setVisibility(View.GONE);
        holder.receiverprofileview.setVisibility(View.GONE);
        holder.sendermessagetext.setVisibility(View.GONE);
        holder.receivermessageimage.setVisibility(View.GONE);
        holder.sendermessageimage.setVisibility(View.GONE);
if(fromMessageType.equals("text")) {


    if (fromuserid.equals(msgsenderid)) {
        holder.sendermessagetext.setVisibility(View.VISIBLE);
        holder.sendermessagetext.setBackgroundResource(R.drawable.sendermessageslayout);
        holder.sendermessagetext.setText(messages.getMessage()+"  "+messages.getTime());

    }else {


        holder.receiverprofileview.setVisibility(View.VISIBLE);
        holder.receivermessagetext.setVisibility(View.VISIBLE);

        holder.receivermessagetext.setBackgroundResource(R.drawable.receivermessageslayout);
        holder.receivermessagetext.setText(messages.getMessage()+"  "+messages.getTime());

    }

}
else if(fromMessageType.equals("image")){
        if (fromuserid.equals(msgsenderid)){
                    holder.sendermessageimage.setVisibility(View.VISIBLE);
                    Picasso.get().load(messages.getMessage()).into(holder.sendermessageimage);
                }
        else if(!(fromuserid.equals(msgsenderid))) {
            holder.receivermessageimage.setVisibility(View.VISIBLE);
            holder.receiverprofileview.setVisibility(View.VISIBLE);
            Picasso.get().load(messages.getMessage()).into(holder.receivermessageimage);
            }

            }
else {
    if (fromuserid.equals(msgsenderid)){
        holder.sendermessageimage.setVisibility(View.VISIBLE);
        holder.sendermessageimage.setBackgroundResource(R.drawable.filelogo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageslist.get(position).getMessage()));
                holder.itemView.getContext().startActivity(i);
            }
        });
    }
    else if(!(fromuserid.equals(msgsenderid))) {
        holder.receivermessageimage.setVisibility(View.VISIBLE);
        holder.receiverprofileview.setVisibility(View.VISIBLE);
        holder.receivermessageimage.setBackgroundResource(R.drawable.filelogo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse(usermessageslist.get(position).getMessage()));
                holder.itemView.getContext().startActivity(i);
            }
        });
    }



}
    }


    @Override
    public int getItemCount() {

        return usermessageslist.size();
    }
    public interface OnMessageListener{
        boolean onLongMessageclick(int position);
    }
}
