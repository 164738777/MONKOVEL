package com.monke.monkeybook.widget.contentswitchview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.monke.monkeybook.R;
import com.monke.monkeybook.ReadBookControl;
import com.monke.monkeybook.utils.TimeUtils;
import com.monke.monkeybook.widget.MTextView;

import java.util.List;

/**
 * 阅读主View
 */
public class BookContentView extends FrameLayout {
    public long qTag = System.currentTimeMillis();

    public static final int DUR_PAGE_INDEX_BEGIN = -1;
    public static final int DUR_PAGE_INDEX_END = -2;

    private View view;
    private ImageView ivBg;
    private TextView tvTitle;
    private LinearLayout llContent;
    private MTextView tvContent;
    private View vBottom;
    private TextView tvPage;

    private TextView tvLoading;
    private LinearLayout llError;
    private TextView tvErrorInfo;
    private TextView tvLoadAgain;

    private String title;
    private String content;
    private int durChapterIndex;
    private int chapterAll;
    private int durPageIndex;      //如果durPageIndex = -1 则是从头开始  -2则是从尾开始
    private int pageAll;

    private ContentSwitchView.LoadDataListener loadDataListener;
    private SetDataListener setDataListener;

    public interface SetDataListener {
        void setDataFinish(BookContentView bookContentView, int durChapterIndex, int chapterAll, int durPageIndex, int pageAll, int fromPageIndex);
    }

    public BookContentView(Context context) {
        this(context, null);
    }

    public BookContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BookContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_content_switch_item, this, false);
        addView(view);
        ivBg = view.findViewById(R.id.iv_bg);
        tvTitle = view.findViewById(R.id.tv_title);
        llContent = view.findViewById(R.id.ll_content);
        tvContent = view.findViewById(R.id.tv_content);
        vBottom = view.findViewById(R.id.v_bottom);
        tvPage = view.findViewById(R.id.tv_page);

        tvLoading = view.findViewById(R.id.tv_loading);
        llError = view.findViewById(R.id.ll_error);
        tvErrorInfo = view.findViewById(R.id.tv_error_info);
        tvLoadAgain = view.findViewById(R.id.tv_load_again);

        tvLoadAgain.setOnClickListener(v -> {
            if (loadDataListener != null) {
                loading();
            }
        });
    }

    /**
     * 隐藏错误、重试布局，加载小说data。
     */
    public void loading() {
        llError.setVisibility(GONE);
        tvLoading.setVisibility(VISIBLE);
        llContent.setVisibility(INVISIBLE);
        qTag = System.currentTimeMillis();
        // 执行请求操作
        if (loadDataListener != null) {
            loadDataListener.loaddata(this, qTag, durChapterIndex, durPageIndex);
        }
    }

    /**
     * 隐藏错误、记载布局，显示内容布局。
     */
    public void finishLoading() {
        llError.setVisibility(GONE);
        llContent.setVisibility(VISIBLE);
        tvLoading.setVisibility(GONE);
    }

    public void setNoData(String contentLines) {
        this.content = contentLines;

        tvPage.setText((this.durPageIndex + 1) + "/" + this.pageAll);

        finishLoading();
    }

    /**
     * 加载小说页面的核心函数, 对成员变量赋值
     * @param tag 时间
     * @param title 当前章节标题
     * @param contentLines 当前所浏览页面的全部内容List<String>
     * @param durChapterIndex 当前章节下标
     * @param chapterAll 当前小说章节总数
     * @param durPageIndex 当前页面所在的章节的页码
     * @param durPageAll 当前章节的总页数
     */
    public void updateData(long tag, String title, List<String> contentLines, int durChapterIndex, int chapterAll, int durPageIndex, int durPageAll) {
        if (tag == qTag) {

            Log.d("MyLog", "updateData:tag " + TimeUtils.getTimeStringByLongMills(tag)
                    + " , title : " + title + " , contentLines : " + contentLines
                    + " , durChapterIndex : " + durChapterIndex + " , chapterAll : " + chapterAll
                    + " , durPageIndex : " + durPageIndex + " , durPageAll : " + durPageAll);

            if (setDataListener != null) {
                setDataListener.setDataFinish(this, durChapterIndex, chapterAll, durPageIndex, durPageAll, this.durPageIndex);
            }
            if (contentLines == null) {
                this.content = "";
            } else {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < contentLines.size(); i++) {
                    s.append(contentLines.get(i));
                }
                this.content = s.toString();
            }
            this.title = title;
            this.durChapterIndex = durChapterIndex;
            this.chapterAll = chapterAll;
            this.durPageIndex = durPageIndex;
            this.pageAll = durPageAll;

            tvTitle.setText(this.title);
            tvContent.setText(this.content);
            tvPage.setText((this.durPageIndex + 1) + "/" + this.pageAll);

            finishLoading();
        }
    }

    public void loadData(String title, int durChapterIndex, int chapterAll, int durPageIndex) {
        this.title = title;
        this.durChapterIndex = durChapterIndex;
        this.chapterAll = chapterAll;
        this.durPageIndex = durPageIndex;
        tvTitle.setText(title);
        tvPage.setText("");

        loading();
    }

    public void setLoadDataListener(ContentSwitchView.LoadDataListener loadDataListener, SetDataListener setDataListener) {
        this.loadDataListener = loadDataListener;
        this.setDataListener = setDataListener;
    }

    public void setLoadDataListener(ContentSwitchView.LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
    }

    public void loadError() {
        llError.setVisibility(VISIBLE);
        tvLoading.setVisibility(GONE);
        llContent.setVisibility(INVISIBLE);
    }

    public int getPageAll() {
        return pageAll;
    }

    public void setPageAll(int pageAll) {
        this.pageAll = pageAll;
    }

    public int getDurPageIndex() {
        return durPageIndex;
    }

    public void setDurPageIndex(int durPageIndex) {
        this.durPageIndex = durPageIndex;
    }

    public int getDurChapterIndex() {
        return durChapterIndex;
    }

    public void setDurChapterIndex(int durChapterIndex) {
        this.durChapterIndex = durChapterIndex;
    }

    public int getChapterAll() {
        return chapterAll;
    }

    public void setChapterAll(int chapterAll) {
        this.chapterAll = chapterAll;
    }

    public SetDataListener getSetDataListener() {
        return setDataListener;
    }

    public void setSetDataListener(SetDataListener setDataListener) {
        this.setDataListener = setDataListener;
    }

    public long getqTag() {
        return qTag;
    }

    public void setqTag(long qTag) {
        this.qTag = qTag;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    /**
     * 测量阅读TextView的高度来测量获取总行数。
     * @param height
     * @return 阅读View的总行数
     */
    public int getLineCount(int height) {
        Log.d("MyLog", "getLineCount:height " + height);
        float ascent = tvContent.getPaint().ascent();
        float descent = tvContent.getPaint().descent();
        float textHeight = descent - ascent;
        Log.d("MyLog", "getLineCount:ascent " + ascent);
        Log.d("MyLog", "getLineCount:descent " + descent);
        Log.d("MyLog", "getLineCount:getLineSpacingExtra " + tvContent.getLineSpacingExtra());
        int i = (int) ((height * 1.0f - tvContent.getLineSpacingExtra()) / (textHeight + tvContent.getLineSpacingExtra()));
        Log.d("MyLog", "getLineCount: " + i);
        return i;
    }

    /**
     * 加载用户设置（或者默认预设）的字体大小、颜色、背景颜色 进阅读View
     * @param readBookControl
     */
    public void setReadBookControl(ReadBookControl readBookControl) {
        setTextKind(readBookControl);
        setBg(readBookControl);
    }

    /**
     * 加载用户设置（或者默认预设）的各种子View颜色 进阅读View
     * @param readBookControl
     */
    public void setBg(ReadBookControl readBookControl) {
        ivBg.setImageResource(readBookControl.getTextBackground());
        tvTitle.setTextColor(readBookControl.getTextColor());
        tvContent.setTextColor(readBookControl.getTextColor());
        tvPage.setTextColor(readBookControl.getTextColor());
        vBottom.setBackgroundColor(readBookControl.getTextColor());
        tvLoading.setTextColor(readBookControl.getTextColor());
        tvErrorInfo.setTextColor(readBookControl.getTextColor());
    }

    /**
     * 加载用户设置（或者默认预设）的字体大小、间隔 进阅读View
     * @param readBookControl
     */
    public void setTextKind(ReadBookControl readBookControl) {
        tvContent.setTextSize(readBookControl.getTextSize());
        tvContent.setLineSpacing(readBookControl.getTextExtra(), 1);
    }
}
