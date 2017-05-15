package com.example.martin.rgb_catcher.RGB_seznam;

import android.content.Context;
import android.widget.Toast;

import com.example.martin.rgb_catcher.R;

/**
 * Created by Martin on 25.06.2016.
 */
public final class CopyValue {
     public static void copy(Context context,String text){
         int sdk = android.os.Build.VERSION.SDK_INT;
         if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
             android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
             clipboard.setText(text);
         } else {
             android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
             android.content.ClipData clip = android.content.ClipData.newPlainText(context.getString(R.string.app_name),text);
             clipboard.setPrimaryClip(clip);
         }
         Toast.makeText(context, R.string.copySucess,Toast.LENGTH_SHORT).show();
     }
}
