package com.example.sys9.ddrblur.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import com.example.sys9.ddrblur.R;

import com.example.sys9.ddrblur.activity.ShapeBlurActivity;
//import com.example.welcome.ddrblur.activity.ShapeBlurActivity;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;


public class MainActivity extends Activity implements OnClickListener {
    private static Context ctx;

    ImageView shapeBlur;
    ImageView startBtn;
    private static final int PERMISSION_REQUEST_CODE = 200;
    GridView grid;
    String[] web = {
            "Magic Mirror", "Flower frames", "Women Saree","Background","Bubble shoot",
            "Pattu saree","Blouse ", "Pierce photo", "Bike race","Rain photo","Sudoku",
            "Women police"

    } ;
    int[] imageId = {
            R.drawable.echo_magicalmirror,R.drawable.flower_frames,R.drawable.women_jewelerysarees,
            R.drawable.backgroundremover,R.drawable.buble_shooter,R.drawable.women_pattusarees,
            R.drawable.blouse_design,R.drawable.piercing_photoeditor,R.drawable.monster_bikerace,
            R.drawable.rain_onphoto,R.drawable.sudoku,R.drawable.women_policedress

    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;
        requestStoragePermission();
        startBtn = (ImageView) findViewById(R.id.start_blur);
        shapeBlur = (ImageView) findViewById(R.id.shape_blur);
        CustomGrid adapter = new CustomGrid(MainActivity.this, web, imageId);
        grid=(GridView)findViewById(R.id.grid_view);
        grid.setAdapter(adapter);

        startBtn.setOnClickListener(this);
        shapeBlur.setOnClickListener(this);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position){
                    case 0:
                        Intent i=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddrinfo.mirrormagic&hl=en"));
                        startActivity(i);
                        break;
                    case 1:
                        Intent i1=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddr.flower&hl=en"));
                        startActivity(i1);
                        break;
                    case 2:
                        Intent i2=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddr.jewel&hl=en"));
                        startActivity(i2);
                        break;
                    case 3:
                        Intent i3=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddr.remove"));
                        startActivity(i3);
                        break;
                    case 4:
                        Intent i4=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddrinfo.bubble"));
                        startActivity(i4);
                        break;
                    case 5:
                        Intent i5=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddr.pattusaree&hl=en"));
                        startActivity(i5);
                        break;
                    case 6:
                        Intent i6=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddrinfosystem.neckdesigns"));
                        startActivity(i6);
                        break;
                    case 7:
                        Intent i7=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddrinfosystems.piercingphoto&hl=en"));
                        startActivity(i7);
                        break;
                    case 8:
                        Intent i8=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddrinfo.bikeracing&hl=en"));
                        startActivity(i8);
                        break;
                    case 9:
                        Intent i9=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddr.rain&hl=en"));
                        startActivity(i9);
                        break;
                    case 10:
                        Intent i10=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddrinfo.sudoku2&hl=en"));
                        startActivity(i10);
                        break;
                    case 11:
                        Intent i11=new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.ddr.police&hl=en"));
                        startActivity(i11);
                        break;

                }

            }
        });

    }


    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.start_blur:
                launchMainActivity();
                break;
            case R.id.shape_blur:
                launchShapeBlur();
                break;

            default:
                break;
        }
    }

    void launchMainActivity() {
        Intent in = new Intent(MainActivity.this,BlurActivity.class);
            startActivity(in);
    }

    void launchShapeBlur() {
        Intent in = new Intent(MainActivity.this,ShapeBlurActivity.class);
        startActivity(in);
        //Toast.makeText(this, "Not Available now... ", Toast.LENGTH_SHORT).show();
    }


   private void requestStoragePermission() {
       ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);

   }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{READ_EXTERNAL_STORAGE, CAMERA},
                                                        PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }
                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    protected void onPause() {

        super.onPause();
    }

    protected void onResume() {

        super.onResume();
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Context context = this;
        builder.setTitle("Exit!");
        builder.setMessage("Do you want to exit from this app?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }
}