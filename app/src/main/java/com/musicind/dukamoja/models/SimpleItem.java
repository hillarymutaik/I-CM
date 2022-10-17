package com.musicind.dukamoja.models;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.musicind.dukamoja.R;
import com.musicind.dukamoja.adapter.DrawerAdapter;

import androidx.annotation.NonNull;

public class SimpleItem extends DrawerItem<SimpleItem.ViewHolder> {

    private int selectedItemIconInt;
    private int selectedItemTextInt;

    private int normalItemIconInt;
    private int normalItemTextInt;

    private Drawable icon;
    private String title;

    public SimpleItem(Drawable icon, String title){
        this.icon = icon;
        this.title = title;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_option,parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.title.setText(title);
        holder.icon.setImageDrawable(icon);

        holder.title.setTextColor(isChecked() ? selectedItemTextInt : normalItemTextInt);
        holder.icon.setColorFilter(ischecked ? selectedItemIconInt : normalItemIconInt);
    }

    public SimpleItem withSelectedTextInt(int selectedItemTextInt){
        this.selectedItemTextInt = selectedItemTextInt;
        return this;
    }

    public SimpleItem withSelectedIconInt(int selectedIconInt){
        this.selectedItemIconInt = selectedIconInt;
        return this;
    }

    public SimpleItem withTextInt(int normalItemTextInt){
        this.normalItemTextInt = normalItemTextInt;
        return this;
    }

    public SimpleItem withIconInt(int normalItemIconInt){
        this.normalItemIconInt = normalItemIconInt;
        return this;
    }

    static class ViewHolder extends DrawerAdapter.ViewHolder{

        private ImageView icon;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }
    }

}
