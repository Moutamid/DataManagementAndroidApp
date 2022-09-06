package com.example.dataentryandroidapp.user.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.SharedPreferencesManager;
import com.example.dataentryandroidapp.adapters.GroupListAdapter;
import com.example.dataentryandroidapp.adapters.SelectedUserListAdapter;
import com.example.dataentryandroidapp.databinding.FragmentGroupsBinding;
import com.example.dataentryandroidapp.databinding.FragmentUserGroupsBinding;
import com.example.dataentryandroidapp.listener.ItemClickListener;
import com.example.dataentryandroidapp.models.Data;
import com.example.dataentryandroidapp.models.Group;
import com.example.dataentryandroidapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GroupsFragment extends Fragment {

    private FragmentUserGroupsBinding binding;
    private RelativeLayout list_layout,members_layout;
    private TextView titleTxt,totalTxt;
    private RecyclerView recyclerView1,recyclerView2;
    private String groups;
    private List<Data> dataList = new ArrayList<>();
    private DatabaseReference groupDB,db;
    private String id;
    private List<Group> groupList = new ArrayList<>();
    private int pos;
    private ImageView backImg;
    private GroupListAdapter adapter;
    private String adminId = "";
    private SharedPreferencesManager prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUserGroupsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        prefs = new SharedPreferencesManager(getActivity());
        list_layout = root.findViewById(R.id.group_list);
        members_layout = root.findViewById(R.id.view_members);
        titleTxt = root.findViewById(R.id.title);
        backImg = root.findViewById(R.id.back);
        titleTxt.setText("Groups");
        totalTxt = root.findViewById(R.id.total);
        adminId = prefs.retrieveString("adminId","");
        db = FirebaseDatabase.getInstance().getReference().child("Data").child(adminId);
        groupDB = FirebaseDatabase.getInstance().getReference().child("Groups").child(adminId);
        recyclerView1 = root.findViewById(R.id.recylerView);
        recyclerView2 = root.findViewById(R.id.recylerView1);
        getGroupList();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_layout.setVisibility(View.VISIBLE);
                members_layout.setVisibility(View.GONE);
                backImg.setVisibility(View.GONE);
                getGroupList();
            }
        });
        return root;
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
                    totalTxt.setVisibility(View.GONE);
                    list_layout.setVisibility(View.VISIBLE);
                    members_layout.setVisibility(View.GONE);
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
                            list_layout.setVisibility(View.GONE);
                            members_layout.setVisibility(View.VISIBLE);
                            backImg.setVisibility(View.VISIBLE);
                            titleTxt.setText(groupList.get(position).getName());
                            getGroupMembersCount(id);
                            getUsersList(id);
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

    private void getUsersList(String id) {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView2.setLayoutManager(manager);
        groupDB.child(id).child("Members")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            dataList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                String key = dataSnapshot.getKey().toString();
                                Query query = db.orderByChild("id").equalTo(key);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        if (snapshot1.exists()){
                                            for (DataSnapshot ds : snapshot1.getChildren()){
                                                Data user = ds.getValue(Data.class);
                                                dataList.add(user);
                                            }
                                            totalTxt.setVisibility(View.VISIBLE);
                                            Collections.sort(dataList, new Comparator<Data>() {
                                                @Override
                                                public int compare(Data data, Data t1) {
                                                    return data.getFullname().compareToIgnoreCase(t1.getFullname());
                                                }
                                            });
                                            SelectedUserListAdapter adapter = new SelectedUserListAdapter(getActivity(),
                                                    dataList);
                                            recyclerView2.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
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