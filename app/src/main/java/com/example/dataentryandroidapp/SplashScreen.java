package com.example.dataentryandroidapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.dataentryandroidapp.AccountsRegistrationScreen.Login;
import com.example.dataentryandroidapp.AccountsRegistrationScreen.LoginVersions;
import com.example.dataentryandroidapp.adapters.SlideViewPagerAdapter;
import com.example.dataentryandroidapp.listener.ItemClickListener;

public class SplashScreen extends AppCompatActivity {

    public static ViewPager viewPager;
    SlideViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        viewPager=findViewById(R.id.viewpager);
        adapter=new SlideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (position < 2) {
                    viewPager.setCurrentItem(position + 1);
                }else{
                    Intent intent=new Intent(SplashScreen.this, LoginVersions.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        if(isOpenAlread())
        {
            Intent intent=new Intent(SplashScreen.this, LoginVersions.class);
            startActivity(intent);
            finish();
        }
        else
        {
            SharedPreferences.Editor editor =getSharedPreferences("slide", MODE_PRIVATE).edit();
            editor.putBoolean("slide",true);
            editor.commit();
        }
    }

    private boolean isOpenAlread() {
        SharedPreferences sharedPreferences=getSharedPreferences("slide", MODE_PRIVATE);
        boolean result= sharedPreferences.getBoolean("slide", false);
        return result;
    }
}