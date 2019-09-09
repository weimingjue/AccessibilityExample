package com.wang.hongbaotest;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by cloudplug on 18/4/27.
 * 手指触摸的点
 */
public class TouchView extends View {

    public final Path mPath = new Path();

    private final Paint mPaint = new Paint();

    public TouchView(Context context) {
        this(context, null, 0);
    }

    public TouchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TouchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData();
    }

    public void initData() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(10);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xff333333);
        setBackgroundColor(0x44000000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPath.lineTo(event.getX(), event.getY());
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }
}
 