package cn.hzw.graffiti;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import static cn.hzw.graffiti.DrawUtil.*;

/**
 * Created by huangziwei on 2017/3/16.
 */

public class GraffitiText implements Undoable, GraffitiSelectableItem {

    private final static Paint sPaint = new Paint();
    private String mText;
    private float mSize;
    private GraffitiColor mColor;
    private float mTextRotate; // 文字的旋转角度
    private int mRotateDegree; // 图片的旋转角度
    float mPivotX, mPivotY;
    private float mX, mY;

    private Rect mRect = new Rect();

    public GraffitiText(String mText, float mSize, GraffitiColor mColor, int mTextRotate, int mRotateDegree, float mX, float mY, float px, float py) {
        this.mText = mText;
        this.mSize = mSize;
        this.mColor = mColor;
        this.mTextRotate = mTextRotate;
        this.mRotateDegree = mRotateDegree;
        this.mX = mX;
        this.mY = mY;
        this.mPivotX = px;
        this.mPivotY = py;

        resetBounds();
    }

    private void resetBounds() {
        sPaint.setTextSize(mSize);
        sPaint.setStyle(Paint.Style.FILL);
        sPaint.getTextBounds(mText, 0, mText.length(), mRect);
        mRect.left -= 10 * GRAFFITI_PIXEL_UNIT;
        mRect.top -= 10 * GRAFFITI_PIXEL_UNIT;
        mRect.right += 10 * GRAFFITI_PIXEL_UNIT;
        mRect.bottom += 10 * GRAFFITI_PIXEL_UNIT;
    }

    public float getSize() {
        return mSize;
    }

    public void setSize(float size) {
        mSize = size;
        resetBounds();
    }

    public void setXy(int currentRotate, float x, float y) {
        float[] xy = restoreRotatePointInGraffiti(currentRotate, mRotateDegree, x, y, mPivotX, mPivotY);
        mX = xy[0];
        mY = xy[1];
    }

    public float[] getXy(int currentDegree) {
        return rotatePointInGraffiti(currentDegree, mRotateDegree, mX, mY, mPivotX, mPivotY);
    }

    public void setText(String text) {
        mText = text;
        resetBounds();
    }

    public String getText() {
        return mText;
    }

    public GraffitiColor getColor() {
        return mColor;
    }

    public void setColor(GraffitiColor color) {
        mColor = color;
    }

    public Rect getBounds(int currentRotate) {
        return mRect;
    }

    public void setItemRotate(float textRotate) {
        mTextRotate = textRotate;
    }

    public float getItemRotate() {
        return mTextRotate;
    }

    public int getGraffitiRotate() {
        return mRotateDegree;
    }

    // 判断xy是否在文字范围内
    @Override
    public boolean isInIt(int currentRotate, float x, float y, GraffitiView.Pen pen) {
        if (pen != GraffitiView.Pen.TEXT) {
            return false;
        }
        float[] xy = getXy(currentRotate);
        // 把触摸点转换成在文字坐标系（即以文字起始点作为坐标原点）内的点
        x = x - xy[0];
        y = y - xy[1];
        // 把变换后相对于矩形的触摸点，还原回未变换前的点，然后判断是否矩形中
        float[] rectXy = rotatePoint((int) -(currentRotate - mRotateDegree + mTextRotate), x, y, 0, 0);
        return mRect.contains((int) rectXy[0], (int) rectXy[1]);
    }

    @Override
    public boolean isCanRotate(int currentRotate, float x, float y) {
        float[] xy = getXy(currentRotate);
        // 把触摸点转换成在文字坐标系（即以文字起始点作为坐标原点）内的点
        x = x - xy[0];
        y = y - xy[1];
        // 把变换后矩形中的触摸点，还原回未变换前矩形中的点，然后判断是否矩形中
        float[] rectXy = rotatePoint((int) -(currentRotate - mRotateDegree + mTextRotate), x, y, 0, 0);

        return rectXy[0] >= mRect.right && rectXy[0] <= mRect.right + ITEM_CAN_ROTATE_BOUND * GRAFFITI_PIXEL_UNIT
                && rectXy[1] >= mRect.top && rectXy[1] <= mRect.bottom;
    }

    @Override
    public void draw(Canvas canvas, GraffitiView graffitiView, Paint paint) {
        paint.setTextSize(getSize());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(getText(), 0, 0, paint);
    }

}


