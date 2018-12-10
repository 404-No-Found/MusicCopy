package com.example.sjy.musiccopy;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    private MediaPlayer mediaplayer;
    private  int musicposition;
    private ArrayList<String> musicspath;
    private int currentposition;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(musicspath==null){
            musicspath=intent.getStringArrayListExtra("all_music_path");
        }

        int type=intent.getIntExtra("type",222);
        switch (type) {
            case Config.START_NEW_MUSIC_LISTVIEW:
                musicposition=intent.getIntExtra("music_position", 0);
                currentposition=musicposition;
                startNewMusiclistview();
                break;
            case Config.SEEKTO:
                int progress=intent.getIntExtra("progress", 0);
                mediaplayer.seekTo(progress);
                break;
            case Config.START_NOW_BY_TOGGLEBUTTON:
                int currenttime=intent.getIntExtra("mcurrenttime", 0);
                while (!mediaplayer.isPlaying()) {
                    mediaplayer.start();
                }


                //  startNewMusiclistview();
                break;
            case Config.PAUSE_NOW_BY_TOGGLEBUTTON:
                while (mediaplayer.isPlaying()) {
                    mediaplayer.pause();
                }

                break;
            case Config.SELECT_PREVIOUS_BY_TOGGLEBUTTON:
                Log.d("musicString",currentposition +"");
                if(currentposition==0){
                    musicposition=musicspath.size()-1;

                }else{
                    musicposition--;

                }
                currentposition=musicposition;
                startNewMusiclistview();
                break;
            case Config.SELECT_NEXT_BY_TOGGLEBUTTON:
                if(currentposition==musicspath.size()-1){
                    musicposition=0;
                }else{
                    musicposition++;
                }
                currentposition=musicposition;
                startNewMusiclistview();
                break;
            case 222:break;
            default:
                break;
        }



        return super.onStartCommand(intent, flags, startId);
    }
    public void  startNewMusiclistview(){
        if(mediaplayer==null){
            mediaplayer=new MediaPlayer();
        }
        mediaplayer.reset();
        try {

            mediaplayer.setDataSource(musicspath.get(musicposition).toString());
            mediaplayer.prepare();
            mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaplayer.start();

                    mediaplayer.getCurrentPosition();
                    int duration=mediaplayer.getDuration();
                    Intent  intent2=new Intent("play");
                    intent2.putExtra("type", 0);
                    intent2.putExtra("totaltime",mediaplayer.getDuration());
                    intent2.putExtra("ischecked", true);
                    sendBroadcast(intent2);

                    MyThreadPlay  myplaythread=new MyThreadPlay();
                    myplaythread.start();
                }
            });
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(musicposition==musicspath.size()-1){
                        musicposition=0;
                    }else{
                        musicposition++;
                    }
                    startNewMusiclistview();
                }
            });
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    class MyThreadPlay extends Thread{
        @Override
        public void run() {
            while(mediaplayer.isPlaying()){
                Intent intent=new Intent("play");
                intent.putExtra("type", 1);
                intent.putExtra("currentime", mediaplayer.getCurrentPosition());
                sendBroadcast(intent);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }
}