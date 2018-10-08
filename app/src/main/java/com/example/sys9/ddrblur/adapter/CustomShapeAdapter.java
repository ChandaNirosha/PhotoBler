package com.example.sys9.ddrblur.adapter;

/**
 * Created by welcome on 7/28/2017.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.res.ResourcesCompat;
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
import com.example.sys9.ddrblur.activity.TouchImage;
import com.example.sys9.ddrblur.R;
import com.example.sys9.ddrblur.activity.ShapeBlurActivity;
import com.example.sys9.ddrblur.activity.TouchImage;

public class CustomShapeAdapter extends Adapter<CustomShapeAdapter.ItemHolder> {
    Context context;
    RecyclerView mRecyclerView;

    public class ItemHolder extends ViewHolder {
        ImageButton shapeItem;

        public ItemHolder(View itemView) {
            super(itemView);
            shapeItem = (ImageButton) itemView.findViewById(R.id.shapeItem);
        }
    }

    public CustomShapeAdapter(Context context, RecyclerView mRecyclerView) {
        this.context = context;
        this.mRecyclerView = mRecyclerView;
    }

    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.shape_item_layout, parent, false));
    }

    public void onBindViewHolder(ItemHolder holder, final int position) {
        Glide.with(context).load(Integer.valueOf(ShapeBlurActivity.shapeButtonID[ShapeBlurActivity.categoryIndex][position])).fitCenter().into(holder.shapeItem);
        holder.shapeItem.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                boolean z = true;
                ShapeBlurActivity.imageView.setImageResource(ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex][position]);
                if (!ShapeBlurActivity.imageView.hover) {
                    ShapeBlurActivity.imageView.hover = true;
                }
                if (ShapeBlurActivity.imageView.lastCatIndex == ShapeBlurActivity.categoryIndex && ShapeBlurActivity.imageView.lastPosIndex == position) {
                    if (ShapeBlurActivity.imageView.bool) {
                        ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader2);
                        ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapClear);
                    } else {
                        ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader1);
                        ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapBlur);
                    }
                    TouchImage touchImage = ShapeBlurActivity.imageView;
                    if (ShapeBlurActivity.imageView.bool) {
                        z = false;
                    }
                    touchImage.bool = z;
                    ShapeBlurActivity.imageView.invalidate();
                    return;
                }
                ShapeBlurActivity.imageView.paint.setShader(ShapeBlurActivity.imageView.shader1);
                ShapeBlurActivity.imageView.setImageBitmap(ShapeBlurActivity.bitmapBlur);
                ShapeBlurActivity.imageView.bool = true;
                ShapeBlurActivity.imageView.lastCatIndex = ShapeBlurActivity.categoryIndex;

                ShapeBlurActivity.imageView.lastPosIndex = position;
                CustomShapeAdapter.this.notifyDataSetChanged();
                ShapeBlurActivity.imageView.resetLast();
                ShapeBlurActivity.imageView.mask = BitmapFactory.decodeResource(CustomShapeAdapter.this.context.getResources(), ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex][position]);
                ShapeBlurActivity.imageView.spot = BitmapFactory.decodeResource(CustomShapeAdapter.this.context.getResources(), ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex][position]).copy(Config.ALPHA_8, true);
                ShapeBlurActivity.imageView.svgId = ShapeBlurActivity.shapeViewID[ShapeBlurActivity.categoryIndex][position];
                ShapeBlurActivity.imageView.wMask = ShapeBlurActivity.imageView.mask.getWidth();
                ShapeBlurActivity.imageView.mRotationDegree = 0.0f;
                ShapeBlurActivity.imageView.canvasMask = new Canvas(ShapeBlurActivity.imageView.maskContainer);
                ShapeBlurActivity.imageView.invalidate();
            }
        });
        if (ShapeBlurActivity.imageView.hover && ShapeBlurActivity.imageView.lastPosIndex == position) {
            Resources r = this.context.getResources();
            holder.shapeItem.setImageDrawable(new LayerDrawable(new Drawable[]{ResourcesCompat.getDrawable(r, ShapeBlurActivity.shapeButtonID[ShapeBlurActivity.categoryIndex][position], null), ResourcesCompat.getDrawable(r, R.drawable.hover1, null)}));
        }
    }

    public int getItemCount() {
        return ShapeBlurActivity.shapeID[ShapeBlurActivity.categoryIndex].length;
    }
}
