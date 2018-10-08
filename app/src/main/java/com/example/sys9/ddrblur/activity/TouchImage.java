package com.example.sys9.ddrblur.activity;

/**
 * Created by welcome on 7/31/2017.
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.ImageRequest;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.example.sys9.ddrblur.R;

public class TouchImage extends ImageView {
    static final int CLICK = 3;
    public int blurriness;
    public boolean bool = true;
    public Canvas canvasMask;
    Context context;
    float density;
    float factor;
    public Bitmap finalBitmap;
    public Canvas finalCanvas;
    public boolean hover = false;
    public PointF initialShapePos = new PointF(-5000.0f, -5000.0f);
    public PointF last = new PointF(-5000.0f, -5000.0f);
    public int lastCatIndex = 0;
    public int lastPosIndex = 6;
    float[] f65m;
    RotateGestureDetector mRotateDetector;
    public float mRotationDegree = 0.0f;
    ScaleGestureDetector mScaleDetector;
    public Bitmap mask;
    public Bitmap maskContainer;
    Matrix mat;
    Matrix matrix;
    int oldMeasuredHeight;
    int oldMeasuredWidth;
    protected float origHeight;
    protected float origWidth;
    public Paint paint;
    public Bitmap preview;
    Rect rect;
    float saveScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    public Shader shader1;
    public Shader shader2;
    public Bitmap spot;
    PointF start;
    public int svgId = R.raw.b_7;
    public Canvas temp;
    int viewHeight;
    int viewWidth;
    public int wMask;


    private class ScaleListener extends SimpleOnScaleGestureListener {
        private ScaleListener() {
        }

        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            TouchImage touchImage = TouchImage.this;
            touchImage.wMask = (int) (((float) touchImage.wMask) * mScaleFactor);
            if (TouchImage.this.wMask < 100) {
                TouchImage.this.wMask = 100;
            } else if (TouchImage.this.wMask > 900) {
                TouchImage.this.wMask = 900;
            }
            TouchImage.this.mask = Bitmap.createScaledBitmap(TouchImage.this.spot, TouchImage.this.wMask, TouchImage.this.wMask, true);
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    }

    private class RotateListener implements RotateGestureDetector.OnRotateGestureListener {
        private RotateListener() {
        }

        public boolean onRotate(RotateGestureDetector detector) {
            TouchImage touchImage = TouchImage.this;
            touchImage.mRotationDegree -= detector.getRotationDegreesDelta();
            return true;
        }

        @Override
        public boolean onRotateBegin(RotateGestureDetector rotateGestureDetector) {
            return false;
        }

        @Override
        public void onRotateEnd(RotateGestureDetector rotateGestureDetector) {

        }
    }

    public TouchImage(Context context) {
        super(context);
        sharedConstructing(context);
        setDrawingCacheEnabled(true);
    }

    public TouchImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
        setDrawingCacheEnabled(true);
    }

    public void init() {
        shader1 = new BitmapShader(ShapeBlurActivity.bitmapClear, TileMode.CLAMP, TileMode.CLAMP);
        shader2 = new BitmapShader(ShapeBlurActivity.bitmapBlur, TileMode.CLAMP, TileMode.CLAMP);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader1);
        if (lastPosIndex != -1) {
            mask = BitmapFactory.decodeResource(getResources(), ShapeBlurActivity.shapeID[lastCatIndex][lastPosIndex]).copy(Config.ALPHA_8, true);
            spot = BitmapFactory.decodeResource(getResources(), ShapeBlurActivity.shapeID[lastCatIndex][lastPosIndex]).copy(Config.ALPHA_8, true);
        } else {
            mask = BitmapFactory.decodeResource(getResources(), ShapeBlurActivity.shapeID[lastCatIndex][0]).copy(Config.ALPHA_8, true);
            spot = BitmapFactory.decodeResource(getResources(), ShapeBlurActivity.shapeID[lastCatIndex][0]).copy(Config.ALPHA_8, true);
        }
        wMask = mask.getWidth();
        canvasMask = new Canvas(maskContainer);
        mRotationDegree = 0.0f;
        bool = true;
        finalCanvas = new Canvas();
        temp = new Canvas();
        mat = new Matrix();
        if (ShapeBlurActivity.f63w > ShapeBlurActivity.f62h) {
            factor = ((float) ShapeBlurActivity.f63w) / ((float) ShapeBlurActivity.wScreen);
        } else {
            factor = ((float) ShapeBlurActivity.f62h) / ((float) ShapeBlurActivity.imageView.getHeight());
        }
    }

    private void sharedConstructing(Context context) {
        super.setClickable(true);
        this.context = context;
        density = context.getResources().getDisplayMetrics().density;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mRotateDetector = new RotateGestureDetector(context, new RotateListener());
        matrix = new Matrix();
        f65m = new float[9];
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int pCount = motionEvent.getPointerCount();
                mScaleDetector.onTouchEvent(motionEvent);
                mRotateDetector.onTouchEvent(motionEvent);
                PointF curr = new PointF(motionEvent.getX(), motionEvent.getY());
                if (pCount == 1) {
                    if (start != null) {
                        PointF pointF = last;
                        pointF.x += curr.x - start.x;
                        pointF = last;
                        pointF.y += curr.y - start.y;
                    }
                    start = new PointF(curr.x, curr.y);
                } else {
                    start = null;
                }
                switch (motionEvent.getAction()) {
                    case 1:
                        start = null;
                        break;
                }
                invalidate();
                return false;
            }
        });
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ShapeBlurActivity.bitmapClear != null) {
            try {
                if (bool) {
                    finalBitmap = ShapeBlurActivity.bitmapBlur.copy(Config.ARGB_8888, true);
                } else {
                    finalBitmap = ShapeBlurActivity.bitmapClear.copy(Config.ARGB_8888, true);
                }
                finalCanvas.setBitmap(finalBitmap);
                maskContainer = Bitmap.createBitmap(ShapeBlurActivity.f63w, ShapeBlurActivity.f62h, Config.ALPHA_8);
                temp.setBitmap(maskContainer);
            } catch (Exception e) {
            }
            mat = new Matrix();
            mat.setScale(factor, factor);
            mat.postRotate(mRotationDegree, (this.factor * ((float) this.wMask)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT, (this.factor * ((float) this.wMask)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT);
            mat.postTranslate(((this.last.x - this.f65m[2]) / this.f65m[0]) - ((this.factor * ((float) this.wMask)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT), ((this.last.y - this.f65m[5]) / this.f65m[4]) - ((this.factor * ((float) this.wMask)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT));
            temp.drawBitmap(mask, mat, null);
            finalCanvas.drawBitmap(maskContainer, 0.0f, 0.0f, paint);
          if (ShapeBlurActivity.border.isChecked()) {
                preview = svgToBitmap(this.context.getResources(), this.svgId, (int) (this.factor * ((float) this.wMask)));
                mat = new Matrix();
                mat.postTranslate(((this.last.x - this.f65m[2]) / this.f65m[0]) - ((this.factor * ((float) this.wMask)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT), ((this.last.y - this.f65m[5]) / this.f65m[4]) - ((this.factor * ((float) this.wMask)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT));
                finalCanvas.drawBitmap(preview, mat, null);
            }
            if (finalBitmap != null) {
                setImageBitmap(finalBitmap);
            }
        }
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    void fixTrans() {
        matrix.getValues(f65m);
        float transX = f65m[2];
        float transY = f65m[5];
        float fixTransX = getFixTrans(transX, (float) viewWidth, origWidth * saveScale);
        float fixTransY = getFixTrans(transY, (float) viewHeight, origHeight * saveScale);
        if (fixTransX != 0.0f || fixTransY != 0.0f) {
            matrix.postTranslate(fixTransX, fixTransY);
        }
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

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        if ((oldMeasuredHeight != viewWidth || oldMeasuredHeight != viewHeight) && viewWidth != 0 && viewHeight != 0) {
            oldMeasuredHeight = viewHeight;
            oldMeasuredWidth = viewWidth;
            if (saveScale == DefaultRetryPolicy.DEFAULT_BACKOFF_MULT) {
                Drawable drawable = getDrawable();
                if (drawable != null && drawable.getIntrinsicWidth() != 0 && drawable.getIntrinsicHeight() != 0) {
                    int bmWidth = drawable.getIntrinsicWidth();
                    int bmHeight = drawable.getIntrinsicHeight();
                    float scale = Math.min(((float) viewWidth) / ((float) bmWidth), ((float) viewHeight) / ((float) bmHeight));
                    matrix.setScale(scale, scale);
                    float redundantYSpace = (((float) viewHeight) - (((float) bmHeight) * scale)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT;
                    float redundantXSpace = (((float) viewWidth) - (((float) bmWidth) * scale)) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT;
                    matrix.postTranslate(redundantXSpace, redundantYSpace);
                    origWidth = ((float) viewWidth) - (ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT * redundantXSpace);
                    origHeight = ((float) viewHeight) - (ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT * redundantYSpace);
                    initialShapePos = new PointF((float) (viewWidth / 2), (float) (viewHeight / 2));
                    rect = new Rect((int) redundantXSpace, (int) redundantYSpace, ((int) origWidth) + ((int) redundantXSpace), ((int) this.origHeight) + ((int) redundantYSpace));
                    setImageMatrix(this.matrix);
                } else {
                    return;
                }
            }
            fixTrans();
            matrix.getValues(f65m);
        }
    }

    public void resetLast() {
        last.set(initialShapePos);
    }

    public Bitmap svgToBitmap(Resources res, int resource, int size) {
        try {
            size = (int) (((float) size) * density);
            SVG svg = SVG.getFromResource(res, resource);
            Bitmap bmp = Bitmap.createBitmap(size, size, Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            canvas.save();
            canvas.rotate(mRotationDegree, (((float) size) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT) / density, (((float) size) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT) / density);
            canvas.scale(((float) size) / (density * 370.0f), ((float) size) / (density * 370.0f));
            svg.renderToCanvas(canvas);
            canvas.restore();
            return bmp;
        } catch (SVGParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
