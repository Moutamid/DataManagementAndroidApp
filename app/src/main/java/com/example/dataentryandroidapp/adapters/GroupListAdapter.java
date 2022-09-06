package com.example.dataentryandroidapp.adapters;

import android.content.Context;
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
import com.example.dataentryandroidapp.listener.ItemLongClickListener;
import com.example.dataentryandroidapp.models.Group;
import com.example.dataentryandroidapp.models.User;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupsViewHolder>{

    private Context mContext;
    public List<Group> users;
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    public GroupListAdapter(Context mContext, List<Group> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_custom_layout,parent,false);
        return new GroupsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupsViewHolder holder, int position) {

        Group model = users.get(position);
        holder.nameTxt.setText(model.getName());

    }
    public void deleteItem(int pos){
        users.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeRemoved(pos,users.size());
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        private TextView nameTxt;
        private ImageView menuImg;

        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name);
            menuImg = itemView.findViewById(R.id.menu);
            menuImg.setOnCreateContextMenuListener(this);
            menuImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemLongClickListener != null){
                        itemLongClickListener.onItemLongClick(getAdapterPosition(),view);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(getAdapterPosition(),view);
                    }
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {

            menu.add(0,v.getId(),getAdapterPosition(),"Edit");
            menu.add(0,v.getId(),getAdapterPosition(),"Delete");
        }
    }

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }
}
