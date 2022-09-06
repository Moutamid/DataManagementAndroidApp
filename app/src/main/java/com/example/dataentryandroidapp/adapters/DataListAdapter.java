package com.example.dataentryandroidapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dataentryandroidapp.R;
import com.example.dataentryandroidapp.listener.ItemClickListener;
import com.example.dataentryandroidapp.listener.ItemLongClickListener;
import com.example.dataentryandroidapp.models.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DataListAdapter extends RecyclerView.Adapter<DataListAdapter.DataListViewHolder> implements Filterable {

    private Context mContext;
    private List<Data> dataList;
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;

    public DataListAdapter(Context mContext, List<Data> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public DataListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.data_custom_layout,parent,false);
        return new DataListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataListViewHolder holder, int position) {

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

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void deleteItem(int pos){
        dataList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeRemoved(pos,dataList.size());
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            List<Data> filterList = new ArrayList<>();

            if (charSequence.toString().isEmpty()){
                filterList.addAll(dataList);
            }else {
                for (Data data : dataList){
                    if (data.getFullname().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterList.add(data);
                    }
                }
            }

            FilterResults filterResults = new FilterResults();
            filterResults.values = filterList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dataList.clear();
            dataList.addAll((Collection<? extends Data>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class DataListViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTxt;
        private ImageView menuImg,callImg;

        public DataListViewHolder(@NonNull View itemView) {
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemLongClickListener != null){
                        itemLongClickListener.onItemLongClick(getAdapterPosition(),view);
                    }
                }
            });
        }
    }

    public void setItemLongClickListener(ItemLongClickListener itemLongClickListener){
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

}
