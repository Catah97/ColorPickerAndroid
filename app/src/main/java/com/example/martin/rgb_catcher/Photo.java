package com.example.martin.rgb_catcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.rgb_catcher.BitmapFiles.BitmapAnimation.PhotoDoubleTouchAnimation;
import com.example.martin.rgb_catcher.BitmapFiles.BitmapControl;
import com.example.martin.rgb_catcher.BitmapFiles.BitmapLoading;
import com.example.martin.rgb_catcher.BitmapFiles.BitmapRender;
import com.example.martin.rgb_catcher.BitmapFiles.BitmapAnimation.PhotoAnimation;
import com.example.martin.rgb_catcher.Draw.Draw_Postion;
import com.example.martin.rgb_catcher.Draw.Draw_RGB;
import com.example.martin.rgb_catcher.Other.Ads_Loading;
import com.example.martin.rgb_catcher.Other.Dialogs;
import com.example.martin.rgb_catcher.Other.Convertor;
import com.example.martin.rgb_catcher.PhotoMenu.Menu;
import com.example.martin.rgb_catcher.Other.Save_Load;
import com.example.martin.rgb_catcher.RGB_seznam.RGB_Seznam;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class Photo extends Activity implements Runnable{

    Draw_RGB drawRGB;
    Draw_Postion draw_postion;
    PhotoAnimation zoom;
    BitmapLoading loading;


    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    PointF midPoint = new PointF();
    PointF startMidPoint = new PointF();
    float scale;
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int RGB = 3;
    int mode = NONE;

    static final int BITMAPLOADET = 45;
    static final int BITMAPLOADINGFAIL = 54;

    public static InterstitialAd interstitialAd;
    String bitmapPath;
    RelativeLayout imgRGBview,positionView;
    ImageView imageView,imageViewCache,lupa;
    RelativeLayout reltiveLayoutSave;
    static boolean otocLeva,otocPrava,positionShowed,startAsView;
    Dialog menuDialog,waitingDialog;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage (Message msg) {   /**předáva informace z classu pro dialogy*/
            switch (msg.arg1) {
                case BITMAPLOADET:
                    Bitmap photo = null;
                    try {
                        photo = loading.get();
                    } catch (InterruptedException e) {
                        Toast.makeText(Photo.this, R.string.PhotoGetFail, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        finish();
                    } catch (ExecutionException e) {
                        Toast.makeText(Photo.this, R.string.PhotoGetFail, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        finish();
                    }
                    BitMapLoadet(photo);
                    waitingDialog.dismiss();
                    break;
                case BITMAPLOADINGFAIL:
                    waitingDialog.dismiss();
                    Toast.makeText(Photo.this, R.string.PhotoGetFail, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case 0:
                    otocLeva = true;
                    Bitmap rotatedLeft = BitmapRender.bitmap(bitmapPath, imageView.getWidth(), imageView.getHeight(), otocLeva, otocPrava);
                    BitMapLoadet(rotatedLeft);
                    break;
                case 1:
                    otocPrava = true;
                    Bitmap rotatedRight = BitmapRender.bitmap(bitmapPath, imageView.getWidth(), imageView.getHeight(), otocLeva, otocPrava);
                    BitMapLoadet(rotatedRight);
                    break;
                case 2:
                    if (Menu.lupaShowStav)
                        Menu.lupaShowStav = false;
                    else
                        Menu.lupaShowStav = true;
                    menuDialog.dismiss();
                    break;
                case 3:
                    Intent setting = new Intent(Photo.this, Setting.class);
                    startActivityForResult(setting, 789);
                    break;
                case 4:
                    Intent RGBseznam = new Intent(Photo.this, RGB_Seznam.class);
                    startActivity(RGBseznam);
                    break;
                case 100:

                    Bundle data = msg.getData();
                    String name = data.getString("name");
                    Log.e("uloy", name);
                    Save_Load.SaveData(Photo.this, Save_Load.LoadedData(Photo.this, "name.txt"), name, "name", "name.txt");                                 /**save jmeno barvy*/
                    Save_Load.SaveData(Photo.this,Save_Load.LoadedData(Photo.this, "mainValues.txt"),ChooseScrenn_Main.system.toLowerCase(), "name","mainValues.txt");    /**save the main Value*/
                    Save_Load.SaveData(Photo.this, Save_Load.LoadedData(Photo.this, "RGB_Seznam.txt"), null, "saveRGB", "RGB_Seznam.txt");                  /**save RGB hodnoty*/

            }
        }
    };

    Thread setImageView = new Thread(this);
    public void run() {
        synchronized (Photo.this) {
            while (imageView.getWidth() == 0) {                 /**cekání na to než hodnota ImageView bude platná*/
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Convertor.RELATIVELAYOUT_width = imgRGBview.getWidth();
                    Convertor.PhotoMainScreenConvertor(reltiveLayoutSave,scale);
                    loading = new BitmapLoading(handler);
                    loading.execute(bitmapPath, imageView.getWidth(), imageView.getHeight(), otocLeva, otocPrava);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu sipku zpet**/
        getActionBar().setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.option:
                menuDialog = Menu.MenuDialog(this, handler,getResources().getStringArray(R.array.MenuPhoto));
                menuDialog.show();
                int pixels = (int) (200 * scale + 0.5f);
                menuDialog.getWindow().setLayout(pixels, WindowManager.LayoutParams.WRAP_CONTENT);
                break;
            case android.R.id.home:
                BitmapRender.roateteuhel = 0;
                String path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath();
                path = path + "/Temp.jpg";
                File dowlandedPicture = new File(path);
                if (dowlandedPicture.exists())
                {
                    dowlandedPicture.delete();
                }
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Convertor.PhotoMainScreenConvertor(reltiveLayoutSave, scale);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ChooseScrenn_Main.system = Save_Load.LoadedData(this,"mainValueModel.txt");
        scale = Photo.this.getResources().getDisplayMetrics().density;                  /**získání skale faktroru*/
        drawRGB = new Draw_RGB(this);
        drawRGB.R = drawRGB.G = drawRGB.B = 255;
        draw_postion = new Draw_Postion(this,scale);
        Menu.lupaShowStav = false;                                                      /**nastaví lupu do defaultní pozice*/
        BitmapScreen();
    }
    private void BitmapScreen(){
        if (imgRGBview != null)
            imgRGBview.removeAllViews();
        if (positionView != null)
            positionView.removeAllViews();
        setContentView(R.layout.photo);                                   /**Z neznámeho důvodu zde musím definovat screen*/
        imgRGBview = (RelativeLayout) findViewById(R.id.imgOUT);
        SetImage();
    }
    private void    SetImage() {
        setContentView(R.layout.photo);                                   /**Z neznámeho důvodu zde musím definovat screen*/
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAd(Photo.this);
            Ads_Loading.AdsWindows(interstitialAd);
        }
        imageViewCache = (ImageView) findViewById(R.id.imageViewChache);
        imgRGBview = (RelativeLayout) findViewById(R.id.imgOUT);
        imgRGBview.addView(drawRGB);
        positionView = (RelativeLayout) findViewById(R.id.positionOUT);
        positionView.addView(draw_postion);
        imageView = (ImageView) findViewById(R.id.background);
        lupa = (ImageView) findViewById(R.id.lupa);
        reltiveLayoutSave = (RelativeLayout) findViewById(R.id.btnSave);
        TextView pick = (TextView) findViewById(R.id.txtPick);
        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = Dialogs.SaveRGBDialog(getLayoutInflater(), Photo.this, handler);
                dialog.show();
            }
        });
        Intent intent = getIntent();
        Log.e("intent", intent.getAction());
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            startAsView = true;
            Uri imageUri = intent.getData();
            bitmapPath = getRealPathFromURI(Photo.this, imageUri);
        }
        else {
            bitmapPath = getIntent().getStringExtra("path");
        }
        Log.e("bitma", bitmapPath + "");
        waitingDialog = Dialogs.WaitingDialog(this);
        waitingDialog.show();
        if (!setImageView.isAlive()) {                                     /**vlákno které vrací hodnoty layoutů*/
            setImageView = new Thread(this);
            setImageView.start();
        }


    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void BitMapLoadet(final Bitmap photo){
        final Matrix first_matrix = new Matrix();
        imageView.setImageBitmap(photo);
        BitmapRender.CenterMatrix(photo, imageView, matrix);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);                                 /**nastaví obrázek na matrix*/
        imageView.setImageMatrix(matrix);                                                   /**nahraje matrix*/

        imageViewCache.setImageBitmap(photo);
        BitmapRender.CenterMatrix(photo, imageView, matrix);
        imageViewCache.setScaleType(ImageView.ScaleType.MATRIX);                                 /**nastaví obrázek na matrix*/
        imageViewCache.setImageMatrix(matrix);

        final float[] first_Values = new float[9];
        matrix.getValues(first_Values);                                                           /**vzgeneruje maximalni a minimalni hodnotu pribliyeni*/
        otocLeva = otocPrava = false;                                                       /**aby se bitmapa netocila do nekonecna*/
        first_matrix.set(matrix);
        final float MIN_ZOOM = first_Values[0];
        zoom = new PhotoAnimation(matrix, midPoint, imageView, first_Values[0], first_Values[0] + 1F, photo.getWidth(), photo.getHeight());
        final ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(Photo.this, zoom);
        final GestureDetector gestureDetector = new GestureDetector(Photo.this, new PhotoDoubleTouchAnimation(matrix, midPoint, imageView, first_Values[0], first_Values[0] + 1F, photo.getWidth(), photo.getHeight()));
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ImageView view = (ImageView) v;
                imageView.setDrawingCacheEnabled(false);
                draw_postion.kresli = false;
                float[] values = new float[9];
                matrix.getValues(values);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mode = RGB;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        savedMatrix.set(matrix);
                        BitmapControl.MidPoint(startMidPoint, event);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        positionShowed = false;
                        lupa.setVisibility(View.INVISIBLE);
                        draw_postion.kresli = true;
                        mode = NONE;
                        if (values[Matrix.MSCALE_X] >= MIN_ZOOM)                            /**animace probehne pouye tehnd pokud matrix neni menší než je minimální hodnota aby nebeželi 2 aimace naraz*/
                            PhotoAnimation.DragCheck(values);                               /**tento řádek je zde aby appka bežele na všech zařízeních*/
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int lastMode = mode;
                        positionShowed = false;
                        lupa.setVisibility(View.INVISIBLE);
                        if (event.getPointerCount() == 2) {
                            BitmapControl.MidPoint(midPoint, event);
                            mode = BitmapControl.MoveDetektor(event, photo.getWidth(), photo.getHeight(), values[Matrix.MSCALE_X], imageView);
                            if (lastMode != mode) {
                                BitmapControl.MidPoint(startMidPoint, event);
                                savedMatrix.set(matrix);
                            }
                        }
                        if (mode == DRAG) {
                            zoom.zoom = false;
                            matrix.set(savedMatrix);
                            matrix.postTranslate(midPoint.x - startMidPoint.x, midPoint.y - startMidPoint.y);
                            imageView.setImageMatrix(matrix);
                        } else if (mode == ZOOM) {
                            zoom.zoom = true;                                               /**oznami classu že probíhá približování*/
                            zoom.midPoint = midPoint;
                        }
                        break;
                }
                scaleGestureDetector.onTouchEvent(event);
                gestureDetector.onTouchEvent(event);
                view.setImageMatrix(matrix);
                if (mode == RGB) {
                    if (!positionShowed && Menu.lupaShowStav) {
                        positionShowed = true;
                        lupa.setVisibility(View.VISIBLE);
                    }

                    SetDialog(event.getX(), event.getY());

                    draw_postion.kresli = true;
                    draw_postion.x = event.getX();
                    draw_postion.y = event.getY();
                    imageView.setDrawingCacheEnabled(true);                             /**nastaví jí tak aby bylo možno pracovat s pixeli tak jek jsou vidět na display*/
                    int pixel;
                    Bitmap cacheBitmap = Bitmap.createBitmap(imageView.getDrawingCache());

                    if (positionShowed) {
                        imageViewCache.setImageMatrix(BitmapControl.SetMatrixPosition(event.getX(), event.getY(), photo.getWidth(), photo.getHeight(), values, first_Values, imageView, first_matrix, scale));

                        imageViewCache.setDrawingCacheEnabled(true);
                        Bitmap bitmap = Bitmap.createBitmap(imageViewCache.getDrawingCache());
                        lupa.setImageBitmap(BitmapControl.GetCroppedBitmap(bitmap, (int) (120 * scale)));


                        imageViewCache.setDrawingCacheEnabled(false);
                    }

                    try {               /**pokud je pixel mimo Bitmapu potom se rovná černý*/
                        pixel = cacheBitmap.getPixel((int) event.getX(), (int) event.getY());
                        if (pixel == 0)
                            drawRGB.R = drawRGB.G = drawRGB.B = 255;
                        else {
                            drawRGB.R = Color.red(pixel);
                            drawRGB.B = Color.blue(pixel);
                            drawRGB.G = Color.green(pixel);
                        }
                    } catch (Exception e) {
                        drawRGB.R = drawRGB.B = drawRGB.G = 255;
                    }
                    Convertor.PhotoMainScreenConvertor(reltiveLayoutSave,scale);
                    drawRGB.invalidate();
                }
                draw_postion.invalidate();
                return true;
            }
        });
    }
    private void SetDialog(float x ,float y){

        /**nastaveni velikosti*/
        int size = (int) (100 * scale);
        lupa.setX(x - size / 2);   //x position
        if (0>lupa.getX())
            lupa.setX(10);
        else if (lupa.getX()+size > imageView.getWidth())
            lupa.setX(imageView.getWidth()-(size+10));
        lupa.setY(y - size);   //x position
        if (0>lupa.getY())
            lupa.setY(size+y);   //y position, pokud je Y dole
        else if (lupa.getY()+size>imageView.getHeight())
            lupa.setY(imageView.getHeight() - (size + 10));   //y position, pokud je Y nahoře a dialog je posunut dolů

    }
}
