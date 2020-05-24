package com.example.audiorecorderproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AllSongsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Data> songsList;
    private String username;
    private AdapterData adapterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);
        recyclerView=findViewById(R.id.recyclerViewShowingAllSongs);
        Intent intent = getIntent();
        username=intent.getStringExtra("username");
        songsList=new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        reference.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Data data =ds.getValue(Data.class);
                    songsList.add(data);
                }
                adapterData=new AdapterData(AllSongsActivity.this,songsList);
                Toast.makeText(AllSongsActivity.this, "adapter Data finished", Toast.LENGTH_SHORT).show();
                recyclerView.setAdapter(adapterData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AllSongsActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
