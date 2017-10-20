package com.llf.bohe;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by llf on 2017/10/16.
 * 数值选择view
 */

public class MeterView extends View {
    private int width;
    private int textColor;
    private float textSize;
    private float textMarginTop;
    private int lineColor;
    private float lineWidth;
    private float lineSpace;//每格宽度
    private float lineMaxHeight, lineMinHeight;//两种(整点和非整点)线高
    private float mineValue;
    private float selectValue;
    private int perValue;
    private int totalLine;//总共有多少根线
    private float mOffset, mMaxOffset;//默认状态下尺子所在的位置
    private Paint linePaint, textPaint;
    private float mXDown;
    private float mXMove;
    private float mXLastMove, mMove;//上次移动的位置和需要移动的距离
    private OnValueChangeListener mListener;//选中数值回调

    public MeterView(Context context) {
        this(context, null);
    }

    public MeterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MeterView);
        textColor = array.getColor(R.styleable.MeterView_textColor, Color.parseColor("#373737"));
        textSize = array.getDimension(R.styleable.MeterView_textSize, 30);
        textMarginTop = array.getDimension(R.styleable.MeterView_textMarginTop, 10);
        lineColor = array.getColor(R.styleable.MeterView_lineColor, Color.parseColor("#DFDFDF"));
        lineWidth = array.getDimension(R.styleable.MeterView_lineWidth, 3);
        lineSpace = array.getDimension(R.styleable.MeterView_lineSpace, 24);
        lineMinHeight = array.getDimension(R.styleable.MeterView_lineMinHeight, 40);
        lineMaxHeight = array.getDimension(R.styleable.MeterView_lineMaxHeight, 60);//这两个根据高度来取比较合理
        array.recycle();
        
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);
        linePaint.setStrokeWidth(lineWidth);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (totalLine <= 0) {
            throw new RuntimeException("请调用setValue设置各个值");
        }
        float left, height;
        float srcPointX = width / 2;
        for (int i = 0; i < totalLine; i++) {
            left = srcPointX + mOffset + i * lineSpace;
            if (i % 10 == 0) {
                height = lineMaxHeight;
            } else {
                height = lineMinHeight;
            }
            canvas.drawLine(left, 0, left, height, linePaint);
            if (i % 10 == 0) {
                String value = String.valueOf((int) (mineValue + i * perValue / 10));
                canvas.drawText(value, left - textPaint.measureText(value) / 2,
                        height + textMarginTop - textPaint.getFontMetrics().top, textPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = event.getRawX();
                mXLastMove = mXDown;
                mMove = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                mMove = mXLastMove - mXMove;
                mXLastMove = mXMove;
                moveAndChangeValue();
                break;
            case MotionEvent.ACTION_UP:
                moveEnd();
                break;
        }
        return true;
    }

    /**
     * @param selectValue 初始选中值
     * @param minValue    最小值
     * @param maxValue    最大值
     * @param per         每格对应值
     */
    public void setValue(float selectValue, float minValue, float maxValue, float per) {
        if (selectValue < minValue || selectValue > maxValue) {
            throw new RuntimeException("选中的值不能小于最小值或者大于最大值");
        }

        this.mineValue = minValue;
        this.selectValue = selectValue;
        this.perValue = (int) (per * 10);
        totalLine = ((int) ((maxValue * 10 - mineValue * 10) / perValue)) + 1;
        mMaxOffset = (int) (-(totalLine - 1) * lineSpace);
        mOffset = (minValue - selectValue) / perValue * lineSpace * 10;
        invalidate();
    }

    private void moveAndChangeValue() {
        mOffset -= mMove;

        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset;
        } else if (mOffset >= 0) {
            mOffset = 0;
        }

        postInvalidate();
    }

    /**
     * 让指针正好指在刻度上
     */
    private void moveEnd() {
        mOffset -= mMove;
        if (mOffset <= mMaxOffset) {
            mOffset = mMaxOffset;
        } else if (mOffset >= 0) {
            mOffset = 0;
        }

        selectValue = mineValue + Math.round(Math.abs(mOffset) * 1.0f / lineSpace) * perValue / 10.0f;
        mOffset = (mineValue - selectValue) * 10.0f / perValue * lineSpace;

        if (null != mListener) {
            mListener.onValueChange(selectValue);
        }
        postInvalidate();
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        mListener = listener;
    }

    /**
     * 滑动后的回调
     */
    public interface OnValueChangeListener {
        void onValueChange(float value);
    }
}
