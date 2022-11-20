package com.example.treecheck.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.treecheck.R;

public class Splash extends AppCompatActivity {
TextView build_number_tv,splash_tv;



        @Override
    protected void attachBaseContext(Context newBase) {

        //Implement this for api 28 and below
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
        }
        //Or implement this for api 29 and above
        else {
            super.attachBaseContext(newBase);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/IRANSans_Bold.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        build_number_tv =findViewById(R.id.splash_build_text);
        splash_tv =findViewById(R.id.splash_text);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splash_tv.setVisibility(View.VISIBLE);
                splash_tv.startAnimation(AnimationUtils.loadAnimation(Splash.this, R.anim.splah_text));
            }
        }, 1500);

        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = ((PackageInfo) pInfo).versionName;
            build_number_tv.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void check_login_information() {
        SharedPreferences pref= getSharedPreferences("sharedpref",MODE_PRIVATE);
        boolean admin_login=pref.getBoolean("admin_login",false);
        boolean bazres_login=pref.getBoolean("bazres_login",false);
        String admin_username=pref.getString("admin_username",null);
        if (admin_login) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                        Intent intent = new Intent(Splash.this, Admin_menu.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Splash.this.finish();
                }
            }, 5000);
        }
        else if (bazres_login){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash.this, Admin_menu.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Splash.this.finish();
                }
            }, 5000);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Splash.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Splash.this.finish();
                }
            },5000);
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (isNetworkConnected()){
            check_login_information();
        }else{
            AlertDialog.Builder alert=new AlertDialog.Builder(Splash.this);
            alert.setTitle(null) ;
            alert.setMessage("ارتباط شما با اینترنت برقرار نیست") ;
            alert.setPositiveButton("رفتن به تنظیمات wifi", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(Settings.ACTION_WIFI_SETTINGS) ;
                    startActivity(intent);
                }
            });
            alert.setNegativeButton("خروج", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alert.setCancelable(false) ;
            alert.show() ;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}