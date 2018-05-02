package com.slwy.lwq.lrcplayer;

import android.media.MediaPlayer;
import java.util.Timer;
import java.util.TimerTask;

class MyMediaPlayer{

    private MediaPlayer mediaPlayer;
    private int mStartTime;  // 每条记录的开始时间
    private Timer timer;
    private Task task;

    void play(LrcRecord lrcRecord){
        mStartTime = lrcRecord.getStartTime();
        int duration = lrcRecord.getStopTime() - lrcRecord.getStartTime();


     if (task != null){ task.cancel(); }
        task = new Task();
        timer.schedule(task, 1000, duration);
    }

    class Task extends TimerTask {
        @Override
        public void run() {
//            mediaPlayer.start();
            mediaPlayer.seekTo(mStartTime);
        }
    }


}
