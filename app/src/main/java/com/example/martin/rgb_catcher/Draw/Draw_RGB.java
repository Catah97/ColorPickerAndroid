package com.example.martin.rgb_catcher.Draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Martin on 30.6.2015.
 */
public class Draw_RGB extends View {

    //public static boolean save;
    public static int R=255;
    public static int G=255;
    public static int B=255;
    private final static Paint textPen = new Paint();
    private final static Paint textBackground = new Paint();
    final float scale;

    public Draw_RGB(Context context) {
        super(context);
        textPen.setColor(Color.WHITE);
        textPen.setTextSize(15);
        textBackground.setStyle(Paint.Style.FILL);
        textBackground.setARGB(50, 0, 0, 0);
        scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawARGB(255, R, G, B);
        /*if (!save) {
            String text = "R: " + R + ",G: " + G + ",B: " + B;
            Rect bounds = new Rect();
            textPen.getTextBounds(text, 0, text.length(), bounds);
            int x = (canvas.getWidth() / 2) - (bounds.width() / 2);
            int y = (canvas.getHeight() / 2) - (bounds.height() / 2);
            int pridanaValue = (int) (4 * scale + 0.5f);                                           /**hodnota která zvetši rect aby nebyl nalapenej na text
            canvas.drawRect(x - pridanaValue, y-pridanaValue, (canvas.getWidth() / 2 + bounds.width() / 2) + pridanaValue, ((canvas.getHeight() / 2) + (bounds.height() / 2))+pridanaValue, textBackground);
            y = (int) ((canvas.getHeight() / 2)  - ((textPen.descent() + textPen.ascent()) / 2));
            canvas.drawText(text, x, y, textPen);
        }*/
    }
}

