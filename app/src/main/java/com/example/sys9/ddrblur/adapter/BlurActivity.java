package com.example.sys9.ddrblur.adapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;


import com.android.volley.DefaultRetryPolicy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import com.example.sys9.ddrblur.R;


public class BlurActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
    static Bitmap bitmapBlur;
    static Bitmap bitmapClear;
    static SeekBar blurrinessBar;
    static BrushView brushView;
    static int displayHight;
    static int displayWidth;
    static SeekBar offsetBar;
    //static ImageView prView;
    static SeekBar radiusBar;
    static String tempDrawPath = (Environment.getExternalStorageDirectory().getPath() + "/DDR BlurEffect");
    static File tempDrawPathFile;
    static TouchImageView tiv;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    TextView blurText;
    Uri myUri;
    LinearLayout blurView;
    int btnbgColor = 1644825;
    int btnbgColorCurrent = 12303292;
    File cameraImage = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
    Uri cameraImageUri = Uri.fromFile(cameraImage);
    String currentPath;
    boolean erase = true;

    String imageSavePath = (Environment.getExternalStorageDirectory().getPath() + "/DDR Blur");

    ImageButton new_btn;
    ImageButton fit_btn;
    ImageButton save_btn;
    ImageButton share_btn;
    ImageButton gray_btn;
    ImageView offsetDemo_btn;
    ImageButton zoom_btn;
    ImageButton undo_btn;
    ImageButton color_btn;
    Button offsetOk_btn;
    LinearLayout offsetLayout;
    ProgressBar progressBar;
    ProgressDialog progressBlurring;
    ImageButton resetBtn;

    int startBlurSeekbarPosition;

    private String userChoosenTask;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHight = size.y;
        setContentView(R.layout.activity_blur);
        blurView = (LinearLayout) findViewById(R.id.blur_view);
        blurText = (TextView) findViewById(R.id.blur_text);
        tiv = (TouchImageView) findViewById(R.id.drawingImageView);
        tiv.setVisibility(View.VISIBLE);
        offsetDemo_btn = (ImageView) findViewById(R.id.offsetDemo);
        offsetLayout = (LinearLayout) findViewById(R.id.offsetLayout);
        bitmapClear = BitmapFactory.decodeResource(getResources(), R.drawable.me);
        bitmapBlur = blur(this, bitmapClear, tiv.opacity);
        new_btn = (ImageButton) findViewById(R.id.newBtn);
        undo_btn = (ImageButton) findViewById(R.id.undoBtn);
        fit_btn = (ImageButton) findViewById(R.id.fitBtn);
        save_btn = (ImageButton) findViewById(R.id.saveBtn);
        share_btn = (ImageButton) findViewById(R.id.shareBtn);
        color_btn = (ImageButton) findViewById(R.id.colorBtn);
        gray_btn = (ImageButton) findViewById(R.id.grayBtn);
        zoom_btn = (ImageButton) findViewById(R.id.zoomBtn);
        offsetOk_btn = (Button) findViewById(R.id.offsetOk);
        color_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
        new_btn.setOnClickListener(this);
        undo_btn.setOnClickListener(this);
        fit_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        color_btn.setOnClickListener(this);
        gray_btn.setOnClickListener(this);
        zoom_btn.setOnClickListener(this);
        offsetOk_btn.setOnClickListener(this);
        offsetBar = (SeekBar) findViewById(R.id.offsetBar);
        radiusBar = (SeekBar) findViewById(R.id.widthSeekBar);
        blurrinessBar = (SeekBar) findViewById(R.id.blurrinessSeekBar);
        brushView = (BrushView) findViewById(R.id.magnifyingView);
        brushView.setShapeRadiusRatio(((float) radiusBar.getProgress()) / ((float) radiusBar.getMax()));
        radiusBar.setMax(300);
        radiusBar.setProgress((int) tiv.radius);
        blurrinessBar.setMax(24);
        blurrinessBar.setProgress(tiv.opacity);
        offsetBar.setMax(100);
        offsetBar.setProgress(0);
        radiusBar.setOnSeekBarChangeListener(this);
        blurrinessBar.setOnSeekBarChangeListener(this);
        offsetBar.setOnSeekBarChangeListener(this);
        File imgSaveFolder = new File(this.imageSavePath);
        if (!imgSaveFolder.exists()) {
            imgSaveFolder.mkdirs();
        }
       // clearTempBitmap();
        tiv.initDrawing();
        progressBlurring = new ProgressDialog(this);

    }

    private SimpleTarget gTarget = new SimpleTarget<Bitmap>(512, 512) {
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {

            BlurActivity.bitmapClear = bitmap.copy(Config.ARGB_8888, true);
            BlurActivity.bitmapBlur = BlurActivity.blur(BlurActivity.this.getApplicationContext(), BlurActivity.bitmapClear, BlurActivity.tiv.opacity);
            BlurActivity.this.clearTempBitmap();
            BlurActivity.tiv.initDrawing();
            BlurActivity.tiv.saveScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            BlurActivity.tiv.fitScreen();
            BlurActivity.tiv.updatePreviewPaint();
            BlurActivity.tiv.updatePaintBrush();
        }
    };

    private class BlurUpdater extends AsyncTask<String, Integer, Bitmap> {
        private BlurUpdater() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            BlurActivity.this.progressBlurring.setMessage("Blurring...");
            BlurActivity.this.progressBlurring.setIndeterminate(true);
            BlurActivity.this.progressBlurring.setCancelable(false);
            BlurActivity.this.progressBlurring.show();
        }

        protected Bitmap doInBackground(String... params) {
            BlurActivity.bitmapBlur = BlurActivity.blur(BlurActivity.this.getApplicationContext(), BlurActivity.bitmapClear, BlurActivity.tiv.opacity);
            return BlurActivity.bitmapBlur;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (!BlurActivity.this.erase) {
                BlurActivity.tiv.splashBitmap = BlurActivity.bitmapBlur;
                BlurActivity.tiv.updateRefMetrix();
                BlurActivity.tiv.changeShaderBitmap();
            }
            BlurActivity.this.clearTempBitmap();
            BlurActivity.tiv.initDrawing();
            BlurActivity.tiv.saveScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
            BlurActivity.tiv.fitScreen();
            BlurActivity.tiv.updatePreviewPaint();
            BlurActivity.tiv.updatePaintBrush();
            if (BlurActivity.this.progressBlurring.isShowing()) {
                BlurActivity.this.progressBlurring.dismiss();
            }
        }
    }

    void clearTempBitmap() {
        tempDrawPathFile = new File(tempDrawPath);
        if (!tempDrawPathFile.exists()) {
            tempDrawPathFile.mkdirs();
        }
        if (tempDrawPathFile.isDirectory()) {
            String[] children = tempDrawPathFile.list();
            for (String file : children) {
                new File(tempDrawPathFile, file).delete();
            }
        }
    }

    public static Bitmap blur(Context context, Bitmap image, int radius) {
        Bitmap inputBitmap = image.copy(Config.ARGB_8888, true);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        if (VERSION.SDK_INT < 17) {
            return blurify(inputBitmap, radius);
        }
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius((float) radius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public static Bitmap blurify(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        if (radius < 1) {
            return null;
        }
        int i;
        int y;
        int x;
        int[] sir;
        int p;
        int stackpointer;
        int rbs;
        int bsum;
        int gsum;
        int rsum;
        int boutsum;
        int goutsum;
        int routsum;
        int binsum;
        int ginsum;
        int rinsum;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[(w * h)];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);
        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = (radius + radius) + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int[] vmin = new int[Math.max(w, h)];
        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int[] dv = new int[(divsum * 256)];
        for (i = 0; i < divsum * 256; i++) {
            dv[i] = i / divsum;
        }
        int yi = 0;
        int yw = 0;
        int[][] stack = (int[][]) Array.newInstance(Integer.TYPE, new int[]{div, 3});
        int r1 = radius + 1;
        for (y = 0; y < h; y++) {
            bsum = 0;
            gsum = 0;
            rsum = 0;
            boutsum = 0;
            goutsum = 0;
            routsum = 0;
            binsum = 0;
            ginsum = 0;
            rinsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[Math.min(wm, Math.max(i, 0)) + yi];
                sir = stack[i + radius];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & 255;
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min((x + radius) + 1, wm);
                }
                p = pix[vmin[x] + yw];
                sir[0] = (16711680 & p) >> 16;
                sir[1] = (MotionEventCompat.ACTION_POINTER_INDEX_MASK & p) >> 8;
                sir[2] = p & 255;
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            bsum = 0;
            gsum = 0;
            rsum = 0;
            boutsum = 0;
            goutsum = 0;
            routsum = 0;
            binsum = 0;
            ginsum = 0;
            rinsum = 0;
            int yp = (-radius) * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (((ViewCompat.MEASURED_STATE_MASK & pix[yi]) | (dv[rsum] << 16)) | (dv[gsum] << 8)) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                sir = stack[((stackpointer - radius) + div) % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public void onClick(View view) {
        Log.wtf("Click : ", "Inside onclick");
        TouchImageView touchImageView;
        TouchImageView touchImageView2;
        switch (view.getId()) {
            case R.id.newBtn:
                selectImage();
                fit_btn.setBackgroundColor(btnbgColor);
                new_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                undo_btn.setBackgroundColor(btnbgColor);
                save_btn.setBackgroundColor(btnbgColor);
                return;
            case R.id.undoBtn:
                String path = tempDrawPath + "/canvasLog" + (tiv.currentImageIndex - 1) + ".jpg";
                Log.wtf("Current Image ", path);
                fit_btn.setBackgroundColor(btnbgColor);
                undo_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                save_btn.setBackgroundColor(btnbgColor);
                new_btn.setBackgroundColor(btnbgColor);
                if (new File(path).exists()) {
                    tiv.drawingBitmap = null;
                    Options options = new Options();
                    options.inPreferredConfig = Config.ARGB_8888;
                    options.inMutable = true;
                    tiv.drawingBitmap = BitmapFactory.decodeFile(path, options);
                    tiv.setImageBitmap(tiv.drawingBitmap);
                    tiv.canvas.setBitmap(tiv.drawingBitmap);
                    File file2 = new File(tempDrawPath + "canvasLog" + tiv.currentImageIndex + ".jpg");
                    if (file2.exists()) {
                        file2.delete();
                    }
                    touchImageView = tiv;
                    touchImageView.currentImageIndex--;
                    return;
                }
                return;
            case R.id.fitBtn:
                tiv.saveScale = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
                tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
                brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
                tiv.fitScreen();
                tiv.updatePreviewPaint();
                save_btn.setBackgroundColor(btnbgColor);
                fit_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                undo_btn.setBackgroundColor(btnbgColor);
                new_btn.setBackgroundColor(btnbgColor);
                return;

            case R.id.saveBtn:
                saveImage();
                fit_btn.setBackgroundColor(btnbgColor);
                save_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                undo_btn.setBackgroundColor(btnbgColor);
                new_btn.setBackgroundColor(btnbgColor);
                return;
            case R.id.offsetOk:
                offsetLayout.setVisibility(View.GONE);
                return;
            case R.id.colorBtn:
                erase = true;
                touchImageView = tiv;
                touchImageView2 = tiv;
                touchImageView.mode = 0;
                gray_btn.setBackgroundColor(btnbgColor);
                zoom_btn.setBackgroundColor(btnbgColor);
                color_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                tiv.splashBitmap = bitmapClear;
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = true;
                return;
            case R.id.grayBtn:
                erase = false;
                touchImageView = tiv;
                touchImageView2 = tiv;
                touchImageView.mode = 0;
                color_btn.setBackgroundColor(btnbgColor);
                zoom_btn.setBackgroundColor(btnbgColor);
                gray_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                tiv.splashBitmap = bitmapBlur;
                tiv.updateRefMetrix();
                tiv.changeShaderBitmap();
                tiv.coloring = false;
                return;
            case R.id.zoomBtn:
                touchImageView = tiv;
                touchImageView2 = tiv;
                touchImageView.mode = 1;
                gray_btn.setBackgroundColor(btnbgColor);
                color_btn.setBackgroundColor(btnbgColor);
                zoom_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                return;
            default:
                return;
        }
    }

    void saveImage() {
        currentPath = imageSavePath + "/" + System.currentTimeMillis() + ".jpg";
        File currentFile = new File(currentPath);
        try {
            FileOutputStream out = new FileOutputStream(currentFile);
            tiv.drawingBitmap.compress(CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentFile.exists()) {
            MyMediaConnectorClient client = new MyMediaConnectorClient(this.currentPath);
            MediaScannerConnection scanner = new MediaScannerConnection(this, client);
            client.setScanner(scanner);
            scanner.connect();
            Intent finishWork = new Intent(this, ShareImageActivity.class);
            finishWork.putExtra("image", this.currentPath);
            startActivity(finishWork);
        }
    }

    public void onBackPressed() {

        super.onBackPressed();
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.offsetBar:
                Bitmap bm = Bitmap.createBitmap(300, 300, Config.ARGB_8888).copy(Config.ARGB_8888, true);
                Canvas offsetCanvas = new Canvas(bm);
                Paint p = new Paint(1);
                p.setColor(SupportMenu.CATEGORY_MASK);
                offsetCanvas.drawCircle(150.0f, 150.0f, 30.0f, p);
                p.setColor(16711936);
                offsetCanvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, p);
                offsetDemo_btn.setImageBitmap(bm);
                return;
            case R.id.widthSeekBar:
                brushView.isBrushSize = true;
                brushView.brushSize.setPaintOpacity(255);
                brushView.setShapeRadiusRatio(((float) (radiusBar.getProgress() + 50)) / tiv.saveScale);
                Log.wtf("radious :", radiusBar.getProgress() + "");
                brushView.invalidate();
                tiv.radius = ((float) (radiusBar.getProgress() + 50)) / tiv.saveScale;
                tiv.updatePaintBrush();
                return;
            case R.id.blurrinessSeekBar:
                brushView.isBrushSize = false;
                brushView.setShapeRadiusRatio(tiv.radius);
                brushView.brushSize.setPaintOpacity(blurrinessBar.getProgress());
                brushView.invalidate();
                tiv.opacity = i + 1;
                this.blurText.setText(blurrinessBar.getProgress() + "");
                tiv.updatePaintBrush();
                return;
            default:
                return;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case UserPermission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE /*123*/:
                if (grantResults.length > 0 && grantResults[0] == 0) {
                    if (this.userChoosenTask.equals("Camera")) {
                        cameraIntent();
                        return;
                    } else if (this.userChoosenTask.equals("Gallery")) {
                        galleryIntent();
                        return;
                    } else {
                        return;
                    }
                }
                return;
            default:
                return;
        }
    }

    private void selectImage() {
        final CharSequence[] items = new CharSequence[]{"Gallery", "Camera", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UserPermission.checkPermission(BlurActivity.this);
                if (items[item].equals("Camera")) {
                    BlurActivity.this.userChoosenTask = "Camera";
                    if (result) {
                        BlurActivity.this.cameraIntent();
                    }
                } else if (items[item].equals("Gallery")) {
                    BlurActivity.this.userChoosenTask = "Gallery";
                    if (result) {
                        BlurActivity.this.galleryIntent();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction("android.intent.action.GET_CONTENT");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        cameraImage = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
        cameraImageUri = Uri.fromFile(this.cameraImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", this.cameraImageUri);
        startActivityForResult(intent, this.REQUEST_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            return;
        }
        if (requestCode == SELECT_FILE) {
            onSelectFromGalleryResult(data);
        } else if (requestCode == REQUEST_CAMERA) {
            onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Glide.with(BlurActivity.this).load(cameraImageUri).asBitmap().into(this.gTarget);
        //Glide.with(BlurActivity.this).load(myUri).asBitmap().into(this.gTarget);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Glide.with(BlurActivity.this).load(data.getData()).asBitmap().into(this.gTarget);
        //Glide.with(BlurActivity.this).load(myUri).asBitmap().into(this.gTarget);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.offsetBar:
                offsetDemo_btn.setVisibility(View.VISIBLE);
                Bitmap bm = Bitmap.createBitmap(300, 300, Config.ARGB_8888).copy(Config.ARGB_8888, true);
                Canvas offsetCanvas = new Canvas(bm);
                Paint p = new Paint(1);
                p.setColor(SupportMenu.CATEGORY_MASK);
                offsetCanvas.drawCircle(150.0f, 150.0f, 30.0f, p);
                p.setColor(SupportMenu.CATEGORY_MASK);
                offsetCanvas.drawCircle(150.0f, (float) (150 - offsetBar.getProgress()), 30.0f, p);
                offsetDemo_btn.setImageBitmap(bm);
                return;
            case R.id.widthSeekBar:
                brushView.setVisibility(View.VISIBLE);
                return;
            case R.id.blurrinessSeekBar:
                blurView.setVisibility(View.VISIBLE);
                startBlurSeekbarPosition = blurrinessBar.getProgress();
                blurText.setText(this.startBlurSeekbarPosition + "");
                return;
            default:
                return;
        }
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.blurView.setVisibility(View.GONE);
        if (seekBar.getId() == R.id.blurrinessSeekBar) {
            new BlurUpdater().execute(new String[0]);
            BlurActivity.this.color_btn.setBackgroundColor(BlurActivity.this.btnbgColorCurrent);
            BlurActivity.this.gray_btn.setBackgroundColor(BlurActivity.this.btnbgColor);
          /*  AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Changing Bluriness will lose your current drawing progress!");
            alertDialog.setButton(-1, "Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    new BlurUpdater().execute(new String[0]);
                    BlurActivity.this.colorBtn.setBackgroundColor(BlurActivity.this.btnbgColorCurrent);
                    BlurActivity.this.grayBtn.setBackgroundColor(BlurActivity.this.btnbgColor);
                }
            });*/
           /* alertDialog.setButton(-2, "Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    BlurActivity.blurrinessBar.setProgress(BlurActivity.this.startBlurSeekbarPosition);
                }
            });
            alertDialog.show();*/
        } else if (seekBar.getId() == R.id.offsetBar) {
            offsetDemo_btn.setVisibility(View.GONE);
        } else if (seekBar.getId() == R.id.widthSeekBar) {
            brushView.setVisibility(View.GONE);
        }
    }


}