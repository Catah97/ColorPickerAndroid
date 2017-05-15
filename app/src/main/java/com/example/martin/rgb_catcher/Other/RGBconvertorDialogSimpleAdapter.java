package com.example.martin.rgb_catcher.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.example.martin.rgb_catcher.R;
import com.example.martin.rgb_catcher.RGB_seznam.CopyValue;

import java.util.List;
import java.util.Map;

/**
 * Created by Martin on 25.06.2016.
 */
public class RGBconvertorDialogSimpleAdapter extends SimpleAdapter {

    List<? extends Map<String, ?>> data;
    Context context;

    public RGBconvertorDialogSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.data = data;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.rgb_convert_listview,null);
        ImageView imgCopy = (ImageView) v.findViewById(R.id.imgCopy);
        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = (String) data.get(position).get("convert");
                CopyValue.copy(context,value);
            }
        });
        return super.getView(position, v, parent);
    }
}
