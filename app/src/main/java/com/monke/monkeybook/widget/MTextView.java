package com.monke.monkeybook.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * Created by ZQH on 2017/4/10.
 * 作用：初步鉴定是一个自动换行的TextView. 目前只用于阅读界面中。
 */

public class MTextView extends android.support.v7.widget.AppCompatTextView {

    public MTextView(Context context) {
        this(context, null);
    }

    public MTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getTextColors().getDefaultColor());
        Layout layout = new StaticLayout(getText(), paint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, getLineSpacingMultiplier(), getLineSpacingExtra(), false);
        layout.draw(canvas);
    }
}