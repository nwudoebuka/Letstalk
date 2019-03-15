package com.newage.letstalk;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Splashscreen extends AppCompatActivity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        StartAnimations();
    }


    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splashlogo);
        iv.clearAnimation();
        iv.startAnimation(anim);
        int secondsDelayed = 3;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                finish();
                startActivity(new Intent(Splashscreen.this, Register.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

            }
        }, secondsDelayed * 3000);

    }
}
