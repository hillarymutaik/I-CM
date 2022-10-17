package com.musicind.dukamoja.models;

import android.view.ViewGroup;

import com.musicind.dukamoja.adapter.DrawerAdapter;

public abstract class DrawerItem<T extends DrawerAdapter.ViewHolder> {

    protected boolean ischecked;
    public  abstract T createViewHolder(ViewGroup parent);

    public abstract void bindViewHolder(T holder);

    public DrawerItem<T>setIsChecked(boolean isChecked){
        this.ischecked = isChecked;
        return this;
    }
     public boolean isSelectable(){
        return true;
     }

     public boolean isChecked(){
        return ischecked;
     }

}