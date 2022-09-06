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
import com.example.dataentryandroidapp.models.User;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListView>{

    private Context mContext;
    public List<User> users;
    private ItemClickListener itemClickListener;

    public UserListAdapter(Context mContext, List<User> users) {
        this.mContext = mContext;
        this.users = users;
    }

    @NonNull
    @Override
    public UserListView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_custom_layout,parent,false);
        return new UserListView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListView holder, int position) {

        User model = users.get(position);
        holder.nameTxt.setText(model.getFullname());

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

    public class UserListView extends RecyclerView.ViewHolder{

        private TextView nameTxt;
        private ImageView menuImg;

        public UserListView(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name);
            menuImg = itemView.findViewById(R.id.menu);
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
