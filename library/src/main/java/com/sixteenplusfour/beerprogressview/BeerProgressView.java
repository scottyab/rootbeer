package com.sixteenplusfour.beerprogressview;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.nineoldandroids.animation.ObjectAnimator;

/**
 * $desc
 *
 * @author zxb
 * @author Andy Barber
 * @date 15/10/28 下午5:46
 * @date 21/04/2016
 */
public class BeerProgressView extends View {

    private static final String TAG = "BeerProgressView";
    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_MAX = "state_max";
    private static final String STATE_PROGRESS = "state_progress";
    private static final String STATE_WAVE_COLOR = "state_wave_color";
    private static final String STATE_BUBBLE_COLOR = "state_bubble_color";
    private static final int BEER_DEFAULT_COLOR = Color.parseColor("#1abc9c");
    private static final int BUBBLE_DEFAULT_COLOR = Color.parseColor("#b67200");
    private static final int BUBBLE_DEFAULT_COUNT = 20;
    private static final int BUBBLE_FPS = 30;

    private Paint mPaint;
    private Paint mBorderPaint;
    private RectF mBorderRectF;
    private Path mPath;
    private ObjectAnimator mAngleAnim;

    private float mBorderRadius;// = dp2px(2);
    private float mHaftBorderRadius = mBorderRadius / 2;
    private float mAngularVelocity = 2.0f;
    private float mBeerProgressHeight = 50;
    private int mBorderWidth;// = dp2px(3);
    private int mBeerWidth;
    private int mBeerHeight;
    private int mAmplitude = dp2px(3);
    private int mAngle = 0;
    private int mMax = 100;
    private int mBeerProgress = 0;
    private int mBeerColor = BEER_DEFAULT_COLOR;

    //bubble vars
    private Runnable mDrawBubblesRunnable;
    private Bubble[] mBubbles;
    private Handler handler = new Handler();
    private long mStartMilli;
    private int mBubbleHeight;
    private int mBubbleTopMargin;
    private int mBubbleWidth;
    private int mBubbleColor = BUBBLE_DEFAULT_COLOR;

    public BeerProgressView(Context context) {
        this(context, null);
    }

    public BeerProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeerProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int attrId = getResources().getIdentifier("colorAccent", "attr", getContext().getPackageName());
        if (attrId > 0){
            TypedValue colorAccent = new TypedValue();
            getContext().getTheme().resolveAttribute(attrId, colorAccent, true);
            mBeerColor = colorAccent.data;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BeerProgressView);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.BeerProgressView_waveBorderWidth, mBorderWidth);
        mAmplitude = a.getDimensionPixelSize(R.styleable.BeerProgressView_waveAmplitude, mAmplitude);
        mBorderRadius = a.getDimensionPixelSize(R.styleable.BeerProgressView_waveBorderRadius, (int)mBorderRadius);
        mHaftBorderRadius = mBorderRadius / 2;

        mBeerColor = a.getColor(R.styleable.BeerProgressView_beerColor, mBeerColor);
        mMax = a.getInt(R.styleable.BeerProgressView_waveMax, 100);
        mBeerProgress = a.getInteger(R.styleable.BeerProgressView_beerProgress, 0);
        mBubbleColor = a.getColor(R.styleable.BeerProgressView_bubbleColor, mBubbleColor);

        a.recycle();

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw wave
        if (mBeerProgressHeight > 0) {
            updatePath();

            canvas.drawPath(mPath, mPaint);
            if (mBorderWidth!=0) {
                canvas.drawRoundRect(mBorderRectF, mBorderRadius, mBorderRadius, mBorderPaint);
            }
        }

        //draw bubbles
        if (mBeerProgressHeight > 0 & mBeerProgress > 10) {
            drawBubbles(canvas);
        } else {
            handler.removeCallbacks(mDrawBubblesRunnable);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mBeerWidth = (int) (getWidth() - mHaftBorderRadius);
        mBeerHeight = (int) (getHeight() - mHaftBorderRadius);

        mBorderRectF.set(mHaftBorderRadius, mHaftBorderRadius, mBeerWidth, mBeerHeight);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_MAX, mMax);
        bundle.putInt(STATE_PROGRESS, mBeerProgress);
        bundle.putInt(STATE_WAVE_COLOR, mBeerColor);
        bundle.putInt(STATE_BUBBLE_COLOR, mBubbleColor);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mMax = bundle.getInt(STATE_MAX, 100);
            mBeerProgress = bundle.getInt(STATE_PROGRESS, 0);
            mBeerColor = bundle.getInt(STATE_WAVE_COLOR, BEER_DEFAULT_COLOR);
            mBubbleColor = bundle.getInt(STATE_BUBBLE_COLOR, BUBBLE_DEFAULT_COLOR);
            super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
            return ;
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setupAngleAnim();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAngleAnim();
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        startOrCancelAngleAnim();
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        startOrCancelAngleAnim();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mBeerColor);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(mBeerColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setStyle(Paint.Style.STROKE);

        mBorderRectF = new RectF();
        mPath = new Path();

        setupAngleAnim();

        mDrawBubblesRunnable = new Runnable() {
            public void run() {
                invalidate();
            }
        };
    }

    private void drawBubbles(Canvas canvas) {
        mStartMilli = SystemClock.uptimeMillis();
        mBubbleHeight = mBeerHeight;
        mBubbleTopMargin = mBubbleHeight - ((int) mBeerProgressHeight) + 20;
        mBubbleWidth = canvas.getWidth();

        if (mBubbles == null || mBubbles.length != BUBBLE_DEFAULT_COUNT) {
            createBubbles(mBubbleWidth, mBubbleHeight, mBubbleTopMargin);
        }

        /* Draw each Bubble */
        for (Bubble bubble : mBubbles) {
            /* Move the Bubble */
            bubble.update(BUBBLE_FPS, 0);

            /* Draw circle */
            bubble.draw(canvas);

            if (bubble.popped(mBubbleWidth, mBubbleHeight, mBubbleTopMargin)) {
                bubble.recycle(false, mBubbleWidth, mBubbleHeight, mBubbleTopMargin);
            }
        }

        long duration = SystemClock.uptimeMillis() - mStartMilli;
        handler.postDelayed(mDrawBubblesRunnable, (1000 / BUBBLE_FPS) - duration);
    }

    private void createBubbles(int width, int height, int topMargin) {
        mBubbles = new Bubble[BUBBLE_DEFAULT_COUNT];
        for (int i = 0; i < BUBBLE_DEFAULT_COUNT; i++) {
            this.mBubbles[i] = new Bubble(width, height, topMargin, mBubbleColor);
        }
    }

    private void setupAngleAnim() {
        if (!isViewVisiable()){
            return;
        }
        if (mAngleAnim == null) {
            mAngleAnim = ObjectAnimator.ofInt(this, "angle", 0, 360);
            mAngleAnim.setDuration(800);
            mAngleAnim.setRepeatMode(ObjectAnimator.RESTART);
            mAngleAnim.setRepeatCount(ObjectAnimator.INFINITE);
            mAngleAnim.setInterpolator(new LinearInterpolator());
        }
        if (!mAngleAnim.isRunning()) {
            mAngleAnim.start();
        }
    }

    private void cancelAngleAnim(){
        if (mAngleAnim != null){
            mAngleAnim.cancel();
        }
    }

    private void updatePath() {
        //default progress
        setBeerProgress(mBeerProgress);
        this.mPath.reset();
        for (int i = 0; i < mBeerWidth; i++) {
            int x = i;
            int y = (int) clamp(
                    mAmplitude * Math.sin((i* mAngularVelocity + mAngle * Math.PI) / 180.0f) + (mBeerHeight - mBeerProgressHeight),
                    mHaftBorderRadius,
                    mBeerHeight
            );
            if (i == 0) {
                this.mPath.moveTo( x, y);
            }
            this.mPath.quadTo( x, y, x + 1, y);
        }
        this.mPath.lineTo(mBeerWidth, mBeerHeight);
        this.mPath.lineTo(0, mBeerHeight);
        this.mPath.close();
    }

    private void startOrCancelAngleAnim() {
        if (isViewVisiable()) {
            setupAngleAnim();
        }else {
            cancelAngleAnim();
        }
    }

    private boolean isViewVisiable(){
        return getVisibility() == VISIBLE && getAlpha()*255>0;
    }

    private static double clamp(double value, double max, double min) {
        return Math.max(Math.min(value, min), max);
    }

    private static int dp2px(int dp){
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp);
    }


    /* public methods */

    /**
     * get maximum progress value of view
     *
     * @return
     */
    public int getMax() {
        return mMax;
    }

    /**
     * set maximum value of progress view
     *
     * @param max
     */
    public void setMax(int max) {
        mMax = max;
    }

    /**
     * get the wave amplitude
     *
     * @return
     */
    public int getAmplitude() {
        return mAmplitude;
    }

    /**
     * set the wave amplitude
     *
     * @param amplitude
     */
    public void setAmplitude(int amplitude) {
        mAmplitude = amplitude;
    }

    /**
     * get current progress value of view
     *
     * @return
     */
    public int getBeerProgress() {
        return mBeerProgress;
    }

    /**
     * set the current beerProgress of the view
     *
     * @param beerProgress
     */
    public void setBeerProgress(int beerProgress) {
        mBeerProgress = beerProgress;
        if (mBeerProgress > mMax){
            mBeerProgress = mMax;
        }
        if (mBeerProgress < 0){
            mBeerProgress = 0;
        }
        float pecent = mBeerProgress * 1.0f / mMax;
        mBeerProgressHeight = pecent * mBeerHeight;
        invalidate();
    }

    /**
     * get the colour value of the view
     *
     * @return
     */
    public int getBeerColor() {
        return mBeerColor;
    }

    /**
     * set the colour value of the view
     *
     * @param beerColor
     */
    public void setBeerColor(int beerColor) {
        mBeerColor = beerColor;
        mPaint.setColor(mBeerColor);
        mBorderPaint.setColor(mBeerColor);
    }

    /**
     * get the colour value of the bubbles
     *
     * @return
     */
    public int getBubbleColor() {
        return mBubbleColor;
    }

    /**
     * set the colour value of the bubbles
     *
     * @param bubbleColor
     */
    public void setBubbleColor(int bubbleColor) {
        mBubbleColor = bubbleColor;
        mBubbles = null;
    }

    /**
     * set the angle of the wave
     *
     * @param angle
     */
    public void setAngle(int angle){
        this.mAngle = angle;
        invalidate();
    }

}

