package com.example.martin.rgb_catcher.RGB_seznam;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.martin.rgb_catcher.Other.Convertor;
import com.example.martin.rgb_catcher.R;

import java.util.ArrayList;

/**
 * Created by Martin on 25. 6. 2015.
 */
public class RGB_List_Adapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> hodnoty;
    private final ArrayList<String> names;
    private final ArrayList<String> red;
    private final ArrayList<String> green;
    private final ArrayList<String> blue;

    public RGB_List_Adapter(Context context, ArrayList hodnoty,ArrayList names, ArrayList red, ArrayList green, ArrayList blue) {
        super(context, R.layout.rgb_seznam_list_view,hodnoty);
        this.context = context;
        this.hodnoty = hodnoty;
        this.names = names;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.rgb_seznam_list_view, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.rgb);

        textView.setText(hodnoty.get(position));
        TextView name = (TextView)rowView.findViewById(R.id.name);
        name.setText(names.get(position));
        ImageView imgRGB = (ImageView) rowView.findViewById(R.id.RGB_FrameLayout);
        imgRGB.setBackgroundColor(Color.argb(255,Integer.parseInt(red.get(position)),Integer.parseInt(green.get(position)),Integer.parseInt(blue.get(position))));
        return rowView;
    }

}
