package com.example.martin.rgb_catcher;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.martin.rgb_catcher.Internet.CopyURL_Check;
import com.example.martin.rgb_catcher.Internet.Dowloanding;
import com.example.martin.rgb_catcher.Other.Ads_Loading;
import com.example.martin.rgb_catcher.Other.Save_Load;
import com.example.martin.rgb_catcher.RGB_seznam.RGB_Seznam;
import com.example.martin.rgb_catcher.Other.Dialogs;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.io.IOException;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Martin on 30.6.2015.
 */
public class ChooseScrenn_Main extends Activity {

    AdView mAdView;

    private static final int CAMERA = 10;
    private static final int ALBUM = 100;
    private static final int DOWLANDCOMPLETE = 666;
    private static final int MY_PERMISSIONS_READ_STORAGE = 1;
    private boolean otevreno;
    public static boolean servicesStop,URl_copyed = false;
    private Dialog getUrld;

    public static String system = "";


    ImageView imgStart,btnInternetPhoto,btnFoundPhoto,btnPhoto;

    String bitmapPath;
    Button seznam;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ALBUM && resultCode == RESULT_OK) {
            GetPhotoDir(data);
        }
        if (requestCode == CAMERA && resultCode == RESULT_OK){
            Photo();
        }
        if (requestCode == 1){
            Create();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ALBUM: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        getPhotoFromGalery();
                    else
                        Toast.makeText(ChooseScrenn_Main.this, R.string.permisionError,Toast.LENGTH_LONG).show();
                }
                break;
            }
            case CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPhotoFromCamere();
                }
                break;
            }
            case DOWLANDCOMPLETE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    dowland();
                }
                break;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choosescreen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.setting:
                Intent intent = new Intent(ChooseScrenn_Main.this,Setting.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Create();
    }
    private void Create(){
        otevreno = false;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        system = Save_Load.LoadedData(this,"mainValueModel.txt");
        if (system.equals("")){
            Log.e("Color picker","File doesnt exist");
            system = "rgb";
            Save_Load.SaveData(this,system,null,"justSave","mainValueModel.txt");
        }



        String URL = "";
        try {
            if (!servicesStop) {
                URl_copyed = getIntent().getExtras().getBoolean("copy");
                URL = getIntent().getExtras().getString("URL");
                Intent intent = new Intent(ChooseScrenn_Main.this, CopyURL_Check.class);
                stopService(intent);
            }
        }
        catch (Exception ignore) {
        }
        setContentView(R.layout.choose_screen);

            Thread reklama = new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdView = (AdView) findViewById(R.id.adView);
                            Ads_Loading.Banner(mAdView);
                            Photo.interstitialAd = new InterstitialAd(ChooseScrenn_Main.this);
                            Ads_Loading.AdsWindows(Photo.interstitialAd);
                        }
                    });
                }
            });
            reklama.start();



        btnPhoto = (ImageView) findViewById(R.id.btnTakePhoto);
        btnFoundPhoto = (ImageView) findViewById(R.id.btnFoundPhoto);
        btnInternetPhoto = (ImageView) findViewById(R.id.btnInternetPhoto);
        seznam = (Button) findViewById(R.id.btnHodnoty);
        imgStart = (ImageView) findViewById(R.id.imgViewStart);
        imgStart.setVisibility(View.VISIBLE);
        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!otevreno) {
                    Start();
                    otevreno = true;
                } else {
                    End();
                    otevreno = false;
                }
            }
        });
        /**tato podminka probehne pouye tehndy pokud aplikace nabiha podruhe po kliknuti na moynost nahrat y netu*/
        if (URl_copyed) {
            URl_copyed = false;
            servicesStop = true;
            Log.e("Welcome", URl_copyed + "");
            InternetStartDowland(URL);
        }
    }

    public void btnSeznamClick(View v){
        Intent intent = new Intent(ChooseScrenn_Main.this, RGB_Seznam.class);
        startActivityForResult(intent, 1);
    }

    private void Start() {
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ChooseScrenn_Main.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(ChooseScrenn_Main.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            CAMERA);
                }
                else
                    getPhotoFromCamere();
            }
        });
        btnFoundPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(ChooseScrenn_Main.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                 != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.requestPermissions(ChooseScrenn_Main.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            ALBUM);
                }
                else {
                    getPhotoFromGalery();
                }

            }
        });
        btnInternetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    dowland();
                else if (ActivityCompat.checkSelfPermission(ChooseScrenn_Main.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(ChooseScrenn_Main.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChooseScrenn_Main.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            ALBUM);
                }
                else {
                    getPhotoFromGalery();
                }
            }
        });
        Animation albumAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.startanimation_for_album);
        Animation internetAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.startanimation_for_internet);
        Animation photoAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.startanimation_for_photo);
        btnFoundPhoto.setVisibility(View.VISIBLE);
        btnInternetPhoto.setVisibility(View.VISIBLE);
        btnPhoto.setVisibility(View.VISIBLE);
        btnFoundPhoto.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        btnFoundPhoto.startAnimation(albumAnimation);
        btnInternetPhoto.startAnimation(internetAnimation);
        btnPhoto.startAnimation(photoAnimation);

    }

    private void End(){
        btnFoundPhoto.setOnClickListener(null);
        btnInternetPhoto.setOnClickListener(null);
        btnPhoto.setOnClickListener(null);
        Animation welcomeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out);
        welcomeAnimation.setStartOffset(200);
        welcomeAnimation.setDuration(500);
        Animation albumAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.endanimation_for_album);
        Animation internetAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.endanimation_for_internet);
        Animation photoAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.endanimation_for_photo);
        btnFoundPhoto.startAnimation(albumAnimation);
        btnInternetPhoto.startAnimation(internetAnimation);
        btnPhoto.startAnimation(photoAnimation);
    }

    private void getPhotoFromCamere(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("error", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent, CAMERA);
            }
            else
                Toast.makeText(ChooseScrenn_Main.this, R.string.cameraError,Toast.LENGTH_SHORT).show();
        }
    }

    private void getPhotoFromGalery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, ALBUM);
    }

    private void InternetStartDowland(String URL){
        final String path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).getAbsolutePath();
        Log.e("path", path);
        final Handler dowlandCheck = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.arg1 == DOWLANDCOMPLETE) {
                    bitmapPath = path + "/Temp.jpg";
                    Photo();
                }
            }
        };
        getUrld = Dialogs.GetUrld(ChooseScrenn_Main.this, path + "/Temp.jpg", dowlandCheck, URL);
        getUrld.show();
    }

    private void dowland(){
        if (Dowloanding.isOnline((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))) {
            URl_copyed = false;
            String url = "http://www.google.com";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            Intent intent = new Intent(ChooseScrenn_Main.this, CopyURL_Check.class);
            intent.setAction("start_services");
            startService(intent);
            Toast.makeText(ChooseScrenn_Main.this, R.string.Downloading_start, Toast.LENGTH_LONG).show();
            /**finish the main activity*/
            finish();
        }
        else
            Toast.makeText(getApplicationContext(), R.string.NO_internet, Toast.LENGTH_SHORT).show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        bitmapPath = image.getAbsolutePath();
        Log.e("bitmap", bitmapPath);
        return image;
    }

    private void GetPhotoDir(Intent data){
        Uri selectedImage = data.getData();
        String[] protection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, protection, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        bitmapPath = cursor.getString(columnIndex);
        cursor.close();
        Photo();
    }
    private void Photo(){
        if (bitmapPath != null) {
            Intent intent = new Intent(this, Photo.class);
            intent.setAction("Ahoj");
            intent.putExtra("path", bitmapPath);
            startActivityForResult(intent, 1);
        }
        else
            Toast.makeText(getApplicationContext(), R.string.LoadingPhoto_Fail,Toast.LENGTH_LONG).show();
    }


    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
