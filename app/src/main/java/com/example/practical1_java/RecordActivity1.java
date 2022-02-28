package com.example.practical1_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class RecordActivity1 extends AppCompatActivity {

    private Button startRecording, stopRecording;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String path = null;
    ListView listView;
    ArrayList<String> fileList = new ArrayList<>();;
    ArrayList<String> recordingNamesList = new ArrayList<>();
    ArrayList<String> dateList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record1);

        startRecording = findViewById(R.id.button_start_recording);
        stopRecording = findViewById(R.id.button_stop_recording);
        listView = findViewById(R.id.listView);
        stopRecording.setEnabled(false);
        CustomBaseAdapter cbr = new CustomBaseAdapter(getApplicationContext(), recordingNamesList, dateList, R.drawable.ic_baseline_folder_24);

        if(isMicrophonePresent()){
            getPermission();
        }

        startRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                path = getRecordingFilePath();
                String[] pathRecordingName = path.split("/");
                recordingNamesList.add(pathRecordingName[9]);
                mediaRecorder.setOutputFile(path);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                Date d = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                String formattedDate = df.format(d);

                dateList.add(formattedDate);

                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    Toast.makeText(RecordActivity1.this, "recording started", Toast.LENGTH_SHORT).show();
                    stopRecording.setEnabled(true);
                    startRecording.setEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(String element : fileList){
                    Log.i("fileList elements: ", element);
                }
            }
        });

        stopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
                stopRecording.setEnabled(false);
                startRecording.setEnabled(true);
                addItems(path);
                listView.setAdapter(cbr);
            }
        });

        //adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,fileList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    int index = position;
                    String pathToFile = fileList.get(index);
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(pathToFile);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.camera){
            Intent intent = new Intent(RecordActivity1.this, MainActivity.class);
            startActivity(intent);

            return true;
        }
        else if(id == R.id.delete){
            Toast.makeText(this, "pressed delete items", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isMicrophonePresent(){
        if(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE)){
            return true;
        }
        else{
            return false;
        }
    }

    private void getPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},200);
        }
    }

    private String getRecordingFilePath(){
        int int_random = ThreadLocalRandom.current().nextInt();
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(directory,"recording"+int_random+".mp3");
        return file.getPath();
    }

    public void addItems(String item){
        fileList.add(item);
    }
}