package com.example.dataentryandroidapp.admin.ui.groups;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.adapters.GroupListAdapter;
import com.example.dataentryandroidapp.adapters.MembersListAdapters;
import com.example.dataentryandroidapp.adapters.SelectedUserListAdapter;
import com.example.dataentryandroidapp.adapters.UserListAdapter;
import com.example.dataentryandroidapp.databinding.FragmentGroupsBinding;
import com.example.dataentryandroidapp.listener.ItemClickListener;
import com.example.dataentryandroidapp.listener.ItemLongClickListener;
import com.example.dataentryandroidapp.models.Data;
import com.example.dataentryandroidapp.models.Group;
import com.example.dataentryandroidapp.models.Members;
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


public class GroupsFragment extends Fragment {

    private FragmentGroupsBinding binding;
    private RelativeLayout list_layout,edit_group_layout,members_layout;
    private LinearLayout form_layout;
    private EditText groupTxt;
    private TextView titleTxt,totalTxt;
    private RecyclerView recyclerView1,recyclerView2,recyclerView3;
    private Button saveBtn,groupBtn,membersBtn,editBtn;
    private String groups;
    private List<Data> dataList = new ArrayList<>();
    private DatabaseReference groupDB,db;
    private List<Group> groupList = new ArrayList<>();
    private String id;
    private String status = "save";
    private int pos = 0;
    private GroupListAdapter adapter;
    private ImageView backImg;
    private SelectedUserListAdapter listAdapter;
    private boolean isChecked;
    List<Data> dataArrayList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        list_layout = root.findViewById(R.id.group_list);
        members_layout = root.findViewById(R.id.group_members);
        edit_group_layout = root.findViewById(R.id.edit_members);
        form_layout = root.findViewById(R.id.add_group);
        groupTxt = root.findViewById(R.id.group_name);
        titleTxt = root.findViewById(R.id.title);
        backImg = root.findViewById(R.id.back);
        titleTxt.setText("Groups");
        totalTxt = root.findViewById(R.id.total);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("Data").child(firebaseUser.getUid());
        groupDB = FirebaseDatabase.getInstance().getReference().child("Groups").child(firebaseUser.getUid());
        recyclerView1 = root.findViewById(R.id.recylerView);
        recyclerView2 = root.findViewById(R.id.recylerView1);
        recyclerView3 = root.findViewById(R.id.recylerView2);
        saveBtn = root.findViewById(R.id.add);
        groupBtn = root.findViewById(R.id.groupBtn);
        membersBtn = root.findViewById(R.id.add_member);
        editBtn = root.findViewById(R.id.update_member);

        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form_layout.setVisibility(View.VISIBLE);
                list_layout.setVisibility(View.GONE);
                edit_group_layout.setVisibility(View.GONE);
                members_layout.setVisibility(View.GONE);
                backImg.setVisibility(View.VISIBLE);
                totalTxt.setVisibility(View.GONE);
                titleTxt.setText("Add Group");
            }
        });
        getGroupList();
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groups = groupTxt.getText().toString();
                if (!groups.isEmpty()){
                    if (status.equals("save")) {
                        saveGroup();
                    }else {
                        updateGroup();
                    }
                }
            }
        });
        membersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form_layout.setVisibility(View.GONE);
                list_layout.setVisibility(View.GONE);
                edit_group_layout.setVisibility(View.VISIBLE);
                members_layout.setVisibility(View.GONE);
                totalTxt.setVisibility(View.VISIBLE);
                backImg.setVisibility(View.VISIBLE);
                getUsersList(id);
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form_layout.setVisibility(View.GONE);
                list_layout.setVisibility(View.VISIBLE);
                edit_group_layout.setVisibility(View.GONE);
                members_layout.setVisibility(View.GONE);
                totalTxt.setVisibility(View.GONE);
                getGroupList();
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroupList();
            }
        });
        return root;
    }

    private void updateGroup() {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("name",groups);

        groupDB.child(id).updateChildren(hashMap);
        getGroupList();
    }

    private void saveGroup() {
        String key = groupDB.push().getKey();
        Group group = new Group(key,groups);
        groupDB.child(key).setValue(group);
        getGroupList();
    }

    private void getGroupList() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView1.setLayoutManager(manager);
        groupDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    groupList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Group group = ds.getValue(Group.class);
                        groupList.add(group);
                    }
                    titleTxt.setText("Groups");
                    form_layout.setVisibility(View.GONE);
                    list_layout.setVisibility(View.VISIBLE);
                    edit_group_layout.setVisibility(View.GONE);
                    members_layout.setVisibility(View.GONE);
                    totalTxt.setVisibility(View.GONE);
                    backImg.setVisibility(View.GONE);
                    Collections.sort(groupList, new Comparator<Group>() {
                        @Override
                        public int compare(Group group, Group t1) {
                            return group.getName().compareToIgnoreCase(t1.getName());
                        }
                    });
                    adapter = new GroupListAdapter(getActivity(),groupList);
                    recyclerView1.setAdapter(adapter);

                    adapter.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onItemClick(int position, View view) {
                            id = groupList.get(position).getId();
                            pos = position;

                            getSelectedUsers(id,groupList.get(position).getName());
                           // getUsersList(id);
                        }
                    });
                    adapter.setItemLongClickListener(new ItemLongClickListener() {
                        @Override
                        public void onItemLongClick(int position, View view) {
                            id = groupList.get(position).getId();
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

    private void getSelectedUsers(String id, String name) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView2.setLayoutManager(manager);
        groupDB.child(id).child("Members")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            dataArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String key = dataSnapshot.getKey().toString();
                                Query query = db.orderByChild("id").equalTo(key);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        if (snapshot1.exists()){
                                            for (DataSnapshot ds : snapshot1.getChildren()){
                                                Data user = ds.getValue(Data.class);
                                                dataArrayList.add(user);
                                            }
                                            form_layout.setVisibility(View.GONE);
                                            list_layout.setVisibility(View.GONE);
                                            edit_group_layout.setVisibility(View.GONE);
                                            members_layout.setVisibility(View.VISIBLE);
                                            backImg.setVisibility(View.VISIBLE);
                                            totalTxt.setVisibility(View.VISIBLE);
                                            titleTxt.setText(name);
                                            totalTxt.setText("TOTAL: " + snapshot.getChildrenCount());

                                            Collections.sort(dataArrayList, new Comparator<Data>() {
                                                @Override
                                                public int compare(Data data, Data t1) {
                                                    return data.getFullname().compareToIgnoreCase(t1.getFullname());
                                                }
                                            });
                                            listAdapter = new SelectedUserListAdapter(getActivity(),
                                                    dataArrayList);
                                            recyclerView2.setAdapter(listAdapter);
                                            listAdapter.setItemClickListener(new ItemClickListener() {
                                                @Override
                                                public void onItemClick(int position, View view) {
                                                    String mId = dataArrayList.get(position).getId();
                                                    showRemovePopup(position,id,view,mId);
                                                }
                                            });
                                            listAdapter.notifyDataSetChanged();
                                        }else {
                                            int count = (int) snapshot.getChildrenCount()-1;
                                            totalTxt.setText("TOTAL: " + count);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }else {
                            dataArrayList.clear();
                            form_layout.setVisibility(View.GONE);
                            list_layout.setVisibility(View.GONE);
                            edit_group_layout.setVisibility(View.GONE);
                            members_layout.setVisibility(View.VISIBLE);
                            backImg.setVisibility(View.VISIBLE);
                        //    totalTxt.setVisibility(View.VISIBLE);
                            titleTxt.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void showRemovePopup(int position, String ids, View view, String mId) {
        PopupMenu popupMenu = new PopupMenu(getActivity(),view);
        popupMenu.inflate(R.menu.admin_menu_main);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_remove:
                        groupDB.child(ids).child("Members").child(mId).removeValue();
                        listAdapter.deleteItem(position);
                        getGroupMembersCount(id);
                        break;
                }

                return true;
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
                        edit_group_layout.setVisibility(View.GONE);
                        members_layout.setVisibility(View.GONE);
                        backImg.setVisibility(View.VISIBLE);
                        titleTxt.setText("Update Group");
                        totalTxt.setVisibility(View.GONE);
                        status = "update";
                        checkExistData(id);
                        break;
                    case R.id.action_delete:
                        groupDB.child(id).removeValue();
                        adapter.deleteItem(position);
                        break;
                }

                return true;
            }
        });
    }

    private void checkExistData(String id) {
        groupDB.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Group model = snapshot.getValue(Group.class);
                    groupTxt.setText(model.getName());
                    saveBtn.setText("Update");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsersList(String id) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView3.setLayoutManager(manager);
        //Query query = db.orderByChild("type").equalTo("user");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    dataList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Data user = ds.getValue(Data.class);
                        dataList.add(user);
                    }

                    getGroupMembersCount(id);
                    Collections.sort(dataList, new Comparator<Data>() {
                        @Override
                        public int compare(Data data, Data t1) {
                            return data.getFullname().compareToIgnoreCase(t1.getFullname());
                        }
                    });

                    MembersListAdapters adapter = new MembersListAdapters(getActivity(),dataList,id);
                    recyclerView3.setAdapter(adapter);

                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getGroupMembersCount(String id) {
        groupDB.child(id).child("Members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    totalTxt.setVisibility(View.VISIBLE);
                    totalTxt.setText("TOTAL: " + snapshot.getChildrenCount());
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