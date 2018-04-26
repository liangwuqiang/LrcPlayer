package com.slwy.lwq.lrcplayer;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private Context mContext;
    private List<LrcRecord> mLrcList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvLrcIndex;
        TextView tvStartTime;
        TextView tvStopTime;
        TextView tvLrcText;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            tvLrcIndex = view.findViewById(R.id.lrc_index);
            tvStartTime = view.findViewById(R.id.start_time);
            tvStopTime = view.findViewById(R.id.stop_time);
            tvLrcText = view.findViewById(R.id.lrc_text);
        }
    }

    Adapter(List<LrcRecord> lrcList) {
        mLrcList = lrcList;
    }

//    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.lrc_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        File mp3File = new File(Environment.getExternalStorageDirectory(), "205.mp3");
        final MyMediaPlayer myMediaPlayer = new MyMediaPlayer(mp3File);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                LrcRecord lrcRecord = mLrcList.get(position);
                myMediaPlayer.play(lrcRecord);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LrcRecord lrcRecord = mLrcList.get(position);
        holder.tvLrcIndex.setText(String.valueOf(position + 1));
        holder.tvStartTime.setText(timeFromIntToString(lrcRecord.getStartTime()));
        holder.tvStopTime.setText(timeFromIntToString(lrcRecord.getStopTime()));
        holder.tvLrcText.setText(lrcRecord.getLrcText());
    }

    //数字时间 --> 字符串时间  例如：mm:ss.ms
    public String timeFromIntToString(int intTime) {
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

    @Override
    public int getItemCount() {
        return mLrcList.size();
    }
}
