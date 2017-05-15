package com.example.martin.rgb_catcher.BitmapFiles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.ImageView;

import com.example.martin.rgb_catcher.BitmapFiles.BitmapAnimation.PhotoAnimation;
import com.example.martin.rgb_catcher.BitmapFiles.BitmapAnimation.PhotoDoubleTouchAnimation;

import java.io.IOException;

/**
 * Created by Martin on 30.6.2015.
 */
public class BitmapRender {

    public static int roateteuhel;
    static boolean otocLeva,otocPrava;

    public static Bitmap bitmap(String cesta,int reqWidth, int reqHeight,Boolean otocLevaTaken,boolean otocPravaTaken) {
        otocLeva =otocLevaTaken;
        otocPrava = otocPravaTaken;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(cesta, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(cesta, options);
        try {                                                       /**zjistěni orientace mapy*/
            ExifInterface exif = new ExifInterface(cesta);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            Matrix matrixRotate = new Matrix();
            Log.e("oriantation", orientation + " "+roateteuhel);
            if (roateteuhel == 0)                           /**pokud se rovna nule znamena to ze se fotka jeste nebyla nactena*/
                SetPhoto(matrixRotate, orientation);
            else {
                roateteuhel = roateteuhel + UserRotation();
            }
            matrixRotate.setRotate(roateteuhel);
            Bitmap rotatebitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixRotate, true);
            return rotatebitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Error", e.getMessage());
        }
        return bitmap;
    }
    private static int UserRotation(){ /**otocit plati pro orientaci (možná ještě předělám*/
        if (otocLeva) {
            otocLeva = false;
            return -90;
        }
        else if (otocPrava) {
            otocPrava = false;
            return 90;
        }
        else
            return 0;
    }
    private static void SetPhoto(Matrix matrix, int orientation){
        roateteuhel = 3600;                                                                         /**aby se uhel nerovnal nule*/
        switch (orientation) {
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                roateteuhel = roateteuhel+180;
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                roateteuhel = roateteuhel+180;
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                roateteuhel = roateteuhel+90;
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                roateteuhel = roateteuhel+90;
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                roateteuhel = roateteuhel+-90;
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                roateteuhel = roateteuhel+-90;
                break;
        }
    }
    static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        PhotoAnimation.smallPhoto = false;
        PhotoDoubleTouchAnimation.smallPhoto = false;


        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        else {
            PhotoAnimation.smallPhoto = true;           /**pokud je fotka moc mala tak nep;jde pribliyovat*/
            PhotoDoubleTouchAnimation.smallPhoto = false;
        }
        return inSampleSize;
    }
    public static void CenterMatrix(Bitmap photo,ImageView imageView,Matrix matrix){
        RectF drawableRect = new RectF(0, 0, photo.getWidth(), photo.getHeight());
        RectF viewRect = new RectF(0, 0, imageView.getWidth(), imageView.getHeight());
        matrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
    }
}
