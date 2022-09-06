package com.example.dataentryandroidapp.admin.ui.profile;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.adapters.UserListAdapter;
import com.example.dataentryandroidapp.databinding.FragmentProfileBinding;
import com.example.dataentryandroidapp.listener.ItemClickListener;
import com.example.dataentryandroidapp.models.Data;
import com.example.dataentryandroidapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ImageView addImg;
    private TextView titleTxt;
    private RelativeLayout list_layout;
    private ProgressDialog pd;
    private LinearLayout form_layout;
    private EditText fnameTxt,emailTxt,passTxt,cpassTxt;
    private Button add_user;
    private String fullname,email,pass;
    private DatabaseReference db;
    private String id;
    private String status = "save";
    private List<User> userList = new ArrayList<>();
    private UserListAdapter adapter;
    private int pos;
    private ImageView backImg;
    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = root.findViewById(R.id.recylerView);
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        addImg = root.findViewById(R.id.add);
        titleTxt = root.findViewById(R.id.title);
        titleTxt.setText("Users");
        backImg = root.findViewById(R.id.back);
        list_layout = root.findViewById(R.id.user_list);
        form_layout = root.findViewById(R.id.form);
        fnameTxt = root.findViewById(R.id.fullname);
        emailTxt = root.findViewById(R.id.email);
        passTxt = root.findViewById(R.id.password);
        cpassTxt = root.findViewById(R.id.cpassword);
        add_user = root.findViewById(R.id.add_user);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_layout.setVisibility(View.GONE);
                form_layout.setVisibility(View.VISIBLE);
                titleTxt.setText("ADD USER");
            }
        });

        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_user.setClickable(false);
                if(validDataFields()) {
                    add_user.setClickable(true);
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage("Adding User");
                    pd.show();
                    if (status.equals("save")) {
                        saveUser();
                    }else {
                        updateData();
                    }
                }
                else{
                    add_user.setClickable(true);
                }
            }
        });

        getUserList();

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_layout.setVisibility(View.VISIBLE);
                form_layout.setVisibility(View.GONE);
                backImg.setVisibility(View.GONE);
                getUserList();
            }
        });

        return root;
    }

    private void updateData() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("fullname",fullname);
        hashMap.put("email",email);
        hashMap.put("pasword",pass);

        db.child(id).updateChildren(hashMap);

        pd.dismiss();
        getUserList();
    }

    private void saveUser() {
        String key = db.push().getKey();
        User user = new User(key,fullname,email,pass,"user",firebaseUser.getUid());
        db.child(key).setValue(user);
        fnameTxt.setText("");
        emailTxt.setText("");
        passTxt.setText("");
        cpassTxt.setText("");
        pd.dismiss();
        getUserList();
    }

    private void getUserList(){
        Query query = db.orderByChild("adminId").equalTo(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        User user = ds.getValue(User.class);
                        userList.add(user);
                    }
                    list_layout.setVisibility(View.VISIBLE);
                    form_layout.setVisibility(View.GONE);
                    titleTxt.setText("Users");
                    Collections.sort(userList, new Comparator<User>() {
                        @Override
                        public int compare(User user, User t1) {
                            return user.getFullname().compareToIgnoreCase(t1.getFullname());
                        }
                    });
                    adapter = new UserListAdapter(getActivity(),userList);
                    recyclerView.setAdapter(adapter);
                    adapter.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(int position, View view) {
                            id = userList.get(position).getId();
                            showPopup(position,id,view);
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void showPopup(int position, String id, View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.inflate(R.menu.menu_main);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_edit:

                        form_layout.setVisibility(View.VISIBLE);
                        list_layout.setVisibility(View.GONE);
                        backImg.setVisibility(View.VISIBLE);
                        titleTxt.setText("Update User");
                        status = "update";
                        checkExistData(id);
                        break;
                    case R.id.action_delete:
                        db.child(id).removeValue();
                        adapter.deleteItem(position);
                        break;
                }

                return true;
            }
        });
    }
    private void checkExistData(String id) {
        db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User model = snapshot.getValue(User.class);
                    fnameTxt.setText(model.getFullname());
                    emailTxt.setText(model.getEmail());
                    passTxt.setText(model.getPasword());
                    cpassTxt.setText(model.getPasword());
                    add_user.setText("Update");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean validDataFields() {
        fullname = fnameTxt.getText().toString();
        email = emailTxt.getText().toString();
        pass = passTxt.getText().toString();

        if (fullname.isEmpty()) {
            fnameTxt.setError("Input Fullname!");
            fnameTxt.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            emailTxt.setError("Input Email!");
            emailTxt.requestFocus();
            return false;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailTxt.setError("Please input valid email!");
            emailTxt.requestFocus();
            return false;
        }
        if (pass.isEmpty()) {
            passTxt.setError("Input password!");
            passTxt.requestFocus();
            return false;
        }
        if (cpassTxt.getText().toString().isEmpty()) {
            cpassTxt.setError("Input Confirm password!");
            cpassTxt.requestFocus();
            return false;
        }
        if (cpassTxt.getText().toString().equals(passTxt)){
            Toast.makeText(getActivity(),"Passwords are not matched!",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}