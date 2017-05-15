package com.example.martin.rgb_catcher.Other;

import android.os.AsyncTask;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Martin on 14.11.2015.
 */
public class Ads_Loading{

    private static AdRequest adRequest = new AdRequest.Builder()
            //.addTestDevice("DBE8817089771C7EFC42917BC2342732")
            .build();

    public static void Banner(AdView mAdView){
        mAdView.loadAd(adRequest);
    }
    public static void AdsWindows(InterstitialAd mInterstitialAd) {
        mInterstitialAd.setAdUnitId("ca-app-pub-6034316604830505/6018376475");

        mInterstitialAd.loadAd(adRequest);
    }
}
