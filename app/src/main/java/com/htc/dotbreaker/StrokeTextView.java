package com.htc.dotbreaker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class StrokeTextView extends TextView {

    private TextPaint m_StrokePaint;

    // constructors
    public StrokeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initalStrokePaint();
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initalStrokePaint();
    }

    public StrokeTextView(Context context) {
        super(context);
        initalStrokePaint();
    }

    public void initalStrokePaint() {
        initalOriginalTextView();
        // set stroke paint
        Resources mRes = getResources();
        m_StrokePaint.setStyle(Paint.Style.STROKE);
        m_StrokePaint.setColor(mRes.getColor(R.color.text_stroke_default));
        m_StrokePaint.setStrokeWidth(mRes.getDimensionPixelSize(R.dimen.stroke_width_default));
    }

    public void initalOriginalTextView() {
        if (m_StrokePaint == null) {
            m_StrokePaint = new TextPaint();
        }
        m_StrokePaint.setTextSize(getTextSize());
        m_StrokePaint.setTypeface(getTypeface());
        m_StrokePaint.setFlags(getPaintFlags());
    }

    public void setStrokeColor(int color) {
        initalStrokePaint();
        float width = m_StrokePaint.getStrokeWidth();
        m_StrokePaint.setColor(color);
        m_StrokePaint.setStrokeWidth(width);
        StrokeTextView.this.invalidate();
    }

    public void setStrokeWidth(float width) {
        int color = m_StrokePaint.getColor();
        initalStrokePaint();
        m_StrokePaint.setColor(color);
        m_StrokePaint.setStrokeWidth(width);
        StrokeTextView.this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CharSequence text = getText();
        if (text != null && text.length() > 0) {
            String content = text.toString();
            canvas.drawText(content, (getWidth() - m_StrokePaint.measureText(content)) / 2, getBaseline(), m_StrokePaint);
        }
        super.onDraw(canvas);
    }
}
