package com.example.martin.rgb_catcher;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.martin.rgb_catcher.Other.Ads_Loading;
import com.example.martin.rgb_catcher.Other.Dialogs;
import com.example.martin.rgb_catcher.Other.Save_Load;
import com.google.android.gms.ads.AdView;

/**
 * Created by Martin on 9. 11. 2015.
 */
public class Setting extends Activity {

    AdView adView;
    private String system;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.arg1 == 1){
                Save_Load.SaveData(Setting.this,system,null,"justSave","mainValueModel.txt");
                ChooseScrenn_Main.system = system;
                finish();
            }
            if (msg.arg1 == 0){
                finish();
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                End();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            End();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void End(){
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int idView = radioGroup.getCheckedRadioButtonId();
        RadioButton btn = (RadioButton) findViewById(idView);
        system = btn.getText().toString().toLowerCase();
        if (!Allright()){
            Dialog dialog = Dialogs.WarringDialog(this, handler, getString(R.string.Settings_SaveAsk));
            dialog.show();
        }
        else {
            finish();
        }
    }

    private boolean Allright(){
        if (system.equals(ChooseScrenn_Main.system))
            return true;
        else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayHomeAsUpEnabled(true);                  /**zobrazí iconu**/
        getActionBar().setHomeButtonEnabled(true);                       /**nastaví iconu eneble pro click*/
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.setting);
        ChooseScrenn_Main.system = Save_Load.LoadedData(this,"mainValueModel.txt");

        adView = (AdView) findViewById(R.id.adView);
        Ads_Loading.Banner(adView);

        RadioButton RGB = (RadioButton) findViewById(R.id.radionBtnRGB);
        RadioButton HEX = (RadioButton) findViewById(R.id.radionBtnHEX);
        RadioButton CMYK = (RadioButton) findViewById(R.id.radionBtnCMYK);
        RadioButton HSV = (RadioButton) findViewById(R.id.radionBtnHSV);
        if (ChooseScrenn_Main.system.equals("rgb"))
            RGB.setChecked(true);
        else if (ChooseScrenn_Main.system.equals("hex"))
            HEX.setChecked(true);
        else if (ChooseScrenn_Main.system.equals("cmyk"))
            CMYK.setChecked(true);
        else if (ChooseScrenn_Main.system.equals("hsv"))
            HSV.setChecked(true);
    }

    public void Save(View v){
        setResult(RESULT_OK);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int idView = radioGroup.getCheckedRadioButtonId();
        RadioButton btn = (RadioButton) findViewById(idView);
        system = btn.getText().toString().toLowerCase();
        Save_Load.SaveData(Setting.this,system,null,"justSave","mainValueModel.txt");
        ChooseScrenn_Main.system = system;
        finish();
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
