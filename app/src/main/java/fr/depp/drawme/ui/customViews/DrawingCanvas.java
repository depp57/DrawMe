package fr.depp.drawme.ui.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import fr.depp.drawme.models.Game;


public class DrawingCanvas extends View {

    private Paint pencil;

    private boolean canDraw;
    private ArrayList<ColoredPath> coloredPaths;
    private ColoredPath currentPath;
    private int currentColor;
    private long lastUpdateToDb;

    public DrawingCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setColor(int color) {
        currentColor = color;
        currentPath = new ColoredPath(currentColor);
    }

    public void setCanDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }

    private void init() {
        pencil = new Paint(Paint.ANTI_ALIAS_FLAG);
        pencil.setStyle(Paint.Style.STROKE);
        pencil.setStrokeWidth(13);

        coloredPaths = new ArrayList<>();
        setColor(Color.BLACK);

        lastUpdateToDb = System.currentTimeMillis();
    }

    @SuppressLint("ClickableViewAccessibility")
    // Sorry, but I can't because I need the position where the user clicked
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canDraw) {
            Toasty.info(getContext(), "Attendez votre tour pour dessiner !", Toasty.LENGTH_SHORT).show();
            return false;
        }

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath.addPoint(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.addPoint(touchX, touchY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                coloredPaths.add(currentPath);
                // try to upload the current path to the DB
                tryUploadCurrentPath();
                currentPath = new ColoredPath(currentColor);
                break;
            default:
                return false;
        }

        return true;
    }

    private void tryUploadCurrentPath() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastUpdateToDb > 1000) {
            lastUpdateToDb = currentTime;
            Game.getInstance().updateDrawing(currentPath);
        }
    }

    public void updateDrawing(ColoredPath newPath) {
        // reset the canvas if there isn't a newPath = someone guessed the drawing
        if (newPath == null) {
            clearCanvas();
            return;
        }

        if (currentPath != null) {
            coloredPaths.add(currentPath);
        }
        currentPath = newPath;
        invalidate();
    }

    public void clearCanvas() {
        coloredPaths.clear();
        currentPath = new ColoredPath(currentColor);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (ColoredPath coloredPath: coloredPaths) {
           coloredPath.drawPath(canvas, pencil);
        }
        currentPath.drawPath(canvas, pencil);
    }

    public static class ColoredPath {

        private final ArrayList<PointF> path;
        private final int color;

        public ColoredPath(int color) {
            this.path = new ArrayList<>();
            this.color = color;
        }

        public void addPoint(float x, float y) {
            this.path.add(new PointF(x, y));
        }

        private void drawPath(Canvas canvas, Paint pencil) {
            pencil.setColor(color);
            for (int i = 0; i < path.size() - 1; i++) {
                PointF point = path.get(i);
                PointF nextPoint = path.get(i+1);
                canvas.drawLine(point.x, point.y, nextPoint.x, nextPoint.y, pencil);
            }
        }

        public ArrayList<PointF> getPath() {
            return path;
        }

        public int getColor() {
            return color;
        }
    }
}