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

public class MainActivity extends AppCompatActivity implements
        MediaPlayer.OnPreparedListener,   //实现 MediaPlayer 的 3 个的事件监听接口
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //全局变量的定义
    Uri mp3Uri, lrcUri;
    TextView txtContent, beginTime, endTime;
    Button btnPlay;
    MediaPlayer mediaPlayer;
    LrcRecord lrcRecord;

    LrcUtil lrc = new LrcUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置屏幕不随手机旋转、以及画面直向显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//设置屏幕不随手机旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置屏幕直向显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置屏幕不进入休眠

        mp3Uri = Uri.parse("android.resource://" + //默认会播放程序内的音乐文件
                getPackageName() + "/" + R.raw.welcome);
        lrcUri = Uri.parse("android.resource://" + //默认会播放程序内的音乐文件
                getPackageName() + "/" + R.raw.panda);

        //从界面布局文件中获得引用
        txtContent = findViewById(R.id.txtContent);
        beginTime = findViewById(R.id.beginTime);
        endTime = findViewById(R.id.endTime);
        btnPlay = findViewById(R.id.btnPlay);

        //打开lrc文件,并定位到第一个记录
        String path = Environment.getExternalStorageDirectory().getPath();
        String filename = path + "/205.lrc";
        lrc.openFile(filename);
        lrcRecord = lrc.reLocation(0);

        //修改界面布局元素的值
        txtContent.setText(lrcRecord.text);
        beginTime.setText(timeFromIntToString(lrcRecord.beginTime));
        endTime.setText(timeFromIntToString(lrcRecord.endTime));
        btnPlay.setText(R.string.pause);

        //打开声音文件，做好播放准备
        mediaPlayer = new MediaPlayer();           //创建 MediaPlayer 对象
        mediaPlayer.setOnPreparedListener(this);   //设置 3 个事件监听器
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);

        prepareMusic();

        //工具条的设置
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer.isPlaying()) {  //如果正在播, 就暂停
            btnPlay.setText("继续");
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
        btnPlay.setText(R.string.play);  //按钮改"播放"
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaPlayer.seekTo(0);
        btnPlay.setText(R.string.play);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "出错停止", Toast.LENGTH_SHORT).show();
        return true;
    }

    //处理菜单点击事件================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  //显示菜单
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  //菜单选择，相应的点击事件
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_open) {
            onPick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPick() {  //菜单点击事件
        Intent it = new Intent(Intent.ACTION_GET_CONTENT);
//        it.setType("audio/*");     //音乐类型
        it.setType("file/*");     //音乐类型
        startActivityForResult(it, 100);
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {  //回调方法
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            mp3Uri = convertUri(data.getData());
//            txtContent.setText(mp3Uri.getLastPathSegment ());
//            txtContent.setText(data.getData().toString());
            prepareMusic();
        }
    }

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
        btnPlay.setText(R.string.play);   //按钮改"播放"
        try {
            mediaPlayer.reset();  //复位，开始播放前都这样处理
            mediaPlayer.setDataSource(this, mp3Uri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(this, "错误:" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //界面按钮点击事件的方法==============================================================
    public void onMpPlay(View v) {  //对应于播放按钮的点击事件，ok
        if (mediaPlayer.isPlaying()) {  //正在播放时，暂停，按钮改"播放"
            mediaPlayer.pause();
            btnPlay.setText(R.string.play);
        }
        else {
            mediaPlayer.start();  //不播放时，开始播放，按钮改"暂停"
            btnPlay.setText(R.string.pause);
        }
    }

    public void onMpBackward(View v) {
//        if(!mediaPlayer.isPlaying()) return;
//        int pos = mediaPlayer.getCurrentPosition();
//        pos -= 10000;  //倒退 10 秒 (10000ms)
//        if(pos <0) pos = 0;
//        mediaPlayer.seekTo(pos);
//


        String text = lrc.reLocation(-1).text;
        txtContent.setText(text);
        //修改界面布局元素的值
        txtContent.setText(lrcRecord.text);
//        beginTime.setText(timeFromIntToString(lrcRecord.beginTime));
//        endTime.setText(timeFromIntToString(lrcRecord.endTime));
        btnPlay.setText(R.string.pause);
    }

    public void onMpForward(View v) {
//        if(!mediaPlayer.isPlaying()) return;
//        int len = mediaPlayer.getDuration();
//        int pos = mediaPlayer.getCurrentPosition();
//        pos += 10000;  //前进 10 秒 (10000ms)
//        if(pos > len) pos = len;
//        mediaPlayer.seekTo(pos);

        String text = lrc.reLocation(1).text;
        txtContent.setText(text);
        //修改界面布局元素的值
        txtContent.setText(lrcRecord.text);
//        beginTime.setText(timeFromIntToString(lrcRecord.beginTime));
//        endTime.setText(timeFromIntToString(lrcRecord.endTime));
        btnPlay.setText(R.string.pause);
    }

    //数字时间 --> 字符串时间  例如：mm:ss.ms
    private String timeFromIntToString(int intTime) {
        int min = intTime/1000/60;
        int second = (intTime - min * 60 * 1000)/1000;
        int millisecond = intTime - min * 60 * 1000 - second * 1000;
        String strMin = String.format(Locale.CHINA,"%02d", min);
        String strSecond = String.format(Locale.CHINA,"%02d", second);
        String strMillisecond = String.format(Locale.CHINA,"%02d", millisecond).substring(0, 2);
        return strMin + ":" + strSecond + "." + strMillisecond;   // 返回 mm:ss.ms
    }
}
