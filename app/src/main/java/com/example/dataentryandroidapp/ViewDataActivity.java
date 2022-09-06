package com.example.dataentryandroidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dataentryandroidapp.admin.AdminDashboard;
import com.example.dataentryandroidapp.models.Data;
import com.example.dataentryandroidapp.user.UserDashboard;

public class ViewDataActivity extends AppCompatActivity {

    private ImageView backImg;
    private TextView nameTxt,phoneTxt,locationTxt,noteTxt;
    private Data data;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        backImg = findViewById(R.id.back);
        nameTxt = findViewById(R.id.fullname);
        phoneTxt = findViewById(R.id.phone);
        locationTxt = findViewById(R.id.location);
        noteTxt = findViewById(R.id.note);

        data = getIntent().getParcelableExtra("data");
        type = getIntent().getStringExtra("type");

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("user")){
                    startActivity(new Intent(ViewDataActivity.this, UserDashboard.class));
                    finish();
                }else {
                    startActivity(new Intent(ViewDataActivity.this, AdminDashboard.class));
                    finish();
                }
            }
        });

        nameTxt.setText(data.getFullname());
        locationTxt.setText(data.getLocation());
        phoneTxt.setText(data.getPhone());
        noteTxt.setText(data.getNotes());
    }
}