package com.example.dataentryandroidapp.AccountsRegistrationScreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.SharedPreferencesManager;
import com.example.dataentryandroidapp.admin.AdminDashboard;
import com.example.dataentryandroidapp.models.Admin;
import com.example.dataentryandroidapp.models.User;
import com.example.dataentryandroidapp.user.UserDashboard;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private EditText emailInput,passInput;
    private Button loginBtn;
    private FirebaseAuth mAuth;
    private String email,password;
    private ProgressDialog pd;
    private FirebaseUser firebaseUser;
    private DatabaseReference db,adminDB;
    private String type;
    private TextView titleTxt;
    private SharedPreferencesManager manager;
    //private static String adminEmail = "admin123@gmail.com";
   // private static String  adminPassword = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailInput = (EditText)findViewById(R.id.email);
        passInput = (EditText)findViewById(R.id.password);
        loginBtn = (Button)findViewById(R.id.login);
        titleTxt = (TextView) findViewById(R.id.title);
        manager = new SharedPreferencesManager(Login.this);
        type = getIntent().getStringExtra("type");

        titleTxt.setText(type + " LOGIN");
        mAuth = FirebaseAuth.getInstance();
        // Staying signed in
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        adminDB = FirebaseDatabase.getInstance().getReference().child("Admins");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginBtn.setClickable(false);
                if(validEmailAndPassword()) {
                    loginBtn.setClickable(true);
                    pd = new ProgressDialog(Login.this);
                    pd.setMessage("Loading");
                    pd.show();
                    if (type.equals("admin")){
                        adminLogin();
                    }else {
                        userLogin();
                    }
                }
                else{
                    loginBtn.setClickable(true);
                }
            }
        });

    }

    private void userLogin() {
        Query query = db.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        if (user.getPasword().equals(password)){
                            // if(user.getEmail().equals(email) && user.getPasword().equals(password)) {
                            manager.storeString("userId", user.getId());
                            manager.storeString("adminId", user.getAdminId());
                          //  manager.storeString("email", user.getEmail());
                            manager.storeString("type", "user");
                            pd.dismiss();
                            startActivity(new Intent(getApplicationContext(),
                                    UserDashboard.class));
                            finish();
                        }else {
                            pd.dismiss();
                            Toast.makeText(Login.this, "Incorrect Email And Password! ", Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void adminLogin() {
      /*  if (email.equals(adminEmail) && password.equals(adminPassword)){
            manager.storeString("type", "admin");
            manager.storeString("email", adminEmail);
            pd.dismiss();
            startActivity(new Intent(getApplicationContext(),
                    AdminDashboard.class));
            finish();
        }else{
            pd.dismiss();
            Toast.makeText(Login.this,"Incorrect Email And Password! ",Toast.LENGTH_LONG).show();
        }*/
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    manager.storeString("type", "admin");
                   // manager.storeString("email", email);
                    pd.dismiss();
                    startActivity(new Intent(getApplicationContext(),
                            AdminDashboard.class));
                    finish();
                }else {
                    pd.dismiss();
                    Toast.makeText(Login.this,"Incorrect Email And Password! ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    //Validate Email And Password
    public boolean validEmailAndPassword() {
        email = emailInput.getText().toString();
        password = passInput.getText().toString();

        if (email.isEmpty()) {
            emailInput.setError("Input Email!");
            emailInput.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please input valid email!");
            emailInput.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            passInput.setError("Input password!");
            passInput.requestFocus();
            return false;

        }
        return true;
    }


}

