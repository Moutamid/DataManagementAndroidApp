package com.example.dataentryandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.listener.ItemClickListener;
import com.example.dataentryandroidapp.models.Data;
import com.example.dataentryandroidapp.models.User;

import java.util.List;

public class SelectedUserListAdapter extends RecyclerView.Adapter<SelectedUserListAdapter.UserListView>{

    private Context mContext;
    public List<Data> dataList;
    private ItemClickListener itemClickListener;

    public SelectedUserListAdapter(Context mContext, List<Data> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public UserListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.data_custom_layout,parent,false);
        return new UserListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListView holder, int position) {

        Data model = dataList.get(position);
        holder.nameTxt.setText(model.getFullname());
        holder.callImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);

                callIntent.setData(Uri.parse("tel:"+model.getPhone()));

                mContext.startActivity(callIntent);
            }
        });
    }

    public void deleteItem(int pos){
        dataList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeRemoved(pos,dataList.size());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class UserListView extends RecyclerView.ViewHolder{

        private TextView nameTxt;
        private ImageView menuImg;
        private ImageView callImg;

        public UserListView(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name);
            menuImg = itemView.findViewById(R.id.menu);
            callImg = itemView.findViewById(R.id.call);
            menuImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(getAdapterPosition(),view);
                    }
                }
            });
        }

    }
    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
