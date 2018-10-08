package com.example.sys9.ddrblur.adapter;

/**
 * Created by welcome on 7/27/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.internal.view.SupportMenu;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.ImageRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

public class TouchImageView extends ImageView {
    static final int CLICK = 3;
    static final int DRAG = 1;
    static final int NONE = 0;
    static final int ZOOM = 2;
    static final int ZOOMDRAG = 3;
    static float resRatio;
    BitmapShader bitmapShader;
    Bitmap brushBitmap;
    BitmapShader brushBitmapShader;
    Paint brushPaint;
    Path brushPath;
    Canvas canvas;
    Canvas canvasPreview;
    Paint circlePaint;
    Path circlePath;
    Bitmap colorBitmap;
    boolean coloring = true;
    Context context;
    PointF curr = new PointF();
    int currentImageIndex = 0;
    boolean draw = false;
    Paint drawPaint;
    Path drawPath;
    Bitmap drawingBitmap;
    Rect dstRect;
    Bitmap grayBitmap;
    PointF last = new PointF();
    Paint logPaintColor;
    Paint logPaintGray;
    float[] f59m;
    ScaleGestureDetector mScaleDetector;
    Matrix matrix;
    float maxScale = 5.0f;
    float minScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    int mode = 0;
    int oldMeasuredHeight;
    int oldMeasuredWidth;
    float oldX = 0.0f;
    float oldY = 0.0f;
    boolean onMeasureCalled = false;
    int opacity = 25;
    protected float origHeight;
    protected float origWidth;
    int pCount1 = -1;
    int pCount2 = -1;
    Vector<DrawPath> pathLogVector = new Vector();
    public boolean prViewDefaultPosition;
    BitmapShader previewBitmapShader;
    Paint previewPaint;
    float radius = 150.0f;
    float saveScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    Bitmap splashBitmap;
    PointF start = new PointF();
    Paint tempPaint;
    Bitmap tempPreviewBitmap;
    int viewHeight;
    int viewWidth;
    float f60x;
    float f61y;

    private class SaveCanvasLog extends AsyncTask<String, Integer, String> {
        private SaveCanvasLog() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            TouchImageView touchImageView = TouchImageView.this;
            touchImageView.currentImageIndex++;
            File file = new File(BlurActivity.tempDrawPathFile, "canvasLog" + TouchImageView.this.currentImageIndex + ".jpg");
            if (file.exists()) {
                file.delete();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                TouchImageView.this.drawingBitmap.compress(CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (TouchImageView.this.currentImageIndex > 5) {
                File file2 = new File(BlurActivity.tempDrawPathFile, "canvasLog" + (TouchImageView.this.currentImageIndex - 5) + ".jpg");
                if (file2.exists()) {
                    file2.delete();
                }
            }
            return "this string is passed to onPostExecute";
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private class ScaleListener extends SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //BlurActivity.prView.setVisibility(GONE);
            if (TouchImageView.this.mode == 1 || TouchImageView.this.mode == 3) {
                TouchImageView.this.mode = 3;
            } else {
                TouchImageView.this.mode = 2;
            }
            TouchImageView.this.draw = false;
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            float origScale = TouchImageView.this.saveScale;
            TouchImageView touchImageView = TouchImageView.this;
            touchImageView.saveScale *= mScaleFactor;
            if (TouchImageView.this.saveScale > TouchImageView.this.maxScale) {
                TouchImageView.this.saveScale = TouchImageView.this.maxScale;
                mScaleFactor = TouchImageView.this.maxScale / origScale;
            } else if (TouchImageView.this.saveScale < TouchImageView.this.minScale) {
            }
            if (TouchImageView.this.origWidth * TouchImageView.this.saveScale <= ((float) TouchImageView.this.viewWidth) || TouchImageView.this.origHeight * TouchImageView.this.saveScale <= ((float) TouchImageView.this.viewHeight)) {
                TouchImageView.this.matrix.postScale(mScaleFactor, mScaleFactor, (float) (TouchImageView.this.viewWidth / 2), (float) (TouchImageView.this.viewHeight / 2));
            } else {
                TouchImageView.this.matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
            }
            TouchImageView.this.matrix.getValues(TouchImageView.this.f59m);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
            TouchImageView.this.radius = ((float) (BlurActivity.radiusBar.getProgress() + 50)) / TouchImageView.this.saveScale;
            BlurActivity.brushView.setShapeRadiusRatio(((float) (BlurActivity.radiusBar.getProgress() + 50)) / TouchImageView.this.saveScale);
            TouchImageView.this.updatePreviewPaint();
        }
    }

    public TouchImageView(Context context) {
        super(context);
        this.context = context;
        sharedConstructing(context);
        this.prViewDefaultPosition = true;
        setDrawingCacheEnabled(true);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        sharedConstructing(context);
        this.prViewDefaultPosition = true;
        setDrawingCacheEnabled(true);
    }

    void initDrawing() {
        this.splashBitmap = BlurActivity.bitmapClear.copy(Config.ARGB_8888, true);
        this.drawingBitmap = Bitmap.createBitmap(BlurActivity.bitmapBlur).copy(Config.ARGB_8888, true);
        setImageBitmap(this.drawingBitmap);
        this.canvas = new Canvas(this.drawingBitmap);
        this.circlePath = new Path();
        this.drawPath = new Path();
        this.brushPath = new Path();
        this.circlePaint = new Paint(1);
        this.circlePaint.setColor(SupportMenu.CATEGORY_MASK);
        this.circlePaint.setStyle(Style.STROKE);
        this.circlePaint.setStrokeWidth(5.0f);
        this.drawPaint = new Paint(1);
        this.drawPaint.setStyle(Style.STROKE);
        this.drawPaint.setStrokeWidth(this.radius);
        this.drawPaint.setStrokeCap(Cap.ROUND);
        this.drawPaint.setStrokeJoin(Join.ROUND);
        this.drawPaint.setMaskFilter(new BlurMaskFilter(30.0f, Blur.NORMAL));
        this.brushPaint = new Paint(1);
        this.brushPaint.setStyle(Style.STROKE);
        this.brushPaint.setStrokeCap(Cap.ROUND);
        this.brushPaint.setStrokeJoin(Join.ROUND);
        this.brushPaint.setMaskFilter(new BlurMaskFilter(30.0f, Blur.NORMAL));
        setLayerType(1, this.drawPaint);
        setLayerType(1, this.brushPaint);
        this.tempPaint = new Paint();
        this.tempPaint.setStyle(Style.FILL);
        this.tempPaint.setColor(-1);
        this.previewPaint = new Paint();
        this.previewPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        this.tempPreviewBitmap = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
        this.canvasPreview = new Canvas(this.tempPreviewBitmap);
        this.dstRect = new Rect(0, 0, 100, 100);
        this.logPaintGray = new Paint(this.drawPaint);
        this.logPaintGray.setShader(new BitmapShader(BlurActivity.bitmapBlur, TileMode.CLAMP, TileMode.CLAMP));
        this.bitmapShader = new BitmapShader(this.splashBitmap, TileMode.CLAMP, TileMode.CLAMP);
        this.drawPaint.setShader(this.bitmapShader);
        this.logPaintColor = new Paint(this.drawPaint);
        new SaveCanvasLog().execute(new String[0]);
    }

    void updatePaintBrush() {
        try {
            this.drawPaint.setStrokeWidth(this.radius);
            this.brushPaint.setStrokeWidth(this.radius * resRatio);
        } catch (Exception e) {
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePreviewPaint();
    }

    void changeShaderBitmap() {
        this.bitmapShader = new BitmapShader(this.splashBitmap, TileMode.CLAMP, TileMode.CLAMP);
        this.drawPaint.setShader(this.bitmapShader);
        updatePreviewPaint();
    }

    void updatePreviewPaint() {
        if (BlurActivity.bitmapClear.getWidth() > BlurActivity.bitmapClear.getHeight()) {
            resRatio = ((float) BlurActivity.displayWidth) / ((float) BlurActivity.bitmapClear.getWidth());
            resRatio *= this.saveScale;
        } else {
            resRatio = this.origHeight / ((float) BlurActivity.bitmapClear.getHeight());
            resRatio *= this.saveScale;
        }
        this.brushPaint.setStrokeWidth(this.radius * resRatio);
        this.drawPaint.setStrokeWidth(this.radius);
        Matrix prM = new Matrix();
        prM.postScale(this.saveScale, this.saveScale);
        prM.postTranslate(this.f59m[2] + (this.saveScale * 0.4f), this.f59m[5] + (this.saveScale * 0.4f));
        this.brushBitmapShader = new BitmapShader(Bitmap.createScaledBitmap(this.splashBitmap, (int) this.origWidth, (int) this.origHeight, true), TileMode.CLAMP, TileMode.CLAMP);
        this.brushBitmapShader.setLocalMatrix(prM);
        this.brushPaint.setShader(this.brushBitmapShader);
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        this.mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.matrix = new Matrix();
        this.f59m = new float[9];
        setImageMatrix(this.matrix);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                TouchImageView.this.mScaleDetector.onTouchEvent(motionEvent);
                TouchImageView.this.pCount2 = motionEvent.getPointerCount();
                TouchImageView.this.curr = new PointF(motionEvent.getX(), motionEvent.getY() - (((float) BlurActivity.offsetBar.getProgress()) * 3.0f));
                TouchImageView.this.f60x = (TouchImageView.this.curr.x - TouchImageView.this.f59m[2]) / TouchImageView.this.f59m[0];
                TouchImageView.this.f61y = (TouchImageView.this.curr.y - TouchImageView.this.f59m[5]) / TouchImageView.this.f59m[4];
                switch (motionEvent.getAction()) {
                    case 0:
                        TouchImageView.this.oldX = 0.0f;
                        TouchImageView.this.oldY = 0.0f;
                        TouchImageView.this.last.set(TouchImageView.this.curr);
                        TouchImageView.this.start.set(TouchImageView.this.last);
                        if (!(TouchImageView.this.mode == 1 || TouchImageView.this.mode == 3)) {
                            TouchImageView.this.draw = true;
                            // BlurActivity.prView.setVisibility(VISIBLE);
                        }
                        TouchImageView.this.circlePath.reset();
                        TouchImageView.this.circlePath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                        TouchImageView.this.circlePath.addCircle(TouchImageView.this.curr.x, TouchImageView.this.curr.y, (TouchImageView.this.radius * TouchImageView.resRatio) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT, Direction.CW);
                        TouchImageView.this.drawPath.moveTo(TouchImageView.this.f60x, TouchImageView.this.f61y);
                        TouchImageView.this.brushPath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                        TouchImageView.this.showBoxPreview();
                        break;
                    case 1:
                        if (TouchImageView.this.mode == 1) {
                            TouchImageView.this.matrix.getValues(TouchImageView.this.f59m);
                        }
                        int yDiff = (int) Math.abs(TouchImageView.this.curr.y - TouchImageView.this.start.y);
                        if (((int) Math.abs(TouchImageView.this.curr.x - TouchImageView.this.start.x)) < 3 && yDiff < 3) {
                            TouchImageView.this.performClick();
                        }
                        if (TouchImageView.this.draw) {
                            TouchImageView.this.canvas.drawPath(TouchImageView.this.drawPath, TouchImageView.this.drawPaint);
                            new SaveCanvasLog().execute(new String[0]);
                        }
                        //BlurActivity.prView.setVisibility(GONE);
                        TouchImageView.this.circlePath.reset();
                        TouchImageView.this.drawPath.reset();
                        TouchImageView.this.brushPath.reset();
                        TouchImageView.this.draw = false;
                        break;
                    case 2:
                        if (TouchImageView.this.mode != 1 && TouchImageView.this.mode != 3) {
                            if (TouchImageView.this.draw) {
                                TouchImageView.this.circlePath.reset();
                                TouchImageView.this.circlePath.moveTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                                TouchImageView.this.circlePath.addCircle(TouchImageView.this.curr.x, TouchImageView.this.curr.y, (TouchImageView.this.radius * TouchImageView.resRatio) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT, Direction.CW);
                                TouchImageView.this.drawPath.lineTo(TouchImageView.this.f60x, TouchImageView.this.f61y);
                                TouchImageView.this.brushPath.lineTo(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                                TouchImageView.this.showBoxPreview();
                                TouchImageView.this.prViewDefaultPosition = false;
                                break;
                            }
                        }
                        if (TouchImageView.this.pCount1 == 1 && TouchImageView.this.pCount2 == 1) {
                            TouchImageView.this.matrix.postTranslate(TouchImageView.this.curr.x - TouchImageView.this.last.x, TouchImageView.this.curr.y - TouchImageView.this.last.y);
                        }
                        TouchImageView.this.last.set(TouchImageView.this.curr.x, TouchImageView.this.curr.y);
                        break;

                    case 6:
                        if (TouchImageView.this.mode == 2) {
                            TouchImageView.this.mode = 0;
                            break;
                        }
                        break;
                }
                TouchImageView.this.pCount1 = TouchImageView.this.pCount2;
                TouchImageView.this.setImageMatrix(TouchImageView.this.matrix);
                TouchImageView.this.invalidate();
                return true;
            }
        });
    }

    void updateRefMetrix() {
        this.matrix.getValues(this.f59m);
    }

    void showBoxPreview() {
        buildDrawingCache();
        Bitmap cacheBit = Bitmap.createBitmap(getDrawingCache());
        this.canvasPreview.drawRect(this.dstRect, this.tempPaint);
        this.canvasPreview.drawBitmap(cacheBit, new Rect(((int) this.curr.x) - 100, ((int) this.curr.y) - 100, ((int) this.curr.x) + 100, ((int) this.curr.y) + 100), this.dstRect, this.previewPaint);
        destroyDrawingCache();
    }

    public void onDraw(Canvas c) {
        float[] imageMatrix = new float[9];
        this.matrix.getValues(imageMatrix);
        int transX = (int) imageMatrix[2];
        int transY = (int) imageMatrix[5];
        super.onDraw(c);
        float maxClipHeight = (this.origHeight * this.saveScale) + ((float) transY);
        if (transY < 0) {
            c.clipRect((float) transX, 0.0f, ((float) transX) + (this.origWidth * this.saveScale), maxClipHeight > ((float) this.viewHeight) ? (float) this.viewHeight : maxClipHeight, Op.REPLACE);
        } else {
            c.clipRect((float) transX, (float) transY, ((float) transX) + (this.origWidth * this.saveScale), maxClipHeight > ((float) this.viewHeight) ? (float) this.viewHeight : maxClipHeight, Op.REPLACE);
        }
        if (this.draw) {
            c.drawPath(this.brushPath, this.brushPaint);
            c.drawPath(this.circlePath, this.circlePaint);
        }
    }

    void fixTrans() {
        this.matrix.getValues(this.f59m);
        float transX = this.f59m[2];
        float transY = this.f59m[5];
        float fixTransX = getFixTrans(transX, (float) this.viewWidth, this.origWidth * this.saveScale);
        float fixTransY = getFixTrans(transY, (float) this.viewHeight, this.origHeight * this.saveScale);
        if (!(fixTransX == 0.0f && fixTransY == 0.0f)) {
            this.matrix.postTranslate(fixTransX, fixTransY);
        }
        this.matrix.getValues(this.f59m);
        updatePreviewPaint();
    }

    float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans;
        float maxTrans;
        if (contentSize <= viewSize) {
            minTrans = 0.0f;
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0.0f;
        }
        if (trans < minTrans) {
            return (-trans) + minTrans;
        }
        if (trans > maxTrans) {
            return (-trans) + maxTrans;
        }
        return 0.0f;
    }

    float getFixDragTrans(float delta, float viewSize, float contentSize) {
        return delta;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!this.onMeasureCalled) {
            this.viewWidth = MeasureSpec.getSize(widthMeasureSpec);
            this.viewHeight = MeasureSpec.getSize(heightMeasureSpec);
            if ((this.oldMeasuredHeight != this.viewWidth || this.oldMeasuredHeight != this.viewHeight) && this.viewWidth != 0 && this.viewHeight != 0) {
                this.oldMeasuredHeight = this.viewHeight;
                this.oldMeasuredWidth = this.viewWidth;
                if (this.saveScale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                    fitScreen();
                }
                this.onMeasureCalled = true;
            }
        }
    }

    void fitScreen() {
        Drawable drawable = getDrawable();
        if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
            int bmWidth = drawable.getIntrinsicWidth();
            int bmHeight = drawable.getIntrinsicHeight();
            float scale = Math.min(((float) this.viewWidth) / ((float) bmWidth), ((float) this.viewHeight) / ((float) bmHeight));
            this.matrix.setScale(scale, scale);
            float redundantYSpace = (((float) this.viewHeight) - (((float) bmHeight) * scale)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT;
            float redundantXSpace = (((float) this.viewWidth) - (((float) bmWidth) * scale)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT;
            this.matrix.postTranslate(redundantXSpace, redundantYSpace);
            this.origWidth = ((float) this.viewWidth) - (ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT * redundantXSpace);
            this.origHeight = ((float) this.viewHeight) - (ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT * redundantYSpace);
            setImageMatrix(this.matrix);
            this.matrix.getValues(this.f59m);
            fixTrans();
        }
    }
}
