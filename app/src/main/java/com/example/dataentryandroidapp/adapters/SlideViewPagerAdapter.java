package com.example.dataentryandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.dataentryandroidapp.AccountsRegistrationScreen.Login;
import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.SplashScreen;
import com.example.dataentryandroidapp.listener.ItemClickListener;


public class SlideViewPagerAdapter extends PagerAdapter {

    Context ctx;
    private ImageView logo1;
    private TextView title;
    private ItemClickListener itemClickListener;

    public SlideViewPagerAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater= (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide_screen,container,false);
        logo1 =view.findViewById(R.id.logo1);

        title =view.findViewById(R.id.titles);
        Button btnGetStarted =view.findViewById(R.id.started);

        btnGetStarted.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null){
                    itemClickListener.onItemClick(position,view);
                }
            }
        });
        nextSplash(position);
        container.addView(view);
        return view;
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    private void nextSplash(int position) {
        switch (position)
        {
            case 0:
                logo1.setImageResource(R.drawable.splash1);

                title.setText("ADD DATA");
                //back.setVisibility(View.GONE);
                break;

            case 1:
                logo1.setImageResource(R.drawable.splash2);

                title.setText("CALL DATA");
                //back.setVisibility(View.INVISIBLE);
                //  next.setVisibility(View.INVISIBLE);
                break;

            case 2:
                logo1.setImageResource(R.drawable.splash3);

                title.setText("BE FAMILY WITH THEM");
                // back.setVisibility(View.INVISIBLE);
                //next.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
