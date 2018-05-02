package com.slwy.lwq.lrcplayer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
        implements View.OnClickListener{

    private Context mContext;
    private List<LrcRecord> mLrcList;
    private OnRecyclerClickListener mOnRecyclerViewItemClickListener;

    //构造方法中添加自定义监听接口
    RecyclerViewAdapter(List<LrcRecord> lrcList, OnRecyclerClickListener OnRecyclerViewItemClickListener) {
        mLrcList = lrcList;
        mOnRecyclerViewItemClickListener = OnRecyclerViewItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.lrc_item, parent, false);
//        final ViewHolder holder = new ViewHolder(view);
//        final MyMediaPlayer myMediaPlayer = new MyMediaPlayer();
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                LrcRecord lrcRecord = mLrcList.get(position);
//                myMediaPlayer.play(lrcRecord);
//            }
//        });
//        return holder;
        view.setOnClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        LrcRecord lrcRecord = mLrcList.get(position);
        myViewHolder.tvLrcIndex.setText(String.valueOf(position + 1));
        myViewHolder.tvStartTime.setText(timeFromIntToString(lrcRecord.getStartTime()));
        myViewHolder.tvStopTime.setText(timeFromIntToString(lrcRecord.getStopTime()));
        myViewHolder.tvLrcText.setText(lrcRecord.getLrcText());
        myViewHolder.itemView.setTag(position);  //给view设置tag以作为参数传递到监听回调方法中
    }

    private String timeFromIntToString(int intTime) {
        //数字时间 --> 字符串时间  例如：mm:ss.ms
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

    @Override
    public void onClick(View view) {
        mOnRecyclerViewItemClickListener.onItemClickListener(view, (int)view.getTag());
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvLrcIndex;
        TextView tvStartTime;
        TextView tvStopTime;
        TextView tvLrcText;

        MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            tvLrcIndex = view.findViewById(R.id.lrc_index);
            tvStartTime = view.findViewById(R.id.start_time);
            tvStopTime = view.findViewById(R.id.stop_time);
            tvLrcText = view.findViewById(R.id.lrc_text);
        }
    }
}
