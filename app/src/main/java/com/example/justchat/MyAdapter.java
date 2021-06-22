package com.example.justchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Profile> profiles;
    private OnGroupListener mOnGroupListener;
    //private OnGroupListener mOnGroupListener;

    public MyAdapter(Context c,ArrayList<Profile> p,OnGroupListener onGroupListener){
        context=c;
        profiles=p;
        this.mOnGroupListener=onGroupListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview,parent,false),mOnGroupListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.name.setText(profiles.get(position).getGroupname());
    holder.desc.setText(profiles.get(position).getGroupdescp());
    //Picasso.get().load(profiles.get(position).getProfulepic.into(holder.profilepic));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView name,desc;
        OnGroupListener onGroupListener;
        //View.OnLongClickListener onLongClickListener;
         public MyViewHolder(@NonNull View itemView, OnGroupListener onGroupListener) {
            super(itemView);
            name=itemView.findViewById(R.id.groupnameid);
            desc=itemView.findViewById(R.id.groupdescid);
            this.onGroupListener=onGroupListener;
            //this.onLongClickListener=onLongClickListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }



        @Override
        public void onClick(View v) {
            onGroupListener.onGroupclick(getAdapterPosition());
        }


        @Override
        public boolean onLongClick(View v) {
              onGroupListener.onLongGroupclick(getAdapterPosition());
            return true;
        }
    }
    public interface OnGroupListener{
        void onGroupclick(int position);
        boolean onLongGroupclick(int position);
    }
}
