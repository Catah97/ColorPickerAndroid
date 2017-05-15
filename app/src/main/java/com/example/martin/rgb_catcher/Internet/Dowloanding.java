package com.example.martin.rgb_catcher.Internet;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.example.martin.rgb_catcher.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Martin on 23.7.2015.
 * zráva handler zpráva -1 znamená že operace byla zrušena uživatelem zbylé zprávy jsou procenta
 */
public class Dowloanding extends AsyncTask<Void, Integer, String> {

    private final Context context;
    private final String path,urldAdress;
    private final ProgressDialog progresDialog;
    private final Handler progresCheck;
    private static final int DOWLAND_COMPLETE = 666;
    private PowerManager.WakeLock mWakeLock;


    public Dowloanding(Context context, String path, String urldAdress, ProgressDialog progresDialog, Handler handler){
        this.context = context;
        this.path = path;
        this.urldAdress =urldAdress;
        this.progresDialog = progresDialog;
        this.progresCheck = handler;
    }


    public static boolean isOnline(ConnectivityManager cm) {
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected String doInBackground(Void... params) {
        InputStream inputStream = null;
        OutputStream outStream = null;
        try {
            URL url = new URL(urldAdress); //you can write here any link

            URLConnection ucon = url.openConnection();
            inputStream = ucon.getInputStream();
                    /*
                     * Read bytes to the Buffer until there is nothing more to read(-1).
                     */
            outStream = new FileOutputStream(path);
            int fileSize = ucon.getContentLength();
            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = inputStream.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    inputStream.close();
                    break;
                }
                total += count;
                if (fileSize > 0) // only if total length is known
                    publishProgress((int) total, fileSize);
                outStream.write(data, 0, count);
            }
            Log.e("dowland","dowlandet "+total+ " a obrayek na netu "+fileSize);
            Message msg = new Message();        /**posílá do hlavní activyty zprávu o tom že dowloadn byl hotov*/
            msg.arg1 = DOWLAND_COMPLETE;
            progresCheck.sendMessage(msg);
        } catch (IOException e) {
            return e.getMessage();
        } finally {
            try {
                if (outStream != null)
                    outStream.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        progresDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        progresDialog.setMax(progress[1]);
        progresDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        progresDialog.dismiss();
        if (result != null)
            Toast.makeText(context,context.getString(R.string.Downloand_Complete)+result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, R.string.Download_Error, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }
}
