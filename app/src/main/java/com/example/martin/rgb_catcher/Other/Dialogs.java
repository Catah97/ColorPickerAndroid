package com.example.martin.rgb_catcher.Other;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.martin.rgb_catcher.ChooseScrenn_Main;
import com.example.martin.rgb_catcher.Draw.Draw_RGB;
import com.example.martin.rgb_catcher.Internet.Dowloanding;
import com.example.martin.rgb_catcher.Photo;
import com.example.martin.rgb_catcher.R;
import com.example.martin.rgb_catcher.RGB_seznam.CopyValue;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Martin on 30.6.2015.
 */
public class Dialogs {


    static Dowloanding connection;

    public static AlertDialog RGBChoices(Context context,ListView listView){
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(listView);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    public static AlertDialog RGBconvertorDialog(final Context context, LayoutInflater layoutInflater, final Handler handler, final ArrayList<HashMap<String, String>> list, final
                                                int positionInRGBseznam, String name, String colorModel, String mainColorText, int[] rgb){
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogTheme));
        View rootViewTitle = layoutInflater.inflate(R.layout.dialog_rgbconvertor_title,null);
        TextView title = (TextView) rootViewTitle.findViewById(R.id.txtTitle);
        title.setText(name);
        ImageButton exit = (ImageButton) rootViewTitle.findViewById(R.id.imgExit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.arg1 = 1;
                msg.arg2 = 666;
                handler.sendMessage(msg);
            }
        });


        View rootView = layoutInflater.inflate(R.layout.dialog_rgbconvertor,null);
        TextView mainColorModel = (TextView) rootView.findViewById(R.id.TxtColorModel);
        mainColorModel.setText(colorModel);
        final TextView textView = (TextView) rootView.findViewById(R.id.mainColor);
        textView.setText(mainColorText);
        ImageView imgCopy = (ImageView) rootView.findViewById(R.id.imgCopy);
        imgCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CopyValue.copy(context,textView.getText().toString());
            }
        });
        ImageView img = (ImageView) rootView.findViewById(R.id.imgColorExample);
        img.setBackgroundColor(Color.rgb(rgb[0],rgb[1],rgb[2]));
        ListView listView = (ListView) rootView.findViewById(R.id.listViewConvert);
        SimpleAdapter adapter = new RGBconvertorDialogSimpleAdapter(context,list,R.layout.rgb_convert_listview, new String[]{"convert", "system"}, new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String system = list.get(position).get("system");
                Bundle bundle = new Bundle();
                bundle.putString("system", system);
                bundle.putInt("position", positionInRGBseznam);
                Message msg = new Message();
                msg.arg1 = 1;
                msg.setData(bundle);
                handler.sendMessage(msg);

            }
        });
        builder.setCustomTitle(rootViewTitle);
        builder.setView(rootView);
        AlertDialog dialog = builder.create();
        return dialog;
    }

    private static int SaveCalled = 0;
    private final static String[] pole = new String[]{"RGB","HEX","CMYK","HSV"};

    public static AlertDialog SaveRGBDialog(LayoutInflater inflater, final Context context, final Handler handler){
        final Draw_RGB draw = new Draw_RGB(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View rootView = inflater.inflate(R.layout.dialog_save,null);
        final TextView text = (TextView) rootView.findViewById(R.id.txtRGB);
        final ImageView copy = (ImageView) rootView.findViewById(R.id.imgCopy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CopyValue.copy(context,text.getText().toString());
            }
        });
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(context,R.layout.dialog_save_spinner,pole);
        spinner.setAdapter(adapter);
        final String system = ChooseScrenn_Main.system;
        int position = 0;
        if (ChooseScrenn_Main.system.equals("rgb"))
            position = 0;
        else if (ChooseScrenn_Main.system.equals("hex"))
            position = 1;
        else if (ChooseScrenn_Main.system.equals("cmyk"))
            position = 2;
        else if (ChooseScrenn_Main.system.equals("hsv"))
            position = 3;
        spinner.setSelection(position);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        ChooseScrenn_Main.system = "rgb";
                        break;
                    case 1:
                        ChooseScrenn_Main.system = "hex";
                        break;
                    case 2:
                        ChooseScrenn_Main.system = "cmyk";
                        break;
                    case 3:
                        ChooseScrenn_Main.system = "hsv";
                        break;
                }
                text.setText(Convertor.SaveDialogConvertor());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SaveCalled = SaveCalled+1;



        text.setText(Convertor.SaveDialogConvertor());
        final EditText nameEditTXT = (EditText)rootView.findViewById(R.id.txtName);
        FrameLayout RGBlayout = (FrameLayout) rootView.findViewById(R.id.dialogRGBcolor);
        RGBlayout.removeAllViews();
        RGBlayout.addView(draw);
        builder.setView(rootView);
        builder.setPositiveButton(R.string.SAVE, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditTXT.getText().toString();
                if (name.length() == 0) {
                    Message zprava = new Message();
                    zprava.arg1 = 100;
                    Bundle jmenoBundle = new Bundle();
                    jmenoBundle.putString("name", context.getString(R.string.SaveDialog_NothingNAME));
                    zprava.setData(jmenoBundle);
                    handler.sendMessage(zprava);
                }
                else if (name.contains(";"))
                    Toast.makeText(context, R.string.SaveDialogError,Toast.LENGTH_LONG).show();
                else {
                    Message zprava = new Message();
                    zprava.arg1 = 100;
                    Bundle jmenoBundle = new Bundle();
                    Log.e("nameText", name);
                    jmenoBundle.putString("name", name);
                    zprava.setData(jmenoBundle);
                    handler.sendMessage(zprava);
                }
                if (Photo.interstitialAd.isLoaded() && SaveCalled ==3){
                    Photo.interstitialAd.show();
                }
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ChooseScrenn_Main.system = system;
            }
        });
        return dialog;
    }
    public static AlertDialog GetUrld(final Context context, final String path, final Handler handler,String URLadress){
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogTheme));
        builder.setTitle(R.string.GetURL_EnterAddres);
        final EditText urlAdres = new EditText(context);
        urlAdres.setText(URLadress);
        builder.setView(urlAdres);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String urlAdresa = urlAdres.getText().toString();
                if (urlAdresa.length() != 0) {
                    connection = new Dowloanding(context,path,urlAdresa,Dowloading(context),handler);
                    connection.execute();

                } else
                    Toast.makeText(context, "You have not entered adress.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }
    public static ProgressDialog Dowloading(final Context context){
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.Dowlanding));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMax(0);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                connection.cancel(true);
            }
        });
        return mProgressDialog;
    }
    public static Dialog WaitingDialog(final Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        ImageView loding = (ImageView) dialog.findViewById(R.id.imgSearLoading);
        Animation anim_logo = AnimationUtils.loadAnimation(context, R.anim.loading_rotation);
        loding.startAnimation(anim_logo);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);    /**nastavi dialog tak ze se nevypne pokod je kliknuto mimo nej*/
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }
    public static AlertDialog WarringDialog (Context context, final Handler handler,String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.Warring);
        builder.setMessage(text);
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.arg1 = 1;
                handler.sendMessage(msg);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                msg.arg1 = 0;
                handler.sendMessage(msg);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
