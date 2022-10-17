package com.musicind.dukamoja.adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.musicind.dukamoja.models.DrawerItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {

    private List<DrawerItem> items;
    private Map<Class<? extends DrawerItem>, Integer> viewType;
    private SparseArray<DrawerItem> holderFactories;

    private OnItemSelectedListener listener;

    public DrawerAdapter( List<DrawerItem> items){
        this.items = items;
        this.viewType = new HashMap<>();
        this.holderFactories = new SparseArray<>();
        processViewType();
        
    }

    private void processViewType() {
        int type =0;
        for (DrawerItem item: items){

            if(!viewType.containsKey(item.getClass())){
                viewType.put(item.getClass(), type);
                holderFactories.put(type,item);
                type++;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       ViewHolder holder = holderFactories.get(viewType).createViewHolder(parent);
       holder.drawerAdapter =this;
       return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull DrawerAdapter.ViewHolder holder, int position) {
        items.get(position).bindViewHolder(holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType.get(items.get(position).getClass());
    }

    public void setSelected(int position) {
        DrawerItem newChecked = items.get(position);
        if (!newChecked.isSelectable()){
            return;
        }
        for (int i=0; i<items.size(); i++){
            DrawerItem item = items.get(i);
            if (item.isChecked()) {
                item.setIsChecked(false);
                notifyItemChanged(i);
                break;
            }
        }
        newChecked.setIsChecked(true);
        notifyItemChanged(position);

        if (listener != null){
            listener.onItemSelected(position);
        }

    }

    public void setListener(OnItemSelectedListener listener){
       this.listener = listener;
    }

    public  interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    public static abstract  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private DrawerAdapter drawerAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            drawerAdapter.setSelected(getAdapterPosition());
        }
    }
}
