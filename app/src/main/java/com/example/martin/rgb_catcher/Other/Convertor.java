package com.example.martin.rgb_catcher.Other;

import android.app.ActionBar;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.martin.rgb_catcher.ChooseScrenn_Main;
import com.example.martin.rgb_catcher.Draw.Draw_RGB;
import com.example.martin.rgb_catcher.R;

import java.util.ArrayList;

/**
 * Created by Martin on 9. 11. 2015.
 */
public class Convertor {



    public static String decToHex(int dec) {
        String output = Integer.toHexString(dec);
        if (output.length() == 1)
            output = 0+output;
        return output;
    }

    /**nastaveni pro fotku*/

    public static int RELATIVELAYOUT_width;
    public static float c,m,y,k;
    public static float h,s,v;

    public static void PhotoMainScreenConvertor(RelativeLayout rlt,float scale){
        TextView txt1 = (TextView) rlt.findViewById(R.id.txt1);
        TextView txt2 = (TextView) rlt.findViewById(R.id.txt2);
        TextView txt3 = (TextView) rlt.findViewById(R.id.txt3);
        TextView txt4 = (TextView) rlt.findViewById(R.id.txt4);
        TextView txtValues1 = (TextView) rlt.findViewById(R.id.txtValue1);
        TextView txtValues2 = (TextView) rlt.findViewById(R.id.txtValue2);
        TextView txtValues3 = (TextView) rlt.findViewById(R.id.txtValue3);
        TextView txtValues4 = (TextView) rlt.findViewById(R.id.txtValue4);


        txt1.setVisibility(View.VISIBLE);
        txtValues1.setVisibility(View.VISIBLE);
        txt2.setVisibility(View.VISIBLE);
        txtValues2.setVisibility(View.VISIBLE);
        txt3.setVisibility(View.VISIBLE);
        txtValues3.setVisibility(View.VISIBLE);
        txt4.setVisibility(View.GONE);
        txtValues4.setVisibility(View.GONE);


        int currentWidth = 0;
        if (ChooseScrenn_Main.system.equals("rgb")) {
            txtValues1.setWidth((int)(25*scale));
            txtValues2.setWidth((int)(25*scale));
            txtValues3.setWidth((int)(25*scale));
            txt1.setText("R:");
            txtValues1.setText(String.valueOf(Draw_RGB.R));
            txt2.setText("G:");
            txtValues2.setText(String.valueOf(Draw_RGB.G));
            txt3.setText("B:");
            txtValues3.setText(String.valueOf(Draw_RGB.B));
            currentWidth = 171;
        }
        else if (ChooseScrenn_Main.system.equals("hex")) {
            txtValues1.setWidth((int) (48 * scale));
            txt2.setVisibility(View.GONE);
            txtValues2.setVisibility(View.GONE);
            txt3.setVisibility(View.GONE);
            txtValues3.setVisibility(View.GONE);
            txt4.setVisibility(View.GONE);
            txtValues4.setVisibility(View.GONE);
            txt1.setText("#");
            txtValues1.setText(decToHex(Draw_RGB.R).toUpperCase() + decToHex(Draw_RGB.G).toUpperCase() + decToHex(Draw_RGB.B).toUpperCase());
            currentWidth = 118;
        }
        else if (ChooseScrenn_Main.system.equals("cmyk")) {
            toCMYK(Draw_RGB.R,Draw_RGB.G,Draw_RGB.B,false);
            txtValues1.setWidth((int) (35 * scale));
            txtValues2.setWidth((int)(35*scale));
            txtValues3.setWidth((int)(35*scale));
            txtValues4.setWidth((int)(35*scale));
            txt4.setVisibility(View.VISIBLE);
            txtValues4.setVisibility(View.VISIBLE);
            txt1.setText("C:");
            txtValues1.setText(String.valueOf(c));
            txt2.setText("M:");
            txtValues2.setText(String.valueOf(m));
            txt3.setText("Y:");
            txtValues3.setText(String.valueOf(y));
            txt4.setText("K:");
            txtValues4.setText(String.valueOf(k));
            currentWidth = 250;
        }
        else if (ChooseScrenn_Main.system.equals("hsv")) {
            txtValues1.setWidth((int)(40*scale));
            txtValues2.setWidth((int)(43*scale));
            txtValues3.setWidth((int)(45*scale));
            toHSV(Draw_RGB.R,Draw_RGB.G,Draw_RGB.B);
            txt1.setText("H:");
            txtValues1.setText(String.valueOf(h)+"°");
            txt2.setText("S:");
            txtValues2.setText(String.valueOf(s)+"%");
            txt3.setText("V:");
            txtValues3.setText(String.valueOf(v)+"%");
            currentWidth = 225;
        }
        rlt.getLayoutParams().width = (int)(currentWidth*scale);
    }

    /**nastaveni pro ulozeni*/
    public static String SaveDialogConvertor(){
        String s= "";
        if (ChooseScrenn_Main.system.equals("rgb"))
            s = "R:" + Draw_RGB.R + "  G:" + Draw_RGB.G + "  B:" + Draw_RGB.B;
        else if (ChooseScrenn_Main.system.equals("hex"))
            s = "#"+ decToHex(Draw_RGB.R).toUpperCase()+ decToHex(Draw_RGB.G).toUpperCase() +decToHex(Draw_RGB.B).toUpperCase();
        else if (ChooseScrenn_Main.system.equals("cmyk"))
            s = toCMYK(Draw_RGB.R,Draw_RGB.G,Draw_RGB.B,true);
        else if (ChooseScrenn_Main.system.equals("hsv"))
            s = toHSV(Draw_RGB.R,Draw_RGB.G,Draw_RGB.B);
        return s;
    }

    public static String toHTML(int red,int green,int blue){
        String s = "#"+ decToHex(red).toUpperCase()+ decToHex(green).toUpperCase() +decToHex(blue).toUpperCase();
        return s;
    }
    public static String toCMYK(float red,float green,float blue,boolean newLine){
        /**new line urcuje zda ma byt vytvorena nova radka protoze convert je moc dlouhy*/

        c = 1 - (red/255);
        m = 1 - (green/255);
        y = 1 - (blue/255);

        float minCMY = Math.min(c,
                Math.min(m,y));
        c = (c - minCMY) / (1 - minCMY) ;
        m = (m - minCMY) / (1 - minCMY) ;
        y = (y - minCMY) / (1 - minCMY) ;
        k = minCMY;

        /**zaokrouhleni*/
        c = c*1000;
        c = Math.round(c);
        c = c/1000;
        m = m*1000;
        m = Math.round(m);
        m = m/1000;
        y = y*1000;
        y = Math.round(y);
        y = y/1000;
        k = k*1000;
        k = Math.round(k);
        k = k/1000;
        String output;
        if (newLine)
            output = "C:"+c+"  M:"+m+"\nY:"+y+"  K:"+k;
        else
            output = "C:"+c+"  M:"+m+"  Y:"+y+"  K:"+k;
        return output;
    }
    public static String toHSV(float r,float g,float b){

        r = r/255;
        g = g/255;
        b = b /255;

        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r,Math.min(g,b));

        /**h*/
        h = 0;
        if (max == min)
            h =0;
        else if (max == r && g >= b)
            h = (int) (60 * ((g-b)/(max-min))+0);
        else if (max == r && g<b)
            h = (int)  (60 * ((g-b)/(max-min))+360);
        else if (max == g)
            h = (int)  (60 * ((b-r)/(max-min))+120);
        else if (max == b)
            h = (int)  (60 * ((r-g)/(max-min))+240);
        /**s*/
        s = 0;
        if (max == 0)
            s = 0;
        else
            s =  1-(min/max);
        /**v*/
        v =  max;

        /**zaokrouhlovani*/
        h = Math.round(h);
        s = s*1000;
        s = Math.round(s);
        s = s/10;
        v = v*1000;
        v = Math.round(v);
        v = v/10;
        String output = "H:"+h+"°  S:"+s+"%  V:"+v+"%";
        return output;

    }
}
