package com.example.dataentryandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataentryandroidapp.R;
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
import java.util.HashMap;
import java.util.List;

public class MembersListAdapters extends RecyclerView.Adapter<MembersListAdapters.MembersViewHolder>{

    private Context mContext;
    private List<Data> dataList;
    private String id;
    private ItemClickListener itemClickListener;

    public MembersListAdapters(Context mContext, List<Data> dataList, String id) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.id = id;
    }

    @NonNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.update_group_custom_layout,parent,false);
        return new MembersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersViewHolder holder, int position) {
        Data model = dataList.get(position);
        holder.nameTxt.setText(model.getFullname());
        Log.d("id",id);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference groupDB = FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(user.getUid());
        checkParticipants(model.getId(),holder.checkBox,user.getUid());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", model.getId());
                    groupDB.child(id).child("Members").child(model.getId()).setValue(hashMap);
                }else {
                    groupDB.child(id).child("Members").child(model.getId()).removeValue();
                }
            }
        });

        holder.callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);

                callIntent.setData(Uri.parse("tel:"+model.getPhone()));

                mContext.startActivity(callIntent);
            }
        });
    }

    private void checkParticipants(String uId, CheckBox checkBox, String uid) {
        Query query = FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(uid).child(id).child("Members").orderByChild("id").equalTo(uId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                  //  for (DataSnapshot ds : snapshot.getChildren()){
                        checkBox.setChecked(true);
                   // }
                }else {
                    checkBox.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MembersViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTxt;
        private ImageView callImg;
        private CheckBox checkBox;

        public MembersViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name);
            checkBox = itemView.findViewById(R.id.checkBox);
            callImg = itemView.findViewById(R.id.call);

          /*  checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(getAdapterPosition(),view);
                    }
                }
            });*/
        }
    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
