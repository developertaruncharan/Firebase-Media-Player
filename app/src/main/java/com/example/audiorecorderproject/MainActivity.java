package com.example.audiorecorderproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private Button mRecordButton,mPlayButton,mUploadButton,mFirebasePlayButton;
    private TextView mRecordLabel;
    private MediaRecorder recorder = null;
    private String fileName = null;
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_AUDIO_PERMISSION= 200;
    private static final int REQUEST_EXTERNAL_PERMISSION= 300;
    boolean bool=false,bool1=false,touched=true;
    private MediaPlayer player = null;
    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    private EditText name,audioTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecordButton=findViewById(R.id.recordButton);
        mRecordLabel=findViewById(R.id.holdAndPlay);
        mPlayButton=findViewById(R.id.playButton);
        mUploadButton=findViewById(R.id.uploadButton);
        mFirebasePlayButton=findViewById(R.id.firebasePlayButton);
        name=findViewById(R.id.enterYourName);
        audioTitle=findViewById(R.id.enterAudioTitle);
        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        progressDialog=new ProgressDialog(this);

        if(checkAudioPermission()) {
            recordAudio();
            playAudio();
        }
        else{
            requestExternalPermission();
        }
        if (fileName!=null){
            uploadAudio();
        }

        mFirebasePlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAudioPlay();
            }
        });



    }

    private void firebaseAudioPlay() {
        String mName;
        mName=name.getText().toString().trim();
        if (mName.isEmpty()){
            Toast.makeText(this, "Please enter username ", Toast.LENGTH_SHORT).show();
            return ;
        }else {
            Intent intent = new Intent(MainActivity.this, AllSongsActivity.class);
            intent.putExtra("username", mName);
            startActivity(intent);
        }
    }


    private void playAudio() {
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (touched){
                    touched=!touched;
                    Toast.makeText(MainActivity.this, "Playing video", Toast.LENGTH_SHORT).show();
                    startPlaying();
                }else {
                    touched=!touched;
                    Toast.makeText(MainActivity.this, "Video stopped", Toast.LENGTH_SHORT).show();
                    stopPlaying();
                }
            }
        });
    }

    private boolean checkAudioPermission() {
        boolean bool = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == (PackageManager.PERMISSION_GRANTED);
        boolean bool1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return bool&&bool1;
    }
//    private boolean checkStoragePermission() {
//
//        boolean bool1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == (PackageManager.PERMISSION_GRANTED);
//        if (bool1==false){
//            requestExternalPermission();
//        }
//        return bool1;
//    }


//    private void requestAudioPermission(){
//        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},REQUEST_AUDIO_PERMISSION);
//    }
    private void requestExternalPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_AUDIO_PERMISSION);
    }

    private void recordAudio() {

        mRecordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction()==MotionEvent.ACTION_DOWN){
//                    Toast.makeText(MainActivity.this, "I am touched", Toast.LENGTH_SHORT).show();
//                    mRecordLabel.setText("Recording started");
//                    startRecording();
//                    return true;
//                }
//                else if (event.getAction()==MotionEvent.ACTION_UP){
//                    Toast.makeText(MainActivity.this, "i am not touched", Toast.LENGTH_SHORT).show();
//                    mRecordLabel.setText("Recording stopped");
//                    stopRecording();
//                    return true;
//                }
//                else {
//                    Toast.makeText(MainActivity.this, "Hold and press to record", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        Toast.makeText(MainActivity.this, "I am touched", Toast.LENGTH_SHORT).show();
                        mRecordLabel.setText("Recording started");
                        startRecording();
                        return true;
                    }
                    case  MotionEvent.ACTION_UP:{
                        Toast.makeText(MainActivity.this, "i am not touched", Toast.LENGTH_SHORT).show();
                        mRecordLabel.setText("Recording stopped");
                        stopRecording();
                        return true;
                    }
                    default:{
                        Toast.makeText(MainActivity.this, "Hold and press to record", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
            }
        });
    }



    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case REQUEST_AUDIO_PERMISSION: {
                if (grantResults.length > 0) {
                    bool = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    bool1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (bool&&bool1){
                        recordAudio();
                        playAudio();
                    }
                    else{
                        Toast.makeText(this, "Permission is required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
                break;
//            case REQUEST_EXTERNAL_PERMISSION:{
//                if (grantResults.length > 0) {
//                    bool1 = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                }
//            }
        }
//        if (bool&&bool1){
//            recordAudio();
//            playAudio();
//        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void uploadAudio(){

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mName,mAudioTitle;
                mName=name.getText().toString().trim();
                mAudioTitle=audioTitle.getText().toString().trim();
                final String timestamp = ""+System.currentTimeMillis();
                if (TextUtils.isEmpty(mName)){
                    Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(mAudioTitle)){
                    Toast.makeText(MainActivity.this, "Please enter audio title", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setMessage("Uploading audio");
                progressDialog.show();
                Uri fileUri=Uri.fromFile(new File(fileName));
                String filePath;
                filePath="audio/"+mName+mAudioTitle;
                mStorage= FirebaseStorage.getInstance().getReference(filePath);
                mStorage.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadVideoUri = uriTask.getResult();

                        if (uriTask.isSuccessful()){
                            Toast.makeText(MainActivity.this, "uri generated", Toast.LENGTH_SHORT).show();
                            Data data=new Data(mName,mAudioTitle,""+downloadVideoUri);
//                            HashMap<String, Object> hashMap = new HashMap<>();
//                            hashMap.put("name",mName);
//                            hashMap.put("videoUri",""+downloadVideoUri);
//                            hashMap.put("title",mAudioTitle);
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
                            reference.child(mName).child(timestamp).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "data saved in database", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                        Toast.makeText(MainActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Upload Failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });








    }
}
