package com.musicind.dukamoja.models;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.musicind.dukamoja.adapter.DrawerAdapter;

import androidx.annotation.NonNull;

public class SpaceItem extends  DrawerItem<SpaceItem.ViewHolder>{

    private  int spaceDp;

    public SpaceItem(int spaceDp){
        this.spaceDp = spaceDp;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        View v = new View(context);
        int height = (int) (context.getResources().getDisplayMetrics().density*spaceDp);
        v.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        ));
        return new ViewHolder(v);
   }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {

    }

    public class ViewHolder extends DrawerAdapter.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
