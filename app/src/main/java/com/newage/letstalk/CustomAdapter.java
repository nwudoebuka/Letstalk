package com.newage.letstalk;

/**
 * Created by Newage_android on 7/30/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
public class CustomAdapter extends BaseAdapter{
    String [] result;
    String grpname;
    Context context;
    DataAdapter dataadapter;
    int [] imageId;
    private static LayoutInflater inflater=null;
    public CustomAdapter(Activity mainActivity, String[] prgmNameList, int[] prgmImages) {
        // TODO Auto-generated constructor stub
        result=prgmNameList;
        context=mainActivity;
        imageId=prgmImages;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
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
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.list_row, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView1);
        holder.img=(ImageView) rowView.findViewById(R.id.profile_image);
        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);
        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(result[position].equalsIgnoreCase("Business Group")){
                    Intent mIntent = new Intent(context, Groupchat.class);
                    mIntent.putExtra("group", result[position]);
                    context.startActivity(mIntent);

                }
                if(result[position].equalsIgnoreCase("Friends Group")){

                    Intent mIntent = new Intent(context, Groupchat.class);
                    mIntent.putExtra("group", result[position]);
                    context.startActivity(mIntent);
                }
                if(result[position].equalsIgnoreCase("Family Group")){

                    Intent mIntent = new Intent(context, Groupchat.class);
                    mIntent.putExtra("group", result[position]);
                    context.startActivity(mIntent);
                }
                if(result[position].equalsIgnoreCase("Religion Group")){
                    Intent mIntent = new Intent(context, Groupchat.class);
                    mIntent.putExtra("group", result[position]);
                    context.startActivity(mIntent);
                }
                Toast.makeText(context, "You Clicked "+result[position], Toast.LENGTH_LONG).show();
            }
        });
        return rowView;
    }

}
