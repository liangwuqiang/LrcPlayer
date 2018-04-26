package com.slwy.lwq.lrcplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //全局变量的定义
    String mp3Filename, lrcFilename;
    MediaPlayer mediaPlayer;
    LrcRecord lrcRecord;
    LrcUtil lrcUtil;
    Timer timer;
    TimerTask task;

    private DrawerLayout drawerLayout;
    private List<LrcRecord> lrcRecordList = new ArrayList<>();
    private Adapter lrcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();  // 界面初始化
        initMusic();
        List<LrcRecord> lrcRecordList = getLrcData();
        initRecyclerView(lrcRecordList);  // 向RecyclerView填充数据
    }

    private void initViews() {
        //设置屏幕不随手机旋转、以及画面直向显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//设置屏幕不随手机旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置屏幕直向显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置屏幕不进入休眠

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  //显示出默认的返回图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);  //将默认的图标替换成菜单图标
        }
        navigationView.setCheckedItem(R.id.nav_call);  //设置默认选中项
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();  //关闭菜单栏
                return true;  //不做其他的点击响应处理
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "数据已删除", Snackbar.LENGTH_SHORT)
                        .setAction("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "数据已恢复",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });
    }

    private void initMusic() {
        File mp3File = new File(Environment.getExternalStorageDirectory(), "205.mp3");
        new MyMediaPlayer(mp3File);
    }
    private List<LrcRecord> getLrcData() {
        //打开lrc文件,并定位到第一个记录
        File lrcFile = new File(Environment.getExternalStorageDirectory(), "205.lrc");
        lrcUtil = new LrcUtil(lrcFile);
        return lrcUtil.getRecordList();
    }

    private void initRecyclerView(List<LrcRecord> list) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        Adapter adapter = new Adapter(list);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  //显示菜单
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //菜单选择，相应的点击事件
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_settings:
                Toast.makeText(this, "点击了设置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_open:
                Toast.makeText(this, "点击了打开", Toast.LENGTH_SHORT).show();
                onPick();
                break;
            case R.id.action_delete:
                Toast.makeText(this, "点击了打开", Toast.LENGTH_SHORT).show();
                onPick();
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer.isPlaying()) {  //如果正在播, 就暂停
//            btnPlay.setText("继续");
            mediaPlayer.pause();  //暂停播放
        }
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();  //释放 MediaPlayer 对象
        super.onDestroy();
    }





    public void onPick() {  //菜单点击事件
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
        it.setType("audio/*");     //音乐类型
//        it.setType("file/*");
        startActivityForResult(it, 100);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {  //回调方法
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
//            mp3Uri = convertUri(data.getData());
//            txtContent.setText(mp3Uri.getLastPathSegment ());
//            txtContent.setText(data.getData().toString());
//            prepareMusic();
        }
    }

}


