package com.slwy.lwq.lrcplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetBehavior;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity  implements
        MediaPlayer.OnPreparedListener,   //实现 MediaPlayer 的 3 个的事件监听接口
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private int mPosition = 0;
    private Timer loopTimer, jumpTimer;
    private MyLoopTask myLoopTask;
    private MyJumpTask myJumpTask;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView bottomSheetHeading;
    private DrawerLayout drawerLayout;
    private List<LrcRecord> lrcRecordList;
    private  int sleepTime;
    RecyclerViewAdapter recyclerViewAdapter;

    private OnRecyclerClickListener onRecyclerClickListener = new OnRecyclerClickListener() {
        @Override
        public void onItemClickListener(View view, int position) {
            //这里的view就是我们点击的view  position就是点击的position
//            mPosition = position;
//            Toast.makeText(view.getContext(),"点击: " + (position + 1),Toast.LENGTH_SHORT).show();
            lrcLoop(position);
        }
    };

    private void lrcLoop(int position) {
        mPosition = position;
        if ( position > lrcRecordList.size()){
            mPosition = 1;
        }
        int startTime = lrcRecordList.get(mPosition).getStartTime();
        int duration = lrcRecordList.get(mPosition).getStopTime() - startTime;
        recyclerViewAdapter.setDefSelect(mPosition);

        //  定时器1，监听句子循环
        if (myLoopTask != null){ myLoopTask.cancel(); }
        myLoopTask = new MyLoopTask();
        loopTimer.schedule(myLoopTask, 0, duration + sleepTime);
        //定时器2，监听是否跳转
        // 加入上面的setDefSelect之后，以下代码不能正常运行，不知道是啥原因。手动点击跳转没有问题。
//        if (myJumpTask != null){ myJumpTask.cancel(); }
//        myJumpTask = new MyJumpTask();
//        jumpTimer.schedule(myJumpTask, (duration + sleepTime) * 3);
    }

    class MyLoopTask extends TimerTask {
        @Override
        public void run() {
            int startTime = lrcRecordList.get(mPosition).getStartTime();
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                try{
                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mediaPlayer.seekTo(startTime);
            mediaPlayer.start();
        }
    }

    class MyJumpTask extends TimerTask {
        @Override
        public void run() {
            lrcLoop(mPosition + 1);
        }
    }

    NavigationView navigationView;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();  // 界面初始化
        initData();
        initListener();
    }

    private void initViews() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);//屏幕不随手机旋转
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//屏幕垂直显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕不进入休眠
        //工具条
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);  //显示出默认的返回图标
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);  //将默认的图标替换成菜单图标
        }
        //侧边导航栏
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_call);  //设置默认项
        //浮动按钮
        fab = findViewById(R.id.fab);
        //底部表单
        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheetLayout));
        bottomSheetHeading = findViewById(R.id.bottomSheetHeading);
    }

    private void initData() {
        loopTimer = new Timer();
        jumpTimer = new Timer();
        sleepTime = 1000;
//        myTask = new MyTask();
        mediaPlayer = new MediaPlayer();
        File mp3File = new File(Environment.getExternalStorageDirectory(), "205.mp3");
        try {
            mediaPlayer.setDataSource(mp3File.getPath());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int theLastTime = mediaPlayer.getDuration();

        File lrcFile = new File(Environment.getExternalStorageDirectory(), "205.lrc");
        LrcUtil lrcUtil = new LrcUtil(lrcFile, theLastTime);
        lrcRecordList = lrcUtil.getRecordList();
        updateRecyclerView(lrcRecordList);  // 向RecyclerView填充数据
        lrcLoop(mPosition);
    }

    private void updateRecyclerView(List<LrcRecord> list) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerViewAdapter = new RecyclerViewAdapter(list, onRecyclerClickListener);
        recyclerView.setAdapter(recyclerViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void initListener() {
        //监听侧边栏
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                drawerLayout.closeDrawers();  //关闭菜单栏
                return true;  //不做其他的点击响应处理
            }
        });
        //监听浮动按钮
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "数据已删除", Snackbar.LENGTH_SHORT)
//                        .setAction("取消", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Toast.makeText(MainActivity.this, "数据已恢复",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }).show();
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });
        //捕获底部表单的回调
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetHeading.setText("折叠");
                } else {
                    bottomSheetHeading.setText("扩展");
                }

                // Check Logs to see how bottom sheets behaves
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("Bottom Sheet Behaviour", "STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e("Bottom Sheet Behaviour", "STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("Bottom Sheet Behaviour", "STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e("Bottom Sheet Behaviour", "STATE_HIDDEN");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e("Bottom Sheet Behaviour", "STATE_SETTLING");
                        break;
                }
            }
            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        });
        //监听3个接口
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
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

    //实现接口的三个函数=====================================================================
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //Todo: 准备好时
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.seekTo(0);  // 播放完成 跳到开始
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        //Todo: 出现错误时
        return true;
    }
}


