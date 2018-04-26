package com.slwy.lwq.lrcplayer;

import android.media.MediaPlayer;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

class MyMediaPlayer implements
        MediaPlayer.OnPreparedListener,   //实现 MediaPlayer 的 3 个的事件监听接口
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private int mStartTime;  // 每条记录的开始时间
    private  int mTheLastTime;  // 最终的时间
    private Timer timer;
    private Task task;

    MyMediaPlayer(File file){  // 类的构造方法
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);   //设置 3 个事件监听器
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);

        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(file.getPath());
            mediaPlayer.prepare();
            mTheLastTime = mediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }

        timer = new Timer();
    }

    void play(LrcRecord lrcRecord){
        mStartTime = lrcRecord.getStartTime();
        int duration = lrcRecord.getStopTime() - lrcRecord.getStartTime();


     if (task != null){ task.cancel(); }
        task = new Task();
        timer.schedule(task, 1000, duration);
    }

    public int getEndTime(){
        return mTheLastTime;
    }

    class Task extends TimerTask {
        @Override
        public void run() {
            mediaPlayer.start();
//            mediaPlayer.seekTo(mStartTime);
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
