package com.example.audiorecorderproject;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterData extends RecyclerView.Adapter<AdapterData.HolderData> {

    private Context context;
    private ArrayList<Data> songsList;

    public AdapterData(Context context, ArrayList<Data> songsList) {
        this.context = context;
        this.songsList = songsList;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_data_showed,parent,false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        final Data data=songsList.get(position);
        final String mName,mTitle,url;
        Button playButton;

        mName=data.getName();
        mTitle=data.getTitle();
        url=data.getVideoUri();

        holder.name.setText(mName);
        holder.title.setText(mTitle);
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Play Button Clicked", Toast.LENGTH_SHORT).show();
                MediaPlayer player = new MediaPlayer();
        try {
            player.setDataSource(url);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            player.prepare();

        }catch (Exception e){
            Toast.makeText(context, "Firebase Player not working"+e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
            }
        });

    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    class HolderData extends RecyclerView.ViewHolder{
        private TextView name,title;
        private Button playButton;
        public HolderData(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            title=itemView.findViewById(R.id.songTitle);
            playButton=itemView.findViewById(R.id.playButton);

        }
    }
}
