package com.example.sys9.ddrblur.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import com.example.sys9.ddrblur.R;


public class ShareImageActivity extends Activity implements OnClickListener {
    Button share_btn;
    ImageButton back_btn;

    Context ctx;
    Button setWallpaper_btn;

    ImageButton home_btn;
    String imageSaveLocation;
    String imageSavePath = (Environment.getExternalStorageDirectory().getPath() + "/DDR Blur");
    Bitmap previewBitmap;
    ImageView imageView;
    ProgressDialog progressDialog;
    ImageButton save_btn;


    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_iamge);
        this.ctx = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.imageSaveLocation = extras.getString("image");
        }
        try {
            previewBitmap = Media.getBitmap(getContentResolver(), Uri.fromFile(new File(imageSaveLocation)));
        } catch (Exception e) {
        }
        imageView = (ImageView) findViewById(R.id.previewThumb);
        setWallpaper_btn = (Button) findViewById(R.id.set_wallpaper_btn);
        share_btn = (Button) findViewById(R.id.share_btn);
        back_btn = (ImageButton) findViewById(R.id.back_btn);
        home_btn = (ImageButton) findViewById(R.id.home_btn);


        imageView.setImageBitmap(previewBitmap);
        setWallpaper_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        home_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                return;
            case R.id.home_btn:
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(67108864);
                    startActivity(intent);
                    return;
            case R.id.set_wallpaper_btn:
                try {
                    WallpaperManager.getInstance(getApplicationContext()).setBitmap(previewBitmap);
                    Toast.makeText(getApplicationContext(), "Image set As WallPaper", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            case R.id.share_btn:
                shareImage("share");
                return;
            default:
                return;
        }
    }


    void shareImage(String appPackage) {
        File shareFile = new File(imageSavePath, System.currentTimeMillis() + ".jpg");
        try {
            Intent share = new Intent("android.intent.action.SEND");
            share.setType("image/jpeg");
            if (appPackage != "share") {
                share.setPackage(appPackage);
            }
            FileOutputStream out = new FileOutputStream(shareFile);
            previewBitmap.compress(CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            share.putExtra("android.intent.extra.STREAM", Uri.fromFile(shareFile));
            startActivity(Intent.createChooser(share, "Share Image"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        this.progressDialog.hide();

    }


    public void onBackPressed() {
       super.onBackPressed();
    }

}

