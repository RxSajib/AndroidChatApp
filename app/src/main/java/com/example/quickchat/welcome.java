package com.example.quickchat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class welcome extends AppCompatActivity {

    private TextView Headtext, destext;
    private ImageView bacroundimage;
    private Button gobutton;
    private Typeface typeface1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        Headtext = findViewById(R.id.WelcomeTextID);
        destext = findViewById(R.id.desID);
        bacroundimage = findViewById(R.id.BackroundIageID);
        gobutton = findViewById(R.id.GoButtonID);

        typeface1 = Typeface.createFromAsset(getAssets(), "fonts/costomfonts.otf");
        Headtext.setTypeface(typeface1);

        bacroundimage.animate().scaleX(1.7F).scaleY(1.7F).setDuration(700);

        gobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(welcome.this, HomeActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
