package com.example.sjy.musiccopy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.os.Environment.DIRECTORY_MUSIC;

public class MainActivity extends Activity implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener {
    private ListView mlistview;
    private TextView mtv_currenttime;
    private TextView mtv_totaltime;

    private ToggleButton mradoibutton_pre;
    private ToggleButton mradoibutton_start;
    private ToggleButton mradoibutton_next;
    private SeekBar mseekbar;

    private File musicDir;
    private File[] files;
    private int seekbarprogress;
    private MusicAdapter madapter;
    private LayoutInflater inflater;
    private MyReceiver receiver;
    private ArrayList<String> files_path=new ArrayList<String>();
    private int currenttime;
    private boolean flag=false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mradoibutton_pre =findViewById(R.id.bt_previeous);
        mradoibutton_start =findViewById(R.id.bt_start);
        mradoibutton_next =findViewById(R.id.bt_next);
        mseekbar = findViewById(R.id.seekbar);
        mlistview =findViewById(R.id.listview);
        mtv_totaltime =findViewById(R.id.textview_totaltime);
        mtv_currenttime =findViewById(R.id.textview_currenttime);


//      for (File file : files) {
//          Log.d("音乐文件", file.getName() + "路径" + file.getAbsolutePath());
//
//      }
        verifyStoragePermissions(this);
        initData();

    }

    private void initData() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("play");
        registerReceiver(receiver, filter);

        musicDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC);//获取SD卡目录
        musicDir = new File(musicDir, "/musics");
        files = musicDir.listFiles();

        for (int i = 0; i < files.length; i++) {

            files_path.add(files[i].getAbsolutePath().toString());
            Log.d("files_path", files_path.get(i));
        }

        Intent intent = new Intent(getApplicationContext(), MusicService.class);

        intent.putStringArrayListExtra("all_music_path", files_path);

        startService(intent);

        inflater = getLayoutInflater();
        madapter = new MusicAdapter(inflater, files);
        mlistview.setAdapter(madapter);
        mlistview.setOnItemClickListener(this);

        mradoibutton_pre.setOnClickListener(this);
        mradoibutton_start.setOnClickListener(this);
        mradoibutton_next.setOnClickListener(this);

        mseekbar.setOnSeekBarChangeListener(this);
    }


    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        mradoibutton_start.setChecked(true);
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.putExtra("type", Config.START_NEW_MUSIC_LISTVIEW);
        intent.putExtra("music_position",position );
        intent.putExtra("all_music_path", files_path);
        startService(intent);
    }

    class MyReceiver extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {

            int type = intent.getIntExtra("type", 0);
            Date date = new Date();
            SimpleDateFormat format=new SimpleDateFormat("mm:ss");
            switch (type) {
                case 0:
                    int duration = intent.getIntExtra("totaltime", 0);

                    date.setTime(duration);

                    String  format_duration= format.format(date);
                    mseekbar.setMax(duration);
                    mtv_totaltime.setText("" + format_duration);
                    break;
                case 1:
                    currenttime = intent.getIntExtra("currentime", 0);

                    date.setTime(currenttime);
                    String  format_currenttime= format.format(date);
                    mtv_currenttime.setText(format_currenttime);
                    mseekbar.setProgress(currenttime);
                    break;
                default:
                    break;
            }

        }

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.putExtra("type", Config.SEEKTO);
        intent.putExtra("progress", mseekbar.getProgress());
        startService(intent);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        switch (v.getId()) {
            case R.id.bt_previeous:
                intent.putExtra("type", Config.SELECT_PREVIOUS_BY_TOGGLEBUTTON);
                startService(intent);
                break;
            case R.id.bt_next:
                intent.putExtra("type", Config.SELECT_NEXT_BY_TOGGLEBUTTON);
                startService(intent);
                break;
            case R.id.bt_start:
                if(flag){
                    intent.putExtra("type", Config.START_NOW_BY_TOGGLEBUTTON);
                    intent.putExtra("mcurrenttime",currenttime);
                    startService(intent);
                    flag=!flag;
                }else{
                    intent.putExtra("type", Config.PAUSE_NOW_BY_TOGGLEBUTTON);
                    startService(intent);
                    flag=!flag;
                }

                break;
            default:
                break;
        }
    }
}