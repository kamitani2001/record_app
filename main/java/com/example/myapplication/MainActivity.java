package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.PlaybackParams;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

    private float play_speed_judgment = 1;
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
                    stopPlaying(); //音声停止
                    ImageView playButton = (ImageView)findViewById(R.id.playback);
                    playButton.setImageResource(R.drawable.playback);
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

    //再生速度
    private void changePlaybackSpeed(float speed) {
        if (player != null) {
            PlaybackParams params = player.getPlaybackParams();
            params.setSpeed(speed);
            player.setPlaybackParams(params);
            if(playback_bool && !play_pause_bool){
                player.pause();
            }
        }
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main); //activity_main.xmlと接続

        findViewById(R.id.record).setOnClickListener(this);
        findViewById(R.id.record_pause).setOnClickListener(this);
        findViewById(R.id.playback).setOnClickListener(this);
        findViewById(R.id.playstop).setOnClickListener(this);
        findViewById(R.id.tenSkip).setOnClickListener(this);
        findViewById(R.id.tenReturn).setOnClickListener(this);
        findViewById(R.id.speed).setOnClickListener(this);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/file.3gp";

        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    //ボタンイベント
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.record) {
            if(record_bool){ //録音開始
                if(!playback_bool) {
                    playback_bool = true;
                    ImageView playButton = (ImageView)findViewById(R.id.playback);
                    playButton.setImageResource(R.drawable.playback);
                    stopPlaying(); //音声停止
                }
                if(!play_pause_bool) {
                    play_pause_bool = true;
                    ImageView playButton = (ImageView)findViewById(R.id.playback);
                    playButton.setImageResource(R.drawable.playback);
                }
                record_bool = false;
                ImageView playButton = (ImageView)findViewById(R.id.record);
                playButton.setImageResource(R.drawable.recordstop);
                startRecording();
            }
            else{ //録音停止
                if(!record_pause_bool){
                    record_pause_bool = true;
                    ImageView playButton = (ImageView)findViewById(R.id.record_pause);
                    playButton.setImageResource(R.drawable.recordinterruption);
                }
                record_bool = true;
                ImageView playButton = (ImageView)findViewById(R.id.record);
                playButton.setImageResource(R.drawable.recordstart);
                stopRecording(); //録音停止
            }
        }
        else if(v.getId() == R.id.record_pause){
            if(record_pause_bool && !record_bool){
                record_pause_bool = false;
                ImageView playButton = (ImageView)findViewById(R.id.record_pause);
                playButton.setImageResource(R.drawable.recordplay);
                recorder.pause(); //録音一時停止
            }
            else if(!record_pause_bool){
                record_pause_bool = true;
                ImageView playButton = (ImageView)findViewById(R.id.record_pause);
                playButton.setImageResource(R.drawable.recordinterruption);
                recorder.resume(); //録音スタート
            }
        }

        else if (v.getId() == R.id.playback) {
            if(playback_bool) {
                if(!record_bool) {
                    record_bool = true;
                    ImageView playButton = (ImageView)findViewById(R.id.record);
                    playButton.setImageResource(R.drawable.recordstart);
                    stopRecording(); //録音停止
                }
                if(!record_pause_bool){
                    record_pause_bool = true;
                    ImageView playButton = (ImageView)findViewById(R.id.record_pause);
                    playButton.setImageResource(R.drawable.recordinterruption);
                }
                playback_bool = false;

                ImageView playButton = (ImageView)findViewById(R.id.playback);
                playButton.setImageResource(R.drawable.play_pause);

                if(!play_pause_bool){
                    play_pause_bool = true;
                    player.start();
                }
                else{
                    startPlaying(); //音声再生
                }
            }
            else {
                playback_bool = true;
                ImageView playButton = (ImageView)findViewById(R.id.playback);
                playButton.setImageResource(R.drawable.playback);

                play_pause_bool = false;
                player.pause(); //音声一時停止
            }
        }

        else if(v.getId() == R.id.playstop){
            if(!play_pause_bool){
                stopPlaying(); //音声停止
                play_pause_bool = true;
            }
            else if(!playback_bool){
                stopPlaying(); //音声停止
                play_pause_bool = true;
                playback_bool = true;
                ImageView playButton = (ImageView)findViewById(R.id.playback);
                playButton.setImageResource(R.drawable.playback);
            }
        }

        else if (v.getId() == R.id.tenSkip){
            if(!playback_bool) {
                int currentPosition = player.getCurrentPosition();
                player.seekTo(currentPosition + 5000);
            }
        }
        else if (v.getId() == R.id.tenReturn){
            if(!playback_bool) {
                int currentPosition = player.getCurrentPosition();
                player.seekTo(currentPosition - 10000);
            }
        }
        else if (v.getId() == R.id.speed){
            if(play_speed_judgment == 1){
                changePlaybackSpeed((float) 1.5);
                ImageView playButton = (ImageView)findViewById(R.id.speed);
                playButton.setImageResource(R.drawable.speed1dot5);
                play_speed_judgment = 2;
            }
            else if(play_speed_judgment == 2){
                changePlaybackSpeed((float) 2);
                ImageView playButton = (ImageView)findViewById(R.id.speed);
                playButton.setImageResource(R.drawable.speed2);
                play_speed_judgment = 3;
            }
            else if(play_speed_judgment == 3){
                changePlaybackSpeed((float) 0.5);
                ImageView playButton = (ImageView)findViewById(R.id.speed);
                playButton.setImageResource(R.drawable.speed0dot5);
                play_speed_judgment = 4;
            }
            else if(play_speed_judgment == 4){
                changePlaybackSpeed((float) 1);
                ImageView playButton = (ImageView)findViewById(R.id.speed);
                playButton.setImageResource(R.drawable.speed1);
                play_speed_judgment = 1;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            record_bool = true;
            ImageView playButton = (ImageView)findViewById(R.id.record);
            playButton.setImageResource(R.drawable.recordstart);

            recorder.release();
            recorder = null;
        }

        if (player != null) {
            playback_bool = true;
            ImageView playButton = (ImageView)findViewById(R.id.playback);
            playButton.setImageResource(R.drawable.playback);

            player.release();
            player = null;
        }

        if (!play_pause_bool){
            play_pause_bool = true;
            ImageView playButton = (ImageView)findViewById(R.id.playback);
            playButton.setImageResource(R.drawable.playback);
        }

        if(!record_pause_bool){
            record_pause_bool = false;
            ImageView playButton = (ImageView)findViewById(R.id.record_pause);
            playButton.setImageResource(R.drawable.recordinterruption);
        }
    }
}