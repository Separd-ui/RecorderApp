package com.example.recorderapp;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolderData> {
    private Context context;
    private File[] files;
    private MediaPlayer mediaPlayer;
    private AdapterOnItem adapterOnItem;
    private File currentFile;

    public Adapter(Context context, File[] files,AdapterOnItem adapterOnItem) {
        this.context = context;
        this.files = files;
        this.adapterOnItem=adapterOnItem;
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.audio_item,parent,false);
        return new ViewHolderData(view,adapterOnItem);
    }

    @Override
    public void onBindViewHolder(@NonNull  Adapter.ViewHolderData holder, int position) {
        holder.setData(files[position]);
    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    public class ViewHolderData extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageButton b_play;
        TextView title,date,duration;
        AdapterOnItem adapterOnItem;
        boolean isPlaying;
        int durationPause=0;
        public ViewHolderData(@NonNull  View itemView,AdapterOnItem adapterOnItem) {
            super(itemView);
            b_play=itemView.findViewById(R.id.adapter_b_play);
            title=itemView.findViewById(R.id.adapter_title);
            date=itemView.findViewById(R.id.adapter_date);
            duration=itemView.findViewById(R.id.adapter_duration);
            this.adapterOnItem=adapterOnItem;
            itemView.setOnClickListener(this);
        }
        private void setData(File file)
        {
            MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
            long durationFile=Long.parseLong(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            duration.setText(DateFormat.format("mm:ss",durationFile));

            title.setText(file.getName());
            date.setText(DateFormat.format("dd.MM.yyyy hh:mm",file.lastModified()));
            b_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isPlaying)
                    {
                        isPlaying=false;
                        durationPause=mediaPlayer.getCurrentPosition();
                        b_play.setImageResource(R.drawable.ic_play);
                        mediaPlayer.pause();
                    }
                    else
                    {
                        b_play.setImageResource(R.drawable.ic_pause);
                        isPlaying=true;
                        if(currentFile!=file)
                        {
                            playAudio(file);
                            currentFile=file;
                        }
                        else
                        {
                            resumeAudio(durationPause);
                        }

                    }
                }
            });
        }


        @Override
        public void onClick(View v) {
            adapterOnItem.onAdapterClick(getAdapterPosition(),files[getAdapterPosition()]);
        }
    }
    public void updateAdapter(File[] fileList){
        files=fileList;
        notifyDataSetChanged();
    }
    private void playAudio(File file)
    {
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void resumeAudio(int durationPause)
    {
        mediaPlayer.seekTo(durationPause);
        mediaPlayer.start();
    }
    public interface AdapterOnItem
    {
        void onAdapterClick(int position,File file);
    }
    public void removeFile(int position)
    {
        files[position].delete();
        File[] buffer=new File[files.length-1];
        int buf=0;
        for(int i=0;i<files.length;i++)
        {
            if(i!=position)
                buffer[buf++]=files[i];
        }
        files=buffer;
        notifyItemRemoved(position);
    }





}
