package com.example.dataentryandroidapp.user.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.SharedPreferencesManager;
import com.example.dataentryandroidapp.databinding.FragmentProfileBinding;
import com.example.dataentryandroidapp.databinding.FragmentUserProfileBinding;
import com.example.dataentryandroidapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class ProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    private EditText fnameTxt,emailTxt,passTxt,cpassTxt;
    private Button add_user;
    private String fullname,email,pass;
    private DatabaseReference db;
    String id;
    private SharedPreferencesManager manager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fnameTxt = root.findViewById(R.id.fullname);
        emailTxt = root.findViewById(R.id.email);
        passTxt = root.findViewById(R.id.password);
        cpassTxt = root.findViewById(R.id.cpassword);
        add_user = root.findViewById(R.id.update);
        manager = new SharedPreferencesManager(getActivity());
        id = manager.retrieveString("userId","");
        db = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        checkUserDetails();
        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData();
            }
        });
        return root;
    }

    private void updateData() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",id);
        hashMap.put("fullname",fnameTxt.getText().toString());
        hashMap.put("email",emailTxt.getText().toString());
        hashMap.put("pasword",passTxt.getText().toString());
        hashMap.put("type","user");
        db.updateChildren(hashMap);

    }

    private void checkUserDetails() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    fnameTxt.setText(model.getFullname());
                    emailTxt.setText(model.getEmail());
                    passTxt.setText(model.getPasword());
                    cpassTxt.setText(model.getPasword());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}