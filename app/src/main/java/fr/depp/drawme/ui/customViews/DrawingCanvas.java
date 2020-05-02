package fr.depp.drawme.ui.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class DrawingCanvas extends View {

    private Paint pencil;

    private ArrayList<ColoredPath> coloredPaths;
    private ColoredPath currentPath;
    private int currentColor;


    public DrawingCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setColor(int color) {
        currentColor = color;
        currentPath = new ColoredPath(currentColor);
    }

    private void init() {
        pencil = new Paint(Paint.ANTI_ALIAS_FLAG);
        pencil.setStyle(Paint.Style.STROKE);
        pencil.setStrokeWidth(13);

        coloredPaths = new ArrayList<>();
        currentPath = new ColoredPath();
    }

    @SuppressLint("ClickableViewAccessibility")
    // Sorry, but I can't because I need the position where the user clicked
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                coloredPaths.add(currentPath);
                currentPath = new ColoredPath(currentColor);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (ColoredPath coloredPath: coloredPaths) {
            pencil.setColor(coloredPath.color);
            canvas.drawPath(coloredPath.path, pencil);
        }

        pencil.setColor(currentPath.color);
        canvas.drawPath(currentPath.path, pencil);
    }

    private static class ColoredPath {

        private Path path;
        private int color;

        ColoredPath() {
            this.path = new Path();
            this.color = Color.BLACK;
        }

        ColoredPath(int color) {
            this.path = new Path();
            this.color = color;
        }

        void moveTo(float x, float y) {
            path.moveTo(x, y);
        }

        void lineTo(float x, float y) {
            path.lineTo(x, y);
        }
    }
}