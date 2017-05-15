package com.example.martin.rgb_catcher.BitmapFiles;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * Created by Martin on 15.11.2015.
 */
public class BitmapLoading extends AsyncTask<Object,Void,Bitmap> {


    final Handler handler;
    static final int BITMAPLOADET = 45;
    static final int BITMAPLOADINGFAIL = 54;

    public BitmapLoading(Handler handler){
        this.handler = handler;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        String bitmapPath = (String) params[0];
        int width = (int) params[1];
        int height = (int) params[2];
        boolean otocLeva = (boolean) params[3];
        boolean otocPrava = (boolean) params[4];
        Bitmap photo = null;
        try {
            photo = BitmapRender.bitmap(bitmapPath, width, height, otocLeva, otocPrava);
        }
        catch (Exception e){
            Message msg = new Message();
            msg.arg1 = BITMAPLOADINGFAIL;
            handler.sendMessage(msg);
        }
        return photo;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Message msg = new Message();
        msg.arg1 = BITMAPLOADET;
        handler.sendMessage(msg);
    }
}
