package com.example.dataentryandroidapp.AccountsRegistrationScreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.SharedPreferencesManager;
import com.example.dataentryandroidapp.admin.AdminDashboard;
import com.example.dataentryandroidapp.user.UserDashboard;

public class LoginVersions extends AppCompatActivity {


    private AppCompatButton adminBtn,userBtn;
    private SharedPreferencesManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_versions);
        adminBtn  =findViewById(R.id.admin);
        userBtn = findViewById(R.id.user);
        manager = new SharedPreferencesManager(LoginVersions.this);
        adminBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginVersions.this,Login.class);
                intent.putExtra("type","admin");
                startActivity(intent);
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginVersions.this,Login.class);
                intent.putExtra("type","user");
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        String type = manager.retrieveString("type","");
        if (!type.equals("")){
            if (type.equals("user")){
                startActivity(new Intent(getApplicationContext(),
                        UserDashboard.class));
                finish();
            }else {
                startActivity(new Intent(getApplicationContext(),
                        AdminDashboard.class));
                finish();
            }
        }
    }
}