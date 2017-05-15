package com.example.martin.rgb_catcher.BitmapFiles.BitmapAnimation;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.ScaleGestureDetector;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

/**
 * Created by Martin on 10.7.2015.
 */
public class PhotoAnimation extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    static Matrix matrix;
    static ImageView imgView;
    final float MIN_ZOOM,MAX_ZOOM;
    static float photoWidth,photHeight,current_trans_X,current_trans_Y;
    final PointF centerPoint = new PointF();
    float needXforScale, needYforScale;
    float[] lastValues = new float[9];
    public boolean zoom;
    public PointF midPoint;
    public static boolean smallPhoto;

    public PhotoAnimation(Matrix matrix, PointF midPoint, ImageView imgView, float MIN_ZOOM, float MAX_ZOOM, float photoWidth, float photoHeight){

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
    public boolean onScale(ScaleGestureDetector detector) {                                         /**při scalu*/
        if (!smallPhoto) {                                                                           /**pokud je fotka moc mala, prto6e by potom přibližování blblo*/
            Float scale = detector.getScaleFactor();
            float[] values = new float[9];
            matrix.getValues(values);
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));                                      /**generuje scale*/
            if (values[Matrix.MSCALE_X] > MAX_ZOOM) {

            } else {
                matrix.postScale(scale, scale, midPoint.x, midPoint.y);                                     /**provádí scale, mid point se generuje v classu photo a je to prosterdní bod mezi prsty*/
                lastValues = values;
            }
            imgView.setImageMatrix(matrix);
        }
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {                                         /**konec scale*/
        super.onScaleEnd(detector);
        if (!smallPhoto) {                                                                           /**pokud je fotka moc mala, prto6e by potom přibližování blblo*/
            float[] values = new float[9];
            matrix.getValues(values);
            if (values[Matrix.MSCALE_X] < MIN_ZOOM) {                                                   /**pokud je obrázek moc velký tak se zde zmensuješ a zárpven rovná na střed*/
                ZoomAnimation zoom = new ZoomAnimation(values[0], MIN_ZOOM - values[0]);                   /**první hodnota je momentální přiblížení a druhé je potřebná hodnota k zarovnání*/
                zoom.setDuration(300);                                                                  /**nastavení trvání pro animaci*/
                zoom.values = values;
                zoom.anim = true;
                imgView.startAnimation(zoom);
            } else if (values[Matrix.MSCALE_X] > MAX_ZOOM) {
                matrix.setTranslate(lastValues[Matrix.MTRANS_X], lastValues[Matrix.MTRANS_Y]);          /**tato hodnota se prepisuje pouze pokud je vše OK*/
                matrix.preScale(lastValues[Matrix.MSCALE_X], lastValues[Matrix.MSCALE_X]);
            } else {
                DragCheck(values);
            }
        }
    }
    public static void DragCheck(float[] values){
        current_trans_X =(imgView.getWidth()/2)-(photoWidth*values[Matrix.MSCALE_X]/2);
        current_trans_Y =(imgView.getHeight()/2)-(photHeight*values[Matrix.MSCALE_Y]/2);
        if (photoWidth * values[Matrix.MSCALE_X]>imgView.getWidth())                            /**pokud je obrázek už vetší než obrazvoka tak potom se díky tomuto řádku při vracení na zpětnou pozici obrázku*/
            current_trans_X = 0;                                                                /**neobjví bílí pruh*/
        if (photHeight * values[Matrix.MSCALE_Y]>imgView.getHeight())
            current_trans_Y = 0;
        PhotoAnimation.DragAnimation dragAnimation = new PhotoAnimation.DragAnimation(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y], values[Matrix.MSCALE_X]);
        if (values[Matrix.MTRANS_X] > current_trans_X)
            dragAnimation.left = true;
        if (values[Matrix.MTRANS_Y] > current_trans_Y)
            dragAnimation.top = true;
        if ((values[Matrix.MTRANS_X]-imgView.getWidth())*-1 > current_trans_X+(photoWidth * values[Matrix.MSCALE_X]))
            dragAnimation.right = true;
        if ((values[Matrix.MTRANS_Y]-imgView.getHeight())*-1 > current_trans_Y+(photHeight * values[Matrix.MSCALE_Y]))
            dragAnimation.bot = true;
        if (dragAnimation.left || dragAnimation.right || dragAnimation.top || dragAnimation.bot) {
            dragAnimation.setDuration(200);
            dragAnimation.anim = true;
            imgView.startAnimation(dragAnimation);
        }
    }

    private class ZoomAnimation extends Animation{

        private final float needScale;
        private final float startScale;
        public boolean anim;
        public float[] values = new float[9];
        public PointF pointF;

        ZoomAnimation (float startScale,float needScale){
            this.needScale = needScale;
            this.startScale = startScale;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {              /** interpolatedTime je progres animace který je později převeden na procenta :D */
            super.applyTransformation(interpolatedTime, t);
            interpolatedTime = Math.round(interpolatedTime*100);                                    /**zaokrouhli hodnotu na 100 */
            float scale = startScale + (needScale/100*interpolatedTime);                            /**cele pricitam hodnotu kterou nasobim interpolatedTime coz je hodnota v na kolik procent je animace hotova*/
            float photoCenterWidth = (photoWidth*scale/2);
            float photoCenterHeight = (photHeight*scale/2);

            if (anim) {                                                                             /**zapne se jenom poprvé a potom uz ne*/
                ReturnAnimeForDrag(photoCenterWidth, photoCenterHeight);                            /**vygeneruje hodnotu potřebnou pro zarovnání obrázku na střed*/
                anim = false;
            }
            matrix.setTranslate((pointF.x + needXforScale * interpolatedTime) - photoCenterWidth, (pointF.y + needYforScale * interpolatedTime) - photoCenterHeight);/**začíná na středu obrázku a pomalu se posouvá ke středu */
            matrix.preScale(scale, scale);                                                                                                                           /**interpolatedTime vyznačuje progres časovače*/
            imgView.setImageMatrix(matrix);
        }
        private void ReturnAnimeForDrag(float photoCenterWidth,float photoCenterHeight){
            pointF = new PointF();
            pointF.set(values[Matrix.MTRANS_X]+photoCenterWidth,values[Matrix.MTRANS_Y]+photoCenterHeight); /**nastavení pointu tak aby byl nalezen střed obrázku*/
            needXforScale = (centerPoint.x - pointF.x)/100;                                                /**vztvo59 hodnotu potřebnout k dosežený středu obrazovky*/
            needYforScale = (centerPoint.y - pointF.y)/100;                                                 /**je rovnou převedeno na procente (/100) a prot už stačí jenom vynásobit prograsem animace*/
        }
    }
    public static class DragAnimation extends Animation{
        final float scale;
        float needXforDrag,needYforDrag,transX,transY;
        public boolean anim,left,right,top,bot;
        private float startPointX,startPointY;


        public DragAnimation(float transX,float transY,float scale){
            this.transX= startPointX = transX;
            this.transY = startPointY= transY;
            this.scale = scale;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            interpolatedTime = Math.round(interpolatedTime*100);                                    /**zaokrouhli hodnotu na 100 */
            if (anim){
                if (left) {
                    needXforDrag = (transX - current_trans_X) / 100;                                /**zjistení jakou část posouváme*/
                    startPointX =transX;
                }
                if (top) {
                    needYforDrag = (transY - current_trans_Y) / 100;
                    startPointY = transY;
                }
                if (right) {
                    /**pokud je na šířku bitmap menší jak display potom platí tato podmínka*/
                    if (current_trans_X !=0){
                        needXforDrag = transX - current_trans_X;
                        startPointX = transX;
                        needXforDrag = needXforDrag / 100;
                    }
                    else {
                        needXforDrag = (transX - imgView.getWidth()) + (photoWidth * scale);
                        startPointX = imgView.getWidth() - (photoWidth * scale) + needXforDrag;     /**need X je yaporne proto plus pro pochopeni tohoto sta4it napsat do logu Log.e("right anime",((transX-imgView.getWidth())*-1)+" "+(DEFAULT_TRANS_X+(photoWidth * scale))+" "+needXforDrag);*/
                        needXforDrag = needXforDrag / 100;
                    }
                }
                if (bot) {
                    /**pokud je na výšku bitmap menší jak display potom platí tato podmínka*/
                    if (current_trans_Y != 0){
                        needYforDrag = transY - current_trans_Y;
                        startPointY = transY;
                        needYforDrag = needYforDrag / 100;
                    }
                    /**v ostatních připadech plati toto*/
                    else{
                        needYforDrag = (transY - imgView.getHeight()) + (photHeight * scale);
                        startPointY = imgView.getHeight() - (photHeight * scale) + needYforDrag;    /**need Y je yaporne proto plus pro pochopeni tohoto sta4it napsat do logu Log.e("right anime",((transX-imgView.getWidth())*-1)+" "+(DEFAULT_TRANS_X+(photoWidth * scale))+" "+needXforDrag);*/
                        needYforDrag = needYforDrag / 100;
                    }
                }
                anim=false;
            }
            matrix.setTranslate(startPointX - (needXforDrag * interpolatedTime), startPointY - (needYforDrag * interpolatedTime));
            matrix.preScale(scale, scale);      /**nastavení scela protože po setTranslate se scale vynuluje */
            imgView.setImageMatrix(matrix);
        }
    }
}
