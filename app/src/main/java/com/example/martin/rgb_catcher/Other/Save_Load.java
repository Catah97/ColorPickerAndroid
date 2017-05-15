package com.example.martin.rgb_catcher.Other;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.martin.rgb_catcher.Draw.Draw_RGB;
import com.example.martin.rgb_catcher.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


    public class Save_Load {
        public static void SaveData(Context context, String datainFile,String name, String parent,String file) {         /**Ukládá se ve formátu R,G,B;R,B,G,; atd.....*/
            Draw_RGB draw = new Draw_RGB(context);
            String readData = datainFile;
            try {
                FileOutputStream out = openFileOutput(context, file, Context.MODE_PRIVATE);
                OutputStreamWriter writer = new OutputStreamWriter(out);
                try {
                    if (parent.equals("saveRGB")) {
                        writer.write(draw.R + "," + draw.G + "," + draw.B + ";"+readData);
                        Toast.makeText(context, R.string.SAVED, Toast.LENGTH_SHORT).show();
                    }
                    else if (parent.equals("justSave"))
                        writer.write(readData);
                    else if (parent.equals("name"))
                        writer.write(name+";"+readData);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, R.string.SAVE_FAIL, Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(context, R.string.SAVE_FAIL, Toast.LENGTH_SHORT).show();
            }
        }

        public static FileOutputStream openFileOutput(Context context, String name, int mode)
                throws FileNotFoundException {
            return context.openFileOutput(name, mode);
        }

        public static String LoadedData(Context context,String file) {
            String final_data = "";
            try {
                FileInputStream fi = openFileInput(context, file);
                InputStreamReader in = new InputStreamReader(fi);
                char[] buffer = new char[128];
                int size;
                try {
                    while ((size = in.read(buffer)) > 0) {
                        String read_data = String.valueOf(buffer, 0, size);
                        final_data += read_data;
                        buffer = new char[128];
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("loadet", final_data);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return final_data;
        }

        private static FileInputStream openFileInput(Context context, String name)
                throws FileNotFoundException {
            return context.openFileInput(name);

        }
    }


