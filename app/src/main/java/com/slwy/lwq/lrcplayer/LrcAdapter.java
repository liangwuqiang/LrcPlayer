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
    private List<LrcRecord> mLrcList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvLrcIndex;
        TextView tvLrcText;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            tvLrcIndex = view.findViewById(R.id.lrc_index);
            tvLrcText = view.findViewById(R.id.lrc_text);
        }
    }

    LrcAdapter(List<LrcRecord> lrcList) {
        mLrcList = lrcList;
    }

//    @NonNull
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
        LrcRecord lrcRecord = mLrcList.get(position);
        holder.tvLrcIndex.setText(String.valueOf(position + 1));
        holder.tvLrcText.setText(lrcRecord.getLrcText());
    }

    @Override
    public int getItemCount() {
        return mLrcList.size();
    }
}
