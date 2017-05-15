package com.example.martin.rgb_catcher.BitmapFiles.BitmapAnimation;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Created by Martin on 05.12.2015.
 */
public class PhotoDoubleTouchAnimation extends GestureDetector.SimpleOnGestureListener {

    static Matrix matrix;
    ImageView imgView;
    final float MIN_ZOOM, MAX_ZOOM;
    static float photoWidth, photHeight;
    final PointF centerPoint = new PointF();
    float needXforScale, needYforScale;
    public PointF midPoint;
    public static boolean smallPhoto;

    public PhotoDoubleTouchAnimation(Matrix matrix, PointF midPoint, ImageView imgView, float MIN_ZOOM, float MAX_ZOOM, float photoWidth, float photoHeight) {

        this.matrix = matrix;
        this.midPoint = midPoint;
        this.imgView = imgView;
        this.MAX_ZOOM = MAX_ZOOM;
        this.MIN_ZOOM = MIN_ZOOM;
        this.photoWidth = photoWidth;
        this.photHeight = photoHeight;
        centerPoint.set(imgView.getWidth() / 2, imgView.getHeight() / 2);                                /**center point je presně polovina imageView*/
    }


    @Override
    public boolean onDoubleTap(MotionEvent e) {
        if (!smallPhoto) {                                                                           /**pokud je fotka moc mala, prto6e by potom přibližování blblo*/
            float[] values = new float[9];
            matrix.getValues(values);
            if (values[Matrix.MSCALE_X] > MAX_ZOOM - 0.2F) {                                                   /**pokud je obrázek moc velký tak se zde zmensuješ a zárpven rovná na střed*/
                ZoomOutAnimation zoom = new ZoomOutAnimation(values[0], MIN_ZOOM - values[0]);                   /**první hodnota je momentální přiblížení a druhé je potřebná hodnota k zarovnání*/
                zoom.setDuration(300);                                                                  /**nastavení trvání pro animaci*/
                zoom.values = values;
                zoom.anim = true;
                imgView.startAnimation(zoom);
            }
        }
        return true;
    }

    /**private class ZoomInAnimation extends Animation {


        private final float needScale;
        private final float startScale;
        private final float x,y;
        private float POMER_X,POMER_Y;
        public boolean anim;
        private PointF pointF;
        public float[] values = new float[9];

        ZoomInAnimation(float startScale, float needScale,float x,float y) {
            this.needScale = needScale;
            this.startScale = startScale;
            this.x = x;
            this.y = y;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            interpolatedTime = Math.round(interpolatedTime * 100);
            float scale = startScale + (needScale / 100 );
            float widthImage = (imgView.getWidth() * scale / 2);
            float heightImage = (imgView.getHeight() * scale / 2);

            if (anim) {
                ReturnAnimeForDrag(widthImage,heightImage);
                anim = false;
            }

            float[] values = new float[9];
            matrix.getValues(values);
            matrix.postScale(scale,scale,x/-100,y/-100);
            //matrix.preScale(scale, scale);
            imgView.setImageMatrix(matrix);
        }

        private void ReturnAnimeForDrag(float widthImage,float heightImage) {
            POMER_X = widthImage/x;

            float pixelunaobrayovce = (photoWidth-(values[Matrix.MTRANS_X]));
            POMER_Y = photHeight/heightImage;
            needXforScale = (values[Matrix.MTRANS_X]+((photoWidth/2)*POMER_X));
            needYforScale = (values[Matrix.MTRANS_Y]+(y)*POMER_Y+((widthImage/2)*POMER_Y))/-100;
        }
    }
*/
    private class ZoomOutAnimation extends Animation {

        private final float needScale;
        private final float startScale;
        public boolean anim;
        public float[] values = new float[9];
        private PointF pointF;

        ZoomOutAnimation(float startScale, float needScale) {
            this.needScale = needScale;
            this.startScale = startScale;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {              /** interpolatedTime je progres animace který je později převeden na procenta :D */
            super.applyTransformation(interpolatedTime, t);
            interpolatedTime = Math.round(interpolatedTime * 100);                                    /**zaokrouhli hodnotu na 100 */
            float scale = startScale + (needScale / 100 * interpolatedTime);                            /**cele pricitam hodnotu kterou nasobim interpolatedTime coz je hodnota v na kolik procent je animace hotova*/
            float photoCenterWidth = (photoWidth * scale / 2);
            float photoCenterHeight = (photHeight * scale / 2);

            if (anim) {                                                                             /**zapne se jenom poprvé a potom uz ne*/
                ReturnAnimeForDrag(photoCenterWidth, photoCenterHeight);                            /**vygeneruje hodnotu potřebnou pro zarovnání obrázku na střed*/
                anim = false;
            }
            matrix.setTranslate((pointF.x + needXforScale * interpolatedTime) - photoCenterWidth, (pointF.y + needYforScale * interpolatedTime) - photoCenterHeight);/**začíná na středu obrázku a pomalu se posouvá ke středu */
            matrix.preScale(scale, scale);                                                                                                                           /**interpolatedTime vyznačuje progres časovače*/
            imgView.setImageMatrix(matrix);
        }

        private void ReturnAnimeForDrag(float photoCenterWidth, float photoCenterHeight) {
            pointF = new PointF();
            pointF.set(values[Matrix.MTRANS_X] + photoCenterWidth, values[Matrix.MTRANS_Y] + photoCenterHeight); /**nastavení pointu tak aby byl nalezen střed obrázku*/
            needXforScale = (centerPoint.x - pointF.x) / 100;                                                /**vztvo59 hodnotu potřebnout k dosežený středu obrazovky*/
            needYforScale = (centerPoint.y - pointF.y) / 100;                                                 /**je rovnou převedeno na procente (/100) a prot už stačí jenom vynásobit prograsem animace*/
        }
    }
}
