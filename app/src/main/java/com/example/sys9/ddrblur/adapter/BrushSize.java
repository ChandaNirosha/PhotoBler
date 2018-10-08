package com.example.sys9.ddrblur.adapter;

/**
 * Created by welcome on 7/27/2017.
 */


import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Shader;
import android.support.v4.view.ViewCompat;

public class BrushSize {
    private Paint paintInner;
    private Paint paintOuter = new Paint();
    private Path path;
    Shader shad = new Shader();

    public BrushSize() {
        this.paintOuter.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.paintOuter.setStrokeWidth(8.0f);
        this.paintOuter.setStyle(Style.STROKE);
        this.paintInner = new Paint();
        this.paintInner.setColor(7829368);
        this.paintInner.setStrokeWidth(8.0f);
        this.paintInner.setStyle(Style.FILL);
        this.path = new Path();
    }

    public void setCircle(float x, float y, float radius, Direction dir) {
        this.path.reset();
        this.path.addCircle(x, y, radius, dir);
    }

    public Path getPath() {
        return this.path;
    }

    public Paint getPaint() {
        return this.paintOuter;
    }

    public Paint getInnerPaint() {
        return this.paintInner;
    }

    public void setPaintOpacity(int opacity) {
        this.paintInner.setAlpha(opacity);
    }
}
