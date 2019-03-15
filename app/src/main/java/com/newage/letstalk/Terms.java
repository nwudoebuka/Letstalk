package com.newage.letstalk;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Terms extends AppCompatActivity {

    Button Agree;
    TextView Dont_agree;
//    boolean doubleBackToExitPressedOnce = false;
//
//    @Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            moveTaskToBack(true);
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        }, 2000);
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Agree = (Button)findViewById(R.id.agree);
        Dont_agree = (TextView)findViewById(R.id.do_not_agree);

        Agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent intent = new Intent(Terms.this, Register.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }

        });

        Dont_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);

            }

        });

    }
}
