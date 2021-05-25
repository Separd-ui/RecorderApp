package com.example.recorderapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

public class SwipeDelete  extends ItemTouchHelper.SimpleCallback {
    private Adapter adapter;
    private Context context;
    private Drawable icon;
    private ColorDrawable background;

    public SwipeDelete(Adapter adapter,Context context) {
        super(0,ItemTouchHelper.LEFT);
        this.adapter=adapter;
        this.context=context;
        icon= ContextCompat.getDrawable(context,R.drawable.ic_delete);
        background=new ColorDrawable(Color.RED);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull  RecyclerView.ViewHolder viewHolder,  RecyclerView.ViewHolder target) {
        return false;
    }


    @Override
    public void onSwiped(@NonNull  RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public void onChildDraw(@NonNull  Canvas c, @NonNull  RecyclerView recyclerView, @NonNull  RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView=viewHolder.itemView;
        int cornerOffset=20, iconMargin=(itemView.getHeight()-icon.getIntrinsicHeight())/2;
        int iconTop=itemView.getTop()+(itemView.getHeight()-icon.getIntrinsicHeight())/2;
        int iconBot=iconTop+icon.getIntrinsicHeight();
        if(dX<0)
        {
            int iconLeft=itemView.getRight()-iconMargin-icon.getIntrinsicWidth();
            int iconRight=itemView.getRight()-iconMargin;
            icon.setBounds(iconLeft,iconTop,iconRight,iconBot);
            background.setBounds(itemView.getRight()+((int)dX)-cornerOffset,itemView.getTop(),itemView.getRight(),itemView.getBottom());
        }
        else
        {
            background.setBounds(0,0,0,0);
        }
        background.draw(c);
        icon.draw(c);
    }




}
