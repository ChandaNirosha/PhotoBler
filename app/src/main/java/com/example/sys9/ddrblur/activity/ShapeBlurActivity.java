package com.example.sys9.ddrblur.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Shader.TileMode;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.ImageRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.sys9.ddrblur.adapter.CustomCategoryAdapter;
import com.example.sys9.ddrblur.adapter.CustomShapeAdapter;
import com.example.sys9.ddrblur.adapter.MyMediaConnectorClient;
import com.example.sys9.ddrblur.R;
import com.example.sys9.ddrblur.adapter.ShareImageActivity;
import com.example.sys9.ddrblur.adapter.TouchImageView;
import com.example.sys9.ddrblur.adapter.UserPermission;
import com.example.sys9.ddrblur.adapter.CustomCategoryAdapter;
import com.example.sys9.ddrblur.adapter.CustomShapeAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;

public class ShapeBlurActivity extends Activity implements OnClickListener {
    public static Bitmap bitmapBlur;
    public static Bitmap bitmapClear;
    static SeekBar blurrinessBar;
    static CheckBox border;
    int btnbgColor = 1644825;
    public static int[] categoryID = new int[2];
    public static int categoryIndex = 0;
    public static LinearLayoutManager categoryLayoutManager;
    public static RecyclerView categoryRecyclerView;
    public static int f62h;
    public static int hScreen;
    public static TouchImage imageView;
    static float ratio = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
    public static int[] selectedCategoryID = new int[2];
    public static CustomShapeAdapter shapeAdapter;
    public static int[][] shapeButtonID = new int[2][];
    public static int[][] shapeID = new int[2][];
    public static LinearLayoutManager shapeLayoutManager;
    public static RecyclerView shapeRecyclerView;
    public static int[][] shapeViewID = new int[2][];
    public static int f63w;
    public static int wScreen;
    private int REQUEST_CAMERA = 0;
    private int SELECT_FILE = 1;
    TextView blurText;
    LinearLayout blurView;
    File cameraImage = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
    Uri cameraImageUri = Uri.fromFile(this.cameraImage);
    CustomCategoryAdapter categoryAdapter;
    String currentPath;
    float f64f = DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;

    String imageSavePath = (Environment.getExternalStorageDirectory().getPath() + "/DDR Blur");
    LinearLayout loader;
    LinearLayout lv_adview;
    //private AdView mAdView;
    //private InterstitialAd mInterstitialAd;
    ImageButton new_btn;
    ImageButton save_btn;
    private String userChoosenTask;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCategoryID();
        setContentView(R.layout.activity_shap_blur);
        //MainActivity.showAdmobInterstitial();
        imageView = (TouchImage) findViewById(R.id.imageView);
        //this.lv_adview = (LinearLayout) findViewById(R.id.lv_adview);
        new_btn = (ImageButton) findViewById(R.id.new_btn);
        save_btn = (ImageButton) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);
        new_btn.setOnClickListener(this);
        loader = (LinearLayout) findViewById(R.id.loader);
        blurView = (LinearLayout) findViewById(R.id.blur_view);
        blurText = (TextView) findViewById(R.id.blur_text);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        wScreen = metrics.widthPixels;
        hScreen = metrics.heightPixels;
        categoryRecyclerView = (RecyclerView) findViewById(R.id.shapeCategory);
        shapeRecyclerView = (RecyclerView) findViewById(R.id.shapes);
        categoryRecyclerView.hasFixedSize();
        shapeRecyclerView.hasFixedSize();
        categoryAdapter = new CustomCategoryAdapter(this, categoryRecyclerView);
        shapeAdapter = new CustomShapeAdapter(this, shapeRecyclerView);
        categoryLayoutManager = new LinearLayoutManager(getApplicationContext(), 0, true);
        categoryLayoutManager.setStackFromEnd(true);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);
        categoryRecyclerView.setAdapter(categoryAdapter);
        shapeLayoutManager = new LinearLayoutManager(getApplicationContext(), 0, true);
        shapeLayoutManager.setStackFromEnd(true);
        shapeRecyclerView.setLayoutManager(shapeLayoutManager);
        shapeRecyclerView.setAdapter(shapeAdapter);
        border = (CheckBox) findViewById(R.id.border);
        border.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ShapeBlurActivity.imageView.invalidate();
            }
        });
        blurrinessBar = (SeekBar) findViewById(R.id.blurrinessSeekBar);
        blurrinessBar.setProgress(24);
        blurrinessBar.setMax(24);
        blurrinessBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ShapeBlurActivity.imageView.blurriness = progress + 1;
                ShapeBlurActivity.this.blurText.setText(progress + "");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                ShapeBlurActivity.this.blurView.setVisibility(View.VISIBLE);
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                ShapeBlurActivity.this.blurView.setVisibility(View.GONE);
                new BlurUpdater().execute(new String[0]);
            }
        });

        load();
    }

    private class BlurUpdater extends AsyncTask<String, Integer, String> {
        private BlurUpdater() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            ShapeBlurActivity.this.loader.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... params) {
            ShapeBlurActivity.bitmapBlur = ShapeBlurActivity.blur(ShapeBlurActivity.this.getApplicationContext(), ShapeBlurActivity.bitmapClear, ShapeBlurActivity.imageView.blurriness);
            return "this string is passed to onPostExecute";
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ShapeBlurActivity.imageView.shader2 = new BitmapShader(ShapeBlurActivity.bitmapBlur, TileMode.CLAMP, TileMode.CLAMP);
            if (ShapeBlurActivity.imageView.bool) {
                ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader1);
                ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapBlur);
            } else {
                ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader2);
                ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapClear);
            }
            ShapeBlurActivity.imageView.invalidate();
            ShapeBlurActivity.this.loader.setVisibility(View.GONE);
        }
    }

    void load() {
        Glide.with((Activity) this).load(Integer.valueOf(R.drawable.me)).asBitmap().into(this.gTarget);
    }

    private SimpleTarget gTarget = new SimpleTarget<Bitmap>(512, 512) {
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            //MainActivity.showAdmobInterstitial();
            ShapeBlurActivity.imageView.last = new PointF(-5000.0f, -5000.0f);
            ShapeBlurActivity.bitmapClear = bitmap.copy(Config.ARGB_8888, true);
            ShapeBlurActivity.f63w = bitmap.getWidth();
            ShapeBlurActivity.f62h = bitmap.getHeight();
            int min = Math.min(ShapeBlurActivity.f63w, ShapeBlurActivity.f62h);
            if (min < 1024) {
                ShapeBlurActivity.this.f64f = ((float) min) / 1024.0f;
            }
            ShapeBlurActivity.bitmapBlur = ShapeBlurActivity.blur(ShapeBlurActivity.this.getApplicationContext(), ShapeBlurActivity.bitmapClear, 25);
            ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapBlur);
            ShapeBlurActivity.imageView.maskContainer = Bitmap.createBitmap(ShapeBlurActivity.f63w, ShapeBlurActivity.f62h, Config.ALPHA_8);
            ShapeBlurActivity.imageView.hover = false;
            ShapeBlurActivity.shapeAdapter.notifyDataSetChanged();
            ShapeBlurActivity.imageView.init();
            ShapeBlurActivity.imageView.lastPosIndex = -1;
            ShapeBlurActivity.imageView.invalidate();
        }
    };
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_btn:
                selectImage();
                save_btn.setBackgroundColor(btnbgColor);
                new_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                return;
            case R.id.save_btn:
                saveImage();
                new_btn.setBackgroundColor(btnbgColor);
                save_btn.setBackgroundColor(Color.parseColor("#BDBDBD"));
                return;
            default:
                return;
        }
    }

    void saveImage() {
        this.currentPath = this.imageSavePath + "/" + System.currentTimeMillis() + ".jpg";
        File currentFile = new File(this.currentPath);
        try {
            FileOutputStream out = new FileOutputStream(currentFile);
            generateOutputBitmap(this).compress(CompressFormat.JPEG, 100, out);
            imageView.destroyDrawingCache();
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
            Intent intent = new Intent(this, ShareImageActivity.class);
            intent.putExtra("image", this.currentPath);
            startActivity(intent);
        }
    }

    static Bitmap generateOutputBitmap(Context context) {
        Bitmap finalBitmap;
        float factor;
        if (imageView.bool) {
            finalBitmap = bitmapBlur.copy(Config.ARGB_8888, true);
        } else {
            finalBitmap = bitmapClear.copy(Config.ARGB_8888, true);
        }
        Canvas finalCanvas = new Canvas(finalBitmap);
        imageView.maskContainer = Bitmap.createBitmap(f63w, f62h, Config.ALPHA_8);
        Canvas temp = new Canvas(imageView.maskContainer);
        Matrix mat = new Matrix();
        if (f63w > f62h) {
            factor = ((float) f63w) / ((float) wScreen);
        } else {
            factor = ((float) f62h) / ((float) imageView.getHeight());
        }
        mat.setScale(factor, factor);
        mat.postRotate(imageView.mRotationDegree, (((float) imageView.wMask) * factor) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT, (((float) imageView.wMask) * factor) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT);
        mat.postTranslate(((imageView.last.x - imageView.f65m[2]) / imageView.f65m[0]) - ((((float) imageView.wMask) * factor) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT), ((imageView.last.y - imageView.f65m[5]) / imageView.f65m[4]) - ((((float) imageView.wMask) * factor) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT));
        temp.drawBitmap(imageView.mask, mat, null);
        finalCanvas.drawBitmap(imageView.maskContainer, 0.0f, 0.0f, imageView.paint);
        if (border.isChecked()) {
            imageView.preview = imageView.svgToBitmap(imageView.getResources(), imageView.svgId, (int) (((float) imageView.wMask) * factor));
            mat = new Matrix();
            mat.postTranslate(((imageView.last.x - imageView.f65m[2]) / imageView.f65m[0]) - ((((float) imageView.wMask) * factor) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT), ((imageView.last.y - imageView.f65m[5]) / imageView.f65m[4]) - ((((float) imageView.wMask) * factor) / ImageRequest.DEFAULT_IMAGE_BACKOFF_MULT));
            finalCanvas.drawBitmap(imageView.preview, mat, null);
        }
        return finalBitmap;
    }

    private void selectImage() {
        final CharSequence[] items = new CharSequence[]{"Gallery", "Camera", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UserPermission.checkPermission(ShapeBlurActivity.this);
                if (items[item].equals("Camera")) {
                    ShapeBlurActivity.this.userChoosenTask = "Camera";
                    if (result) {
                        ShapeBlurActivity.this.cameraIntent();
                    }
                } else if (items[item].equals("Gallery")) {
                    ShapeBlurActivity.this.userChoosenTask = "Gallery";
                    if (result) {
                        ShapeBlurActivity.this.galleryIntent();
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
        startActivityForResult(Intent.createChooser(intent, "Select File"), this.SELECT_FILE);
    }

    private void cameraIntent() {
        this.cameraImage = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
        this.cameraImageUri = Uri.fromFile(this.cameraImage);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", this.cameraImageUri);
        startActivityForResult(intent, this.REQUEST_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != -1) {
            return;
        }
        if (requestCode == this.SELECT_FILE) {
            onSelectFromGalleryResult(data);
        } else if (requestCode == this.REQUEST_CAMERA) {
            onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Glide.with((Activity) this).load(this.cameraImageUri).asBitmap().into(this.gTarget);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Glide.with((Activity) this).load(data.getData()).asBitmap().into(gTarget);
    }

    private void setCategoryID() {
        categoryID[0] = R.drawable.complex;
        categoryID[1] = R.drawable.simple;
        selectedCategoryID[0] = R.drawable.complex_selected;
        selectedCategoryID[1] = R.drawable.simple_selected;
        shapeButtonID[0] = new int[15];
        shapeID[0] = new int[15];
        shapeViewID[0] = new int[15];
        shapeButtonID[1] = new int[15];
        shapeID[1] = new int[15];
        shapeViewID[1] = new int[15];
        shapeButtonID[0][0] = R.drawable.simple1;
        shapeButtonID[0][1] = R.drawable.simple2;
        shapeButtonID[0][2] = R.drawable.simple3;
        shapeButtonID[0][3] = R.drawable.simple4;
        shapeButtonID[0][4] = R.drawable.simple5;
        shapeButtonID[0][5] = R.drawable.simple6;
        shapeButtonID[0][6] = R.drawable.simple7;
        shapeButtonID[0][7] = R.drawable.simple8;
        shapeButtonID[0][8] = R.drawable.simple9;
        shapeButtonID[0][9] = R.drawable.simple10;
        shapeButtonID[0][10] = R.drawable.simple11;
        shapeButtonID[0][11] = R.drawable.simple12;
        shapeButtonID[0][12] = R.drawable.simple13;
        shapeButtonID[0][13] = R.drawable.simple14;
        shapeButtonID[0][14] = R.drawable.simple15;
        shapeButtonID[1][0] = R.drawable.complex1;
        shapeButtonID[1][1] = R.drawable.complex2;
        shapeButtonID[1][2] = R.drawable.complex3;
        shapeButtonID[1][3] = R.drawable.complex4;
        shapeButtonID[1][4] = R.drawable.complex5;
        shapeButtonID[1][5] = R.drawable.complex6;
        shapeButtonID[1][6] = R.drawable.complex7;
        shapeButtonID[1][7] = R.drawable.complex8;
        shapeButtonID[1][8] = R.drawable.complex9;
        shapeButtonID[1][9] = R.drawable.complex10;
        shapeButtonID[1][10] = R.drawable.complex11;
        shapeButtonID[1][11] = R.drawable.complex12;
        shapeButtonID[1][12] = R.drawable.complex13;
        shapeButtonID[1][13] = R.drawable.complex14;
        shapeButtonID[1][14] = R.drawable.complex15;
        shapeID[0][0] = R.drawable.a_1;
        shapeID[0][1] = R.drawable.a_2;
        shapeID[0][2] = R.drawable.a_3;
        shapeID[0][3] = R.drawable.a_4;
        shapeID[0][4] = R.drawable.a_5;
        shapeID[0][5] = R.drawable.a_6;
        shapeID[0][6] = R.drawable.a_7;
        shapeID[0][7] = R.drawable.a_8;
        shapeID[0][8] = R.drawable.a_9;
        shapeID[0][9] = R.drawable.a_10;
        shapeID[0][10] = R.drawable.a_11;
        shapeID[0][11] = R.drawable.a_12;
        shapeID[0][12] = R.drawable.a_13;
        shapeID[0][13] = R.drawable.a_14;
        shapeID[0][14] = R.drawable.a_15;
        shapeID[1][0] = R.drawable.a_16;
        shapeID[1][1] = R.drawable.a_17;
        shapeID[1][2] = R.drawable.a_18;
        shapeID[1][3] = R.drawable.a_19;
        shapeID[1][4] = R.drawable.a_20;
        shapeID[1][5] = R.drawable.a_21;
        shapeID[1][6] = R.drawable.a_22;
        shapeID[1][7] = R.drawable.a_23;
        shapeID[1][8] = R.drawable.a_24;
        shapeID[1][9] = R.drawable.a_25;
        shapeID[1][10] = R.drawable.a_26;
        shapeID[1][11] = R.drawable.a_27;
        shapeID[1][12] = R.drawable.a_28;
        shapeID[1][13] = R.drawable.a_29;
        shapeID[1][14] = R.drawable.a_30;

        shapeViewID[0][0] = R.raw.b_1;
        shapeViewID[0][1] = R.raw.b_2;
        shapeViewID[0][2] = R.raw.b_3;
        shapeViewID[0][3] = R.raw.b_4;
        shapeViewID[0][4] = R.raw.b_5;
        shapeViewID[0][5] = R.raw.b_6;
        shapeViewID[0][6] = R.raw.b_7;
        shapeViewID[0][7] = R.raw.b_8;
        shapeViewID[0][8] = R.raw.b_9;
        shapeViewID[0][9] = R.raw.b_10;
        shapeViewID[0][10] = R.raw.b_11;
        shapeViewID[0][11] = R.raw.b_12;
        shapeViewID[0][12] = R.raw.b_13;
        shapeViewID[0][13] = R.raw.b_14;
        shapeViewID[0][14] = R.raw.b_15;
        shapeViewID[1][0] = R.raw.b_16;
        shapeViewID[1][1] = R.raw.b_17;
        shapeViewID[1][2] = R.raw.b_18;
        shapeViewID[1][3] = R.raw.b_19;
        shapeViewID[1][4] = R.raw.b_20;
        shapeViewID[1][5] = R.raw.b_21;
        shapeViewID[1][6] = R.raw.b_22;
        shapeViewID[1][7] = R.raw.b_23;
        shapeViewID[1][8] = R.raw.b_24;
        shapeViewID[1][9] = R.raw.b_25;
        shapeViewID[1][10] = R.raw.b_26;
        shapeViewID[1][11] = R.raw.b_27;
        shapeViewID[1][12] = R.raw.b_28;
        shapeViewID[1][13] = R.raw.b_29;
        shapeViewID[1][14] = R.raw.b_30;

        reverseArray(shapeButtonID[0]);
        reverseArray(shapeButtonID[1]);
        reverseArray(shapeID[0]);
        reverseArray(shapeViewID[0]);
        reverseArray(shapeID[1]);
        reverseArray(shapeViewID[1]);
        reverseArray(categoryID);
        reverseArray(selectedCategoryID);
    }

    public static Bitmap blur(Context context, Bitmap image, int radius) {
        Bitmap inputBitmap = Bitmap.createBitmap(image);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        if (VERSION.SDK_INT < 17) {
            return blurify(image, radius);
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
        int[] sir;
        int p;
        int x;
        int bsum;
        int gsum;
        int rsum ;
        int boutsum;
        int goutsum ;
        int routsum ;
        int binsum ;
        int ginsum ;
        int rinsum ;
        int rbs;
        int stackpointer;
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

    void reverseArray(int[] validData) {
        for (int i = 0; i < validData.length / 2; i++) {
            int temp = validData[i];
            validData[i] = validData[(validData.length - i) - 1];
            validData[(validData.length - i) - 1] = temp;
        }
    }

    public void onBackPressed() {
        super.onBackPressed();

    }

    void doYouLoveAppPrompt() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Love");
        alertDialog.setMessage("Do you love this app");
        alertDialog.setButton(-1, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                PreferenceManager.getDefaultSharedPreferences(ShapeBlurActivity.this).edit().putString("BlurEffectLove", "yes").commit();
                ShapeBlurActivity.this.ratePrompt();
            }
        });
        alertDialog.setButton(-2, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //MainActivity.showAdmobInterstitial();
                ShapeBlurActivity.this.finish();
            }
        });
        alertDialog.show();
    }

    public void ratePrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final Context context = this;
        builder.setTitle("WOW!");
        builder.setMessage("You love this app. Rate me 5 stars.");
        builder.setPositiveButton("Let's go!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("BlurEffectRate", "yes").commit();
                try {
                    ShapeBlurActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + ShapeBlurActivity.this.getPackageName())));
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                //MainActivity.showAdmobInterstitial();
                ShapeBlurActivity.this.finish();
            }
        });
        builder.create().show();
    }
}
