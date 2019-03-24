package com.newage.letstalk.activity;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.newage.letstalk.R;

public class Terms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        Button mAgree = findViewById(R.id.agree);
        TextView mDontAgree = findViewById(R.id.do_not_agree);

        mAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(Terms.this, Register.class));
                //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                successful();
            }

        });

        mDontAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                //homeIntent.addCategory(Intent.CATEGORY_HOME);
                //homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //startActivity(homeIntent);

                failed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        failed();
    }

    public void successful(){
        Intent intent = new Intent();
        //intent.putExtra("result", "Transaction successful...");
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void failed(){
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }
}