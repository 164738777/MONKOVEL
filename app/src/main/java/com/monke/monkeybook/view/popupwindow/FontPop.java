//Copyright (c) 2017. 章钦豪. All rights reserved.
package com.monke.monkeybook.view.popupwindow;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.monke.monkeybook.R;
import com.monke.monkeybook.ReadBookControl;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 作用：阅读主界面设置字体大小、字体颜色、背景颜色的Popup
 */
public class FontPop extends PopupWindow {
    private Context mContext;
    private View view;
    private FrameLayout flSmaller;
    private FrameLayout flBigger;
    private TextView tvTextSizedDefault;
    private TextView tvTextSize;
    private CircleImageView civBgWhite;
    private CircleImageView civBgYellow;
    private CircleImageView civBgGreen;
    private CircleImageView civBgBlack;

    private ReadBookControl readBookControl;

    public interface OnChangeProListener {
        void textChange(int index);

        void bgChange(int index);
    }

    private OnChangeProListener changeProListener;

    public FontPop(Context context, @NonNull OnChangeProListener changeProListener) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mContext = context;
        this.changeProListener = changeProListener;

        view = LayoutInflater.from(mContext).inflate(R.layout.view_pop_font, null);
        this.setContentView(view);
        initData();
        bindView();
        bindEvent();

        // FIXME 设置Popup背景为圆角，貌似不起作用
        setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.shape_pop_checkaddshelf_bg));
        setFocusable(true);
        setTouchable(true);
        setAnimationStyle(R.style.anim_pop_windowlight);
    }

    private void initData() {
        readBookControl = ReadBookControl.getInstance();
    }

    private void bindView() {
        flSmaller = view.findViewById(R.id.fl_smaller);
        flBigger = view.findViewById(R.id.fl_bigger);
        tvTextSizedDefault = view.findViewById(R.id.tv_textsize_default);
        tvTextSize = view.findViewById(R.id.tv_dur_textsize);
        updateText(readBookControl.getTextKindIndex());

        civBgWhite = view.findViewById(R.id.civ_bg_white);
        civBgYellow = view.findViewById(R.id.civ_bg_yellow);
        civBgGreen = view.findViewById(R.id.civ_bg_green);
        civBgBlack = view.findViewById(R.id.civ_bg_black);
        updateBg(readBookControl.getTextDrawableIndex());
    }

    /**
     * 设置字体大小相关的选择逻辑处理, 并且保存字体大小进SP
     * @param textKindIndex
     */
    private void updateText(int textKindIndex) {
        if (textKindIndex == 0) {
            flSmaller.setEnabled(false);
            flBigger.setEnabled(true);
        } else if (textKindIndex == readBookControl.getTextKind().size() - 1) {
            flSmaller.setEnabled(true);
            flBigger.setEnabled(false);
        } else {
            flSmaller.setEnabled(true);
            flBigger.setEnabled(true);

        }
        if (textKindIndex == ReadBookControl.DEFAULT_TEXT) {
            tvTextSizedDefault.setEnabled(false);
        } else {
            tvTextSizedDefault.setEnabled(true);
        }

        tvTextSize.setText(String.valueOf(readBookControl.getTextKind().get(textKindIndex).get("textSize")));
        readBookControl.setTextKindIndex(textKindIndex);
    }

    /**
     * 设置颜色的选中状态（选中的颜色item的border颜色为褐色）, 并且保存选择的颜色下标进SP
     * @param index
     */
    private void updateBg(int index) {
        civBgWhite.setBorderColor(Color.parseColor("#00000000"));
        civBgYellow.setBorderColor(Color.parseColor("#00000000"));
        civBgGreen.setBorderColor(Color.parseColor("#00000000"));
        civBgBlack.setBorderColor(Color.parseColor("#00000000"));

        switch (index) {
            case 0:
                civBgWhite.setBorderColor(Color.parseColor("#F3B63F"));
                break;
            case 1:
                civBgYellow.setBorderColor(Color.parseColor("#F3B63F"));
                break;
            case 2:
                civBgGreen.setBorderColor(Color.parseColor("#F3B63F"));
                break;
            default:
                civBgBlack.setBorderColor(Color.parseColor("#F3B63F"));
                break;
        }

        readBookControl.setTextDrawableIndex(index);
    }

    private void bindEvent() {
        flSmaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateText(readBookControl.getTextKindIndex() - 1);
                changeProListener.textChange(readBookControl.getTextKindIndex());
            }
        });
        flBigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateText(readBookControl.getTextKindIndex() + 1);
                changeProListener.textChange(readBookControl.getTextKindIndex());
            }
        });
        tvTextSizedDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateText(ReadBookControl.DEFAULT_TEXT);
                changeProListener.textChange(readBookControl.getTextKindIndex());
            }
        });

        civBgWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBg(0);
                changeProListener.bgChange(readBookControl.getTextDrawableIndex());
            }
        });
        civBgYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBg(1);
                changeProListener.bgChange(readBookControl.getTextDrawableIndex());
            }
        });
        civBgGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBg(2);
                changeProListener.bgChange(readBookControl.getTextDrawableIndex());
            }
        });
        civBgBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBg(3);
                changeProListener.bgChange(readBookControl.getTextDrawableIndex());
            }
        });
    }
}