package com.nikhil.vippassscanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nikhil.vippassscanner.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Logo animation
        ImageView logo = findViewById(R.id.imageView2);
        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(logoAnimation);

        // Splash delay
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

            // Smooth transition
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            finish();
        }, 2500);

        TextView title = findViewById(R.id.tvTitle);
        TextView subtitle = findViewById(R.id.tvSubtitle);

        Animation textAnimation =
                AnimationUtils.loadAnimation(this, R.anim.text_animation);

//        title.postDelayed(() -> title.startAnimation(textAnimation), 700);
//
//        subtitle.postDelayed(() -> subtitle.startAnimation(textAnimation), 1000);
    }
}