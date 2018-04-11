package com.ysbing.lrcshow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 歌词显示区域
 * <p>
 * 比较难的逻辑，就是两句歌词交替显示
 * 如果歌词1为空，设置歌词1为下一句歌词，记录时间1-时间2为歌词2的等待时间
 * 歌词1播放->时间2-时间1
 * <p>
 * 记录开始时间
 * 播放前获取现在时间，减去和歌词时间的差
 * 歌词结束时间减去开始播放时间，算出速度
 */
public class LRCFragment extends Fragment {
    private View mView;
    private LRCTextView lrcTv1;
    private LRCTextView lrcTv2;
    private TextView mTimeView;
    private LrcUtil lrcUtil;
    private Timer mTimer;
    private long musicDuration;
    private float time;
    private long startTime;
    private boolean isEnd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView != null) {
            return mView;
        }
        mView = inflater.inflate(R.layout.fragment_lrc, container, false);
        initUI();
        return mView;
    }

    private void initUI() {
        lrcTv1 = (LRCTextView) mView.findViewById(R.id.lrcTv1);
        lrcTv2 = (LRCTextView) mView.findViewById(R.id.lrcTv2);
        mTimeView = (TextView) mView.findViewById(R.id.time);
    }

    /**
     * 开始歌词的播放
     */
    public void start(final String lrcPath, int musicDuration) throws FileNotFoundException {
        this.musicDuration = musicDuration * 1000;//转换成毫秒
        this.startTime = System.currentTimeMillis();
        lrcUtil = new LrcUtil(new File(lrcPath));
        new Thread(new Runnable() {
            @Override
            public void run() {
                time1 = time2 = 0;
                word1 = word2 = null;
                isEnd = false;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lrcTv1.cancel();
                        lrcTv2.cancel();
                        showTime();
                        showLrc1();
                    }
                });
            }
        }).start();
    }

    private void showTime() {
        time = 0;
        if (mTimer != null)
            mTimer.cancel();
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                time += 0.1;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String text = String.format(Locale.getDefault(), "time:%.1f", time);
                        mTimeView.setText(text);
                    }
                });
            }
        };
        mTimer.schedule(task, 0, 100);
    }

    private long time1, time2;
    private String word1, word2;

    private void showLrc1() {
        if (isEnd)
            return;
        Log.i("_lrc", "showLrc1_start_time1:" + time1);
        Log.i("_lrc", "showLrc1_start_time2:" + time2);
        if (time1 == 0 && lrcUtil.hasTime())
            time1 = lrcUtil.getTime();
        if (lrcUtil.hasTime())
            time2 = lrcUtil.getTime();
        else {
            isEnd = true;
            time2 = musicDuration;
        }
        Log.i("_lrc", "showLrc1_update_time1:" + time1);
        Log.i("_lrc", "showLrc1_update_time2:" + time2);

        if (TextUtils.isEmpty(word1) && lrcUtil.hasWord()) {
            word1 = lrcUtil.getWord();
            lrcTv1.setLrc(word1);
        }
        if (lrcUtil.hasWord())
            word2 = lrcUtil.getWord();
        else
            lrcTv2.setVisibility(View.INVISIBLE);
        long delay;
        long period;
        long t = System.currentTimeMillis() - startTime;
        Log.i("_lrc", "showLrc1_t:" + t);
        Log.i("_lrc", "showLrc1_time1:" + time1);
        Log.i("_lrc", "showLrc1time2:" + time2);
        if (time1 - t <= 0) {
            delay = 0;
            period = time2 - t;
            if (period <= 0)
                period = 0;
        } else {
            delay = time1 - t;
            period = time2 - time1;
            if (period <= 0)
                period = 0;
        }
        if (TextUtils.isEmpty(word2)) {
            if (lrcUtil.hasWord())
                word2 = lrcUtil.getWord();
            if (lrcUtil.hasTime())
                time2 = lrcUtil.getTime();
        }
        lrcTv2.setLrc(word2);
        Log.i("_lrc", "showLrc1_delay:" + delay);
        Log.i("_lrc", "showLrc1_period:" + period);
        lrcTv1.showLrc(getActivity(), delay, period, new LRCTextView.ShowListener() {
            @Override
            public void showFinish() {
                showLrc2();
            }
        });
    }

    private void showLrc2() {
        if (isEnd)
            return;
        if (time2 == 0 && lrcUtil.hasTime())
            time2 = lrcUtil.getTime();
        if (lrcUtil.hasTime())
            time1 = lrcUtil.getTime();
        else {
            isEnd = true;
            time1 = musicDuration;
        }

        if (TextUtils.isEmpty(word2) && lrcUtil.hasWord()) {
            word2 = lrcUtil.getWord();
            lrcTv2.setLrc(word2);
        }
        if (lrcUtil.hasWord())
            word1 = lrcUtil.getWord();
        else
            lrcTv1.setVisibility(View.INVISIBLE);
        long delay;
        long period;
        long t = System.currentTimeMillis() - startTime;
        Log.i("_lrc", "showLrc2_t:" + t);
        Log.i("_lrc", "showLrc2_time1:" + time1);
        Log.i("_lrc", "showLrc2_time2:" + time2);
        if (time2 - t <= 0) {
            delay = 0;
            period = time1 - t;
            if (period <= 0)
                period = 0;
        } else {
            delay = time2 - t;
            period = time1 - time2;
            if (period <= 0)
                period = 0;
        }
        if (TextUtils.isEmpty(word1)) {
            if (lrcUtil.hasWord())
                word1 = lrcUtil.getWord();
            if (lrcUtil.hasTime())
                time1 = lrcUtil.getTime();
        }
        lrcTv1.setLrc(word1);
        Log.i("_lrc", "showLrc2_delay:" + delay);
        Log.i("_lrc", "showLrc2_period:" + period);
        lrcTv2.showLrc(getActivity(), delay, period, new LRCTextView.ShowListener() {
            @Override
            public void showFinish() {
                showLrc1();
            }
        });
    }

}
