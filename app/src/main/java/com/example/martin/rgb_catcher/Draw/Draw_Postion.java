package com.example.martin.rgb_catcher.Draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by Martin on 08.08.2015.
 */
public class Draw_Postion extends View {
    public float x=-100;
    public float y=-100;
    final Paint barva = new Paint();
    final float scale;
    public boolean kresli;

    public Draw_Postion(Context context, float scale){
        super(context);
        barva.setAlpha(100);
        barva.setColor(Color.RED);
        barva.setStyle(Paint.Style.STROKE);
        this.scale = scale;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (kresli) {
            canvas.drawCircle(x, y, 2 * scale + 0.5f, barva);
        }
    }
}
