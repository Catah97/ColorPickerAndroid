package com.example.martin.rgb_catcher.BitmapFiles;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Martin on 30.6.2015.
 */
public class BitmapControl {

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static PointF bufferPoint0 = new PointF();
    static PointF bufferPoint1 = new PointF();




    public static Matrix SetMatrixPosition(float positionX,float positionY,int bitmapWidth,int bitmapHeight,float[] values,float[] finalValues,ImageView imgView,Matrix matrix,float scale){
        float x_start_point = 0,y_start_point = 0;
        final float x_photoViewSize = bitmapWidth * values[Matrix.MSCALE_X];
        final float y_photoViewSize = bitmapHeight * values[Matrix.MSCALE_Y];
        final double X_KONSTANT = (bitmapWidth*(finalValues[Matrix.MSCALE_X]+1.8F)) / x_photoViewSize;
        final double Y_KONSTANT = (bitmapHeight*(finalValues[Matrix.MSCALE_Y]+1.8F)) /y_photoViewSize;
        if (bitmapWidth*values[Matrix.MSCALE_X]<imgView.getWidth())                                 /**pokud je šířka obráyku menší než šířka display*/
            x_start_point = (float) (((imgView.getWidth()-x_photoViewSize)/2)*X_KONSTANT);
        if  (bitmapWidth*values[Matrix.MSCALE_X]>=imgView.getWidth())                               /**pokud je šířka obrázku vetší než šířka display*/
            x_start_point = (float) (values[Matrix.MTRANS_X]*X_KONSTANT);
        if (bitmapHeight*values[Matrix.MSCALE_Y]<imgView.getHeight())                               /**pokud je výška obrázku vetší než menší dysplay*/
            y_start_point = (float) (((imgView.getHeight()-y_photoViewSize)/2)*Y_KONSTANT);
        if  (bitmapHeight*values[Matrix.MSCALE_Y]>=imgView.getHeight())                             /**pokud je výška obrázku vetší než výška dysplay*/
            y_start_point = (float) (values[Matrix.MTRANS_Y]*Y_KONSTANT);
        float x = (float) ( x_start_point+( -positionX * X_KONSTANT + (50 *scale) ));
        float y = (float) (y_start_point+( -positionY * Y_KONSTANT +  (50 * scale)));
        matrix.setTranslate(x, y);
        matrix.preScale(finalValues[Matrix.MSCALE_X]+1.8F , finalValues[Matrix.MSCALE_X]+1.8F );
        return matrix;
    }

    public static Bitmap GetCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2, sbmp.getWidth() / 2 - 5, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        paint.setXfermode(null);

        /**kresleni car*/
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);
        canvas.drawLine(canvas.getWidth() / 2, 5, canvas.getWidth() / 2, canvas.getHeight()-5, paint);
        canvas.drawLine(5, canvas.getHeight() / 2, canvas.getWidth()-5, canvas.getHeight() / 2, paint);

        /**kresleni ohraniceni */
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        canvas.drawCircle(sbmp.getWidth() / 2, sbmp.getHeight() / 2, sbmp.getWidth() / 2 - 5, paint);


        return output;
    }

    public static void MidPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    public static int MoveDetektor(MotionEvent event,float photoWidth,float photoHeight,float scale,ImageView imgView) {
        int x0 = (int)event.getX(0);
        int y0 = (int)event.getY(0);
        int x1 = (int)event.getX(1);
        int y1 = (int)event.getY(1);
        int hodnota;
        if (bufferPoint0 != null) {
            if (x0 > bufferPoint0.x && x1 > bufferPoint1.x) {
                hodnota = bitmapXcheck(photoWidth,scale,imgView.getWidth());
            } else if (x0 < bufferPoint0.x && x1 < bufferPoint1.x) {
                hodnota = bitmapXcheck(photoWidth,scale,imgView.getWidth());
            } else if (y0 < bufferPoint0.y && y1 < bufferPoint1.y) {
                hodnota = bitmapYcheck(photoHeight,scale,imgView.getHeight());
            } else if (y0 > bufferPoint0.y && y1 > bufferPoint1.y) {
                hodnota = bitmapYcheck(photoHeight,scale,imgView.getHeight());
            } else
                hodnota = ZOOM;
            bufferPoint0.set((int)event.getX(0),(int) event.getY(0));
            bufferPoint1.set((int)event.getX(1),(int) event.getY(1));
            return hodnota;
        }
        else {

            bufferPoint0.set((int)event.getX(0),(int) event.getY(0));
            bufferPoint1.set((int) event.getX(1), (int) event.getY(1));
            return 0;
        }
    }
    private static int bitmapXcheck(float photoWidth,float scale,float imgWidth){
        if (photoWidth * scale<imgWidth)
            return NONE;
        else
            return DRAG;
    }
    private static int bitmapYcheck(float photoHeight,float scale,float imgHeight ){
        if (photoHeight * scale<imgHeight)
            return NONE;
        else
            return DRAG;
    }
}
