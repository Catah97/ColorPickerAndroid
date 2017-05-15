package com.example.martin.rgb_catcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


/**
 * Created by Martin on 8. 11. 2015.
 */
public class WelcomeScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.welcome_screen);
        final ImageView logoFirma = (ImageView) findViewById(R.id.imgViewAppForceONe);
        Animation fadeou = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.abc_fade_out);
        fadeou.setStartOffset(3000);
        fadeou.setDuration(300);
        logoFirma.startAnimation(fadeou);
        fadeou.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logoFirma.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ImageView img = (ImageView) findViewById(R.id.imageViewLogo);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.welcom_logo_animation);
        img.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(WelcomeScreen.this, ChooseScrenn_Main.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
