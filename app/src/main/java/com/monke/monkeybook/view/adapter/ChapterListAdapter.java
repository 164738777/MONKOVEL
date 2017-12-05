//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook.view.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.monke.monkeybook.R;
import com.monke.monkeybook.bean.BookShelfBean;
import com.monke.monkeybook.widget.ChapterListView;

/**
 * 阅读主界面章节ListView的Adapter
 */
public class ChapterListAdapter extends RecyclerView.Adapter<ChapterListAdapter.ViewHolder> {
    private BookShelfBean bookShelfBean;
    private ChapterListView.OnItemClickListener itemClickListener;
    private int index = 0;
    private Boolean isAsc = true;

    public ChapterListAdapter(BookShelfBean bookShelfBean, @NonNull ChapterListView.OnItemClickListener itemClickListener) {
        this.bookShelfBean = bookShelfBean;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_adapter_chapterlist, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int posiTion) {
        if (posiTion == getItemCount() - 1) {
            holder.vLine.setVisibility(View.INVISIBLE);
        } else
            holder.vLine.setVisibility(View.VISIBLE);

        final int position;
        if (isAsc) {
            position = posiTion;
        } else {
            position = getItemCount() - 1 - posiTion;
        }
        holder.tvName.setText(bookShelfBean.getBookInfoBean().getChapterlist().get(position).getDurChapterName());
        holder.flContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIndex(position);
                itemClickListener.itemClick(position);
            }
        });
        if (position == index) {
            holder.flContent.setBackgroundColor(Color.parseColor("#cfcfcf"));
            holder.flContent.setClickable(false);
        } else {
            holder.flContent.setBackgroundResource(R.drawable.bg_ib_pre2);
            holder.flContent.setClickable(true);
        }
    }

    @Override
    public int getItemCount() {
        if (bookShelfBean == null) {
            return 0;
        } else {
            return bookShelfBean.getBookInfoBean().getChapterlist().size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout flContent;
        private TextView tvName;
        private View vLine;

        public ViewHolder(View itemView) {
            super(itemView);
            flContent = itemView.findViewById(R.id.fl_content);
            tvName = itemView.findViewById(R.id.tv_name);
            vLine = itemView.findViewById(R.id.v_line);
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        notifyItemChanged(this.index);
        this.index = index;
        notifyItemChanged(this.index);
    }
}
