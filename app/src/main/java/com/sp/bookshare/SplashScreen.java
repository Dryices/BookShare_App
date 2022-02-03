package com.sp.bookshare;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;


import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        MediaPlayer music = MediaPlayer.create(SplashScreen.this, R.raw.trimmedsplashmusic);
        music.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, com.sp.bookshare.Login.class));
                music.stop();
                finish();
            }
        },1000);

    }
}