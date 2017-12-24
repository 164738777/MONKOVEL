package com.monke.monkeybook.widget.contentswitchview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.monke.monkeybook.ReadBookControl;
import com.monke.monkeybook.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 阅读主View的容器类
 */
public class ContentSwitchView extends FrameLayout implements BookContentView.SetDataListener {
    private final long animDuration = 300;
    public final static int NONE = -1;
    public final static int PREANDNEXT = 0;
    public final static int ONLYPRE = 1;
    public final static int ONLYNEXT = 2;

    private int state = NONE;    //0是有上一页   也有下一页 ;  2是只有下一页  ；1是只有上一页;-1是没有上一页 也没有下一页；

    private int scrollX;
    private Boolean isMoving = false;

    private BookContentView durPageView;
    private List<BookContentView> viewContents;
    private OnBookReadInitListener bookReadInitListener;
    private ReadBookControl readBookControl;

    private int durHeight = 0;
    private float startX = -1;

    /**
     * 用于反馈阅读TextView的初始化完成。
     */
    public interface OnBookReadInitListener{
        void success();
    }

    public ContentSwitchView(Context context) {
        this(context, null);
    }

    public ContentSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentSwitchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        readBookControl = ReadBookControl.getInstance();

        scrollX = DensityUtil.dp2px(getContext(), 30f);

        // 初始化阅读View，加载字体大小以及各种子View的颜色
        durPageView = new BookContentView(getContext());
        durPageView.setReadBookControl(readBookControl);

        viewContents = new ArrayList<>();
        viewContents.add(durPageView);

        addView(durPageView);
    }

    /**
     * 设置监听阅读TextView初始化完成时候
     */
    public void bookReadInit(OnBookReadInitListener bookReadInitListener){
        this.bookReadInitListener = bookReadInitListener;
        durPageView.getTvContent().getViewTreeObserver().addOnGlobalLayoutListener(layoutInitListener);
    }

    public void startLoading() {
        setDurHeightAndInitLoadDataListenerData();
        durPageView.getTvContent().getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    private void setDurHeightAndInitLoadDataListenerData() {
        int height = durPageView.getTvContent().getHeight();
        if (height > 0) {
            if (loadDataListener != null && durHeight != height) {
                durHeight = height;
                loadDataListener.initData(durPageView.getLineCount(height));
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (!isMoving) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(viewContents.size() > 1){
                        if (startX == -1)
                            startX = event.getX();
                        int durX = (int) (event.getX() - startX);
                        if (durX > 0 && (state == PREANDNEXT || state == ONLYPRE)) {
                            int tempX = durX - getWidth();
                            if (tempX < -getWidth())
                                tempX = -getWidth();
                            else if (tempX > 0)
                                tempX = 0;
                            viewContents.get(0).layout(tempX, viewContents.get(0).getTop(), tempX + getWidth(), viewContents.get(0).getBottom());
                        } else if (durX < 0 && (state == PREANDNEXT || state == ONLYNEXT)) {
                            int tempX = durX;
                            if (tempX > 0)
                                tempX = 0;
                            else if (tempX < -getWidth())
                                tempX = -getWidth();
                            int tempIndex = (state == PREANDNEXT ? 1 : 0);
                            viewContents.get(tempIndex).layout(tempX, viewContents.get(tempIndex).getTop(), tempX + getWidth(), viewContents.get(tempIndex).getBottom());
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if(startX == -1)
                        startX = event.getX();
                    if (event.getX() - startX > 0) {
                        if (state == PREANDNEXT || state == ONLYPRE) {
                            if (event.getX() - startX > scrollX) {
                                //向前翻页成功
                                initMoveSuccessAnim(viewContents.get(0), 0);
                            } else {
                                initMoveFailAnim(viewContents.get(0), -getWidth());
                            }
                        } else {
                            //没有上一页
                            noPre();
                        }
                    } else if (event.getX() - startX < 0) {
                        if (state == PREANDNEXT || state == ONLYNEXT) {
                            int tempIndex = (state == PREANDNEXT ? 1 : 0);
                            if (startX - event.getX() > scrollX) {
                                //向后翻页成功
                                initMoveSuccessAnim(viewContents.get(tempIndex), -getWidth());
                            } else {
                                initMoveFailAnim(viewContents.get(tempIndex), 0);
                            }
                        } else {
                            //没有下一页
                            noNext();
                        }
                    } else {
                        //点击事件
                        if (readBookControl.getCanClickTurn() && event.getX() <= getWidth() / 3) {
                            //点击向前翻页
                            if (state == PREANDNEXT || state == ONLYPRE) {
                                initMoveSuccessAnim(viewContents.get(0), 0);
                            } else {
                                noPre();
                            }
                        } else if (readBookControl.getCanClickTurn() && event.getX() >= getWidth() / 3 * 2) {
                            //点击向后翻页
                            if (state == PREANDNEXT || state == ONLYNEXT) {
                                int tempIndex = (state == PREANDNEXT ? 1 : 0);
                                initMoveSuccessAnim(viewContents.get(tempIndex), -getWidth());
                            } else {
                                noNext();
                            }
                        } else {
                            //点击中间部位
                            if (loadDataListener != null)
                                loadDataListener.showMenu();
                        }
                    }
                    startX = -1;
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (viewContents.size() > 0) {
            if (state == NONE && viewContents.size() >= 1) {
                viewContents.get(0).layout(0, top, getWidth(), bottom);
            } else if (state == PREANDNEXT && viewContents.size() >= 3) {
                viewContents.get(0).layout(-getWidth(), top, 0, bottom);
                viewContents.get(1).layout(0, top, getWidth(), bottom);
                viewContents.get(2).layout(0, top, getWidth(), bottom);
            } else if (state == ONLYPRE && viewContents.size() >= 2) {
                viewContents.get(0).layout(-getWidth(), top, 0, bottom);
                viewContents.get(1).layout(0, top, getWidth(), bottom);
            } else if (viewContents.size() >= 2) {
                viewContents.get(0).layout(0, top, getWidth(), bottom);
                viewContents.get(1).layout(0, top, getWidth(), bottom);
            }
        } else {
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    private void initMoveSuccessAnim(final View view, final int orderX) {
        if (null != view) {
            long temp = Math.abs(view.getLeft() - orderX) / (getWidth() / animDuration);
            ValueAnimator tempAnim = ValueAnimator.ofInt(view.getLeft(), orderX).setDuration(temp);
            tempAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (null != view) {
                        int value = (int) animation.getAnimatedValue();
                        view.layout(value, view.getTop(), value + getWidth(), view.getBottom());
                    }
                }
            });
            tempAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isMoving = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isMoving = false;
                    if (orderX == 0) {
                        //翻向前一页
                        durPageView = viewContents.get(0);
                        if (state == PREANDNEXT) {
                            ContentSwitchView.this.removeView(viewContents.get(viewContents.size() - 1));
                            viewContents.remove(viewContents.size() - 1);
                        }
                        state = ONLYNEXT;
                        if(durPageView.getDurChapterIndex()-1>=0 || durPageView.getDurPageIndex()-1>=0){
                            addPrePage(durPageView.getDurChapterIndex(), durPageView.getChapterAll(), durPageView.getDurPageIndex(), durPageView.getPageAll());
                            if (state == NONE)
                                state = ONLYPRE;
                            else state = PREANDNEXT;
                        }
                    } else {
                        //翻向后一夜
                        if (state == ONLYNEXT) {
                            durPageView = viewContents.get(1);
                        } else {
                            durPageView = viewContents.get(2);
                            ContentSwitchView.this.removeView(viewContents.get(0));
                            viewContents.remove(0);
                        }
                        state = ONLYPRE;
                        if(durPageView.getDurChapterIndex()+1 <=durPageView.getChapterAll()-1 || durPageView.getDurPageIndex()+1 <= durPageView.getPageAll()-1){
                            addNextPage(durPageView.getDurChapterIndex(), durPageView.getChapterAll(), durPageView.getDurPageIndex(), durPageView.getPageAll());
                            if (state == NONE)
                                state = ONLYNEXT;
                            else state = PREANDNEXT;
                        }
                    }
                    if (loadDataListener != null)
                        loadDataListener.updateProgress(durPageView.getDurChapterIndex(), durPageView.getDurPageIndex());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            tempAnim.start();
        }
    }

    private void initMoveFailAnim(final View view, int orderX) {
        if (null != view) {
            long temp = Math.abs(view.getLeft() - orderX) / (getWidth() / animDuration);
            ValueAnimator tempAnim = ValueAnimator.ofInt(view.getLeft(), orderX).setDuration(temp);
            tempAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (null != view) {
                        int value = (int) animation.getAnimatedValue();
                        view.layout(value, view.getTop(), value + getWidth(), view.getBottom());
                    }
                }
            });
            tempAnim.start();
        }
    }

    /**
     * 点击进入某本小说时候初始化数据
     * @param durChapterIndex 章节下表
     * @param chapterAll   总章节数
     * @param durPageIndex  当前页面所在的章节的页数下标
     */
    public void setInitData(int durChapterIndex, int chapterAll, int durPageIndex) {
        updateOtherPage(durChapterIndex, chapterAll, durPageIndex, -1);
        durPageView.setLoadDataListener(loadDataListener, this);
        durPageView.loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex) : "", durChapterIndex, chapterAll, durPageIndex);

        if (loadDataListener != null) {
            loadDataListener.updateProgress(durPageView.getDurChapterIndex(), durPageView.getDurPageIndex());
        }
    }

    private void updateOtherPage(int durChapterIndex, int chapterAll, int durPageIndex, int pageAll) {
        if (chapterAll > 1 || pageAll > 1) {
            if ((durChapterIndex == 0 && pageAll == -1) || (durChapterIndex == 0 && durPageIndex == 0 && pageAll != -1)) {
                //ONLYNEXT
                addNextPage(durChapterIndex, chapterAll, durPageIndex, pageAll);
                if (state == ONLYPRE || state == PREANDNEXT) {
                    this.removeView(viewContents.get(0));
                    viewContents.remove(0);
                }
                state = ONLYNEXT;
            } else if ((durChapterIndex == chapterAll - 1 && pageAll == -1) || (durChapterIndex == chapterAll - 1 && durPageIndex == pageAll - 1 && pageAll != -1)) {
                //ONLYPRE
                addPrePage(durChapterIndex, chapterAll, durPageIndex, pageAll);
                if (state == ONLYNEXT || state == PREANDNEXT) {
                    this.removeView(viewContents.get(2));
                    viewContents.remove(2);
                }
                state = ONLYPRE;
            } else {
                //PREANDNEXT
                addNextPage(durChapterIndex, chapterAll, durPageIndex, pageAll);
                addPrePage(durChapterIndex, chapterAll, durPageIndex, pageAll);
                state = PREANDNEXT;
            }
        } else {
            //NONE
            if (state == ONLYPRE) {
                this.removeView(viewContents.get(0));
                viewContents.remove(0);
            } else if (state == ONLYNEXT) {
                this.removeView(viewContents.get(1));
                viewContents.remove(1);
            } else if (state == PREANDNEXT) {
                this.removeView(viewContents.get(0));
                this.removeView(viewContents.get(2));
                viewContents.remove(2);
                viewContents.remove(0);
            }
            state = NONE;
        }
    }

    private void addNextPage(int durChapterIndex, int chapterAll, int durPageIndex, int pageAll) {
        if (state == ONLYNEXT || state == PREANDNEXT) {
            int temp = (state == ONLYNEXT ? 1 : 2);
            if (pageAll > 0 && durPageIndex >= 0 && durPageIndex < pageAll - 1)
                viewContents.get(temp).loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex) : "", durChapterIndex, chapterAll, durPageIndex + 1);
            else
                viewContents.get(temp).loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex + 1) : "", durChapterIndex + 1, chapterAll, BookContentView.DUR_PAGE_INDEX_BEGIN);
        } else if (state == ONLYPRE || state == NONE) {
            BookContentView next = new BookContentView(getContext());
            next.setReadBookControl(readBookControl);
            next.setLoadDataListener(loadDataListener, this);
            if (pageAll > 0 && durPageIndex >= 0 && durPageIndex < pageAll - 1)
                next.loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex) : "", durChapterIndex, chapterAll, durPageIndex + 1);
            else
                next.loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex + 1) : "", durChapterIndex + 1, chapterAll, BookContentView.DUR_PAGE_INDEX_BEGIN);
            viewContents.add(next);
            this.addView(next, 0);
        }
    }

    private void addPrePage(int durChapterIndex, int chapterAll, int durPageIndex, int pageAll) {
        if (state == ONLYNEXT || state == NONE) {
            BookContentView pre = new BookContentView(getContext());
            pre.setReadBookControl(readBookControl);
            pre.setLoadDataListener(loadDataListener, this);
            if (pageAll > 0 && durPageIndex >= 0 && durPageIndex > 0)
                pre.loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex) : "", durChapterIndex, chapterAll, durPageIndex - 1);
            else
                pre.loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex - 1) : "", durChapterIndex - 1, chapterAll, BookContentView.DUR_PAGE_INDEX_END);
            viewContents.add(0, pre);
            this.addView(pre);
        } else if (state == ONLYPRE || state == PREANDNEXT) {
            if (pageAll > 0 && durPageIndex >= 0 && durPageIndex > 0)
                viewContents.get(0).loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex) : "", durChapterIndex, chapterAll, durPageIndex - 1);
            else
                viewContents.get(0).loadData(null != loadDataListener ? loadDataListener.getChapterTitle(durChapterIndex - 1) : "", durChapterIndex - 1, chapterAll, BookContentView.DUR_PAGE_INDEX_END);
        }
    }


    @Override
    public void setDataFinish(BookContentView bookContentView, int durChapterIndex, int chapterAll, int durPageIndex, int pageAll, int fromPageIndex) {
        if (null != getDurContentView() && bookContentView == getDurContentView() && chapterAll > 0 && pageAll > 0) {
            updateOtherPage(durChapterIndex, chapterAll, durPageIndex, pageAll);
        }
    }

    public interface LoadDataListener {
        void loaddata(BookContentView bookContentView, long tag, int chapterIndex, int pageIndex);

        void updateProgress(int chapterIndex, int pageIndex);

        String getChapterTitle(int chapterIndex);

        void initData(int lineCount);

        void showMenu();
    }

    private LoadDataListener loadDataListener;

    public void setLoadDataListener(LoadDataListener loadDataListener) {
        this.loadDataListener = loadDataListener;
    }

    public BookContentView getDurContentView() {
        return durPageView;
    }

    private void noPre() {
        Toast.makeText(getContext(), "没有上一页", Toast.LENGTH_SHORT).show();
    }

    private void noNext() {
        Toast.makeText(getContext(), "没有下一页", Toast.LENGTH_SHORT).show();
    }

    // 用于反馈阅读TextView的初始化完成。
    private ViewTreeObserver.OnGlobalLayoutListener layoutInitListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (bookReadInitListener != null) {
                bookReadInitListener.success();
            }
            durPageView.getTvContent().getViewTreeObserver().removeOnGlobalLayoutListener(layoutInitListener);
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            setDurHeightAndInitLoadDataListenerData();
        }
    };

    public Paint getTextPaint() {
        return durPageView.getTvContent().getPaint();
    }

    public int getContentWidth(){
        return durPageView.getTvContent().getWidth();
    }

    public void changeBg(){
        for(BookContentView item : viewContents){
            item.setBg(readBookControl);
        }
    }

    public void changeTextSize(){
        for(BookContentView item : viewContents){
            item.setTextKind(readBookControl);
        }
        loadDataListener.initData(durPageView.getLineCount(durHeight));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(readBookControl.getCanKeyTurn() && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            if (state == PREANDNEXT || state == ONLYNEXT) {
                int tempIndex = (state == PREANDNEXT ? 1 : 0);
                initMoveSuccessAnim(viewContents.get(tempIndex), -getWidth());
            } else {
                noNext();
            }
            return true;
        }else if(readBookControl.getCanKeyTurn() && keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            if (state == PREANDNEXT || state == ONLYPRE) {
                initMoveSuccessAnim(viewContents.get(0), 0);
            } else {
                noPre();
            }
            return true;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event){
        if(readBookControl.getCanKeyTurn() && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            return true;
        }else if(readBookControl.getCanKeyTurn() && keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            return true;
        }
        return false;
    }

    public void loadError(){
        if(durPageView != null){
            durPageView.loadError();
        }
    }
}