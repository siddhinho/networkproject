package br.com.roadmaps.networkproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(loadCookie()){
                    startActivity(new Intent(SplashScreenActivity.this,ListActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashScreenActivity.this,LoginActivity.class));
                    finish();
                }

            }
        }, 2000);
    }

    private boolean loadCookie(){
        boolean flag = false;
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        if (!myCookieStore.getCookies().isEmpty()) {
            if (myCookieStore.getCookies().get(0).getDomain().equals("teste-aula-ios.herokuapp.com")) {
                flag = true;
            }
        } else {
            flag = false;
        }
        return flag;
    }
}
