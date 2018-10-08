package com.example.sys9.ddrblur.adapter;

/**
 * Created by welcome on 7/28/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.example.sys9.ddrblur.activity.ShapeBlurActivity;
import com.example.sys9.ddrblur.R;
import com.example.sys9.ddrblur.activity.ShapeBlurActivity;

public class CustomCategoryAdapter extends Adapter<CustomCategoryAdapter.ItemHolder> {
    RecyclerView catRecView;
    Context context;

    public class ItemHolder extends ViewHolder {
        ImageButton categoryItem;

        public ItemHolder(View itemView) {
            super(itemView);
            this.categoryItem = (ImageButton) itemView.findViewById(R.id.categoryItem);
        }
    }

    public CustomCategoryAdapter(Context context, RecyclerView catRecView) {
        this.context = context;
        this.catRecView = catRecView;
    }

    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(((LayoutInflater) this.context.getSystemService("layout_inflater")).inflate(R.layout.item_layout, parent, false));
    }

    public void onBindViewHolder(ItemHolder holder, final int position) {
        Glide.with(this.context).load(Integer.valueOf(ShapeBlurActivity.categoryID[position])).fitCenter().into(holder.categoryItem);
        holder.categoryItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ShapeBlurActivity.categoryIndex = 1 - position;
                ShapeBlurActivity.imageView.lastCatIndex = ShapeBlurActivity.categoryIndex;
                ShapeBlurActivity.shapeAdapter.notifyDataSetChanged();
                ShapeBlurActivity.shapeRecyclerView.scrollToPosition(ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex].length - 1);
                ShapeBlurActivity.imageView.lastPosIndex = -1;
                CustomCategoryAdapter.this.notifyDataSetChanged();
            }
        });
        if (ShapeBlurActivity.imageView.lastCatIndex == 1 - position) {
            Glide.with(this.context).load(Integer.valueOf(ShapeBlurActivity.selectedCategoryID[position])).fitCenter().into(holder.categoryItem);
        }
    }

    public int getItemCount() {
        return ShapeBlurActivity.categoryID.length;
    }
}
