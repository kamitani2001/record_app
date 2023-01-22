package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaPlayer   player = null;
    private MediaRecorder recorder = null;
    private boolean playback_bool = true;
    private boolean record_bool = true;

    //録音の権限用
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    //認証許可
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }

    //レコードに関するの関数の呼び出し
    private void onRecord(boolean start) {
        if (start) {
            startRecording(); //録音開始
        } else {
            stopRecording(); //録音停止
        }
    }

    //再生に関するの関数の呼び出し
    private void onPlay(boolean start) {
        if (start) {
            startPlaying(); //音声再生
        } else {
            stopPlaying(); //音声停止
        }
    }

    //音声再生
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
    //音声停止
    private void stopPlaying() {
        player.release();
        player = null;
    }

    //---------------録音---------------
    //開始
    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        recorder.start();
    }

    //停止
    private void stopRecording() {
        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        findViewById(R.id.record).setOnClickListener(this);
        findViewById(R.id.playback).setOnClickListener(this);


        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    //ボタンイベント
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record) {
            if(record_bool){
                record_bool = false;
                ((TextView)findViewById(R.id.record)).setText("録音停止");
                onRecord(true);
            }
            else{
                record_bool = true;
                ((TextView)findViewById(R.id.record)).setText("録音開始");
                onRecord(false);
            }

        }
        else if (v.getId() == R.id.playback) {
            if(playback_bool) {
                playback_bool = false;
                ((TextView)findViewById(R.id.playback)).setText("音源停止");
                onPlay(true);
            }
            else {
                playback_bool = true;
                ((TextView)findViewById(R.id.playback)).setText("音源再生");
                onPlay(false);
            }

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }
}
