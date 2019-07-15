package com.bytedance.clockapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Locale;

public class Clock extends View {

    private final static String TAG = Clock.class.getSimpleName();

    private static final int FULL_ANGLE = 360;

    private static final int CUSTOM_ALPHA = 140;
    private static final int FULL_ALPHA = 255;

    private static final int DEFAULT_PRIMARY_COLOR = Color.WHITE;
    private static final int DEFAULT_SECONDARY_COLOR = Color.LTGRAY;

    private static final float DEFAULT_DEGREE_STROKE_WIDTH = 0.010f;

    public final static int AM = 0;

    private static final int RIGHT_ANGLE = 90;

    private int mWidth, mCenterX, mCenterY, mRadius;

    /**
     * properties
     */
    private int centerInnerColor;
    private int centerOuterColor;

    private int secondsNeedleColor;
    private int hoursNeedleColor;
    private int minutesNeedleColor;

    private int degreesColor;

    private int hoursValuesColor;

    private int numbersColor;

    private boolean mShowAnalog = true;

    public Clock(Context context) {
        super(context);
        init(context, null);
    }

    public Clock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public Clock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    private void init(Context context, AttributeSet attrs) {

        this.centerInnerColor = Color.LTGRAY;
        this.centerOuterColor = DEFAULT_PRIMARY_COLOR;

        this.secondsNeedleColor = DEFAULT_SECONDARY_COLOR;
        this.hoursNeedleColor = DEFAULT_PRIMARY_COLOR;
        this.minutesNeedleColor = DEFAULT_PRIMARY_COLOR;

        this.degreesColor = DEFAULT_PRIMARY_COLOR;

        this.hoursValuesColor = DEFAULT_PRIMARY_COLOR;

        numbersColor = Color.WHITE;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getHeight() > getWidth() ? getWidth() : getHeight();

        int halfWidth = mWidth / 2;
        mCenterX = halfWidth;
        mCenterY = halfWidth;
        mRadius = halfWidth;

        if (mShowAnalog) {
            drawDegrees(canvas);
            drawHoursValues(canvas);
            drawNeedles(canvas);
            drawCenter(canvas);
        } else {
            drawNumbers(canvas);
        }
        postInvalidateDelayed(1000);

    }

    private void drawDegrees(Canvas canvas) {//画度数

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(mWidth * DEFAULT_DEGREE_STROKE_WIDTH);
        paint.setColor(degreesColor);

        int rPadded = mCenterX - (int) (mWidth * 0.01f);
        int rEnd = mCenterX - (int) (mWidth * 0.05f);

        for (int i = 0; i < FULL_ANGLE; i += 6 /* Step */) {

            if ((i % RIGHT_ANGLE) != 0 && (i % 15) != 0)
                paint.setAlpha(CUSTOM_ALPHA);
            else {
                paint.setAlpha(FULL_ALPHA);
            }

            int startX = (int) (mCenterX + rPadded * Math.cos(Math.toRadians(i)));
            int startY = (int) (mCenterX - rPadded * Math.sin(Math.toRadians(i)));

            int stopX = (int) (mCenterX + rEnd * Math.cos(Math.toRadians(i)));
            int stopY = (int) (mCenterX - rEnd * Math.sin(Math.toRadians(i)));

            canvas.drawLine(startX, startY, stopX, stopY, paint);

        }
    }

    /**
     * @param canvas
     */
    private void drawNumbers(Canvas canvas) {//第二页的绘制部分

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mWidth * 0.2f);
        textPaint.setColor(numbersColor);
        textPaint.setColor(numbersColor);
        textPaint.setAntiAlias(true);

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int amPm = calendar.get(Calendar.AM_PM);

        String time = String.format("%s:%s:%s%s",
                String.format(Locale.getDefault(), "%02d", hour),
                String.format(Locale.getDefault(), "%02d", minute),
                String.format(Locale.getDefault(), "%02d", second),
                amPm == AM ? "AM" : "PM");

        SpannableStringBuilder spannableString = new SpannableStringBuilder(time);
        spannableString.setSpan(new RelativeSizeSpan(0.3f), spannableString.toString().length() - 2, spannableString.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // se superscript percent

        StaticLayout layout = new StaticLayout(spannableString, textPaint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 1, true);
        canvas.translate(mCenterX - layout.getWidth() / 2f, mCenterY - layout.getHeight() / 2f);
        layout.draw(canvas);
    }

    /**
     * Draw Hour Text Values, such as 1 2 3 ...
     *
     * @param canvas
     */
    private void drawHoursValues(Canvas canvas) {
        // Default Color:
        // - hoursValuesColor
        String str[]={"01","02","03","04","05","06","07","08","09","10","11","12"};
        Paint vPaint=new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        vPaint.setStyle(Paint.Style.FILL);
        vPaint.setColor(hoursValuesColor);
        vPaint.setTextSize(mWidth*0.1f);////////
        vPaint.setTextAlign(Paint.Align.CENTER);

        int rPadded=mRadius-(int)(mRadius*0.25f);
        Paint.FontMetrics fontMetrics=vPaint.getFontMetrics();
        for(int i=0;i<FULL_ANGLE;i+=6){
            if((i%RIGHT_ANGLE)!=0&&(i%15)!=0){
                vPaint.setAlpha(CUSTOM_ALPHA);
            }
            else{
                vPaint.setAlpha(FULL_ALPHA);
                int posX=(int)(mCenterX+rPadded*Math.cos(Math.toRadians(i)));
                int posY=(int)(mCenterY+rPadded*Math.sin(Math.toRadians(i)));
                canvas.drawText(str[(i / 30 + 2) % 12], posX, posY - fontMetrics.top / 2 - fontMetrics.bottom / 2, vPaint);
            }
        }
    }

    /**
     * Draw hours, minutes needles
     * Draw progress that indicates hours needle disposition.
     *
     * @param canvas
     */
    private void drawNeedles(final Canvas canvas) {
        // Default Color:
        // - secondsNeedleColor
        // - hoursNeedleColor
        // - minutesNeedleColor
        Calendar calendar=Calendar.getInstance();

        int hour=calendar.get(Calendar.HOUR);
        int minute=calendar.get(Calendar.MINUTE);
        int second=calendar.get(Calendar.SECOND);

        float HourAngle=(hour+(float)minute/60)*360/12;
        float MinuteAngle=(minute+(float)second/60)*360/60;
        int SecondAngle=second*360/60;


        canvas.save();
        canvas.rotate(HourAngle,mCenterX,mCenterY);///
        RectF rectHour=new RectF(mCenterX-(mWidth*0.015f)/2,mCenterY-mRadius*3/5+100f,mCenterX+(mWidth*0.015f)/2,mCenterY+mRadius/6);/////
        Paint paintHour = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintHour.setColor(hoursNeedleColor);
        paintHour.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRoundRect(rectHour,mRadius/5,mRadius/5,paintHour);
        canvas.restore();

        canvas.save();
        canvas.rotate(MinuteAngle,mCenterX,mCenterY);///
        RectF rectMinute=new RectF(mCenterX-(mWidth*0.010f)/2,mCenterY-mRadius*3/5+30f,mCenterX+(mWidth*0.010f)/2,mCenterY+mRadius/6);/////
        Paint paintMinute = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintMinute.setColor(minutesNeedleColor);
        paintMinute.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRoundRect(rectMinute,mRadius/5,mRadius/5,paintMinute);
        canvas.restore();

        canvas.save();
        canvas.rotate(SecondAngle,mCenterX,mCenterY);///
        RectF rectSecond=new RectF(mCenterX-(mWidth*0.005f)/2,mCenterY-mRadius*3/5+5f,mCenterX+(mWidth*0.005f)/2,mCenterY+mRadius/6);////
        Paint paintSecond = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintSecond.setColor(secondsNeedleColor);
        paintSecond.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRoundRect(rectSecond,mRadius/5,mRadius/5,paintSecond);
        canvas.restore();
    }

    /**
     * Draw Center Dot
     *
     * @param canvas
     */
    private void drawCenter(Canvas canvas) {
        // Default Color:
        // - centerInnerColor
        // - centerOuterColor
        Paint cpaint=new Paint();
        cpaint.setStyle(Paint.Style.FILL);
        cpaint.setColor(centerInnerColor);
        canvas.drawCircle(mCenterX,mCenterY,mWidth*0.02f,cpaint);

        cpaint.setStyle(Paint.Style.STROKE);
        cpaint.setColor(centerOuterColor);
        cpaint.setStrokeWidth(mWidth*0.01f);
        canvas.drawCircle(mCenterX,mCenterY,mWidth*0.02f,cpaint);

    }

    public void setShowAnalog(boolean showAnalog) {
        mShowAnalog = showAnalog;
        invalidate();
    }

    public boolean isShowAnalog() {
        return mShowAnalog;
    }

}