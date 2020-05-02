package fr.depp.drawme.ui.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ColorPicker extends View {

    private Paint paint;
    private float circleRadius, spaceBetweenCircle;
    private OnColorPickHandler callback;
    private final int[] colors = {
            Color.rgb(255,0, 0), // red
            Color.rgb(125, 60, 152), // purple
            Color.rgb(31, 97, 141), // dark blue
            Color.rgb(93, 173, 226), // blue
            Color.rgb(40, 180, 99), // green
            Color.rgb(255, 255, 0), // yellow
            Color.rgb(230, 126, 34), // orange
            Color.rgb(255, 255, 255), // white
            Color.rgb(133, 146, 158), // gray
            Color.rgb(0, 0, 0) // dark
    };

    public ColorPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setCallback(OnColorPickHandler callback) {
        this.callback = callback;
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int padding = getPaddingLeft();
        circleRadius = ((float)h - 2*padding) / 5;
        spaceBetweenCircle = ((float)w-2*padding-10*circleRadius)/4;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int padding = getPaddingLeft();
        float row = 0, line = 0;

        for (int color : colors) {
            paint.setColor(color);

            canvas.drawCircle(padding + circleRadius + row*(spaceBetweenCircle+2*circleRadius),
                                padding + circleRadius + line*circleRadius*2,
                    circleRadius, paint);

            if (row++ == 4) {
                row = 0;
                line+=1.5;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility") // Sorry, but I can't because I need the position where the user clicked
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getColorFromPosition(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    private void getColorFromPosition(float x, float y) {
        x-=getPaddingLeft();
        y-=getPaddingTop();

        float xReferencialZero = x%(spaceBetweenCircle + 2*circleRadius) - circleRadius;
        float yReferencialZero = y%(3*circleRadius) - circleRadius;

        if (distanceBetweenOriginAndPoint(xReferencialZero, yReferencialZero) < circleRadius) {
            int row = (int) (x/(spaceBetweenCircle + 2*circleRadius));
            int col = (int) (y/(3*circleRadius));

            callback.getColor(colors[row + col*5]);
        }

    }

    private double distanceBetweenOriginAndPoint(double x, double y) {
        return Math.sqrt(Math.pow(-x, 2) + Math.pow(-y, 2));
    }

    public interface OnColorPickHandler {
        void getColor(int color);
    }
}
