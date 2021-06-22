package com.example.justchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>  {

    Context context;
    ArrayList<UpdatePost> updatePosts;
    private PostAdapter.OnPostListener mOnPostListener;

//private OnGroupListener mOnGroupListener;

    public PostAdapter(Context c, ArrayList<UpdatePost> p, PostAdapter.OnPostListener onGroupListener) {
        context = c;
        updatePosts = p;
        this.mOnPostListener = onGroupListener;

    }

    @NonNull
    @Override
    public PostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.imagepostview, parent, false), mOnPostListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.MyViewHolder holder, int position) {
        holder.postname.setText(updatePosts.get(position).getPostname());
        holder.postdesc.setText(updatePosts.get(position).getPostdescription());

        Picasso.get().load(updatePosts.get(position).getPost()).placeholder(R.drawable.ic_logout).fit().into(holder.post);
        Picasso.get().load(updatePosts.get(position).getSenderpicture()).placeholder(R.drawable.ic_logout).fit().into(holder.senderpic);

    }

    @Override
    public int getItemCount() {
        return updatePosts.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView postname, postdesc;
        ImageView post,senderpic;
        PostAdapter.OnPostListener onPostListener;

        //View.OnLongClickListener onLongClickListener;
        public MyViewHolder(@NonNull View itemView, PostAdapter.OnPostListener onPostListener) {
            super(itemView);
            postname = itemView.findViewById(R.id.postnameid);
            postdesc = itemView.findViewById(R.id.postdescriptionid);
            post=itemView.findViewById(R.id.postimageid);
            senderpic=itemView.findViewById(R.id.postsenderid);
            this.onPostListener = onPostListener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        {
        }

        @Override
        public void onClick(View v) {
            onPostListener.onPostclick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onPostListener.onLongPostclick(getAdapterPosition());
            return true;
        }
    }

    public interface OnPostListener{
        void onPostclick(int position);
        boolean onLongPostclick(int position);
    }
}
