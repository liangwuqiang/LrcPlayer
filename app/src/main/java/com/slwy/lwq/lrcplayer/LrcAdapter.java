package com.slwy.lwq.lrcplayer;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LrcAdapter  extends RecyclerView.Adapter<LrcAdapter.ViewHolder> {

    private Context mContext;

    private List<LrcRecord> lrcRecordList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView lrcIndex;
        TextView lrcText;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            lrcIndex = view.findViewById(R.id.lrc_index);
            lrcText = view.findViewById(R.id.lrc_text);
        }
    }

    public LrcAdapter(List<LrcRecord> lrcRecordList) {
        this.lrcRecordList = lrcRecordList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.lrc_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LrcRecord lrcRecord = lrcRecordList.get(position);
        holder.lrcText.setText(lrcRecord.getLrcText());
//        Glide.with(mContext).load(fruit.getImageId()).into(holder.fruitImage);
    }

    @Override
    public int getItemCount() {
        return lrcRecordList.size();
    }
}
