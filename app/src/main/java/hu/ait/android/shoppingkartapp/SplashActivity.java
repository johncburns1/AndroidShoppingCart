package hu.ait.android.shoppingkartapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * Created by johnc on 11/7/2017.
 */

public class SplashActivity extends Activity {

    public static final int DELAY_MILLIS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final LinearLayout splashContent = findViewById(R.id.splashContent);
        final Animation anim = AnimationUtils.loadAnimation(
                SplashActivity.this, R.anim.show_anim);

        splashContent.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentMain = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intentMain);
                finish();

            }
        }, DELAY_MILLIS);

    }
}
