package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private MediaPlayer player = null;
    private MediaRecorder recorder = null;
    private boolean playback_bool = true; //再生ボタンのbool
    private boolean play_pause_bool = true; //再生の一時停止ボタンのbool
    private boolean record_bool = true; //録音ボタンのbool
    private boolean record_pause_bool = true; //録音の一時停止ボタンのbool
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
    //---------------音声---------------
    //再生
    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 再生終了時に呼び出されます
                    playback_bool = true;
                    ((TextView) findViewById(R.id.playback)).setText("音源再生");
                    stopPlaying(); //音声停止
                }
            });
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    //停止
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
        setContentView(R.layout.activity_main); //activity_main.xmlと接続

        findViewById(R.id.record).setOnClickListener(this);
        findViewById(R.id.record_pause).setOnClickListener(this);
        findViewById(R.id.playback).setOnClickListener(this);
        findViewById(R.id.playback_pause).setOnClickListener(this);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/file.mp3";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    //ボタンイベント
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record) {
            if(record_bool){ //録音開始
                if(!playback_bool) {
                    playback_bool = true;
                    ((TextView) findViewById(R.id.playback)).setText("音源再生");
                    stopPlaying(); //音声停止
                }
                if(!play_pause_bool) {
                    play_pause_bool = true;
                    ((TextView) findViewById(R.id.playback_pause)).setText("音源一時停止");
                }
                record_bool = false;
                ((TextView)findViewById(R.id.record)).setText("録音停止");
                startRecording();
            }
            else{ //録音停止
                if(!record_pause_bool){
                    record_pause_bool = true;
                    ((TextView)findViewById(R.id.record_pause)).setText("録音一時停止");
                }
                record_bool = true;
                ((TextView)findViewById(R.id.record)).setText("録音開始");
                stopRecording(); //録音停止
            }
        }
        else if(v.getId() == R.id.record_pause){
            if(record_pause_bool && !record_bool){
                record_pause_bool = false;
                ((TextView)findViewById(R.id.record_pause)).setText("録音再開");
                recorder.pause(); //録音一時停止
            }
            else if(!record_pause_bool){
                record_pause_bool = true;
                ((TextView)findViewById(R.id.record_pause)).setText("録音一時停止");
                recorder.resume(); //録音スタート
            }
        }
        else if (v.getId() == R.id.playback) {
            if(playback_bool) {
                if(!record_bool) {
                    record_bool = true;
                    ((TextView) findViewById(R.id.record)).setText("録音開始");
                    stopRecording(); //録音停止
                }
                if(!record_pause_bool){
                    record_pause_bool = true;
                    ((TextView)findViewById(R.id.record_pause)).setText("録音一時停止");
                }
                playback_bool = false;
                ((TextView)findViewById(R.id.playback)).setText("音源終了");
                startPlaying(); //音声再生
            }
            else {
                if(!play_pause_bool){
                    play_pause_bool = true;
                    ((TextView) findViewById(R.id.playback_pause)).setText("音源一時停止");
                }
                playback_bool = true;
                ((TextView) findViewById(R.id.playback)).setText("音源再生");
                stopPlaying(); //音声停止
            }
        }
        else if(v.getId() == R.id.playback_pause){
            if(play_pause_bool && !playback_bool){
                play_pause_bool = false;
                ((TextView) findViewById(R.id.playback_pause)).setText("再生");
                player.pause(); //音声一時停止
            }
            else if(!play_pause_bool){
                play_pause_bool = true;
                ((TextView) findViewById(R.id.playback_pause)).setText("音源一時停止");
                player.start(); //途中から再生
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            record_bool = true;
            ((TextView) findViewById(R.id.record)).setText("録音開始");

            recorder.release();
            recorder = null;
        }

        if (player != null) {
            playback_bool = true;
            ((TextView) findViewById(R.id.playback)).setText("音源再生");

            player.release();
            player = null;
        }

        if (!play_pause_bool){
            play_pause_bool = true;
            ((TextView) findViewById(R.id.playback_pause)).setText("音源一時停止");
        }

        if(!record_pause_bool){
            record_pause_bool = false;
            ((TextView) findViewById(R.id.playback_pause)).setText("録音一時停止");
        }
    }
}