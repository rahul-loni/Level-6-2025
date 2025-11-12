package com.example.fitlife;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fitlife.Auth.SignupPage;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        TextView textView=findViewById(R.id.splash_txt);
        ImageView imageView=findViewById(R.id.splash_logo);


        Animation animation=AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        imageView.setAnimation(animation);
        textView.startAnimation(animation);

        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                sleep(5000);
                Intent intent=new Intent(Splash_Screen.this, MainActivity.class);
                startActivity(intent);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            }
        });
       thread.start();
    }
}