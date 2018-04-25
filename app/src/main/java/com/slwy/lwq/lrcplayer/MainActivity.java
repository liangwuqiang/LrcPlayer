package com.slwy.lwq.lrcplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements
        MediaPlayer.OnPreparedListener,   //实现 MediaPlayer 的 3 个的事件监听接口
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //全局变量的定义
//    Uri mp3Uri, lrcUri;
    String mp3Filename, lrcFilename;
    TextView txtContent, beginTime, endTime, txtTest;
    Button btnPlay;
    MediaPlayer mediaPlayer;
    LrcRecord lrcRecord;
    LrcUtil lrcUtil;
    Timer timer;
    TimerTask task;

    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
//        mp3Uri = Uri.parse("android.resource://" + //默认会播放程序内的音乐文件
//                getPackageName() + "/" + R.raw.welcome);

        //从界面布局文件中获得引用
//        txtContent = findViewById(R.id.txtContent);
//        beginTime = findViewById(R.id.beginTime);
//        endTime = findViewById(R.id.endTime);
//        btnPlay = findViewById(R.id.btnPlay);
//        txtTest = findViewById(R.id.txtTest);

        //打开lrc文件,并定位到第一个记录
        String path = Environment.getExternalStorageDirectory().getPath();
        lrcFilename = path + "/205.lrc";
        mp3Filename = path + "/205.mp3";
        lrcUtil = new LrcUtil();  //全局使用的实例
        lrcUtil.openFile(lrcFilename);
        lrcRecord = lrcUtil.reLocation(0);

        //打开声音文件，做好播放准备
        mediaPlayer = new MediaPlayer();           //创建 MediaPlayer 对象
        mediaPlayer.setOnPreparedListener(this);   //设置 3 个事件监听器
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        prepareMusic();

        //计时器 和 界面元素设置
        timer = new Timer();
        recordSkip();
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

    //实现接口的三个函数=====================================================================
    @Override
    public void onPrepared(MediaPlayer mp) {
//        btnPlay.setText(R.string.play);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.seekTo(0);
//        btnPlay.setText(R.string.play);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "出错停止", Toast.LENGTH_SHORT).show();
        return true;
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
            prepareMusic();
        }
    }

    //这段程序留着备用
    Uri convertUri(Uri uri) {  //将"content://"类型的Uri转换为"file://"的Uri
        if(uri.toString().substring(0, 7).equals("content")) {  //如果是以"content"开头
            String[] colName = { MediaStore.MediaColumns.DATA };    //声明要查询的字段
            Cursor cursor = getContentResolver().query(uri, colName,  //以uri进行查询
                    null, null, null);
            if (cursor != null){
                cursor.moveToFirst();      //移到查询结果的第一个记录
                uri = Uri.parse("file://" + cursor.getString(0)); //将路径转为 Uri
                cursor.close();
            }
        }
        return uri;
    }



    //播放初始化, 在主界面调用 和 选定文件的回调方法中使用======================================
    void prepareMusic() {
//        btnPlay.setText(R.string.play);   //按钮改"播放"
        try {
            mediaPlayer.reset();  //复位，开始播放前都这样处理
//            mediaPlayer.setDataSource(this, mp3Uri);
            mediaPlayer.setDataSource(mp3Filename);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(this, "错误:" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //界面按钮点击事件的方法==============================================================
    public void onMpPlay(View v) {  //对应于播放按钮的点击事件，ok
        if (mediaPlayer.isPlaying()) {  //正在播放时，暂停，按钮改"播放"
            mediaPlayer.pause();
//            btnPlay.setText(R.string.play);
        }
        else {
            mediaPlayer.start();  //不播放时，开始播放，按钮改"暂停"
//            btnPlay.setText(R.string.pause);
        }
    }

    public void onMpBackward(View v) {
        lrcRecord = lrcUtil.reLocation(-1);
        recordSkip();
    }

    public void onMpForward(View v) {
        lrcRecord = lrcUtil.reLocation(1);
        recordSkip();
    }

    private void recordSkip() {
//        txtContent.setText(lrcRecord.getLrcText());
//        beginTime.setText(timeFromIntToString(lrcRecord.getStartTime()));
//        endTime.setText(timeFromIntToString(lrcRecord.getStopTime()));
//        btnPlay.setText(R.string.pause);

        //启动计时器
        if (task != null){
            task.cancel();
        }
        task = new myTimerTask();

//        String text= "延迟：" + String.valueOf(lrcRecord.getStopTime()-lrcRecord.getStartTime()) + "秒";
//        txtTest.setText(text);
            timer.schedule(task, 1000, lrcRecord.getStopTime()-lrcRecord.getStartTime());
//        onMpForward();

    }

    //数字时间 --> 字符串时间  例如：mm:ss.ms
    private String timeFromIntToString(int intTime) {
//        int minute = intTime/1000/60;
//        int second = (intTime - minute * 60 * 1000)/1000;
//        int millisecond = intTime - minute * 60 * 1000 - second * 1000;
//        String strMin = String.format(Locale.CHINA,"%02d", min);
//        String strSecond = String.format(Locale.CHINA,"%02d", second);
//        String strMillisecond = String.format(Locale.CHINA,"%02d", millisecond).substring(0, 2);
//        return strMin + ":" + strSecond + "." + strMillisecond;   // 返回 mm:ss.ms
        int millisecond = intTime % 1000;
        intTime = intTime / 1000;
        int second = intTime % 60;
        intTime = intTime /60;
        int minute = intTime % 60;
        return String.format(Locale.CHINA,"%02d:%02d.%02d", minute, second, millisecond/10);
    }

    class myTimerTask extends TimerTask {
        public void run() {
            mediaPlayer.seekTo(lrcRecord.getStartTime());
        }
    }
}


