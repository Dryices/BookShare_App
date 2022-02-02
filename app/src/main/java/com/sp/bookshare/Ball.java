package com.sp.bookshare;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

public class Ball extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ball);

        View bouncingBallView = new BouncingBallView(this);
        setContentView(bouncingBallView);
        bouncingBallView.setBackgroundColor(Color.BLACK);
    }
}