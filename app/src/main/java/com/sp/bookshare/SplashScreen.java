package com.sp.bookshare;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        image = findViewById(R.id.splash_image);

        MediaPlayer music = MediaPlayer.create(SplashScreen.this, R.raw.trimmedsplashmusic);
        music.start();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.bounce);

        image.startAnimation(animation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, com.sp.bookshare.Login.class));
                music.stop();
                finish();
            }
        }, 4000);

    }
}