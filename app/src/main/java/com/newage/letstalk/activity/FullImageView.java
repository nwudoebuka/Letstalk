package com.newage.letstalk.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.newage.letstalk.R;

import androidx.appcompat.app.AppCompatActivity;

public class FullImageView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image_view);

        LinearLayout linearLayout = findViewById(R.id.container);

        String path = getIntent().getStringExtra("image");

        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(imageView).load(path)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_broken_image)
                .into(imageView);
        linearLayout.addView(imageView);
    }
}