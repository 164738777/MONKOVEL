package com.monke.monkeybook.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monke.monkeybook.R;
import com.monke.monkeybook.bean.BookShelfBean;
import com.monke.monkeybook.view.adapter.ChapterListAdapter;

/**
 * 作用: 阅读主界面的章节列表ListView
 */
public class ChapterListView extends FrameLayout {
    private TextView tvName;
    private TextView tvListCount;
    private RecyclerView rvList;
    private RecyclerViewBar rvbSlider;

    private FrameLayout flBg;
    private LinearLayout llContent;

    private ChapterListAdapter chapterListAdapter;

    private Animation animIn;
    private Animation animOut;

    private OnItemClickListener itemClickListener;
    private BookShelfBean bookShelfBean;

    public ChapterListView(@NonNull Context context) {
        this(context, null);
    }

    public ChapterListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChapterListView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setVisibility(INVISIBLE);
        LayoutInflater.from(getContext()).inflate(R.layout.view_chapterlist, this, true);
        initInAndOutAnimation();
        initView();
    }

    /**
     * 左进左出，赋予根FrameLayout的点击隐藏此ListView.(也就是点击非列表区域会隐藏列表View)
     */
    private void initInAndOutAnimation() {
        animIn = AnimationUtils.loadAnimation(getContext(), R.anim.anim_pop_chapterlist_in);
        animIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                flBg.setOnClickListener(null);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                flBg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissChapterList();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animOut = AnimationUtils.loadAnimation(getContext(), R.anim.anim_pop_chapterlist_out);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                flBg.setOnClickListener(null);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llContent.setVisibility(INVISIBLE);
                setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initView() {
        flBg = findViewById(R.id.fl_bg);
        llContent = findViewById(R.id.ll_content);
        tvName = findViewById(R.id.tv_name);
        tvListCount = findViewById(R.id.tv_listcount);
        rvList = findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvList.setItemAnimator(null);
        rvbSlider = findViewById(R.id.rvb_slider);
    }

    public void show(int durChapter) {
        chapterListAdapter.setIndex(durChapter);
        ((LinearLayoutManager) rvList.getLayoutManager()).scrollToPositionWithOffset(durChapter, 0);
        if (getVisibility() != VISIBLE) {
            setVisibility(VISIBLE);
            animOut.cancel();
            animIn.cancel();
            llContent.setVisibility(VISIBLE);
            llContent.startAnimation(animIn);
        }
    }

    public interface OnItemClickListener {
        void itemClick(int index);
    }

    public void setData(BookShelfBean bookShelfBean, OnItemClickListener clickListener) {
        this.itemClickListener = clickListener;
        this.bookShelfBean = bookShelfBean;
        tvName.setText(bookShelfBean.getBookInfoBean().getName());
        tvListCount.setText("共" + bookShelfBean.getBookInfoBean().getChapterlist().size() + "章");
        chapterListAdapter = new ChapterListAdapter(bookShelfBean, new OnItemClickListener() {
            @Override
            public void itemClick(int index) {
                if (itemClickListener != null) {
                    itemClickListener.itemClick(index);
                    rvbSlider.scrollToPositionWithOffset(index);
                }
            }
        });
        rvList.setAdapter(chapterListAdapter);
        rvbSlider.setRecyclerView(rvList);
    }

    public Boolean dismissChapterList() {
        if (getVisibility() != VISIBLE) {
            return false;
        } else {
            animOut.cancel();
            animIn.cancel();
            llContent.startAnimation(animOut);
            return true;
        }
    }
}