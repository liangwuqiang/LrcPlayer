package com.ysbing.lrcshow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class LRCTextView extends RelativeLayout {

    private Timer mTimer;

    private TextView tvDefault;
    private TextView tvSelect;

    private String lrc = "我是歌词，我是歌词，我是歌词";
    private float percent;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    public void setColor1(int color1) {
        tvDefault.setTextColor(color1);
    }

    public void setColor2(int color2) {
        tvSelect.setTextColor(color2);
    }

    public void setSize(float size) {
        tvDefault.setTextSize(size);
        tvSelect.setTextSize(size);
    }

    public void setLrc(String lrc) {
        this.lrc = lrc;
        tvDefault.setText(lrc);
        tvSelect.setText(lrc);
        if (tvSelect.getWidth() > 0)
            tvSelect.setWidth(0);
    }

    public void cancel() {
        if (mTimer != null)
            mTimer.cancel();
        mTimer = null;
        percent = 0;
        if (tvSelect.getWidth() > 0)
            tvSelect.setWidth(0);
        setVisibility(VISIBLE);
    }

    /**
     * 每执行一次，都会降计时器清零
     *
     * @param delay  执行前等待时间
     * @param period 循环时间
     */
    public void showLrc(final Activity context, long delay, long period, final ShowListener listener) {
        tvSelect.setVisibility(VISIBLE);
        tvSelect.setWidth(0);
        tvDefault.setText(lrc);
        tvSelect.setText(lrc);

        Log.i("_lrc", "start_openCounter:" + mOpenCounter.get());
        mOpenCounter.addAndGet(1);
        if (period < 100)
            period = 100;
        float speed = period / 100;

        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (percent < 100) {
                            percent++;
                        } else {
                            Log.i("_lrc", "end_openCounter:" + mOpenCounter.get());
                            if (mOpenCounter.get() > 0) {
                                mOpenCounter.set(0);
                                percent = 0;
                                listener.showFinish();
                                tvSelect.setVisibility(INVISIBLE);
                                cancel();
                            }
                        }
                        setPercent();
                    }
                });
            }
        };
        mTimer.schedule(task, delay, (long) speed);
    }

    public LRCTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LRCTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LRCTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        tvDefault = new TextView(getContext());
        tvDefault.setText(lrc);
        tvDefault.setTextColor(Color.parseColor("#726463"));
        tvDefault.setEllipsize(null);
        tvDefault.setSingleLine();
        tvDefault.setTextSize(20);

        tvSelect = new TextView(getContext());
        tvSelect.setTextColor(Color.parseColor("#39DF7C"));
        tvSelect.setText(lrc);
        tvSelect.setEllipsize(null);
        tvSelect.setSingleLine();
        tvSelect.setTextSize(20);
        addView(tvDefault);
        addView(tvSelect);
        tvSelect.setWidth(0);
    }

    /**
     * 设置颜色渐变百分比
     */
    private void setPercent() {
        setSelectWidth((int) (getSelectWidth() * percent / 100));
    }

    private int getSelectWidth() {
        return tvDefault.getWidth();
    }

    private void setSelectWidth(int pixels) {
        if (pixels <= getSelectWidth()) {
            tvSelect.setWidth(pixels);
        }
    }

    public interface ShowListener {
        void showFinish();
    }
}
