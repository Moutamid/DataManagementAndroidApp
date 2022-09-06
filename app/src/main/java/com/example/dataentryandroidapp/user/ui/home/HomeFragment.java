package com.example.dataentryandroidapp.user.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataentryandroidapp.AccountsRegistrationScreen.LoginVersions;
import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.SharedPreferencesManager;
import com.example.dataentryandroidapp.ViewDataActivity;
import com.example.dataentryandroidapp.adapters.DataListAdapter;
import com.example.dataentryandroidapp.databinding.FragmentUserHomeBinding;
import com.example.dataentryandroidapp.listener.ItemClickListener;
import com.example.dataentryandroidapp.listener.ItemLongClickListener;
import com.example.dataentryandroidapp.models.Data;
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


public class HomeFragment extends Fragment {

    private FragmentUserHomeBinding binding;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private ImageView addImg;
    private TextView titleTxt;
    private RelativeLayout list_layout;
    private ProgressDialog pd;
    private LinearLayout form_layout;
    private EditText fnameTxt,phoneTxt,locationTxt,noteTxt,searchTxt;
    private TextView searchBtn;
    private Button add_data;
    private String fullname,phone,location,notes;
    private DatabaseReference db;
    private List<Data> dataList = new ArrayList<>();
    private String id;
    private int pos;
    private ImageView logoutBtn,backImg;
    private String status = "save";
    private DataListAdapter adapter;
    private SharedPreferencesManager prefs;
    private String adminId = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        prefs = new SharedPreferencesManager(getActivity());
        recyclerView = root.findViewById(R.id.recylerView);
        manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        addImg = root.findViewById(R.id.add);
        titleTxt = root.findViewById(R.id.title);
        titleTxt.setText("DATA");
        list_layout = root.findViewById(R.id.data_list);
        form_layout = root.findViewById(R.id.form);
        fnameTxt = root.findViewById(R.id.fullname);
        phoneTxt = root.findViewById(R.id.phone);
        locationTxt = root.findViewById(R.id.location);
        noteTxt = root.findViewById(R.id.notes);
        add_data = root.findViewById(R.id.add_user);
        searchBtn = root.findViewById(R.id.search);
        searchTxt = root.findViewById(R.id.searchTxt);
        backImg = root.findViewById(R.id.back);
        logoutBtn = root.findViewById(R.id.logout);
        adminId = prefs.retrieveString("adminId","");
        db = FirebaseDatabase.getInstance().getReference().child("Data").child(adminId);
        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_layout.setVisibility(View.GONE);
                form_layout.setVisibility(View.VISIBLE);
                titleTxt.setText("ADD DATA");
            }
        });

        add_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_data.setClickable(false);
                if(validDataFields()) {
                    add_data.setClickable(true);
                    pd = new ProgressDialog(getActivity());
                    pd.setMessage("Adding data");
                    pd.show();
                    if (status.equals("save")) {
                        saveData();
                    }else {
                        updateData();
                    }
                }
                else{
                    add_data.setClickable(true);
                }
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchTxt.getText().toString().isEmpty()){
                    adapter.getFilter().filter(searchTxt.getText().toString());
                }else {
                    getDataList();
                }

            }
        });
        getDataList();

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_layout.setVisibility(View.VISIBLE);
                form_layout.setVisibility(View.GONE);
                backImg.setVisibility(View.GONE);
                logoutBtn.setVisibility(View.VISIBLE);
                getDataList();
            }
        });
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.storeString("type","");
                startActivity(new Intent(getActivity(), LoginVersions.class));
                getActivity().finish();
            }
        });
        return root;
    }


    private void saveData() {
        String key = db.push().getKey();
        Data data = new Data(key,fullname,phone,location,notes);
        db.child(key).setValue(data);
        fnameTxt.setText("");
        phoneTxt.setText("");
        locationTxt.setText("");
        noteTxt.setText("");
        pd.dismiss();
        getDataList();
    }

    private void getDataList() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    dataList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Data data = ds.getValue(Data.class);
                        dataList.add(data);
                    }
                    //pd.dismiss();
                    list_layout.setVisibility(View.VISIBLE);
                    form_layout.setVisibility(View.GONE);
                    titleTxt.setText("TOTAL DATA: "+ snapshot.getChildrenCount());
                    Collections.sort(dataList, new Comparator<Data>() {
                        @Override
                        public int compare(Data data, Data t1) {
                            return data.getFullname().compareToIgnoreCase(t1.getFullname());
                        }
                    });
                    adapter = new DataListAdapter(getActivity(),dataList);
                    recyclerView.setAdapter(adapter);
                    adapter.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(int position, View view) {
                            id = dataList.get(position).getId();
                            showPopup(id,view);
                        }
                    });
                    adapter.setItemLongClickListener(new ItemLongClickListener() {
                        @Override
                        public void onItemLongClick(int position, View view) {
                            Intent intent = new Intent(getActivity(), ViewDataActivity.class);
                            intent.putExtra("data",dataList.get(position));
                            intent.putExtra("type","user");
                            startActivity(intent);
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
    private void showPopup(String id, View view) {
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.inflate(R.menu.user_menu_main);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_edit:
                        form_layout.setVisibility(View.VISIBLE);
                        list_layout.setVisibility(View.GONE);
                        titleTxt.setText("Update Data");
                        backImg.setVisibility(View.VISIBLE);
                        logoutBtn.setVisibility(View.GONE);
                        status = "update";
                        checkExistData(id);
                        break;
                }

                return true;
            }
        });
    }

    private void updateData() {

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("fullname",fullname);
        hashMap.put("phone",phone);
        hashMap.put("location",location);
        hashMap.put("notes",notes);

        db.child(id).updateChildren(hashMap);

        pd.dismiss();
        getDataList();
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Edit")){

            form_layout.setVisibility(View.VISIBLE);
            list_layout.setVisibility(View.GONE);
            titleTxt.setText("Update Data");
            backImg.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
            status = "update";
            checkExistData(id);
        }else if(item.getTitle().equals("Delete")){
            db.child(id).removeValue();
            deleteDataFromGroup(id);
            adapter.deleteItem(pos);
            getGroupMembersCount();

        }
        return super.onContextItemSelected(item);
    }
    private void deleteDataFromGroup(String id) {
        DatabaseReference groupDB = FirebaseDatabase.getInstance().getReference().child("Groups").child(adminId);
        groupDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        groupDB.child(ds.getKey().toString()).child("Members")
                                .child(id).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getGroupMembersCount() {
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    titleTxt.setText("TOTAL DATA: " + snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void checkExistData(String id) {
        db.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Data model = snapshot.getValue(Data.class);
                    fnameTxt.setText(model.getFullname());
                    phoneTxt.setText(model.getPhone());
                    locationTxt.setText(model.getLocation());
                    noteTxt.setText(model.getNotes());
                    add_data.setText("Update");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //Validate Email And Password
    public boolean validDataFields() {
        fullname = fnameTxt.getText().toString();
        phone = phoneTxt.getText().toString();
        location = locationTxt.getText().toString();
        notes = noteTxt.getText().toString();

        if (fullname.isEmpty()) {
            fnameTxt.setError("Input Fullname!");
            fnameTxt.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            phoneTxt.setError("Input phone!");
            phoneTxt.requestFocus();
            return false;

        }
        if (location.isEmpty()) {
            locationTxt.setError("Input Location!");
            locationTxt.requestFocus();
            return false;
        }

        if (notes.isEmpty()) {
            noteTxt.setError("Input Note!");
            noteTxt.requestFocus();
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