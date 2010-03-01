package com.mvw.hudson;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mvw.hudson.model.JobSummary;

public class JobAdapter extends ArrayAdapter<JobSummary> {
	private ArrayList<JobSummary> items;

	LayoutInflater mInflater;
	private Bitmap redIcon;
	private Bitmap greyIcon;
	private Bitmap yellowIcon;
	private Bitmap blueIcon;
	
    public JobAdapter(Context context, int textViewResourceId, ArrayList<JobSummary> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            mInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // create icon bitmaps
            redIcon = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.red);
            greyIcon = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.grey);
            yellowIcon = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.yellow);
            blueIcon = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.blue);
    }
    
    

    @Override
	public JobSummary getItem(int position) {
		return items.get(position);
	}



	@Override
	public long getItemId(int position) {
		return position;
	}



	@Override
	public int getCount() {
		return items.size();
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
			JobSummary js = items.get(position);
		
			View v = mInflater.inflate(R.layout.job_item, null);
			TextView tv = (TextView)v.findViewById(R.id.topLine);
			tv.setText(js.getName());
			//TextView tv2 = (TextView)v.findViewById(R.id.secondLine);
			//tv2.setText("Line 2 info goes here");
			ImageView imgView = (ImageView)v.findViewById(R.id.icon);
			
			if (js.getColor().equals("blue")) {
				imgView.setImageBitmap(blueIcon);
            } else if (js.getColor().equals("red")) {
            	imgView.setImageBitmap(redIcon);
            } else if (js.getColor().equals("yellow")) {
            	imgView.setImageBitmap(yellowIcon);
            } else if (js.getColor().equals("grey")) {
            	imgView.setImageBitmap(greyIcon);
            } else {
            	imgView.setImageBitmap(greyIcon);
            }
			
			return v;
			
//			JobSummary js = items.get(position);
//            TextView tv = new TextView(this.getContext());
//            tv.setHeight(60);
//            tv.setTextSize(30);
//            tv.setTextColor(Color.BLACK);
//            if (js.getColor().equals("blue")) {
//            	tv.setBackgroundColor(Color.BLUE);
//            } else if (js.getColor().equals("red")) {
//            	tv.setBackgroundColor(Color.RED);
//            } else if (js.getColor().equals("yellow")) {
//            	tv.setBackgroundColor(Color.YELLOW);
//            } else if (js.getColor().equals("grey")) {
//            	tv.setBackgroundColor(Color.GRAY);
//            } else {
//            	tv.setBackgroundColor(Color.WHITE);
//            }
//            tv.setText(js.getName());
//            return tv;
    }
}
