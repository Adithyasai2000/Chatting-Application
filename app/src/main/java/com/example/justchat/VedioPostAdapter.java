package com.example.justchat;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VedioPostAdapter extends RecyclerView.Adapter<VedioPostAdapter.MyViewHolder> {

        Context context;
        ArrayList<UpdateVedioPost> updatePosts;


private ProgressDialog progressDialog;
//private OnGroupListener mOnGroupListener;

        public VedioPostAdapter(Context c, ArrayList<UpdateVedioPost> p) {
        context = c;
        updatePosts = p;

        }



    @NonNull
    @Override
    public VedioPostAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VedioPostAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.vedioplayercardview, parent, false) );
    }

    @Override
    public void onBindViewHolder(@NonNull VedioPostAdapter.MyViewHolder holder, int position) {
        holder.vediopostname.setText(updatePosts.get(position).getVediopostname());
        holder.vediopostdesc.setText(updatePosts.get(position).getVediopostdescription());
        Picasso.get().load(updatePosts.get(position).getVediosenderpicture()).placeholder(R.drawable.ic_logout).fit().into(holder.vediosenderpic);
       /* try {
            BandwidthMeter bandwidthMeter= new DefaultBandwidthMeter();
            TrackSelector trackSelector= new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            holder.exoPlayer=ExoPlayerFactory.newSimpleInstance(context,trackSelector);
            Uri video=Uri.parse(updatePosts.get(position).getVediopost());
            DefaultHttpDataSourceFactory defaultHttpDataSourceFactory=new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory=new DefaultExtractorsFactory();
            MediaSource mediaSource=new ExtractorMediaSource(video,defaultHttpDataSourceFactory,extractorsFactory,null,null);
            holder.videoView.setPlayer(holder.exoPlayer);
            holder.exoPlayer.prepare(mediaSource);
            holder.exoPlayer.setPlayWhenReady(false);
        }catch (Exception e){
            //Toast.makeText(context,e,Toast.LENGTH_SHORT).show();
        }*/
       holder.webView.loadUrl(updatePosts.get(position).getVediopost());


    }

    @Override
    public int getItemCount() {
        return updatePosts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView vediopostname, vediopostdesc;
        ImageView vediosenderpic;
        //PlayerView videoView;
        WebView webView;
        SimpleExoPlayer exoPlayer;

        //View.OnLongClickListener onLongClickListener;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            vediopostname = itemView.findViewById(R.id.vediopostnameid);
            vediopostdesc = itemView.findViewById(R.id.vediopostdescriptionid);
            webView=itemView.findViewById(R.id.postvedioid);
            //videoView=itemView.findViewById(R.id.postvedioid);
            vediosenderpic=itemView.findViewById(R.id.vediopostsenderid);

        }
    }
}
