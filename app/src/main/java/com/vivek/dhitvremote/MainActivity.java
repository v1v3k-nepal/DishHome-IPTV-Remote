package com.vivek.dhitvremote;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    private ConsumerIrManager irManager;
    private WebAppInterface webAppInterface; // Declare an instance of WebAppInterface
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        webAppInterface = new WebAppInterface(); // Initialize the WebAppInterface instance
        WebView webView = findViewById(R.id.webview);

        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        // Load the HTML file from assets folder
        webView.loadUrl("file:///android_asset/index.html");

        this.irManager = (ConsumerIrManager) getSystemService(CONSUMER_IR_SERVICE);

        //Ad Related Code Below
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        showBannerAds();
        loadInterstitialAds();
    }

    public void sendIRCommand(int frequency, int[] pattern) {
        Log.d("Remote", "frequency: " + frequency);
        Log.d("Remote", "pattern: " + Arrays.toString(pattern));

        if (irManager != null && irManager.hasIrEmitter()) {
            try {
                // Transmit the IR command using ConsumerIrManager
                irManager.transmit(frequency, pattern);
                webAppInterface.showToast("IR Command Sent");
            } catch (Exception e) {
                e.printStackTrace();
                webAppInterface.showToast("Error:" + e.getMessage());
            }
        } else {
            webAppInterface.showToast("Sorry, IR Emitter not Available in Your Device");
        }
    }


    private class WebAppInterface {
        @JavascriptInterface
        public void HandleButtonClick(int[] buttonPattern, boolean vibrationChoice) {
            int frequency = 38000;
            sendIRCommand(frequency, buttonPattern);
            if (vibrationChoice) {
                vibrateDevice();
            }
        }

        @JavascriptInterface
        public void vibrateDevice() {
            Vibrator vibrator = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, -1));
            } else {
                vibrator.vibrate(50);
            }
        }

        @JavascriptInterface
        public void showToast(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }

    }

    public void showBannerAds() {
        AdView mAdView = findViewById(R.id.banner_ad);
        AdRequest bannerAdRequest = new AdRequest.Builder().build();
        mAdView.loadAd(bannerAdRequest);
    }

    public  void loadInterstitialAds(){
        AdRequest interstitialAdRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-4911611475846102/9901498330", interstitialAdRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        showInterstitialAds();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }

    public void showInterstitialAds(){
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

}

