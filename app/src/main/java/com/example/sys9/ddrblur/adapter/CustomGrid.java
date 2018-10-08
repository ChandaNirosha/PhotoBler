package com.example.sys9.ddrblur.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.sys9.ddrblur.R;

/**
 * Created by welcome on 8/17/2017.
 */

class CustomGrid extends BaseAdapter {
    String[] web;
    Context context;
    int [] imageId;
    private static LayoutInflater inflater=null;
    public CustomGrid(MainActivity mainActivity, String[] osNameList, int[] osImages) {
        // TODO Auto-generated constructor stub
        web=osNameList;
        context=mainActivity;
        imageId=osImages;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return web.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView os_text;
        ImageView os_img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.grid_item, null);
        holder.os_text =(TextView) rowView.findViewById(R.id.grid_text);
        holder.os_img =(ImageView) rowView.findViewById(R.id.grid_image);

        holder.os_text.setText(web[position]);
        holder.os_img.setImageResource(imageId[position]);

        return rowView;
    }

}