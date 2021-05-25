package com.example.recorderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements SetName, Adapter.AdapterOnItem {
    private ImageView b_recorder;
    private ImageButton b_safe;
    private Button b_change;
    private RecyclerView recyclerView;
    private Adapter adapter;
    private boolean isRecording,isPaused;
    private int REQ_PERM=20;
    private Chronometer timer;
    private MediaRecorder mediaRecorder;
    private String fileTitle="empty",path,fileName;
    private long timeWhenStopped;
    private NameDialog nameDialog;
    private File[] files;
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        b_recorder=findViewById(R.id.recorder);
        b_safe=findViewById(R.id.save);
        recyclerView=findViewById(R.id.recycler);
        timer=findViewById(R.id.timer);
        b_change=findViewById(R.id.b_change);

        path=getExternalFilesDir("/").getAbsolutePath();
        directory=new File(path);
        files=directory.listFiles();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new Adapter(this, files,this);
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(new SwipeDelete(adapter,this)
        {
            @Override
            public void onSwiped(@NonNull  RecyclerView.ViewHolder viewHolder, int direction) {
                super.onSwiped(viewHolder, direction);
                alertDialog(viewHolder);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);





        b_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameDialog=new NameDialog(MainActivity.this,MainActivity.this,false);
                nameDialog.show();
            }
        });

        b_safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAudio();
            }
        });
        b_recorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording)
                {
                    pauseAudio();
                }
                else
                {
                    checkPermission();
                }
            }
        });

    }


    private  void alertDialog(RecyclerView.ViewHolder viewHolder)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage("Вы действительно хотите удалить эту аудиозапись?");
        builder.setTitle("Удаление");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.removeFile(viewHolder.getAdapterPosition());
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        });
        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.refresh_menu)
        {
            updateAdapter();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateAdapter()
    {
        files=directory.listFiles();
        adapter.updateAdapter(files);
    }
    private void recordAudio()
    {
        timer.setVisibility(View.VISIBLE);
        b_change.setVisibility(View.GONE);
        timer.setBase(SystemClock.elapsedRealtime());
        timeWhenStopped=0;
        timer.start();

        isRecording=true;
        b_recorder.setImageResource(R.drawable.pause);
        b_safe.setVisibility(View.VISIBLE);

        if(fileTitle.equals("empty"))
            fileTitle=DateFormat.format("dd_MM_yyyy_hh_mm_ss",System.currentTimeMillis())+".3gp";
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(path+"/"+fileTitle);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }
    private void resumeAudio()
    {
        timer.setBase(SystemClock.elapsedRealtime()+timeWhenStopped);
        timer.start();

        isRecording=true;
        b_recorder.setImageResource(R.drawable.pause);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder.resume();
        }
    }

    private void pauseAudio()
    {
        timeWhenStopped=timer.getBase()-SystemClock.elapsedRealtime();
        timer.stop();

        isPaused=true;
        isRecording=false;

        b_recorder.setImageResource(R.drawable.recorder_start);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder.pause();
        }
    }
    private void saveAudio()
    {
        /*getNameDialog.dismiss();
        fileTitle=name+".3gp";
        String path=getExternalFilesDir("/").getAbsolutePath();
        mediaRecorder.setOutputFile(path+"/"+fileTitle);*/
        timer.stop();

        timer.setVisibility(View.GONE);
        b_recorder.setImageResource(R.drawable.recorder_start);
        b_safe.setVisibility(View.GONE);
        b_change.setVisibility(View.VISIBLE);
        isRecording=false;
        isPaused=false;

        mediaRecorder.stop();
        mediaRecorder.release();
        fileTitle="empty";
        Toast.makeText(this, "Аудиозапись была успешно сохранена.", Toast.LENGTH_SHORT).show();

        updateAdapter();
    }

    private void checkPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)
            {
                if(!isPaused)
                    recordAudio();
                else
                    resumeAudio();
            }
            else
            {
                if(shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO))
                    Toast.makeText(this, "Вы должны разрешить записывать аудио..", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},REQ_PERM);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQ_PERM)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults.length>0)
            {
                recordAudio();
            }
            else
            {
                Toast.makeText(this, "Резрешение отклонено...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void setName(String name,boolean isChanged) {
        if(!isChanged)
        {
            fileTitle=name+".3gp";
        }
        else
        {
            File from=new File(path,fileName);
            File to=new File(path,name+".3gp");
            from.renameTo(to);
            updateAdapter();
            Toast.makeText(this, "Редактирование прошло успешно...", Toast.LENGTH_SHORT).show();
        }
        nameDialog.dismiss();
    }

    @Override
    public void onAdapterClick(int position,File file) {
        fileName=file.toString();
        fileName=fileName.substring(fileName.lastIndexOf("/"));
        nameDialog=new NameDialog(MainActivity.this,MainActivity.this,true);
        nameDialog.show();


    }


}