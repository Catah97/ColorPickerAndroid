package com.example.martin.rgb_catcher.RGB_seznam;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.martin.rgb_catcher.Other.Ads_Loading;
import com.example.martin.rgb_catcher.Other.Convertor;
import com.example.martin.rgb_catcher.Other.Dialogs;
import com.example.martin.rgb_catcher.R;
import com.example.martin.rgb_catcher.Other.Save_Load;
import com.example.martin.rgb_catcher.Setting;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Martin on 24. 6. 2015.
 */
public class RGB_Seznam extends Activity {

    boolean longclick= false;
    ListView dialogChoicesListView;
    Dialog choiceDialog,convertDialog;
    Vibrator vibrator;
    AdView adView;

    ArrayList<String> red = new ArrayList<>();
    ArrayList<String> green = new ArrayList<>();
    ArrayList<String> blue = new ArrayList<>();
    ArrayList<String> hodnoty = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> mainValues = new ArrayList<>();

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1){
                if (msg.arg2 == 666) {
                    convertDialog.dismiss();
                }
                else {
                    int position = msg.getData().getInt("position");
                    String system = msg.getData().getString("system").toLowerCase();
                    mainValues.set(position, system);
                    String data = "";
                    for (int i = 0; i < mainValues.size(); i++)
                        data = data + mainValues.get(i) + ";";
                    Save_Load.SaveData(RGB_Seznam.this, data, null, "justSave", "mainValues.txt");
                    LoadData();
                    SetAdapter();
                    Toast.makeText(RGB_Seznam.this, R.string.RGBseznamMainColorChange, Toast.LENGTH_SHORT).show();
                    convertDialog.dismiss();
                }

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu**/
        getActionBar().setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.rgb_seznam);
        setTitle(getString(R.string.PickedColor));
        /**nastavení ListView pro dialog*/
        final String[] chocesDialogValues = getResources().getStringArray(R.array.RGBseznamOptions);


        adView = (AdView) findViewById(R.id.adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adView.setVisibility(View.VISIBLE);
            }
        });
        Ads_Loading.Banner(adView);


        ArrayAdapter listAdapter = new ArrayAdapter(RGB_Seznam.this,android.R.layout.simple_list_item_1,chocesDialogValues);
        dialogChoicesListView = new ListView(RGB_Seznam.this);
        dialogChoicesListView.setAdapter(listAdapter);
        choiceDialog = Dialogs.RGBChoices(RGB_Seznam.this, dialogChoicesListView);
        vibrator = (Vibrator)getSystemService(this.VIBRATOR_SERVICE);
        LoadData();
    }
    private void SetAdapter(){
        TextView text = (TextView) findViewById(R.id.txtRGBfail);
        final ListView list = (ListView) findViewById(R.id.listRGB);
        if (hodnoty.size() == 0){
            text.setVisibility(View.VISIBLE);
            list.setVisibility(View.INVISIBLE);
        }
        else {
            text.setVisibility(View.INVISIBLE);
            list.setVisibility(View.VISIBLE);
            RGB_List_Adapter adapter = new RGB_List_Adapter(this,hodnoty,names,red,green,blue);
            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!longclick) {
                        String system = mainValues.get(position);
                        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
                        if (!system.equals("rgb")) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("convert", "R:" + red.get(position) + "  G:" + green.get(position) + "  B:" + blue.get(position));
                            map.put("system", "RGB");
                            list.add(map);
                        }
                        if (!system.equals("hex")) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("convert", Convertor.toHTML(Integer.parseInt(red.get(position)), Integer.parseInt(green.get(position)), Integer.parseInt(blue.get(position))));
                            map.put("system", "HEX");
                            list.add(map);
                        }
                        if (!system.equals("cmyk")) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("convert", Convertor.toCMYK(Integer.parseInt(red.get(position)), Integer.parseInt(green.get(position)), Integer.parseInt(blue.get(position)), false));
                            map.put("system", "CMYK");
                            list.add(map);
                        }
                        if (!system.equals("hsv")) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put("convert", Convertor.toHSV(Integer.parseInt(red.get(position)), Integer.parseInt(green.get(position)), Integer.parseInt(blue.get(position))));
                            map.put("system", "HSV");
                            list.add(map);
                        }
                        String mainText;
                        if (mainValues.get(position).equals("cmyk"))
                            mainText = Convertor.toCMYK(Integer.parseInt(red.get(position)), Integer.parseInt(green.get(position)), Integer.parseInt(blue.get(position)), true);
                        else
                            mainText = hodnoty.get(position);
                        convertDialog= Dialogs.RGBconvertorDialog(RGB_Seznam.this, getLayoutInflater(), handler, list,position,names.get(position),mainValues.get(position).toUpperCase(),mainText,
                                new int[]{Integer.parseInt(red.get(position)), Integer.parseInt(green.get(position)), Integer.parseInt(blue.get(position))});
                        convertDialog.show();
                        /**zneviditelněni divideru*/
                        int divierId = convertDialog.getContext().getResources()
                                .getIdentifier("android:id/titleDivider", null, null);
                        View divider = convertDialog.findViewById(divierId);
                        divider.setBackgroundColor(Color.argb(0, 255, 255, 255));
                    }
                }
            });
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    vibrator.vibrate(40);
                    longclick = true;
                    final int positionInList = position;
                    dialogChoicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(RGB_Seznam.this, R.style.DialogTheme));
                                builder.setTitle(R.string.RenameDialog);
                                final EditText text = new EditText(RGB_Seznam.this);
                                builder.setView(text);
                                builder.setPositiveButton(R.string.SAVE, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String name = text.getText().toString();
                                        if (name.length() == 0) {
                                            Toast.makeText(RGB_Seznam.this, R.string.SaveDialogValueEmpty, Toast.LENGTH_LONG).show();
                                        } else if (name.contains(";"))
                                            Toast.makeText(RGB_Seznam.this, R.string.SaveDialogError, Toast.LENGTH_LONG).show();
                                        else {
                                            names.set(positionInList, name);
                                            String data = "";
                                            for (int i = 0; i < names.size(); i++)
                                                data = data + names.get(i) + ";";
                                            Save_Load.SaveData(RGB_Seznam.this, data, null, "justSave", "name.txt");
                                            SetAdapter();
                                        }
                                    }
                                });
                                builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.create().show();
                            }
                            /**A user have choice delete*/
                            else if (position == 1)
                                Delete(positionInList);
                            choiceDialog.dismiss();
                        }
                    });
                    choiceDialog.show();
                    /**obcas se zobrazovali oba dialogi jak pro kratky klik tak pro dlouhy tato podminka to resi*/
                    choiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            longclick = false;
                        }
                    });
                    return false;
                }
            });
        }
    }
    private void Delete(int position){
        hodnoty.remove(position);
        red.remove(position);
        green.remove(position);
        blue.remove(position);
        names.remove(position);
        mainValues.remove(position);
        String data = "";
        for (int i = 0; i < hodnoty.size(); i++)
            data = data + red.get(i) + "," + green.get(i) + "," + blue.get(i) + ";";
        Save_Load.SaveData(RGB_Seznam.this, data, null, "justSave", "RGB_Seznam.txt");
        data = "";
        for (int i = 0; i < names.size(); i++)
            data = data + names.get(i) + ";";
        Save_Load.SaveData(RGB_Seznam.this, data, null, "justSave", "name.txt");
        data= "";
        for (int i = 0;i < mainValues.size();i++)
            data = data + mainValues.get(i) + ";";
        Save_Load.SaveData(RGB_Seznam.this, data, null, "justSave", "mainValues.txt");
        Toast.makeText(getApplicationContext(), R.string.RGBseznamDELETE, Toast.LENGTH_SHORT).show();
        SetAdapter();
    }
    private void LoadData() {
        red.clear();
        green.clear();
        blue.clear();
        hodnoty.clear();
        names.clear();
        mainValues.clear();
        String buffer = Save_Load.LoadedData(this,"name.txt");
        String dataName[] = buffer.split(";");
        for (String data:dataName) {
            names.add(data);
        }
        Log.e("NamesFinalData", buffer);
        buffer = Save_Load.LoadedData(this, "mainValues.txt");
        String main[] = buffer.split(";");
        for (String data: main) {
            mainValues.add(data);
        }

        buffer = Save_Load.LoadedData(this, "RGB_Seznam.txt");
        Log.e("RGBFinalData", buffer);
        if (buffer == "")
            SetAdapter();
        else {
            String data[] = buffer.split(";");
            for (int i = 0; i < data.length; i++) {
                String rozdeleno[] = data[i].split(",");
                red.add(rozdeleno[0]);
                green.add(rozdeleno[1]);
                blue.add(rozdeleno[2]);
                String s = null;
                if (mainValues.get(i).equals("rgb"))
                    s = "R:" + red.get(i) + "  G:" + green.get(i) + "  B:" + blue.get(i);
                else if (mainValues.get(i).equals("hex"))
                    s = Convertor.toHTML(Integer.parseInt(red.get(i)),Integer.parseInt(green.get(i)),Integer.parseInt(blue.get(i)));
                else if (mainValues.get(i).equals("cmyk"))
                    s = Convertor.toCMYK(Integer.parseInt(red.get(i)),Integer.parseInt(green.get(i)),Integer.parseInt(blue.get(i)),false);
                else if (mainValues.get(i).equals("hsv"))
                    s = Convertor.toHSV(Integer.parseInt(red.get(i)), Integer.parseInt(green.get(i)), Integer.parseInt(blue.get(i)));
                hodnoty.add(s);
            }
        }
        SetAdapter();
    }

    /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
